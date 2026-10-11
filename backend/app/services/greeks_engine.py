import math
from typing import Tuple, Dict
from scipy.stats import norm
from app.models.schemas import GreekMetrics, OptionType

class GreeksEngine:
    """
    Quantitative Black-Scholes engine for Options Pricing,
    Analytical Greeks calculation, and Newton-Raphson Implied Volatility.
    """

    DEFAULT_RISK_FREE_RATE = 0.07 # 7% RBI Repo rate proxy

    @staticmethod
    def _d1_d2(S: float, K: float, T: float, r: float, sigma: float) -> Tuple[float, float]:
        if T <= 0 or sigma <= 0 or S <= 0 or K <= 0:
            return 0.0, 0.0
        d1 = (math.log(S / K) + (r + 0.5 * sigma ** 2) * T) / (sigma * math.sqrt(T))
        d2 = d1 - sigma * math.sqrt(T)
        return d1, d2

    @classmethod
    def calculate_price(
        cls,
        S: float,
        K: float,
        T: float,
        r: float,
        sigma: float,
        option_type: OptionType
    ) -> float:
        """Black-Scholes Theoretical Option Price."""
        if T <= 0.0001:
            if option_type == OptionType.CALL:
                return max(0.0, S - K)
            else:
                return max(0.0, K - S)

        d1, d2 = cls._d1_d2(S, K, T, r, sigma)
        if option_type == OptionType.CALL:
            return S * norm.cdf(d1) - K * math.exp(-r * T) * norm.cdf(d2)
        else:
            return K * math.exp(-r * T) * norm.cdf(-d2) - S * norm.cdf(-d1)

    @classmethod
    def calculate_greeks(
        cls,
        S: float,
        K: float,
        T: float,
        sigma: float,
        r: float = DEFAULT_RISK_FREE_RATE,
        option_type: OptionType = OptionType.CALL
    ) -> GreekMetrics:
        """
        Calculate Analytical Greeks: Delta, Gamma, Theta, Vega, Rho.
        T is in years (days_to_expiry / 365.0).
        """
        if T <= 0.0001 or sigma <= 0.0001:
            intrinsic_delta = 1.0 if (option_type == OptionType.CALL and S > K) else (
                -1.0 if (option_type == OptionType.PUT and S < K) else 0.0
            )
            return GreekMetrics(delta=intrinsic_delta, gamma=0.0, theta=0.0, vega=0.0, rho=0.0)

        d1, d2 = cls._d1_d2(S, K, T, r, sigma)
        pdf_d1 = norm.pdf(d1)
        sqrt_t = math.sqrt(T)

        if option_type == OptionType.CALL:
            delta = norm.cdf(d1)
            # Theta per calendar day
            theta = (-(S * pdf_d1 * sigma) / (2 * sqrt_t) - r * K * math.exp(-r * T) * norm.cdf(d2)) / 365.0
            rho = (K * T * math.exp(-r * T) * norm.cdf(d2)) / 100.0
        else:
            delta = norm.cdf(d1) - 1.0
            # Put Theta per calendar day
            theta = (-(S * pdf_d1 * sigma) / (2 * sqrt_t) + r * K * math.exp(-r * T) * norm.cdf(-d2)) / 365.0
            rho = (-K * T * math.exp(-r * T) * norm.cdf(-d2)) / 100.0

        # Gamma and Vega are identical for Calls and Puts
        gamma = pdf_d1 / (S * sigma * sqrt_t) if (S * sigma * sqrt_t) > 0 else 0.0
        vega = (S * sqrt_t * pdf_d1) / 100.0 # Per 1% IV change

        return GreekMetrics(
            delta=round(delta, 4),
            gamma=round(gamma, 6),
            theta=round(theta, 2),
            vega=round(vega, 2),
            rho=round(rho, 4)
        )

    @classmethod
    def calculate_implied_volatility(
        cls,
        market_price: float,
        S: float,
        K: float,
        T: float,
        r: float = DEFAULT_RISK_FREE_RATE,
        option_type: OptionType = OptionType.CALL,
        precision: float = 1e-4,
        max_iterations: int = 100
    ) -> float:
        """
        Calculates IV using the Newton-Raphson algorithm with bisection fallback.
        Returns percentage (e.g. 15.5 for 15.5%).
        """
        if market_price <= 0.05 or T <= 0.0001:
            return 15.0

        sigma = 0.25 # Initial seed: 25% volatility
        for _ in range(max_iterations):
            price = cls.calculate_price(S, K, T, r, sigma, option_type)
            diff = price - market_price

            if abs(diff) < precision:
                return round(sigma * 100.0, 2)

            d1, _ = cls._d1_d2(S, K, T, r, sigma)
            vega = S * math.sqrt(T) * norm.pdf(d1)

            if vega < 1e-6:
                break

            sigma -= diff / vega

            if sigma <= 0.01:
                sigma = 0.01
            elif sigma > 3.0: # Cap at 300% IV
                sigma = 3.0

        # Fallback bisection
        low_sig, high_sig = 0.01, 3.0
        for _ in range(30):
            mid_sig = (low_sig + high_sig) / 2.0
            mid_price = cls.calculate_price(S, K, T, r, mid_sig, option_type)
            if abs(mid_price - market_price) < precision:
                return round(mid_sig * 100.0, 2)
            if mid_price < market_price:
                low_sig = mid_sig
            else:
                high_sig = mid_sig

        return round(mid_sig * 100.0, 2)

    @classmethod
    def estimate_spot_from_greeks(
        cls,
        current_spot: float,
        net_delta_exposure: float,
        net_gamma_exposure: float,
        horizon_minutes: int,
        avg_iv: float
    ) -> float:
        """
        Derives anticipated spot drift based on option market maker hedging (vanna/charm/gamma flip).
        Positive GEX dampens moves toward strike clusters; Negative GEX accelerates moves.
        """
        t_factor = math.sqrt(horizon_minutes / (24.0 * 60.0))
        iv_daily = (avg_iv / 100.0) / math.sqrt(252.0)
        expected_move_points = current_spot * iv_daily * t_factor

        # Normalized delta imbalance (-1.0 to 1.0)
        norm_delta = max(-1.0, min(1.0, net_delta_exposure / 5000000.0)) if net_delta_exposure != 0 else 0.0

        # Gamma dampening factor
        gamma_damp = 0.7 if net_gamma_exposure > 0 else 1.3

        spot_drift = norm_delta * expected_move_points * 0.45 * gamma_damp
        return round(current_spot + spot_drift, 2)
