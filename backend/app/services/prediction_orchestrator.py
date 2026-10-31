from typing import Dict, Any, List
from datetime import datetime, timedelta
import numpy as np

from app.models.schemas import (
    PredictionRequest, PredictionResponse, AdvancedAnalytics,
    ModelForecastBreakdown, PredictionHorizon, TrendDirection,
    TechnicalIndicatorInput, OptionChainResponse, StrategyType,
    StrategyPredictionItem
)
from app.services.greeks_engine import GreeksEngine
from app.services.oi_analyzer import OIAnalyzer
from app.services.technical_analysis import TechnicalAnalysisEngine
from app.services.ml_ensemble import MLEnsemblePredictor
from app.services.lstm_predictor import LSTMPredictor
from app.services.price_action import PriceActionEngine
from app.services.strategy_recommender import StrategyRecommender
from app.services.market_data_fetcher import MarketDataFetcher

class PredictionOrchestrator:
    """
    Master Quantitative Prediction Orchestrator.
    Fuses all 5 analytical sub-engines into an ensemble forecast with
    confidence intervals, risk attribution, and strategic positioning.
    """

    @classmethod
    def resolve_horizon_minutes(cls, horizon: Any, custom_minutes: int = None) -> int:
        val = horizon.value if hasattr(horizon, "value") else str(horizon)
        mapping = {
            "MIN_5": 5, "5_MIN": 5,
            "MIN_10": 10, "10_MIN": 10,
            "MIN_15": 15, "15_MIN": 15,
            "MIN_30": 30, "30_MIN": 30,
            "HOUR_1": 60, "1_HOUR": 60,
        }
        if val in ["CUSTOM", "Custom"]:
            return custom_minutes or 45
        return mapping.get(val, 15)

    @classmethod
    def run_prediction(cls, request: PredictionRequest) -> PredictionResponse:
        current_spot = request.current_spot_price
        horizon_mins = cls.resolve_horizon_minutes(request.prediction_horizon, request.custom_minutes)

        # 0. Real-time Quote
        quote = MarketDataFetcher.get_realtime_quote(request.symbol)

        # 1. Option Chain Resolution
        if not request.option_chain or len(request.option_chain) == 0:
            chain_payload = MarketDataFetcher.get_spot_and_chain(
                symbol=request.symbol,
                custom_spot=current_spot
            )
            strikes = chain_payload["strikes"]
        else:
            strikes = request.option_chain

        # 2. Technicals & Trend Evaluation
        tech_eval = TechnicalAnalysisEngine.evaluate_technicals(
            spot_price=current_spot,
            technicals=request.technicals,
            quote=quote,
            trend_mode=request.trend_mode
        )

        # 3. Open Interest & Dealer Positioning Metrics
        oi_pcr, vol_pcr = OIAnalyzer.calculate_pcr(strikes)
        max_pain = OIAnalyzer.calculate_max_pain(strikes)
        supports, resistances = OIAnalyzer.identify_support_resistance(strikes, current_spot)
        dex, gex, dealer_pos = OIAnalyzer.calculate_exposures(strikes, current_spot)

        # 4. Average IV calculation
        atm_strike = min(strikes, key=lambda s: abs(s.strike_price - current_spot)) if strikes else None
        avg_iv = (atm_strike.call.iv + atm_strike.put.iv) / 2.0 if atm_strike else 15.0

        # --- MODEL 1: OI Dynamics ---
        m1_spot = OIAnalyzer.estimate_spot_from_oi(current_spot, strikes, horizon_mins)

        # --- MODEL 2: Option Greeks & MM Hedging ---
        m2_spot = GreeksEngine.estimate_spot_from_greeks(
            current_spot=current_spot,
            net_delta_exposure=dex,
            net_gamma_exposure=gex,
            horizon_minutes=horizon_mins,
            avg_iv=avg_iv
        )

        # --- MODEL 3: ML Regression Ensemble (XGB + LGB + RF) ---
        features = MLEnsemblePredictor.extract_features(
            spot_price=current_spot,
            strikes=strikes,
            tech_eval=tech_eval,
            pcr=oi_pcr,
            dex=dex,
            gex=gex
        )
        m3_spot = MLEnsemblePredictor.predict_spot(current_spot, features, horizon_mins)

        # --- MODEL 4: LSTM Time Series / Recurrent Pathing ---
        m4_spot = LSTMPredictor.predict_spot(
            current_spot=current_spot,
            trend_score=tech_eval["trend_score"],
            momentum_score=tech_eval["momentum_score"],
            volatility_atr=tech_eval["atr"],
            horizon_minutes=horizon_mins
        )

        # --- MODEL 5: Price Action & Liquidity Structure ---
        pa_eval = PriceActionEngine.evaluate_price_action(
            spot_price=current_spot,
            supports=supports,
            resistances=resistances,
            atr=tech_eval["atr"],
            trend_score=tech_eval["trend_score"]
        )
        m5_spot = PriceActionEngine.estimate_spot(
            spot_price=current_spot,
            pa_eval=pa_eval,
            atr=tech_eval["atr"],
            horizon_minutes=horizon_mins
        )

        # --- Multi-Model Ensemble Weighting ---
        # Weights: OI (25%), Greeks (20%), ML Ensemble (25%), LSTM (15%), Price Action (15%)
        ensemble_spot = round(
            (0.25 * m1_spot) +
            (0.20 * m2_spot) +
            (0.25 * m3_spot) +
            (0.15 * m4_spot) +
            (0.15 * m5_spot),
            2
        )

        # --- Dispersion & Confidence % ---
        model_estimates = [m1_spot, m2_spot, m3_spot, m4_spot, m5_spot]
        std_dev = float(np.std(model_estimates))
        dispersion_ratio = std_dev / (tech_eval["atr"] + 1e-5)

        # High confluence between all 5 models yields > 85% confidence; high dispersion lowers it
        base_confidence = 88.0 - (dispersion_ratio * 25.0)
        confidence_pct = round(max(45.0, min(96.0, base_confidence)), 1)

        # --- Probabilistic Drift & Distribution ---
        net_drift = ensemble_spot - current_spot
        normalized_drift = net_drift / (tech_eval["atr"] * 0.75 + 1e-5)

        # Sigmoid probability distribution
        direction_bias = (normalized_drift * 1.5) + (tech_eval["trend_score"] / 60.0)
        p_bull = 1.0 / (1.0 + np.exp(- direction_bias))
        p_bear = 1.0 / (1.0 + np.exp(direction_bias))
        p_side = max(0.08, 1.0 - abs(p_bull - p_bear) * 0.8)

        # Normalize to sum = 1.0
        total_p = p_bull + p_bear + p_side
        prob_bullish = round(float(p_bull / total_p), 3)
        prob_bearish = round(float(p_bear / total_p), 3)
        prob_sideways = round(float(p_side / total_p), 3)

        # --- Expected Range (68% Confidence Band / 1-sigma) ---
        time_scaling = (horizon_mins / 60.0) ** 0.5
        expected_range_half = (tech_eval["atr"] * 0.85) * max(0.5, time_scaling)

        expected_high = round(max(ensemble_spot, current_spot) + (expected_range_half * 0.6), 2)
        expected_low = round(min(ensemble_spot, current_spot) - (expected_range_half * 0.6), 2)

        # Qualitative tags
        market_strength = "Strong" if abs(tech_eval["trend_score"]) >= 45 else ("Moderate" if abs(tech_eval["trend_score"]) >= 15 else "Weak")
        volatility_level = "Extreme" if avg_iv > 26 else ("High" if avg_iv > 19 else ("Normal" if avg_iv > 12 else "Low"))
        risk_level = "High" if (dispersion_ratio > 1.2 or avg_iv > 22) else ("Moderate" if dispersion_ratio > 0.6 else "Low")

        # Suggested Strategy
        strategy, rationale = StrategyRecommender.recommend(
            prob_bullish=prob_bullish,
            prob_bearish=prob_bearish,
            prob_sideways=prob_sideways,
            avg_iv=avg_iv,
            confidence_pct=confidence_pct,
            dealer_positioning=dealer_pos
        )

        # Target and Stop Loss aligned with directional trade bias
        is_bearish_bias = prob_bearish > prob_bullish or strategy in [StrategyType.BUY_PUT, StrategyType.PUT_SPREAD]
        if is_bearish_bias:
            target_price = expected_low
            stop_loss = round(current_spot + (expected_range_half * 0.7), 2)
        else:
            target_price = expected_high
            stop_loss = round(current_spot - (expected_range_half * 0.7), 2)

        expected_time_str = f"{horizon_mins} mins"

        advanced_analytics = AdvancedAnalytics(
            pcr=oi_pcr,
            oi_shift_bias="Bullish OI Accumulation" if oi_pcr >= 1.15 else ("Bearish Call Writing" if oi_pcr <= 0.85 else "Neutral Range"),
            delta_exposure_dex=dex,
            gamma_exposure_gex=gex,
            dealer_positioning=dealer_pos,
            max_pain=max_pain,
            support_levels=supports,
            resistance_levels=resistances,
            vwap_position=tech_eval["vwap_position"],
            atr_volatility=tech_eval["atr"],
            momentum_score=tech_eval["momentum_score"],
            trend_score=tech_eval["trend_score"],
            market_sentiment_score=tech_eval["market_sentiment_score"]
        )

        model_breakdown = ModelForecastBreakdown(
            oi_analysis_spot=m1_spot,
            greeks_spot=m2_spot,
            ml_ensemble_spot=m3_spot,
            lstm_spot=m4_spot,
            price_action_spot=m5_spot
        )

        # Determine unequivocal BUY, HOLD, or SELL recommendation for stock/asset
        if (prob_bullish >= 0.45 and prob_bullish > prob_bearish and ensemble_spot >= current_spot) or (strategy in [StrategyType.BUY_CALL, StrategyType.CALL_SPREAD]):
            action_recommendation = "BUY"
            action_rationale = f"BUY Recommendation: Bullish probability of {round(prob_bullish * 100, 1)}% with upside target of ₹{target_price} and stop-loss at ₹{stop_loss}."
        elif (prob_bearish >= 0.45 and prob_bearish > prob_bullish and ensemble_spot <= current_spot) or (strategy in [StrategyType.BUY_PUT, StrategyType.PUT_SPREAD]):
            action_recommendation = "SELL"
            action_rationale = f"SELL Recommendation: Downward pressure with Bearish probability of {round(prob_bearish * 100, 1)}% targeting ₹{target_price} with risk cap at ₹{stop_loss}."
        else:
            action_recommendation = "HOLD"
            action_rationale = f"HOLD Recommendation: Range-bound consolidation ({round(prob_sideways * 100, 1)}% neutral regime). Maintain existing positions; await clear directional breakout."

        atr_val = tech_eval["atr"]
        various_strategies: List[StrategyPredictionItem] = [
            StrategyPredictionItem(
                strategy_name="Multi-Model AI Master Ensemble",
                predicted_spot=ensemble_spot,
                target_price=target_price,
                stop_loss=stop_loss,
                signal=action_recommendation,
                confidence_pct=confidence_pct,
                rationale=f"Confluence blend across OI, Greeks, XGBoost, LSTM, and Price Action with {confidence_pct}% confidence."
            ),
            StrategyPredictionItem(
                strategy_name="Momentum & Moving Average Crossover",
                predicted_spot=round(current_spot + (atr_val * (1.2 if tech_eval['trend_score'] >= 0 else -1.2)), 2),
                target_price=round(current_spot + (atr_val * (1.8 if tech_eval['trend_score'] >= 0 else -1.8)), 2),
                stop_loss=round(current_spot - (atr_val * (0.9 if tech_eval['trend_score'] >= 0 else -0.9)), 2),
                signal="BUY" if tech_eval["trend_score"] >= 15 else ("SELL" if tech_eval["trend_score"] <= -15 else "HOLD"),
                confidence_pct=round(min(94.0, 60.0 + abs(tech_eval["trend_score"]) * 0.35), 1),
                rationale=f"EMA 20/50 alignment ({tech_eval['ema_20']}/{tech_eval['ema_50']}) with Supertrend {tech_eval['supertrend']}."
            ),
            StrategyPredictionItem(
                strategy_name="Mean Reversion & Volatility Bands",
                predicted_spot=round(current_spot + (atr_val * (-1.0 if tech_eval['rsi'] > 65 else (1.0 if tech_eval['rsi'] < 35 else 0.2))), 2),
                target_price=round(current_spot + (atr_val * (-1.5 if tech_eval['rsi'] > 65 else (1.5 if tech_eval['rsi'] < 35 else 0.5))), 2),
                stop_loss=round(current_spot - (atr_val * (-0.8 if tech_eval['rsi'] > 65 else (0.8 if tech_eval['rsi'] < 35 else 0.4))), 2),
                signal="SELL" if tech_eval["rsi"] >= 68 else ("BUY" if tech_eval["rsi"] <= 35 else ("BUY" if current_spot >= tech_eval["vwap"] else "SELL")),
                confidence_pct=round(min(90.0, 55.0 + abs(tech_eval["rsi"] - 50.0) * 1.4), 1),
                rationale=f"RSI oscillator at {tech_eval['rsi']:.1f} with VWAP reference at ₹{tech_eval['vwap']}."
            ),
            StrategyPredictionItem(
                strategy_name="Option Chain & Max Pain Magnet",
                predicted_spot=m1_spot,
                target_price=round(max_pain + (atr_val * 0.6 if m1_spot >= current_spot else -0.6), 2),
                stop_loss=round(current_spot - (atr_val * 0.8 if m1_spot >= current_spot else -0.8), 2),
                signal="BUY" if m1_spot > current_spot else ("SELL" if m1_spot < current_spot else "HOLD"),
                confidence_pct=round(min(92.0, 65.0 + abs(oi_pcr - 1.0) * 20.0), 1),
                rationale=f"Put-Call Ratio of {oi_pcr:.2f} with institutional Max Pain magnet at ₹{max_pain:.0f}."
            ),
            StrategyPredictionItem(
                strategy_name="ML Gradient Boosting Regressor (XGB+LGB)",
                predicted_spot=m3_spot,
                target_price=round(m3_spot + (atr_val * (0.7 if m3_spot >= current_spot else -0.7)), 2),
                stop_loss=round(current_spot - (atr_val * (0.8 if m3_spot >= current_spot else -0.8)), 2),
                signal="BUY" if m3_spot >= current_spot else "SELL",
                confidence_pct=round(min(95.0, 72.0 + abs(m3_spot - current_spot) / (atr_val + 1e-4) * 8.0), 1),
                rationale=f"Multi-feature tree regression model evaluating IV skew, momentum, and volume profile."
            ),
            StrategyPredictionItem(
                strategy_name="LSTM Deep Recurrent Pathing",
                predicted_spot=m4_spot,
                target_price=round(m4_spot + (atr_val * (0.6 if m4_spot >= current_spot else -0.6)), 2),
                stop_loss=round(current_spot - (atr_val * (0.8 if m4_spot >= current_spot else -0.8)), 2),
                signal="BUY" if m4_spot >= current_spot else "SELL",
                confidence_pct=round(min(91.0, 70.0 + abs(m4_spot - current_spot) / (atr_val + 1e-4) * 7.0), 1),
                rationale=f"Recurrent sequence projection over {horizon_mins}m horizon modeling volatility clustering."
            ),
            StrategyPredictionItem(
                strategy_name="Breakout & Liquidity Zone Price Action",
                predicted_spot=m5_spot,
                target_price=round(m5_spot + (atr_val * (0.8 if m5_spot >= current_spot else -0.8)), 2),
                stop_loss=round(current_spot - (atr_val * (0.9 if m5_spot >= current_spot else -0.9)), 2),
                signal="BUY" if m5_spot >= current_spot else "SELL",
                confidence_pct=round(min(93.0, 68.0 + abs(m5_spot - current_spot) / (atr_val + 1e-4) * 7.5), 1),
                rationale=f"Support/Resistance retest against Key Support at ₹{supports[-1] if supports else round(current_spot * 0.985, 2)}."
            )
        ]

        return PredictionResponse(
            symbol=request.symbol.upper(),
            current_spot=current_spot,
            predicted_spot_price=ensemble_spot,
            expected_high=expected_high,
            expected_low=expected_low,
            confidence_pct=confidence_pct,
            prob_bullish=prob_bullish,
            prob_bearish=prob_bearish,
            prob_sideways=prob_sideways,
            expected_range_lower=round(ensemble_spot - expected_range_half, 2),
            expected_range_upper=round(ensemble_spot + expected_range_half, 2),
            target_price=target_price,
            stop_loss=stop_loss,
            expected_time_to_reach=expected_time_str,
            market_strength=market_strength,
            volatility_level=volatility_level,
            risk_level=risk_level,
            suggested_strategy=strategy,
            strategy_rationale=rationale,
            advanced_analysis=advanced_analytics,
            model_breakdown=model_breakdown,
            action_recommendation=action_recommendation,
            action_rationale=action_rationale,
            various_strategies=various_strategies
        )
