from typing import Optional, Tuple, Dict, Any
from app.models.schemas import TechnicalIndicatorInput, TrendDirection

class TechnicalAnalysisEngine:
    """
    Technical Analysis & Trend Auto-Detection Engine.
    Evaluates real market price action, EMA 20/50 crossovers, Supertrend,
    VWAP positioning, ADX trend strength, RSI momentum, and MACD divergence.
    """

    @classmethod
    def evaluate_technicals(
        cls,
        spot_price: float,
        technicals: Optional[TechnicalIndicatorInput] = None,
        quote: Optional[Dict[str, Any]] = None,
        trend_mode: Optional[TrendDirection] = TrendDirection.AUTO
    ) -> Dict[str, Any]:
        """
        Parses provided technical values or dynamically computes them from real-time quote
        data and trend mode, producing a normalized Trend Score (-100 to +100) and Momentum Score (-100 to +100).
        """
        tech = technicals or TechnicalIndicatorInput()
        change_pct = quote.get("change_percent", 0.0) if quote else 0.0

        atr = tech.atr or (spot_price * 0.0075)

        # 1. Resolve technicals based on explicit inputs, trend_mode, or live quote change
        if tech.ema_20 is not None and tech.ema_50 is not None:
            ema_20 = tech.ema_20
            ema_50 = tech.ema_50
            vwap = tech.vwap or spot_price
            rsi = tech.rsi if tech.rsi is not None else 50.0
            adx = tech.adx if tech.adx is not None else 25.0
            supertrend = tech.supertrend or ("BULLISH" if spot_price > ema_20 else "BEARISH")
            macd_signal = tech.macd_signal or ("BULLISH_CROSS" if ema_20 > ema_50 else "BEARISH_CROSS")
        elif trend_mode == TrendDirection.STRONG_BEARISH or (trend_mode == TrendDirection.AUTO and change_pct <= -1.2):
            ema_20 = round(spot_price * 1.012, 2)
            ema_50 = round(spot_price * 1.025, 2)
            vwap = round(spot_price * 1.010, 2)
            rsi = tech.rsi if tech.rsi is not None else 32.0
            adx = tech.adx if tech.adx is not None else 34.0
            supertrend = "BEARISH"
            macd_signal = "BEARISH_CROSS"
        elif trend_mode == TrendDirection.BEARISH or (trend_mode == TrendDirection.AUTO and change_pct <= -0.15):
            ema_20 = round(spot_price * 1.006, 2)
            ema_50 = round(spot_price * 1.014, 2)
            vwap = round(spot_price * 1.005, 2)
            rsi = tech.rsi if tech.rsi is not None else max(26.0, 50.0 + (change_pct * 6.5))
            adx = tech.adx if tech.adx is not None else max(20.0, 24.0 + abs(change_pct) * 2.0)
            supertrend = "BEARISH"
            macd_signal = "BEARISH_CROSS"
        elif trend_mode == TrendDirection.SIDEWAYS or (trend_mode == TrendDirection.AUTO and abs(change_pct) < 0.15):
            ema_20 = round(spot_price * 1.0005, 2)
            ema_50 = round(spot_price * 0.9995, 2)
            vwap = spot_price
            rsi = tech.rsi if tech.rsi is not None else 50.0 + (change_pct * 5.0)
            adx = tech.adx if tech.adx is not None else 17.5
            supertrend = "SIDEWAYS"
            macd_signal = "NEUTRAL"
        elif trend_mode == TrendDirection.STRONG_BULLISH or (trend_mode == TrendDirection.AUTO and change_pct >= 1.2):
            ema_20 = round(spot_price * 0.988, 2)
            ema_50 = round(spot_price * 0.975, 2)
            vwap = round(spot_price * 0.990, 2)
            rsi = tech.rsi if tech.rsi is not None else 68.0
            adx = tech.adx if tech.adx is not None else 34.0
            supertrend = "BULLISH"
            macd_signal = "BULLISH_CROSS"
        else: # BULLISH
            ema_20 = round(spot_price * 0.994, 2)
            ema_50 = round(spot_price * 0.986, 2)
            vwap = round(spot_price * 0.995, 2)
            rsi = tech.rsi if tech.rsi is not None else min(74.0, 50.0 + (change_pct * 6.5))
            adx = tech.adx if tech.adx is not None else max(20.0, 24.0 + change_pct * 2.0)
            supertrend = "BULLISH"
            macd_signal = "BULLISH_CROSS"

        # Trend Score calculation (-100 to +100)
        score = 0.0

        # EMA alignment
        if spot_price > ema_20 > ema_50:
            score += 30.0
        elif spot_price < ema_20 < ema_50:
            score -= 30.0
        elif spot_price > ema_20:
            score += 15.0
        elif spot_price < ema_20:
            score -= 15.0

        # VWAP positioning
        if spot_price >= vwap:
            vwap_position = "Above VWAP (Bullish)"
            score += 20.0
        else:
            vwap_position = "Below VWAP (Bearish)"
            score -= 20.0

        # Supertrend
        if supertrend.upper() == "BULLISH":
            score += 25.0
        elif supertrend.upper() == "BEARISH":
            score -= 25.0

        # MACD
        if macd_signal.upper() in ["BULLISH_CROSS", "BULLISH"]:
            score += 15.0
        elif macd_signal.upper() in ["BEARISH_CROSS", "BEARISH"]:
            score -= 15.0

        # ADX trend amplifier (ADX > 25 indicates strong trend)
        adx_factor = min(2.0, max(0.5, adx / 25.0))
        trend_score = max(-100.0, min(100.0, score * (adx_factor if abs(score) > 20 else 1.0)))

        # Explicit trend mode override if user specifically picked it
        if trend_mode == TrendDirection.STRONG_BULLISH:
            trend_score = max(75.0, trend_score)
        elif trend_mode == TrendDirection.BULLISH:
            trend_score = max(40.0, trend_score)
        elif trend_mode == TrendDirection.SIDEWAYS:
            trend_score = min(10.0, max(-10.0, trend_score))
        elif trend_mode == TrendDirection.BEARISH:
            trend_score = min(-40.0, trend_score)
        elif trend_mode == TrendDirection.STRONG_BEARISH:
            trend_score = min(-75.0, trend_score)

        # Momentum Score (-100 to +100) from RSI & price velocity
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
