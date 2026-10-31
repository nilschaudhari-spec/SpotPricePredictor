import os
import json
import csv
import io
import re
import urllib.request
import ssl
from typing import List, Dict, Any, Optional

class SecuritiesMaster:
    """
    Comprehensive Securities Master Catalog for NSE and BSE.
    Maintains official universe of:
      - 2,620+ NSE equities from official NSE EQUITY_L.csv
      - 1,513+ BSE equities from official BSE eligible.xls
      - Key market benchmark indices and MCX commodities
    """

    _NSE_SECURITIES: List[Dict[str, Any]] = []
    _BSE_SECURITIES: List[Dict[str, Any]] = []
    _ALL_SECURITIES: List[Dict[str, Any]] = []
    _SYMBOL_LOOKUP: Dict[str, Dict[str, Any]] = {}
    _INITIALIZED: bool = False

    @classmethod
    def initialize(cls) -> None:
        if cls._INITIALIZED:
            return

        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        data_dir = os.path.join(base_dir, "data")
        nse_file = os.path.join(data_dir, "nse_securities.json")
        bse_file = os.path.join(data_dir, "bse_eligible.json")

        # 1. Load NSE
        if os.path.exists(nse_file):
            try:
                with open(nse_file, "r", encoding="utf-8") as f:
                    cls._NSE_SECURITIES = json.load(f)
            except Exception as e:
                print(f"[SecuritiesMaster] Error loading nse_securities.json: {e}")

        # 2. Load BSE
        if os.path.exists(bse_file):
            try:
                with open(bse_file, "r", encoding="utf-8") as f:
                    cls._BSE_SECURITIES = json.load(f)
            except Exception as e:
                print(f"[SecuritiesMaster] Error loading bse_eligible.json: {e}")

        # Fallback if files don't exist yet: sync them
        if not cls._NSE_SECURITIES or not cls._BSE_SECURITIES:
            cls.sync_from_official_sources()

        # Build combined index and lookup map
        cls._build_indices()
        cls._INITIALIZED = True
        print(f"[SecuritiesMaster] Loaded {len(cls._NSE_SECURITIES)} NSE and {len(cls._BSE_SECURITIES)} BSE securities.")

    @classmethod
    def _build_indices(cls) -> None:
        cls._ALL_SECURITIES = []
        cls._SYMBOL_LOOKUP = {}

        # 1. Benchmark Indices & Core Bluechips
        from app.api.endpoints.market_data import ALL_SYMBOLS_CATALOG
        for item in ALL_SYMBOLS_CATALOG:
            cls._ALL_SECURITIES.append(item)
            cls._SYMBOL_LOOKUP[item["symbol"].upper()] = item

        # 2. Add NSE Equities
        for item in cls._NSE_SECURITIES:
            sym = item.get("symbol", "").upper()
            if sym and sym not in cls._SYMBOL_LOOKUP:
                cls._ALL_SECURITIES.append(item)
                cls._SYMBOL_LOOKUP[sym] = item

        # 3. Add BSE Equities
        for item in cls._BSE_SECURITIES:
            sym = item.get("symbol", "").upper()
            scrip = item.get("scrip_code", "")
            if scrip:
                cls._SYMBOL_LOOKUP[f"BSE_{scrip}"] = item
                cls._SYMBOL_LOOKUP[scrip] = item
            if sym and sym not in cls._SYMBOL_LOOKUP:
                cls._SYMBOL_LOOKUP[sym] = item
            cls._ALL_SECURITIES.append(item)

    @classmethod
    def get_all(cls, exchange: Optional[str] = None) -> List[Dict[str, Any]]:
        cls.initialize()
        if not exchange or exchange.upper() in ["ALL", "BOTH"]:
            return cls._ALL_SECURITIES
        ex = exchange.upper().strip()
        return [s for s in cls._ALL_SECURITIES if s.get("exchange", "").upper() == ex]

    @classmethod
    def search(cls, query: str, exchange: Optional[str] = None, limit: int = 30) -> List[Dict[str, Any]]:
        cls.initialize()
        q = query.strip().upper()
        if not q:
            return []

        ex = exchange.upper().strip() if exchange else "ALL"
        pool = cls._ALL_SECURITIES
        if ex not in ["ALL", "BOTH"]:
            pool = [s for s in pool if s.get("exchange", "").upper() == ex]

        exact = []
        symbol_starts = []
        name_starts = []
        name_words = []
        contains = []

        for item in pool:
            sym = item.get("symbol", "").upper()
            scrip = str(item.get("scrip_code", "")).upper()
            name = item.get("name", "").upper()
            words = name.replace("-", " ").replace("(", " ").replace(")", " ").split()

            if sym == q or scrip == q:
                exact.append(item)
            elif sym.startswith(q) or scrip.startswith(q):
                symbol_starts.append(item)
            elif name.startswith(q):
                name_starts.append(item)
            elif any(w.startswith(q) for w in words):
                name_words.append(item)
            elif q in sym or q in name or q in scrip:
                contains.append(item)

        results = []
        seen = set()

        # Prioritize exact matches, then prominent symbol starts & name starts
        for group in [exact, symbol_starts, name_starts, name_words, contains]:
            for it in group:
                sym_key = it.get("symbol", "")
                if sym_key not in seen:
                    seen.add(sym_key)
                    results.append(it)
                    if len(results) >= limit:
                        return results

        # Dynamic fallback for arbitrary unindexed equities
        if len(results) == 0 and len(q) >= 2:
            results.append({
                "symbol": q,
                "name": f"{q} Equity",
                "sector": "Equities",
                "exchange": "NSE" if ex in ["ALL", "NSE"] else "BSE",
                "default_spot": 1000.0
            })

        return results

    @classmethod
    def get_stock(cls, symbol: str) -> Optional[Dict[str, Any]]:
        cls.initialize()
        sym = symbol.strip().upper()
        return cls._SYMBOL_LOOKUP.get(sym)

    @classmethod
    def sync_from_official_sources(cls) -> Dict[str, Any]:
        """
        Downloads the latest securities lists directly from:
        - NSE: https://archives.nseindia.com/content/equities/EQUITY_L.csv
        - BSE: https://www.bseindia.com/downloads1/eligible.xls
        """
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"}
        ctx = ssl.create_default_context()
        ctx.check_hostname = False
        ctx.verify_mode = ssl.CERT_NONE

        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        data_dir = os.path.join(base_dir, "data")
        os.makedirs(data_dir, exist_ok=True)

        nse_count = 0
        bse_count = 0

        # 1. Download & Parse NSE
        try:
            url_nse = "https://archives.nseindia.com/content/equities/EQUITY_L.csv"
            req = urllib.request.Request(url_nse, headers=headers)
            with urllib.request.urlopen(req, context=ctx, timeout=12) as resp:
                content = resp.read().decode("utf-8", errors="ignore")

            reader = csv.reader(io.StringIO(content))
            next(reader, None)  # Skip header
            nse_items = []
            for r in reader:
                if len(r) >= 2:
                    sym = r[0].strip().upper()
                    cname = r[1].strip()
                    series = r[2].strip() if len(r) > 2 else "EQ"
                    isin = r[6].strip() if len(r) > 6 else ""
                    if sym:
                        nse_items.append({
                            "symbol": sym,
                            "name": cname,
                            "series": series,
                            "isin": isin,
                            "exchange": "NSE",
                            "sector": "Equities"
                        })
            if nse_items:
                cls._NSE_SECURITIES = nse_items
                nse_count = len(nse_items)
                with open(os.path.join(data_dir, "nse_securities.json"), "w", encoding="utf-8") as f:
                    json.dump(nse_items, f, indent=2)
        except Exception as e:
            print(f"[SecuritiesMaster] Failed to sync NSE: {e}")

        # 2. Download & Parse BSE
        try:
            import xlrd
            url_bse = "https://www.bseindia.com/downloads1/eligible.xls"
            req_bse = urllib.request.Request(url_bse, headers=headers)
            with urllib.request.urlopen(req_bse, context=ctx, timeout=12) as resp:
                bse_bytes = resp.read()

            book = xlrd.open_workbook(file_contents=bse_bytes)
            sh = book.sheet_by_index(0)
            bse_items = []
            for r in range(2, sh.nrows):
                scrip = sh.cell_value(r, 1)
                cname = str(sh.cell_value(r, 2)).strip()
                if scrip and cname:
                    scrip_str = str(int(scrip)) if isinstance(scrip, float) else str(scrip).strip()
                    bse_items.append({
                        "symbol": scrip_str,
                        "scrip_code": scrip_str,
                        "name": cname,
                        "exchange": "BSE",
                        "sector": "BSE Equities"
                    })
            if bse_items:
                cls._BSE_SECURITIES = bse_items
                bse_count = len(bse_items)
                with open(os.path.join(data_dir, "bse_eligible.json"), "w", encoding="utf-8") as f:
                    json.dump(bse_items, f, indent=2)
        except Exception as e:
            print(f"[SecuritiesMaster] Failed to sync BSE: {e}")

        cls._build_indices()
        return {
            "status": "success",
            "nse_securities_count": nse_count,
            "bse_securities_count": bse_count,
            "total_universe": len(cls._ALL_SECURITIES)
        }
