from datetime import datetime, timezone, timedelta
from typing import List, Dict, Any, Optional

from app.models.schemas import (
    ScanAndExecuteResponse, AutoTradeOrder,
    PatternDetectionItem, VirtualPortfolio
)
from app.services.pattern_recognition_service import PatternRecognitionService
from app.services.frontpage_trading_service import FrontPageTradingService
from app.services.market_data_fetcher import MarketDataFetcher

class AutoTradeScannerService:
    """
    Automated Multi-Stock Scanner & Execution Service.
    Scans entire universe of Indian indices and equities for high-conviction
    candlestick patterns & price action techniques, and automatically places
    BUY/SELL orders on FrontPage or the virtual demo broker.
    """

    SCAN_UNIVERSE = [
        # Indices
        ("NIFTY", "Nifty 50 Index"),
        ("BANKNIFTY", "Nifty Bank Index"),
        ("FINNIFTY", "Nifty Financial Services"),
        ("SENSEX", "BSE Sensex Index"),
        # Core Equities
        ("RELIANCE", "Reliance Industries Ltd"),
        ("TCS", "Tata Consultancy Services"),
        ("HDFCBANK", "HDFC Bank Ltd"),
        ("INFY", "Infosys Ltd"),
        ("TATAMOTORS", "Tata Motors Ltd"),
        ("SBIN", "State Bank of India"),
        ("TITAN", "Titan Company Ltd"),
        ("BHARTIARTL", "Bharti Airtel Ltd"),
        ("ITC", "ITC Limited"),
        ("MARUTI", "Maruti Suzuki India Ltd"),
        ("BAJFINANCE", "Bajaj Finance Ltd"),
        ("ICICIBANK", "ICICI Bank Ltd"),
        ("AXISBANK", "Axis Bank Ltd"),
        ("TATASTEEL", "Tata Steel Ltd"),
        ("SUNPHARMA", "Sun Pharmaceutical Industries"),
        ("ZOMATO", "Zomato Ltd"),
        ("WIPRO", "Wipro Ltd"),
        ("LT", "Larsen & Toubro Ltd"),
        ("BEL", "Bharat Electronics Ltd"),
        ("HAL", "Hindustan Aeronautics Ltd")
    ]

    # Track recently executed pattern IDs to avoid duplicate execution within the same cycle
    _RECENTLY_EXECUTED_PATTERNS = set()

    @classmethod
    def scan_and_execute(
        cls,
        force_execute: bool = False,
        custom_symbols: Optional[List[str]] = None
    ) -> ScanAndExecuteResponse:
        """
        Scans all universe stocks, detects patterns, and executes orders for qualifying patterns.
        """
        settings = FrontPageTradingService.get_settings()
        now_ist = datetime.now(timezone(timedelta(hours=5, minutes=30))).strftime("%d-%b-%Y, %I:%M:%S %p IST")

        # Determine symbols to scan
        if custom_symbols and len(custom_symbols) > 0:
            target_list = [(s.upper(), f"{s.upper()} Equity") for s in custom_symbols]
        else:
            target_list = cls.SCAN_UNIVERSE

        all_detected_patterns: List[PatternDetectionItem] = []
        orders_executed: List[AutoTradeOrder] = []

        # 1. Scan all symbols
        for sym, name in target_list:
            patterns = PatternRecognitionService._scan_for_single_symbol(sym)
            all_detected_patterns.extend(patterns)

        # 2. If auto-trading is enabled OR force_execute is requested, evaluate and place orders
        should_execute = settings.is_auto_trade_enabled or force_execute

        if should_execute:
            current_portfolio = FrontPageTradingService.get_portfolio()
            open_symbols_actions = {(p.symbol, p.action) for p in current_portfolio.positions if p.status == "OPEN"}

            for pat in all_detected_patterns:
                # Reliability check
                if pat.reliability_pct < settings.min_reliability_pct:
                    continue

                # Action direction filter
                if settings.allowed_actions == "BUY_ONLY" and pat.pattern_type != "BUY":
                    continue
                if settings.allowed_actions == "SELL_ONLY" and pat.pattern_type != "SELL":
                    continue

                # Prevent duplicate entry in the same stock in the same direction
                if (pat.symbol, pat.pattern_type) in open_symbols_actions:
                    continue

                # Prevent immediate re-trigger of identical pattern ID in same session
                if pat.pattern_id in cls._RECENTLY_EXECUTED_PATTERNS:
                    continue

                # Place order on FrontPage / Demo broker
                order = FrontPageTradingService.execute_order(
                    symbol=pat.symbol,
                    action=pat.pattern_type,
                    price=pat.current_price,
                    stop_loss=pat.stop_loss,
                    target_price=pat.target1,
                    pattern_trigger=f"{pat.pattern_name} ({pat.reliability_pct:.0f}% Conf)",
                    product_type="INTRADAY",
                    company_name=pat.company_name
                )

                if order.status == "EXECUTED":
                    orders_executed.append(order)
                    cls._RECENTLY_EXECUTED_PATTERNS.add(pat.pattern_id)
                    open_symbols_actions.add((pat.symbol, pat.pattern_type))

                    # Limit max new orders in a single scan cycle to 4
                    if len(orders_executed) >= 4:
                        break

        # Refresh portfolio
        updated_portfolio = FrontPageTradingService.get_portfolio()

        if len(orders_executed) > 0:
            summary = (
                f"Scanned {len(target_list)} stocks: Discovered {len(all_detected_patterns)} patterns. "
                f"Auto-executed {len(orders_executed)} orders on {updated_portfolio.broker_name} "
                f"({', '.join([o.symbol + ' ' + o.action for o in orders_executed])})."
            )
        elif should_execute:
            summary = (
                f"Scanned {len(target_list)} stocks: Discovered {len(all_detected_patterns)} patterns. "
                f"No new orders met risk limits (Max concurrent positions: {settings.max_concurrent_positions})."
            )
        else:
            summary = (
                f"Scanned {len(target_list)} stocks: Discovered {len(all_detected_patterns)} confirmed patterns. "
                f"Auto-trade engine is currently in STANDBY mode."
            )

        return ScanAndExecuteResponse(
            stocks_scanned_count=len(target_list),
            patterns_detected_count=len(all_detected_patterns),
            orders_executed=orders_executed,
            portfolio=updated_portfolio,
            summary=summary,
            timestamp=now_ist
        )
