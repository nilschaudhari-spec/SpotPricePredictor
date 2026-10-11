from typing import List, Optional, Dict, Any
from enum import Enum
from pydantic import BaseModel, Field
from datetime import datetime, timezone

class Exchange(str, Enum):
    NSE = "NSE"
    BSE = "BSE"

class AssetType(str, Enum):
    INDEX = "INDEX"
    STOCK = "STOCK"
    EQUITY = "EQUITY"


class OptionType(str, Enum):
    CALL = "CE"
    PUT = "PE"

class TrendDirection(str, Enum):
    STRONG_BULLISH = "STRONG_BULLISH"
    BULLISH = "BULLISH"
    SIDEWAYS = "SIDEWAYS"
    BEARISH = "BEARISH"
    STRONG_BEARISH = "STRONG_BEARISH"
    AUTO = "AUTO"

class PredictionHorizon(str, Enum):
    MIN_5 = "MIN_5"
    MIN_10 = "MIN_10"
    MIN_15 = "MIN_15"
    MIN_30 = "MIN_30"
    HOUR_1 = "HOUR_1"
    CUSTOM = "CUSTOM"
    MIN_5_ALT = "5_MIN"
    MIN_10_ALT = "10_MIN"
    MIN_15_ALT = "15_MIN"
    MIN_30_ALT = "30_MIN"
    HOUR_1_ALT = "1_HOUR"

class StrategyType(str, Enum):
    BUY_CALL = "BUY CALL"
    BUY_PUT = "BUY PUT"
    CALL_SPREAD = "CALL SPREAD"
    PUT_SPREAD = "PUT SPREAD"
    IRON_CONDOR = "IRON CONDOR"
    STRADDLE = "STRADDLE"
    WAIT = "WAIT"
    NO_TRADE = "NO TRADE"

class GreekMetrics(BaseModel):
    delta: float = 0.0
    gamma: float = 0.0
    theta: float = 0.0
    vega: float = 0.0
    rho: float = 0.0

class OptionLegData(BaseModel):
    ltp: float = Field(0.0, description="Last Traded Price")
    volume: int = Field(0, description="Trading Volume")
    oi: int = Field(0, description="Open Interest")
    change_in_oi: int = Field(0, description="Change in Open Interest")
    iv: float = Field(0.0, description="Implied Volatility percentage")
    greeks: GreekMetrics = Field(default_factory=GreekMetrics)
    buildup: str = Field("Neutral", description="Long Buildup, Short Covering, etc.")

class StrikeRow(BaseModel):
    strike_price: float
    call: OptionLegData
    put: OptionLegData

class TechnicalIndicatorInput(BaseModel):
    ema_20: Optional[float] = None
    ema_50: Optional[float] = None
    supertrend: Optional[str] = None # "BULLISH" or "BEARISH"
    vwap: Optional[float] = None
    adx: Optional[float] = None
    rsi: Optional[float] = None
    macd_signal: Optional[str] = None # "BULLISH_CROSS" or "BEARISH_CROSS"
    atr: Optional[float] = None

class PredictionRequest(BaseModel):
    symbol: str = Field("NIFTY", description="Symbol ticker, e.g. NIFTY, BANKNIFTY, RELIANCE")
    exchange: Exchange = Field(Exchange.NSE)
    asset_type: AssetType = Field(AssetType.INDEX)
    current_spot_price: float = Field(..., gt=0)
    current_time: Optional[datetime] = None
    prediction_horizon: PredictionHorizon = Field(PredictionHorizon.MIN_15)
    custom_minutes: Optional[int] = Field(None, ge=1, le=1440)
    expiry_date: Optional[str] = None
    target_strike: Optional[float] = None
    target_option_type: Optional[OptionType] = None
    trend_mode: TrendDirection = Field(TrendDirection.AUTO)
    technicals: Optional[TechnicalIndicatorInput] = None
    option_chain: Optional[List[StrikeRow]] = None
    fetch_live_data: bool = Field(True, description="Fetch live chain from broker/NSE if option_chain is omitted")

class AdvancedAnalytics(BaseModel):
    pcr: float
    oi_shift_bias: str # Bullish / Bearish / Neutral
    delta_exposure_dex: float
    gamma_exposure_gex: float
    dealer_positioning: str # Short Gamma (High Volatility) or Long Gamma (Pinned)
    max_pain: float
    support_levels: List[float]
    resistance_levels: List[float]
    vwap_position: str # Above / Below
    atr_volatility: float
    momentum_score: float # -100 to +100
    trend_score: float # -100 to +100
    market_sentiment_score: float # 0 to 100

class ModelForecastBreakdown(BaseModel):
    oi_analysis_spot: float
    greeks_spot: float
    ml_ensemble_spot: float
    lstm_spot: float
    price_action_spot: float

class PredictionResponse(BaseModel):
    symbol: str
    current_spot: float
    predicted_spot_price: float
    expected_high: float
    expected_low: float
    confidence_pct: float
    prob_bullish: float
    prob_bearish: float
    prob_sideways: float
    expected_range_lower: float
    expected_range_upper: float
    target_price: float
    stop_loss: float
    expected_time_to_reach: str
    market_strength: str # "Strong", "Moderate", "Weak"
    volatility_level: str # "Low", "Normal", "High", "Extreme"
    risk_level: str # "Low", "Moderate", "High"
    suggested_strategy: StrategyType
    strategy_rationale: str
    advanced_analysis: AdvancedAnalytics
    model_breakdown: ModelForecastBreakdown
    timestamp: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    disclaimer: str = (
        "PROBABILISTIC FORECAST: Future market prices involve uncertainty. "
        "Predictions are statistical estimations derived from options Greeks, OI, and AI models. "
        "This does NOT constitute financial advice."
    )

class ExpiryInfo(BaseModel):
    date: str
    days_to_expiry: float
    type: str # "Weekly" or "Monthly"

class OptionChainResponse(BaseModel):
    symbol: str
    spot_price: float
    pcr: float
    max_pain: float
    immediate_support: float
    immediate_resistance: float
    strikes: List[StrikeRow]
    selected_days_to_expiry: float = 3.5
    selected_expiry_date: Optional[str] = None
    available_expiries: List[ExpiryInfo] = []
    timestamp: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
