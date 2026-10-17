from typing import Tuple
from app.models.schemas import StrategyType

class StrategyRecommender:
    """
    Quantitative Strategy Recommendation Engine.
    Maps probabilistic directional bias (Bullish, Bearish, Sideways), Volatility Regime (IV),
    and Dealer Gamma Positioning into high-probability option trading structures.
    """

    @classmethod
    def recommend(
        cls,
        prob_bullish: float,
        prob_bearish: float,
        prob_sideways: float,
        avg_iv: float,
        confidence_pct: float,
        dealer_positioning: str
    ) -> Tuple[StrategyType, str]:
        """
        Determines strategy and produces clear rationale with balanced Call, Put, and Range structures.
        """
        # Low confidence or choppy noise -> NO TRADE / WAIT
        if confidence_pct < 45.0:
            return (
                StrategyType.WAIT,
                "Confidence score is below threshold (45%). Market signals are in conflict. Wait for confirmation."
            )

        # Volatility regime
        is_high_iv = avg_iv >= 20.0
        is_low_iv = avg_iv < 13.0

        # 1. Bearish Scenarios (Triggers BUY PUT or PUT SPREAD)
        if prob_bearish > prob_bullish and (prob_bearish >= 0.45 or prob_bearish > prob_sideways):
            if is_high_iv:
                return (
                    StrategyType.PUT_SPREAD,
                    f"Strong Bearish bias ({prob_bearish*100:.0f}%) with elevated IV ({avg_iv:.1f}%). Bear Put Spread hedges against high option premium collapse while capturing downside."
                )
            else:
                return (
                    StrategyType.BUY_PUT,
                    f"Bearish breakdown detected ({prob_bearish*100:.0f}%) with favorable IV ({avg_iv:.1f}%). Long Put strategy optimizes delta acceleration on downward momentum."
                )

        # 2. Bullish Scenarios (Triggers BUY CALL or CALL SPREAD)
        elif prob_bullish > prob_bearish and (prob_bullish >= 0.45 or prob_bullish > prob_sideways):
            if is_high_iv:
                return (
                    StrategyType.CALL_SPREAD,
                    f"Strong Bullish bias ({prob_bullish*100:.0f}%) with elevated IV ({avg_iv:.1f}%). Bull Call Spread mitigates vega decay risk while capturing directional upside."
                )
            else:
                return (
                    StrategyType.BUY_CALL,
                    f"Bullish trend confirmed ({prob_bullish*100:.0f}%) with affordable IV ({avg_iv:.1f}%). Outright long call provides asymmetric upside."
                )

        # 3. Sideways / Range-bound Scenarios (Triggers IRON CONDOR or STRADDLE)
        elif prob_sideways >= 0.38 or abs(prob_bullish - prob_bearish) < 0.10:
            if is_high_iv or "Long Gamma" in dealer_positioning:
                return (
                    StrategyType.IRON_CONDOR,
                    f"Range-bound market dynamics ({prob_sideways*100:.0f}%) and dealer pinning. Iron Condor profits from theta decay within defined support/resistance boundaries."
                )
            elif is_low_iv and "Short Gamma" in dealer_positioning:
                return (
                    StrategyType.STRADDLE,
                    f"Compressed IV ({avg_iv:.1f}%) near major inflection zone. Long Straddle captures upcoming volatility expansion breakout."
                )
            else:
                return (
                    StrategyType.WAIT,
                    f"Sideways consolidation ({prob_sideways*100:.0f}%). Balanced order flow with no clear directional edge. Wait for range breakout."
                )

        return (
            StrategyType.NO_TRADE,
            "Indecisive order flow. Risk-reward ratio does not justify capital deployment."
        )
