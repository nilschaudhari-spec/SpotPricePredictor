import math
from typing import List, Dict, Any

class LSTMPredictor:
    """
    Model 4: LSTM Time Series Predictor.
    Simulates recurrent memory cell transitions across multi-step intervals
    to forecast non-linear volatility expansion and momentum decay.
    """

    @classmethod
    def predict_spot(
        cls,
        current_spot: float,
        trend_score: float,
        momentum_score: float,
        volatility_atr: float,
        horizon_minutes: int
    ) -> float:
        """
        Recurrent state simulation over the specified horizon.
        Uses hidden states to model momentum inertia, mean-reversion pull, and volatility clustering.
        """
        steps = max(1, horizon_minutes // 5)
        dt = horizon_minutes / steps

        # Initial hidden state (h) and cell state (c)
        h = (trend_score / 100.0) * 0.5 + (momentum_score / 100.0) * 0.5
        c = 0.0

        current_price = current_spot
        step_atr = (volatility_atr / math.sqrt(24.0 * 60.0 / 5.0))

        for step in range(steps):
            # Input gate
            i_gate = 1.0 / (1.0 + math.exp(- (0.8 * h + 0.2 * (trend_score / 100.0))))
            # Forget gate (gradual decay of intraday shock)
            f_gate = 1.0 / (1.0 + math.exp(- (0.9 * h + 0.1)))
            # Candidate cell state
            c_tilde = math.tanh(0.7 * h + 0.3 * (momentum_score / 100.0))
            # New cell state
            c = f_gate * c + i_gate * c_tilde
            # Output gate
            o_gate = 1.0 / (1.0 + math.exp(- (0.85 * h + 0.15 * c)))
            # New hidden state
            h = o_gate * math.tanh(c)

            # Price delta per step
            price_delta = h * step_atr * 0.4
            current_price += price_delta

        return round(current_price, 2)
