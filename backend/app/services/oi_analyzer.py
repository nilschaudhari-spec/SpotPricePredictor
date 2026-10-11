from typing import List, Dict, Tuple, Any
from app.models.schemas import StrikeRow

class OIAnalyzer:
    """
    Open Interest Analytics engine for Strike-wise Dynamics, Max Pain,
    Put-Call Ratio (PCR), and Dealer Gamma/Delta Positioning.
    """

    @staticmethod
    def calculate_pcr(strikes: List[StrikeRow]) -> Tuple[float, float]:
        """Returns (OI_PCR, Volume_PCR)."""
        total_put_oi = sum(s.put.oi for s in strikes)
        total_call_oi = sum(s.call.oi for s in strikes)

        total_put_vol = sum(s.put.volume for s in strikes)
        total_call_vol = sum(s.call.volume for s in strikes)

        oi_pcr = round(total_put_oi / total_call_oi, 3) if total_call_oi > 0 else 1.0
        vol_pcr = round(total_put_vol / total_call_vol, 3) if total_call_vol > 0 else 1.0
        return oi_pcr, vol_pcr

    @staticmethod
    def calculate_max_pain(strikes: List[StrikeRow]) -> float:
        """
        Calculates the strike price at which option buyers lose the most money
        (i.e. where option writers payout is minimized).
        """
        if not strikes:
            return 0.0

        strike_prices = [s.strike_price for s in strikes]
        min_loss = float("inf")
        max_pain_strike = strike_prices[0]

        for test_strike in strike_prices:
            total_loss = 0.0
            for row in strikes:
                # Call payout if settlement at test_strike
                if test_strike > row.strike_price:
                    total_loss += (test_strike - row.strike_price) * row.call.oi
                # Put payout if settlement at test_strike
                if test_strike < row.strike_price:
                    total_loss += (row.strike_price - test_strike) * row.put.oi

            if total_loss < min_loss:
                min_loss = total_loss
                max_pain_strike = test_strike

        return max_pain_strike

    @staticmethod
    def identify_support_resistance(strikes: List[StrikeRow], current_spot: float) -> Tuple[List[float], List[float]]:
        """
        Key Supports: Highest Put OI strikes at or below current spot.
        Key Resistances: Highest Call OI strikes at or above current spot.
        """
        # Supports (Put OI)
        put_strikes = [s for s in strikes if s.strike_price <= current_spot * 1.02]
        put_strikes_sorted = sorted(put_strikes, key=lambda x: x.put.oi, reverse=True)
        supports = [s.strike_price for s in put_strikes_sorted[:3]]

        # Resistances (Call OI)
        call_strikes = [s for s in strikes if s.strike_price >= current_spot * 0.98]
        call_strikes_sorted = sorted(call_strikes, key=lambda x: x.call.oi, reverse=True)
        resistances = [s.strike_price for s in call_strikes_sorted[:3]]

        supports.sort()
        resistances.sort()

        if not supports:
            supports = [round(current_spot * 0.99, 2), round(current_spot * 0.98, 2)]
        if not resistances:
            resistances = [round(current_spot * 1.01, 2), round(current_spot * 1.02, 2)]

        return supports, resistances

    @staticmethod
    def calculate_exposures(strikes: List[StrikeRow], spot_price: float) -> Tuple[float, float, str]:
        """
        Calculates Net Delta Exposure (DEX) and Net Gamma Exposure (GEX).
        Estimates dealer inventory position: Long Gamma (Pinned) vs Short Gamma (Volatility).
        """
        net_dex = 0.0
        net_gex = 0.0

        for row in strikes:
            # Dealer long calls when retail buys, or short when market makers hedge
            # Standard GEX formula: Sum(Call Gamma * Call OI - Put Gamma * Put OI) * Spot * 100
            call_gex = row.call.greeks.gamma * row.call.oi * spot_price * 100
            put_gex = row.put.greeks.gamma * row.put.oi * spot_price * 100
            net_gex += (call_gex - put_gex)

            call_dex = row.call.greeks.delta * row.call.oi
            put_dex = row.put.greeks.delta * row.put.oi
            net_dex += (call_dex + put_dex)

        dealer_position = "Long Gamma (Range-Bound / Mean Reversion)" if net_gex >= 0 else "Short Gamma (Breakout / High Volatility)"
        return round(net_dex, 2), round(net_gex, 2), dealer_position

    @classmethod
    def estimate_spot_from_oi(
        cls,
        current_spot: float,
        strikes: List[StrikeRow],
        horizon_minutes: int
    ) -> float:
        """
        Calculates anticipated spot target under OI gravitational pull:
        - Max Pain gravitational pull
        - Put-Call OI imbalance shift
        - Strongest wall proximity (Support vs Resistance)
        """
        if not strikes:
            return current_spot

        max_pain = cls.calculate_max_pain(strikes)
        oi_pcr, _ = cls.calculate_pcr(strikes)
        supports, resistances = cls.identify_support_resistance(strikes, current_spot)

        # OI skew direction: PCR > 1.2 is Bullish (Heavy put writing), PCR < 0.8 is Bearish (Heavy call writing)
        pcr_bias = (oi_pcr - 1.0) # e.g. 1.25 -> +0.25 (bullish); 0.75 -> -0.25 (bearish)

        # Pull towards Max pain (decay factor depends on horizon)
        max_pain_pull = (max_pain - current_spot) * 0.15

        # Directional push from PCR and OI concentration
        immediate_sup = supports[-1] if supports else current_spot * 0.99
        immediate_res = resistances[0] if resistances else current_spot * 1.01

        range_width = immediate_res - immediate_sup
        pcr_push = (pcr_bias * range_width) * 0.25

        predicted_spot = current_spot + max_pain_pull + pcr_push
        return round(predicted_spot, 2)
