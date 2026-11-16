from datetime import datetime, timezone, timedelta
from typing import List, Dict, Any, Optional

from app.models.schemas import (
    ScanAndExecuteResponse, AutoTradeOrder,
    PatternDetectionItem, StrategyConditionItem, VirtualPortfolio
)
from app.services.pattern_recognition_service import PatternRecognitionService
from app.services.strategy_condition_service import StrategyConditionService
from app.services.frontpage_trading_service import FrontPageTradingService
from app.services.market_data_fetcher import MarketDataFetcher

class AutoTradeScannerService:
    """
    Automated Multi-Stock & Multi-Strategy Scanner & Execution Service.
    Scans entire universe of Indian indices and equities for strategy conditions
    (5M Tri-Confirmation, Golden Cross, Bollinger Bands, Consolidation Breakout,
    OI Buildup, and Candlestick patterns) and automatically places BUY/SELL orders
    on Anvest Paper Trading (anvest.in/paper-trading) or FrontPage (front.page).
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

    # Track recently executed pattern/strategy IDs to avoid duplicate execution within same session
    _RECENTLY_EXECUTED_TRIGGERS = set()

    @classmethod
    def scan_and_execute(
        cls,
        force_execute: bool = False,
        custom_symbols: Optional[List[str]] = None
    ) -> ScanAndExecuteResponse:
        """
        Scans universe stocks, evaluates all strategy conditions, and executes orders
        when strategy rules are confirmed and met.
        """
        settings = FrontPageTradingService.get_settings()
        now_ist = datetime.now(timezone(timedelta(hours=5, minutes=30))).strftime("%d-%b-%Y, %I:%M:%S %p IST")

        # Determine symbols to scan
        if custom_symbols and len(custom_symbols) > 0:
            target_list = [(s.upper(), f"{s.upper()} Equity") for s in custom_symbols]
        else:
            target_list = cls.SCAN_UNIVERSE

        all_detected_patterns: List[PatternDetectionItem] = []
        all_strategy_conditions: List[StrategyConditionItem] = []
        orders_executed: List[AutoTradeOrder] = []

        # 1. Scan and evaluate strategies for all target symbols
        for sym, name in target_list:
            # Candlestick patterns
            patterns = PatternRecognitionService._scan_for_single_symbol(sym)
            all_detected_patterns.extend(patterns)

            # Full Strategy Conditions (5M Tri-Confirmation, Golden Cross, Bollinger, Consolidation, OI)
            conditions = StrategyConditionService.evaluate_strategies_for_symbol(sym, name)
            all_strategy_conditions.extend(conditions)

        # 2. If auto-trading is enabled OR force_execute is requested, evaluate conditions and place orders
        should_execute = settings.is_auto_trade_enabled or force_execute

        if should_execute:
            current_portfolio = FrontPageTradingService.get_portfolio()
            open_symbols_actions = {(p.symbol, p.action) for p in current_portfolio.positions if p.status == "OPEN"}

            # Strategy conditions that are confirmed and met
            qualifying_conditions = [
                c for c in all_strategy_conditions
                if c.condition_met
                and c.action in ["BUY", "SELL"]
                and c.confidence_score >= settings.min_reliability_pct
            ]

            for cond in qualifying_conditions:
                # Strategy category filtering
                strat_type = "CANDLESTICK_PATTERNS" if "Candlestick" in cond.strategy_name else (
                    "INTRADAY_TRI_CONFIRMATION" if "Tri-Confirmation" in cond.strategy_name else (
                        "GOLDEN_CROSS" if "Golden Cross" in cond.strategy_name else (
                            "BOLLINGER_BREAKOUT" if "Bollinger" in cond.strategy_name else (
                                "CONSOLIDATION_BREAKOUT" if "Consolidation" in cond.strategy_name else "OI_BUILDUP"
                            )
                        )
                    )
                )

                if settings.enabled_strategies and strat_type not in settings.enabled_strategies:
                    continue

                # Action direction filter
                if settings.allowed_actions == "BUY_ONLY" and cond.action != "BUY":
                    continue
                if settings.allowed_actions == "SELL_ONLY" and cond.action != "SELL":
                    continue

                # Prevent duplicate entry in the same stock in the same direction
                if (cond.symbol, cond.action) in open_symbols_actions:
                    continue

                # Prevent immediate re-trigger of identical trigger ID in same session
                if cond.strategy_id in cls._RECENTLY_EXECUTED_TRIGGERS:
                    continue

                # Execute order on Anvest Paper Trading (anvest.in) or FrontPage (front.page)
                order = FrontPageTradingService.execute_order(
                    symbol=cond.symbol,
                    action=cond.action,
                    price=cond.current_price,
                    stop_loss=cond.stop_loss,
                    target_price=cond.target_price,
                    pattern_trigger=f"[{cond.strategy_name}] {cond.trigger_condition}",
                    product_type="INTRADAY",
                    company_name=cond.company_name
                )

                if order.status == "EXECUTED":
                    orders_executed.append(order)
                    cls._RECENTLY_EXECUTED_TRIGGERS.add(cond.strategy_id)
                    open_symbols_actions.add((cond.symbol, cond.action))

                    # Limit max new orders in a single scan cycle to 4
                    if len(orders_executed) >= 4:
                        break

        # Refresh portfolio
        updated_portfolio = FrontPageTradingService.get_portfolio()
        met_count = len([c for c in all_strategy_conditions if c.condition_met])

        if len(orders_executed) > 0:
            summary = (
                f"Scanned {len(target_list)} stocks: Evaluated {len(all_strategy_conditions)} strategy setups ({met_count} conditions met). "
                f"Auto-executed {len(orders_executed)} orders on {updated_portfolio.broker_name} "
                f"({', '.join([o.symbol + ' ' + o.action for o in orders_executed])})."
            )
        elif should_execute:
            summary = (
                f"Scanned {len(target_list)} stocks: Evaluated {len(all_strategy_conditions)} strategy setups ({met_count} conditions met). "
                f"No new orders met risk limits (Max concurrent positions: {settings.max_concurrent_positions})."
            )
        else:
            summary = (
                f"Scanned {len(target_list)} stocks: Evaluated {len(all_strategy_conditions)} strategy setups ({met_count} conditions met). "
                f"Auto-paper trade engine is in STANDBY mode."
            )

        return ScanAndExecuteResponse(
            stocks_scanned_count=len(target_list),
            patterns_detected_count=len(all_detected_patterns),
            strategy_conditions_count=met_count,
            orders_executed=orders_executed,
            strategy_conditions_evaluated=all_strategy_conditions,
            portfolio=updated_portfolio,
            summary=summary,
            timestamp=now_ist
        )
