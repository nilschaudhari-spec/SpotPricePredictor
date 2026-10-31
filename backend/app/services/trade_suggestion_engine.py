import math
from typing import List, Dict, Any, Optional
from datetime import datetime, timezone
import uuid

from app.models.schemas import (
    TradeSuggestion, TradeSuggestionsResponse, TradeAction,
    InstrumentType, SignalCategory, SignalStrength,
    TechnicalSummary, FNODetails, PredictionRequest,
    PredictionHorizon, TrendDirection, OptionType
)
from app.services.market_data_fetcher import MarketDataFetcher
from app.services.technical_analysis import TechnicalAnalysisEngine
from app.services.oi_analyzer import OIAnalyzer
from app.services.prediction_orchestrator import PredictionOrchestrator
from app.services.greeks_engine import GreeksEngine

class TradeSuggestionEngine:
    """
    Quantitative AI Trade Suggestion & Scanning Engine.
    Scans Indices and Equities across Cash (Equity) and Derivatives (F&O: Futures & Options),
    generating actionable trade setups backed by Technical Analysis and Multi-Model AI Spot Price Predictions.
    """

    CORE_INDEX_UNIVERSE = ["NIFTY", "BANKNIFTY", "FINNIFTY", "SENSEX"]
    CORE_EQUITY_UNIVERSE = [
        "RELIANCE", "HDFCBANK", "TCS", "INFY", "SBIN",
        "TATAMOTORS", "ICICIBANK", "BAJFINANCE", "TATASTEEL", "LT"
    ]

    @classmethod
    def get_suggestions(
        cls,
        category: Optional[str] = "ALL",      # "ALL", "INDEX", "EQUITY"
        instrument: Optional[str] = "ALL",    # "ALL", "EQUITY", "FNO"
        action: Optional[str] = "ALL",        # "ALL", "BUY", "SELL"
        symbol: Optional[str] = None          # Custom symbol lookup
    ) -> TradeSuggestionsResponse:
        suggestions: List[TradeSuggestion] = []

        if symbol:
            sym_clean = symbol.upper().strip()
            # Generate both Equity and FNO setups for this specific symbol
            eq_sug = cls._generate_trade_setup(sym_clean, is_fno=False)
            if eq_sug:
                suggestions.append(eq_sug)
            fno_sug = cls._generate_trade_setup(sym_clean, is_fno=True)
            if fno_sug:
                suggestions.append(fno_sug)
        else:
            # Universe scanning
            target_indices = cls.CORE_INDEX_UNIVERSE
            target_equities = cls.CORE_EQUITY_UNIVERSE

            # 1. Process Indices (F&O primarily, plus Cash benchmark index)
            for sym in target_indices:
                sug_fno = cls._generate_trade_setup(sym, is_fno=True)
                if sug_fno:
                    suggestions.append(sug_fno)

            # 2. Process Equities (both Equity Cash and F&O)
            for sym in target_equities:
                sug_eq = cls._generate_trade_setup(sym, is_fno=False)
                if sug_eq:
                    suggestions.append(sug_eq)
                sug_fno = cls._generate_trade_setup(sym, is_fno=True)
                if sug_fno:
                    suggestions.append(sug_fno)

        # Apply filtering
        filtered: List[TradeSuggestion] = []
        for s in suggestions:
            # Category filter
            if category and category.upper() != "ALL":
                if s.category.value != category.upper():
                    continue

            # Instrument filter
            if instrument and instrument.upper() != "ALL":
                if instrument.upper() == "FNO" and s.instrument_type not in [InstrumentType.OPTIONS, InstrumentType.FUTURES]:
                    continue
                if instrument.upper() == "EQUITY" and s.instrument_type != InstrumentType.EQUITY:
                    continue

            # Action filter
            if action and action.upper() != "ALL":
                if s.action.value != action.upper():
                    continue

            filtered.append(s)

        # Market overall sentiment
        bullish_count = sum(1 for s in filtered if s.action == TradeAction.BUY)
        bearish_count = sum(1 for s in filtered if s.action == TradeAction.SELL)
        if bullish_count > bearish_count * 1.5:
            sentiment = "Strongly Bullish Bias Across Universe"
        elif bearish_count > bullish_count * 1.5:
            sentiment = "Bearish Pressure / Defensive Rotation"
        else:
            sentiment = "Mixed / Stock-Specific Stock Picking Regime"

        return TradeSuggestionsResponse(
            suggestions=filtered,
            market_sentiment=sentiment,
            total_signals=len(filtered)
        )

    @classmethod
    def _generate_trade_setup(cls, symbol: str, is_fno: bool) -> Optional[TradeSuggestion]:
        try:
            quote = MarketDataFetcher.get_realtime_quote(symbol)
            spot = quote["spot_price"]
            change_pct = quote["change_percent"]
            step = quote["step"]
            lot_size = quote["lot_size"]

            # 1. Evaluate Technical Indicators
            tech_eval = TechnicalAnalysisEngine.evaluate_technicals(
                spot_price=spot,
                quote=quote,
                trend_mode=TrendDirection.AUTO
            )

            # 2. Evaluate Option Chain & OI
            chain_payload = MarketDataFetcher.get_spot_and_chain(symbol=symbol, custom_spot=spot)
            strikes = chain_payload["strikes"]
            pcr, _ = OIAnalyzer.calculate_pcr(strikes)
            max_pain = OIAnalyzer.calculate_max_pain(strikes)
            supports, resistances = OIAnalyzer.identify_support_resistance(strikes, spot)
            dex, gex, dealer_pos = OIAnalyzer.calculate_exposures(strikes, spot)

            # 3. Run AI Prediction Engine
            pred_req = PredictionRequest(
                symbol=symbol,
                current_spot_price=spot,
                prediction_horizon=PredictionHorizon.MIN_15,
                trend_mode=TrendDirection.AUTO
            )
            prediction = PredictionOrchestrator.run_prediction(pred_req)

            # Determine Action and Strength
            is_bullish = prediction.prob_bullish >= prediction.prob_bearish and prediction.prob_bullish >= 0.40
            action = TradeAction.BUY if is_bullish else TradeAction.SELL
            confidence = prediction.confidence_pct

            if is_bullish:
                strength = SignalStrength.STRONG_BUY if confidence >= 75.0 else SignalStrength.BUY
            else:
                strength = SignalStrength.STRONG_SELL if confidence >= 75.0 else SignalStrength.SELL

            # Calculate Entry, Targets, Stop Loss
            atr = tech_eval["atr"]
            is_index = symbol in cls.CORE_INDEX_UNIVERSE

            if action == TradeAction.BUY:
                entry = round(spot * 0.999, 2)
                t1 = round(spot + (atr * 1.2), 2)
                t2 = round(spot + (atr * 2.2), 2)
                sl = round(spot - (atr * 0.9), 2)
                potential_gain = round(((t1 - entry) / entry) * 100.0, 2)
            else:
                entry = round(spot * 1.001, 2)
                t1 = round(spot - (atr * 1.2), 2)
                t2 = round(spot - (atr * 2.2), 2)
                sl = round(spot + (atr * 0.9), 2)
                potential_gain = round(((entry - t1) / entry) * 100.0, 2)

            risk_dist = abs(entry - sl)
            reward_dist = abs(t1 - entry)
            rr_ratio = f"1:{max(1.1, round(reward_dist / (risk_dist + 1e-4), 1))}"

            # Technical Summary
            rsi_val = tech_eval["rsi"]
            rsi_sig = (
                "Bullish Momentum (>60)" if rsi_val >= 60 else
                ("Bearish Momentum (<40)" if rsi_val <= 40 else "Neutral Zone")
            )
            macd_status = "Bullish Crossover above signal" if tech_eval["trend_score"] > 0 else "Bearish Divergence below signal"
            ema_alignment = f"Spot vs EMA20 ({tech_eval['ema_20']}) & EMA50 ({tech_eval['ema_50']})"
            vwap_sig = tech_eval["vwap_position"]
            supertrend_sig = f"Supertrend {tech_eval['supertrend']}"
            bb_sig = "Volatility Expansion" if atr > (spot * 0.01) else "Volatility Contraction"

            sup_level = supports[-1] if supports else round(spot * 0.985, 2)
            res_level = resistances[0] if resistances else round(spot * 1.015, 2)

            # Detailed Rationale
            action_verb = "Long accumulation" if action == TradeAction.BUY else "Short setup"
            fno_context = f"with PCR of {pcr:.2f} and Max Pain at {max_pain:.0f}"
            rationale = (
                f"{action_verb} triggered for {symbol}. {prediction.strategy_rationale} "
                f"Technical indicators show {supertrend_sig}, RSI at {rsi_val:.1f} ({rsi_sig}), "
                f"and {vwap_sig.lower()}. Multi-model AI confidence is {confidence:.0f}%, "
                f"{fno_context}. Target 1 set at {t1} with defined Stop Loss at {sl}."
            )

            tech_summary = TechnicalSummary(
                trend=prediction.suggested_strategy.value,
                rsi=rsi_val,
                rsi_signal=rsi_sig,
                macd_status=macd_status,
                ema_alignment=ema_alignment,
                supertrend=supertrend_sig,
                vwap_signal=vwap_sig,
                bollinger_signal=bb_sig,
                support_level=sup_level,
                resistance_level=res_level,
                oi_pcr=pcr,
                max_pain=max_pain,
                rationale=rationale
            )

            category_enum = SignalCategory.INDEX if is_index else SignalCategory.EQUITY

            # Determine FNO details or Cash Equity
            fno_details: Optional[FNODetails] = None
            if is_fno:
                instrument_type = InstrumentType.OPTIONS
                opt_type_str = "CE" if action == TradeAction.BUY else "PE"
                # Select Strike (Near ATM / 1 step OTM)
                strike_mult = round(spot / step)
                if action == TradeAction.BUY:
                    chosen_strike = float(strike_mult * step)
                else:
                    chosen_strike = float(strike_mult * step)

                expiries = MarketDataFetcher.get_upcoming_expiries(symbol, count=1)
                exp_date_str = expiries[0].date if expiries else "Nearest Weekly"

                # Estimate Option Premium via analytical Black-Scholes
                T = max(0.005, (expiries[0].days_to_expiry if expiries else 3.5) / 365.0)
                iv = quote.get("iv", 18.0) / 100.0
                prem = GreeksEngine.calculate_price(
                    spot, chosen_strike, T, 0.07, iv,
                    OptionType.CALL if opt_type_str == "CE" else OptionType.PUT
                )
                premium_entry = max(5.0, round(prem, 2))
                premium_target = round(premium_entry * 1.45, 2)
                premium_stop = round(premium_entry * 0.70, 2)

                strategy_name = f"Long {opt_type_str} ({chosen_strike:.0f})"
                contract_name = f"{symbol} {int(chosen_strike)} {opt_type_str}"

                fno_details = FNODetails(
                    contract_name=contract_name,
                    derivative_type="OPTIONS",
                    option_type=opt_type_str,
                    strike_price=chosen_strike,
                    expiry_date=exp_date_str,
                    premium_entry=premium_entry,
                    premium_target=premium_target,
                    premium_stop_loss=premium_stop,
                    lot_size=lot_size,
                    strategy_type=strategy_name
                )
            else:
                instrument_type = InstrumentType.EQUITY

            item_id = f"{symbol}_{'FNO' if is_fno else 'EQ'}_{action.value}_{int(spot)}"

            name_mapping = {
                "NIFTY": "Nifty 50 Benchmark Index",
                "BANKNIFTY": "Nifty Bank Sector Index",
                "FINNIFTY": "Nifty Financial Services",
                "SENSEX": "BSE Sensex Benchmark",
                "RELIANCE": "Reliance Industries Ltd",
                "HDFCBANK": "HDFC Bank Limited",
                "TCS": "Tata Consultancy Services",
                "INFY": "Infosys Limited",
                "SBIN": "State Bank of India",
                "TATAMOTORS": "Tata Motors Limited",
                "ICICIBANK": "ICICI Bank Limited",
                "BAJFINANCE": "Bajaj Finance Limited",
                "TATASTEEL": "Tata Steel Limited",
                "LT": "Larsen & Toubro Ltd"
            }
            display_name = name_mapping.get(symbol, f"{symbol} Stock")

            return TradeSuggestion(
                id=item_id,
                symbol=symbol,
                name=display_name,
                category=category_enum,
                instrument_type=instrument_type,
                action=action,
                signal_strength=strength,
                current_price=spot,
                entry_price=entry,
                target_1=t1,
                target_2=t2,
                stop_loss=sl,
                risk_reward_ratio=rr_ratio,
                potential_gain_pct=potential_gain,
                timeframe="Intraday (15-60m)" if is_index else "Swing (1-3 Days)",
                confidence_pct=confidence,
                fno_details=fno_details,
                technical_analysis=tech_summary,
                timestamp=datetime.now(timezone.utc)
            )
        except Exception:
            return None
