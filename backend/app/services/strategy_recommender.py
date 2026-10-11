from typing import Tuple
from app.models.schemas import StrategyType

class StrategyRecommender:
    """
    Quantitative Strategy Recommendation Engine.
    Maps probabilistic directional bias, Volatility Regime (IV),
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
        Determines strategy and produces clear rationale.
        """
        # Low confidence or choppy noise -> NO TRADE / WAIT
        if confidence_pct < 55.0:
            return (
                StrategyType.WAIT,
                "Confidence score is below threshold (55%). Market signals are in conflict. Wait for confirmation."
            )

        # Extreme IV or event risk check
        is_high_iv = avg_iv >= 20.0
        is_low_iv = avg_iv < 13.0

        # Bullish scenarios
        if prob_bullish >= 0.55:
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

        # Bearish scenarios
        elif prob_bearish >= 0.55:
            if is_high_iv:
                return (
                    StrategyType.PUT_SPREAD,
                    f"Strong Bearish bias ({prob_bearish*100:.0f}%) with elevated IV ({avg_iv:.1f}%). Bear Put Spread hedges against high option premium collapse."
                )
            else:
                return (
                    StrategyType.BUY_PUT,
                    f"Bearish breakdown detected ({prob_bearish*100:.0f}%) with low-to-moderate IV ({avg_iv:.1f}%). Long put strategy optimizes delta velocity."
                )

        # Sideways / Range-bound scenarios
        elif prob_sideways >= 0.45:
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
                    "Sideways consolidation with neutral volatility. Better risk/reward exists by waiting for breakout."
                )

        return (
            StrategyType.NO_TRADE,
            "Indecisive order flow. Risk-reward ratio does not justify capital deployment."
        )
