import pytest
from app.models.schemas import (
    PredictionRequest, PredictionHorizon, TrendDirection, OptionType, Exchange, AssetType
)
from app.services.greeks_engine import GreeksEngine
from app.services.oi_analyzer import OIAnalyzer
from app.services.technical_analysis import TechnicalAnalysisEngine
from app.services.prediction_orchestrator import PredictionOrchestrator
from app.services.market_data_fetcher import MarketDataFetcher

def test_black_scholes_and_greeks():
    S = 24850.0
    K = 24850.0
    T = 7.0 / 365.0
    sigma = 0.15
    r = 0.07

    # At-the-money Call price should be positive and reasonable
    call_price = GreeksEngine.calculate_price(S, K, T, r, sigma, OptionType.CALL)
    put_price = GreeksEngine.calculate_price(S, K, T, r, sigma, OptionType.PUT)
    assert call_price > 50.0
    assert put_price > 50.0

    call_greeks = GreeksEngine.calculate_greeks(S, K, T, sigma, r, OptionType.CALL)
    put_greeks = GreeksEngine.calculate_greeks(S, K, T, sigma, r, OptionType.PUT)

    # ATM Call Delta ~ 0.5, ATM Put Delta ~ -0.5
    assert 0.45 <= call_greeks.delta <= 0.60
    assert -0.55 <= put_greeks.delta <= -0.40
    # Gamma should be positive and identical
    assert call_greeks.gamma > 0
    assert abs(call_greeks.gamma - put_greeks.gamma) < 1e-5
    # Theta should be negative (time decay)
    assert call_greeks.theta < 0
    assert put_greeks.theta < 0

def test_iv_calculation():
    S = 24850.0
    K = 24850.0
    T = 7.0 / 365.0
    expected_sigma_pct = 16.5
    market_price = GreeksEngine.calculate_price(S, K, T, 0.07, expected_sigma_pct / 100.0, OptionType.CALL)

    calculated_iv = GreeksEngine.calculate_implied_volatility(
        market_price=market_price, S=S, K=K, T=T, r=0.07, option_type=OptionType.CALL
    )
    assert abs(calculated_iv - expected_sigma_pct) <= 0.5

def test_option_chain_and_oi_analyzer():
    chain = MarketDataFetcher.get_spot_and_chain("NIFTY", days_to_expiry=4.0, num_strikes=15)
    strikes = chain["strikes"]
    spot = chain["spot_price"]
    assert len(strikes) == 15

    pcr, vol_pcr = OIAnalyzer.calculate_pcr(strikes)
    assert pcr > 0
    assert vol_pcr > 0

    max_pain = OIAnalyzer.calculate_max_pain(strikes)
    assert abs(max_pain - spot) < 2000.0

    supports, resistances = OIAnalyzer.identify_support_resistance(strikes, spot)
    assert len(supports) > 0
    assert len(resistances) > 0
    assert max(supports) <= spot * 1.02
    assert min(resistances) >= spot * 0.98

def test_prediction_orchestrator_end_to_end():
    req = PredictionRequest(
        symbol="NIFTY",
        exchange=Exchange.NSE,
        asset_type=AssetType.INDEX,
        current_spot_price=24850.0,
        prediction_horizon=PredictionHorizon.MIN_15,
        trend_mode=TrendDirection.AUTO
    )

    resp = PredictionOrchestrator.run_prediction(req)

    assert resp.symbol == "NIFTY"
    assert resp.current_spot == 24850.0
    assert resp.predicted_spot_price > 0
    assert resp.expected_high >= resp.expected_low
    assert 40.0 <= resp.confidence_pct <= 99.0
    assert abs((resp.prob_bullish + resp.prob_bearish + resp.prob_sideways) - 1.0) < 0.02
    assert resp.suggested_strategy is not None
    assert len(resp.strategy_rationale) > 10
    assert resp.advanced_analysis.max_pain > 0
    assert resp.model_breakdown.oi_analysis_spot > 0
    assert resp.model_breakdown.greeks_spot > 0
    assert resp.model_breakdown.ml_ensemble_spot > 0
    assert resp.model_breakdown.lstm_spot > 0
    assert resp.model_breakdown.price_action_spot > 0
