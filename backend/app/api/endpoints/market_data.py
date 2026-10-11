from fastapi import APIRouter, Query
from typing import List, Dict, Any
from app.services.market_data_fetcher import MarketDataFetcher
from app.services.technical_analysis import TechnicalAnalysisEngine

router = APIRouter(prefix="/market", tags=["Market Data"])

ALL_SYMBOLS_CATALOG: List[Dict[str, Any]] = [
    # Indices
    {"symbol": "NIFTY", "name": "Nifty 50 Index", "sector": "Benchmark Index", "exchange": "NSE", "default_spot": 22520.0},
    {"symbol": "BANKNIFTY", "name": "Bank Nifty Index", "sector": "Banking Index", "exchange": "NSE", "default_spot": 55250.0},
    {"symbol": "FINNIFTY", "name": "Nifty Financial Services", "sector": "Financial Index", "exchange": "NSE", "default_spot": 23600.0},
    {"symbol": "MIDCPNIFTY", "name": "Nifty Midcap Select", "sector": "Midcap Index", "exchange": "NSE", "default_spot": 12200.0},
    {"symbol": "SENSEX", "name": "BSE Sensex Benchmark Index", "sector": "Benchmark Index", "exchange": "BSE", "default_spot": 74500.0},

    # Tata Group
    {"symbol": "TATAMOTORS", "name": "Tata Motors Limited", "sector": "Automobile", "exchange": "NSE", "default_spot": 980.0},
    {"symbol": "TCS", "name": "Tata Consultancy Services Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 2156.0},
    {"symbol": "TATASTEEL", "name": "Tata Steel Limited", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 158.0},
    {"symbol": "TATACONSUM", "name": "Tata Consumer Products Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 1120.0},
    {"symbol": "TATACHEM", "name": "Tata Chemicals Limited", "sector": "Chemicals", "exchange": "NSE", "default_spot": 1040.0},
    {"symbol": "TATAPOWER", "name": "Tata Power Company Ltd", "sector": "Power & Energy", "exchange": "NSE", "default_spot": 420.0},
    {"symbol": "TATACOMM", "name": "Tata Communications Ltd", "sector": "Telecom", "exchange": "NSE", "default_spot": 1850.0},
    {"symbol": "TATAELXSI", "name": "Tata Elxsi Limited", "sector": "IT & Design", "exchange": "NSE", "default_spot": 6900.0},
    {"symbol": "TRENT", "name": "Trent Limited (Tata Retail)", "sector": "Retail", "exchange": "NSE", "default_spot": 6200.0},
    {"symbol": "VOLTAS", "name": "Voltas Limited (Tata)", "sector": "Consumer Durables", "exchange": "NSE", "default_spot": 1750.0},
    {"symbol": "TITAN", "name": "Titan Company Limited", "sector": "Consumer Lifestyle", "exchange": "NSE", "default_spot": 3400.0},

    # Banking
    {"symbol": "HDFCBANK", "name": "HDFC Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 707.0},
    {"symbol": "ICICIBANK", "name": "ICICI Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1250.0},
    {"symbol": "SBIN", "name": "State Bank of India", "sector": "Public Banking", "exchange": "NSE", "default_spot": 959.0},
    {"symbol": "KOTAKBANK", "name": "Kotak Mahindra Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1780.0},
    {"symbol": "AXISBANK", "name": "Axis Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1180.0},
    {"symbol": "INDUSINDBK", "name": "IndusInd Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1420.0},
    {"symbol": "BANKBARODA", "name": "Bank of Baroda", "sector": "Public Banking", "exchange": "NSE", "default_spot": 250.0},
    {"symbol": "PNB", "name": "Punjab National Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 115.0},
    {"symbol": "CANBK", "name": "Canara Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 105.0},
    {"symbol": "UNIONBANK", "name": "Union Bank of India", "sector": "Public Banking", "exchange": "NSE", "default_spot": 120.0},
    {"symbol": "FEDERALBNK", "name": "Federal Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 190.0},
    {"symbol": "IDFCFIRSTB", "name": "IDFC First Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 74.0},

    # Financial Services
    {"symbol": "BAJFINANCE", "name": "Bajaj Finance Ltd", "sector": "NBFC", "exchange": "NSE", "default_spot": 960.0},
    {"symbol": "BAJAJFINSV", "name": "Bajaj Finserv Ltd", "sector": "Financial Services", "exchange": "NSE", "default_spot": 1820.0},
    {"symbol": "CHOLAFIN", "name": "Cholamandalam Investment", "sector": "Financial Services", "exchange": "NSE", "default_spot": 1490.0},
    {"symbol": "MUTHOOTFIN", "name": "Muthoot Finance Ltd", "sector": "Gold Loans", "exchange": "NSE", "default_spot": 1950.0},
    {"symbol": "JIOFIN", "name": "Jio Financial Services", "sector": "Financial Services", "exchange": "NSE", "default_spot": 330.0},

    # IT & Software
    {"symbol": "INFY", "name": "Infosys Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 1023.0},
    {"symbol": "WIPRO", "name": "Wipro Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 162.0},
    {"symbol": "HCLTECH", "name": "HCL Technologies", "sector": "IT Services", "exchange": "NSE", "default_spot": 1780.0},
    {"symbol": "TECHM", "name": "Tech Mahindra Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 1580.0},
    {"symbol": "LTIM", "name": "LTIMindtree Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 5800.0},
    {"symbol": "PERSISTENT", "name": "Persistent Systems Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 5200.0},
    {"symbol": "COFORGE", "name": "Coforge Limited", "sector": "IT Services", "exchange": "NSE", "default_spot": 7100.0},
    {"symbol": "ZOMATO", "name": "Zomato Limited", "sector": "Internet & Tech", "exchange": "NSE", "default_spot": 260.0},
    {"symbol": "PAYTM", "name": "One97 Communications (Paytm)", "sector": "Fintech", "exchange": "NSE", "default_spot": 720.0},

    # Oil, Gas & Energy
    {"symbol": "RELIANCE", "name": "Reliance Industries", "sector": "Conglomerate", "exchange": "NSE", "default_spot": 1170.0},
    {"symbol": "ONGC", "name": "Oil & Natural Gas Corporation", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 290.0},
    {"symbol": "BPCL", "name": "Bharat Petroleum Corporation", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 340.0},
    {"symbol": "IOC", "name": "Indian Oil Corporation", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 165.0},
    {"symbol": "COALINDIA", "name": "Coal India Limited", "sector": "Mining & Energy", "exchange": "NSE", "default_spot": 480.0},
    {"symbol": "NTPC", "name": "NTPC Limited", "sector": "Power Generation", "exchange": "NSE", "default_spot": 410.0},
    {"symbol": "POWERGRID", "name": "Power Grid Corporation", "sector": "Power Transmission", "exchange": "NSE", "default_spot": 330.0},
    {"symbol": "ADANIENT", "name": "Adani Enterprises", "sector": "Conglomerate", "exchange": "NSE", "default_spot": 2980.0},
    {"symbol": "ADANIPORTS", "name": "Adani Ports and SEZ", "sector": "Ports & Logistics", "exchange": "NSE", "default_spot": 1380.0},

    # Automobile
    {"symbol": "MARUTI", "name": "Maruti Suzuki India", "sector": "Automobile", "exchange": "NSE", "default_spot": 12400.0},
    {"symbol": "M&M", "name": "Mahindra & Mahindra", "sector": "Automobile", "exchange": "NSE", "default_spot": 2950.0},
    {"symbol": "BAJAJ-AUTO", "name": "Bajaj Auto Limited", "sector": "Automobile", "exchange": "NSE", "default_spot": 11500.0},
    {"symbol": "HEROMOTOCO", "name": "Hero MotoCorp Limited", "sector": "Automobile", "exchange": "NSE", "default_spot": 5100.0},
    {"symbol": "EICHERMOT", "name": "Eicher Motors (Royal Enfield)", "sector": "Automobile", "exchange": "NSE", "default_spot": 4800.0},
    {"symbol": "TVSMOTOR", "name": "TVS Motor Company", "sector": "Automobile", "exchange": "NSE", "default_spot": 2400.0},
    {"symbol": "MOTHERSON", "name": "Samvardhana Motherson", "sector": "Auto Components", "exchange": "NSE", "default_spot": 190.0},

    # Pharma & Healthcare
    {"symbol": "SUNPHARMA", "name": "Sun Pharmaceutical", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1850.0},
    {"symbol": "CIPLA", "name": "Cipla Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1500.0},
    {"symbol": "DRREDDY", "name": "Dr. Reddy's Laboratories", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 6500.0},
    {"symbol": "DIVISLAB", "name": "Divi's Laboratories", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 5200.0},
    {"symbol": "APOLLOHOSP", "name": "Apollo Hospitals Enterprise", "sector": "Healthcare", "exchange": "NSE", "default_spot": 6800.0},

    # FMCG & Others
    {"symbol": "HINDUNILVR", "name": "Hindustan Unilever Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 2750.0},
    {"symbol": "ITC", "name": "ITC Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 266.0},
    {"symbol": "NESTLEIND", "name": "Nestle India Limited", "sector": "FMCG", "exchange": "NSE", "default_spot": 2450.0},
    {"symbol": "BRITANNIA", "name": "Britannia Industries Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 5800.0},
    {"symbol": "LT", "name": "Larsen & Toubro Ltd", "sector": "Engineering & Infra", "exchange": "NSE", "default_spot": 3600.0},
    {"symbol": "BHARTIARTL", "name": "Bharti Airtel Limited", "sector": "Telecommunications", "exchange": "NSE", "default_spot": 1650.0},
    {"symbol": "JSWSTEEL", "name": "JSW Steel Limited", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 980.0},
    {"symbol": "BEL", "name": "Bharat Electronics Ltd", "sector": "Defense", "exchange": "NSE", "default_spot": 290.0},
    {"symbol": "HAL", "name": "Hindustan Aeronautics Ltd", "sector": "Defense", "exchange": "NSE", "default_spot": 4400.0},
    {"symbol": "IRCTC", "name": "Indian Railway Catering & Tourism", "sector": "Railways", "exchange": "NSE", "default_spot": 880.0},
    {"symbol": "ASIANPAINT", "name": "Asian Paints Limited", "sector": "Paints", "exchange": "NSE", "default_spot": 3100.0},
]

@router.get("/symbols")
async def get_supported_symbols() -> Dict[str, Any]:
    indices = [s for s in ALL_SYMBOLS_CATALOG if "Index" in s.get("sector", "")]
    stocks = [s for s in ALL_SYMBOLS_CATALOG if "Index" not in s.get("sector", "")]
    return {
        "indices": indices,
        "stocks": stocks
    }

@router.get("/search")
async def search_symbols(q: str = Query(..., min_length=1, description="Search query string")) -> List[Dict[str, Any]]:
    query = q.strip().upper()
    if len(query) < 3:
        return []

    exact = []
    symbol_starts = []
    name_starts = []
    name_words = []
    contains = []

    for item in ALL_SYMBOLS_CATALOG:
        sym = item["symbol"].upper()
        name = item.get("name", "").upper()
        words = name.replace("-", " ").replace("(", " ").replace(")", " ").split()

        if sym == query:
            exact.append(item)
        elif sym.startswith(query):
            symbol_starts.append(item)
        elif name.startswith(query):
            name_starts.append(item)
        elif any(w.startswith(query) for w in words):
            name_words.append(item)
        elif query in sym or query in name:
            contains.append(item)

    results = []
    seen = set()
    for category in [exact, symbol_starts, name_starts, name_words, contains]:
        for it in category:
            if it["symbol"] not in seen:
                seen.add(it["symbol"])
                results.append(it)

    return results[:15]

@router.get("/quote/{symbol}")
async def get_quote_and_technicals(symbol: str) -> Dict[str, Any]:
    sym = symbol.upper().strip()
    quote = MarketDataFetcher.get_realtime_quote(sym)
    spot = quote["spot_price"]
    technicals = TechnicalAnalysisEngine.evaluate_technicals(spot_price=spot)

    return {
        "symbol": sym,
        "spot_price": spot,
        "previous_close": quote["previous_close"],
        "change": quote["change"],
        "change_percent": quote["change_percent"],
        "is_realtime": quote["is_realtime"],
        "step": quote["step"],
        "lot_size": quote["lot_size"],
        "technicals": technicals
    }

@router.get("/news")
async def get_daily_market_news() -> Dict[str, Any]:
    from app.services.market_news_service import MarketNewsService
    return MarketNewsService.get_daily_briefing()

