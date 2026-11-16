import uuid
from datetime import datetime, timezone, timedelta
from typing import List, Dict, Any, Optional, Tuple

from app.models.schemas import (
    StrategyConditionItem,
    AutoTradeOrder
)
from app.services.pro_strategies_service import ProStrategiesService
from app.services.pattern_recognition_service import PatternRecognitionService
from app.services.market_data_fetcher import MarketDataFetcher
from app.services.frontpage_trading_service import FrontPageTradingService

class StrategyConditionService:
    """
    Multi-Strategy Condition Evaluation Engine.
    Evaluates market conditions across 6 core strategy pillars:
    1. 5-Min Intraday Tri-Confirmation (Supertrend + VWAP + MACD)
    2. Golden Cross & Pullback Re-entry (50 & 200 EMA)
    3. Bollinger Bands Expansion & Breakout
    4. Price Action Consolidation Range Breakout
    5. Open Interest (OI) Institutional Buildup / Covering
    6. Candlestick & Price Action Formations (14 confirmed patterns)

    Executes trades on Anvest Paper Trading (anvest.in/paper-trading)
    or FrontPage (front.page) when conditions are met.
    """

    @classmethod
    def _now_ist_str(cls) -> str:
        ist_now = datetime.now(timezone(timedelta(hours=5, minutes=30)))
        return ist_now.strftime("%d-%b-%Y, %I:%M:%S %p IST")

    @classmethod
    def evaluate_strategies_for_symbol(
        cls,
        symbol: str,
        company_name: Optional[str] = None
    ) -> List[StrategyConditionItem]:
        """
        Evaluates all strategies for a single stock and returns their condition status.
        """
        sym = symbol.upper().strip()
        co_name = company_name or f"{sym} Limited"
        quote = MarketDataFetcher.get_realtime_quote(sym)
        spot = quote.get("spot_price", 1000.0)
        now_str = cls._now_ist_str()

        evaluated: List[StrategyConditionItem] = []

        # Fetch Pro Strategies Data (from PPT implementation)
        pro_data = ProStrategiesService.analyze_symbol(sym)

        # ---------------------------------------------------------------------
        # 1. 5-Min Intraday Tri-Confirmation Strategy (Supertrend + VWAP + MACD)
        # ---------------------------------------------------------------------
        intra = pro_data.daily_intraday
        is_intra_buy = "BUY" in intra.tri_confirmation_signal
        is_intra_sell = "SELL" in intra.tri_confirmation_signal
        intra_met = intra.is_confirmed and (is_intra_buy or is_intra_sell)
        intra_action = "BUY" if is_intra_buy else ("SELL" if is_intra_sell else "WAIT")

        evaluated.append(StrategyConditionItem(
            strategy_id=f"STRAT_TRI_{sym}",
            strategy_name="5M Intraday Tri-Confirmation (ST + VWAP + MACD)",
            symbol=sym,
            company_name=co_name,
            timeframe="5 MIN",
            condition_met=intra_met,
            action=intra_action,
            trigger_condition=(
                f"{intra.supertrend_status} | {intra.vwap_status} | {intra.macd_status}"
            ),
            current_price=spot,
            entry_price=intra.entry_price,
            stop_loss=intra.stop_loss,
            target_price=intra.target_price,
            risk_reward=intra.target_sl_ratio,
            confidence_score=90.0 if intra_met else 60.0,
            execution_advice="Rule: Enter 30s before (B) candle close. 1:1 SL & Target strictly followed.",
            timestamp=now_str
        ))

        # ---------------------------------------------------------------------
        # 2. Golden Cross & Pullback Re-entry Strategy (50 & 200 EMA)
        # ---------------------------------------------------------------------
        gc = pro_data.golden_cross
        is_gc_buy = "BUY" in gc.trade_action
        is_gc_sell = "SELL" in gc.trade_action
        gc_met = gc.reenter_opportunity or "GOLDEN CROSS" in gc.crossover_status or "DEATH CROSS" in gc.crossover_status
        gc_action = "BUY" if is_gc_buy else ("SELL" if is_gc_sell else "WAIT")
        risk_gc = max(spot * 0.012, 10.0)

        evaluated.append(StrategyConditionItem(
            strategy_id=f"STRAT_GC_{sym}",
            strategy_name="Golden Cross 50/200 EMA Pullback Re-Entry",
            symbol=sym,
            company_name=co_name,
            timeframe="15m / Daily",
            condition_met=gc_met,
            action=gc_action,
            trigger_condition=f"{gc.crossover_status} | {gc.reenter_type}",
            current_price=spot,
            entry_price=spot,
            stop_loss=round(spot - risk_gc if gc_action == "BUY" else spot + risk_gc, 2),
            target_price=round(spot + (risk_gc * 2.0) if gc_action == "BUY" else spot - (risk_gc * 2.0), 2),
            risk_reward="1:2",
            confidence_score=88.0 if gc_met else 55.0,
            execution_advice=gc.reenter_explanation,
            timestamp=now_str
        ))

        # ---------------------------------------------------------------------
        # 3. Bollinger Bands Breakout Strategy
        # ---------------------------------------------------------------------
        bb = pro_data.bollinger_bands
        is_bb_call = "CALL" in bb.trade_call or "UPPER" in bb.band_status
        is_bb_put = "PUT" in bb.trade_call or "LOWER" in bb.band_status
        bb_met = "TOUCHING" in bb.band_status or "BREAKING" in bb.band_status
        bb_action = "BUY" if is_bb_call else ("SELL" if is_bb_put else "WAIT")
        bb_sl = round(bb.median_line, 2)
        bb_target = round(bb.upper_band * 1.01 if bb_action == "BUY" else bb.lower_band * 0.99, 2)

        evaluated.append(StrategyConditionItem(
            strategy_id=f"STRAT_BB_{sym}",
            strategy_name="Bollinger Bands Band-Walking Breakout",
            symbol=sym,
            company_name=co_name,
            timeframe="15 MIN",
            condition_met=bb_met,
            action=bb_action,
            trigger_condition=f"{bb.band_status} | {bb.trade_call}",
            current_price=spot,
            entry_price=spot,
            stop_loss=bb_sl,
            target_price=bb_target,
            risk_reward="1:2.2",
            confidence_score=85.0 if bb_met else 50.0,
            execution_advice=bb.explanation,
            timestamp=now_str
        ))

        # ---------------------------------------------------------------------
        # 4. Consolidation Range Breakout Strategy
        # ---------------------------------------------------------------------
        cons = pro_data.consolidation
        is_cons_up = "BREAKOUT" in cons.status or "UP" in cons.status
        is_cons_down = "BREAKDOWN" in cons.status or "DOWN" in cons.status
        cons_met = not cons.is_consolidating and (is_cons_up or is_cons_down)
        cons_action = "BUY" if is_cons_up else ("SELL" if is_cons_down else "WAIT")
        box_half = max((cons.range_high - cons.range_low) * 0.5, spot * 0.008)

        evaluated.append(StrategyConditionItem(
            strategy_id=f"STRAT_CONS_{sym}",
            strategy_name="Price Action Consolidation Box Breakout",
            symbol=sym,
            company_name=co_name,
            timeframe="15 MIN",
            condition_met=cons_met,
            action=cons_action,
            trigger_condition=cons.status,
            current_price=spot,
            entry_price=spot,
            stop_loss=round(spot - box_half if cons_action == "BUY" else spot + box_half, 2),
            target_price=round(spot + (box_half * 2.0) if cons_action == "BUY" else spot - (box_half * 2.0), 2),
            risk_reward="1:2",
            confidence_score=86.0 if cons_met else 45.0,
            execution_advice=cons.guidance,
            timestamp=now_str
        ))

        # ---------------------------------------------------------------------
        # 5. Open Interest (OI) Institutional Buildup / Covering
        # ---------------------------------------------------------------------
        oi = pro_data.open_interest
        oi_interp = oi.interpretation.upper()
        is_oi_bull = "LONG BUILDUP" in oi_interp or "SHORT COVERING" in oi_interp
        is_oi_bear = "SHORT BUILDUP" in oi_interp or "LONG UNWINDING" in oi_interp
        oi_met = is_oi_bull or is_oi_bear
        oi_action = "BUY" if is_oi_bull else ("SELL" if is_oi_bear else "WAIT")
        oi_risk = max(spot * 0.01, 8.0)

        evaluated.append(StrategyConditionItem(
            strategy_id=f"STRAT_OI_{sym}",
            strategy_name="Open Interest (OI) Institutional Matrix",
            symbol=sym,
            company_name=co_name,
            timeframe="Real-Time OI",
            condition_met=oi_met,
            action=oi_action,
            trigger_condition=f"{oi.interpretation} ({oi.market_sentiment}) | PCR: {oi.put_call_ratio_oi:.2f}",
            current_price=spot,
            entry_price=spot,
            stop_loss=round(spot - oi_risk if oi_action == "BUY" else spot + oi_risk, 2),
            target_price=round(spot + (oi_risk * 2.5) if oi_action == "BUY" else spot - (oi_risk * 2.5), 2),
            risk_reward="1:2.5",
            confidence_score=87.0 if oi_met else 50.0,
            execution_advice=oi.action_advice,
            timestamp=now_str
        ))

        # ---------------------------------------------------------------------
        # 6. Candlestick & Price Action Formations
        # ---------------------------------------------------------------------
        patterns = PatternRecognitionService._scan_for_single_symbol(sym)
        for pat in patterns:
            pat_met = pat.status == "CONFIRMED" and pat.reliability_pct >= 75.0
            evaluated.append(StrategyConditionItem(
                strategy_id=f"STRAT_PAT_{pat.pattern_id}",
                strategy_name=f"Candlestick: {pat.pattern_name}",
                symbol=sym,
                company_name=co_name,
                timeframe=pat.timeframe,
                condition_met=pat_met,
                action=pat.pattern_type.upper(),
                trigger_condition=f"{pat.pattern_name} ({pat.reliability_pct:.0f}% Reliability) | {pat.candle_anatomy}",
                current_price=pat.current_price,
                entry_price=pat.trigger_price,
                stop_loss=pat.stop_loss,
                target_price=pat.target1,
                risk_reward=pat.risk_reward_ratio,
                confidence_score=pat.reliability_pct,
                execution_advice=pat.trading_rule,
                timestamp=now_str
            ))

        return evaluated
