from typing import Dict, Any, List
from datetime import datetime, timedelta
import numpy as np

from app.models.schemas import (
    PredictionRequest, PredictionResponse, AdvancedAnalytics,
    ModelForecastBreakdown, PredictionHorizon, TrendDirection,
    TechnicalIndicatorInput, OptionChainResponse
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
            technicals=request.technicals
        )

        # Handle user manual trend override if specified
        if request.trend_mode != TrendDirection.AUTO:
            trend_overrides = {
                TrendDirection.STRONG_BULLISH: 75.0,
                TrendDirection.BULLISH: 40.0,
                TrendDirection.SIDEWAYS: 0.0,
                TrendDirection.BEARISH: -40.0,
                TrendDirection.STRONG_BEARISH: -75.0,
            }
            forced_score = trend_overrides.get(request.trend_mode, 0.0)
            tech_eval["trend_score"] = forced_score
            tech_eval["detected_direction"] = request.trend_mode

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
        p_bull = 1.0 / (1.0 + np.exp(- (normalized_drift * 1.5 + (tech_eval["trend_score"] / 75.0))))
        p_bear = 1.0 / (1.0 + np.exp((normalized_drift * 1.5 + (tech_eval["trend_score"] / 75.0))))
        p_side = max(0.05, 1.0 - abs(p_bull - p_bear))

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

        # Target and Stop Loss
        if ensemble_spot >= current_spot:
            target_price = expected_high
            stop_loss = round(current_spot - (expected_range_half * 0.7), 2)
        else:
            target_price = expected_low
            stop_loss = round(current_spot + (expected_range_half * 0.7), 2)

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
            model_breakdown=model_breakdown
        )
