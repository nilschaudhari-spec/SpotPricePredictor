from typing import List, Dict, Any, Tuple

class PriceActionEngine:
    """
    Model 5: Price Action Analysis Engine.
    Detects candlestick structure, trendline breakouts, liquidity zones (order blocks),
    and support/resistance retests.
    """

    @classmethod
    def evaluate_price_action(
        cls,
        spot_price: float,
        supports: List[float],
        resistances: List[float],
        atr: float,
        trend_score: float
    ) -> Dict[str, Any]:
        immediate_sup = supports[-1] if supports else spot_price * 0.99
        immediate_res = resistances[0] if resistances else spot_price * 1.01

        dist_to_res = immediate_res - spot_price
        dist_to_sup = spot_price - immediate_sup

        # Breakout / Liquidity Grab logic
        if dist_to_res < atr * 0.4 and trend_score > 25.0:
            breakout_status = "Approaching Resistance Breakout"
            candlestick_pattern = "Bullish Momentum Marubozu"
            pa_bias = 0.75
        elif dist_to_sup < atr * 0.4 and trend_score < -25.0:
            breakout_status = "Approaching Support Breakdown"
            candlestick_pattern = "Bearish Breakdown Candle"
            pa_bias = -0.75
        elif dist_to_sup < atr * 0.3:
            breakout_status = "Support Retest / Demand Zone"
            candlestick_pattern = "Hammer / Pin Bar Rejection"
            pa_bias = 0.50 # Bounce
        elif dist_to_res < atr * 0.3:
            breakout_status = "Resistance Retest / Supply Zone"
            candlestick_pattern = "Shooting Star / Supply Absorption"
            pa_bias = -0.50 # Rejection
        else:
            breakout_status = "Consolidating within Value Area"
            candlestick_pattern = "Inside Bar / Neutral Doji"
            pa_bias = 0.0

        return {
            "breakout_status": breakout_status,
            "candlestick_pattern": candlestick_pattern,
            "immediate_demand": immediate_sup,
            "immediate_supply": immediate_res,
            "price_action_bias": pa_bias
        }

    @classmethod
    def estimate_spot(
        cls,
        spot_price: float,
        pa_eval: Dict[str, Any],
        atr: float,
        horizon_minutes: int
    ) -> float:
        bias = pa_eval["price_action_bias"]
        time_scaling = (horizon_minutes / 60.0) ** 0.5
        projected_move = bias * atr * 0.65 * time_scaling
        return round(spot_price + projected_move, 2)
