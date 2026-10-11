from typing import Optional, Tuple, Dict, Any
from app.models.schemas import TechnicalIndicatorInput, TrendDirection

class TechnicalAnalysisEngine:
    """
    Technical Analysis & Trend Auto-Detection Engine.
    Evaluates EMA 20/50 crossovers, Supertrend, VWAP positioning,
    ADX trend strength, RSI momentum, and MACD divergence.
    """

    @classmethod
    def evaluate_technicals(
        cls,
        spot_price: float,
        technicals: Optional[TechnicalIndicatorInput] = None
    ) -> Dict[str, Any]:
        """
        Parses provided technical values or defaults to calibrated realistic values,
        producing a normalized Trend Score (-100 to +100) and Momentum Score (-100 to +100).
        """
        tech = technicals or TechnicalIndicatorInput()

        # Defaults if not explicitly supplied
        ema_20 = tech.ema_20 or spot_price * 0.998
        ema_50 = tech.ema_50 or spot_price * 0.995
        vwap = tech.vwap or spot_price * 0.999
        rsi = tech.rsi if tech.rsi is not None else 54.0
        adx = tech.adx if tech.adx is not None else 26.5
        atr = tech.atr or (spot_price * 0.0075) # ~0.75% intraday range
        supertrend = tech.supertrend or ("BULLISH" if spot_price > ema_20 else "BEARISH")
        macd_signal = tech.macd_signal or ("BULLISH_CROSS" if ema_20 > ema_50 else "BEARISH_CROSS")

        # Trend Score calculation (-100 to +100)
        score = 0.0

        # EMA alignment
        if spot_price > ema_20 > ema_50:
            score += 30.0
        elif spot_price < ema_20 < ema_50:
            score -= 30.0
        elif spot_price > ema_20:
            score += 15.0
        else:
            score -= 15.0

        # VWAP positioning
        vwap_position = "Above VWAP (Bullish)" if spot_price >= vwap else "Below VWAP (Bearish)"
        score += 20.0 if spot_price >= vwap else -20.0

        # Supertrend
        if supertrend.upper() == "BULLISH":
            score += 25.0
        else:
            score -= 25.0

        # MACD
        if macd_signal.upper() in ["BULLISH_CROSS", "BULLISH"]:
            score += 15.0
        else:
            score -= 15.0

        # ADX trend amplifier (ADX > 25 indicates strong trend)
        adx_factor = min(2.0, max(0.5, adx / 25.0))
        trend_score = max(-100.0, min(100.0, score * (adx_factor if abs(score) > 20 else 1.0)))

        # Momentum Score (-100 to +100) from RSI & price velocity
        # RSI 50 is neutral, > 60 bullish momentum, < 40 bearish momentum
        rsi_diff = rsi - 50.0
        momentum_score = max(-100.0, min(100.0, rsi_diff * 3.33))

        # Overall market sentiment (0 to 100)
        sentiment_score = round(50.0 + (trend_score * 0.3) + (momentum_score * 0.2), 1)
        sentiment_score = max(5.0, min(95.0, sentiment_score))

        # Auto-detect direction label
        if trend_score >= 50.0:
            detected_direction = TrendDirection.STRONG_BULLISH
        elif trend_score >= 15.0:
            detected_direction = TrendDirection.BULLISH
        elif trend_score <= -50.0:
            detected_direction = TrendDirection.STRONG_BEARISH
        elif trend_score <= -15.0:
            detected_direction = TrendDirection.BEARISH
        else:
            detected_direction = TrendDirection.SIDEWAYS

        return {
            "ema_20": ema_20,
            "ema_50": ema_50,
            "vwap": vwap,
            "vwap_position": vwap_position,
            "rsi": rsi,
            "adx": adx,
            "atr": round(atr, 2),
            "supertrend": supertrend,
            "trend_score": round(trend_score, 1),
            "momentum_score": round(momentum_score, 1),
            "market_sentiment_score": sentiment_score,
            "detected_direction": detected_direction
        }

    @classmethod
    def estimate_spot_from_price_action(
        cls,
        current_spot: float,
        tech_eval: Dict[str, Any],
        horizon_minutes: int
    ) -> float:
        """
        Projects spot price based on ATR expansion and trend/momentum vector.
        """
        atr = tech_eval["atr"]
        trend_score = tech_eval["trend_score"]
        momentum_score = tech_eval["momentum_score"]

        # Combined directional bias (-1.0 to +1.0)
        directional_bias = (trend_score * 0.6 + momentum_score * 0.4) / 100.0

        # Expected ATR movement for the duration
        time_scaling = min(1.5, (horizon_minutes / 60.0) ** 0.5)
        projected_move = directional_bias * (atr * 0.8) * time_scaling

        return round(current_spot + projected_move, 2)
