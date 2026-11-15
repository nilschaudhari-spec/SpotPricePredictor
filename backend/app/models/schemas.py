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

class StrategyPredictionItem(BaseModel):
    strategy_name: str
    predicted_spot: float
    target_price: float
    stop_loss: float
    signal: str # "BUY", "SELL", "HOLD"
    confidence_pct: float
    rationale: str

class StrategySignalItem(BaseModel):
    name: str
    signal: str # "BUY", "SELL", "NEUTRAL"
    strength: float = 80.0
    rationale: str

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
    action_recommendation: Optional[str] = "BUY" # "BUY", "HOLD", or "SELL"
    action_rationale: Optional[str] = None
    various_strategies: List[StrategyPredictionItem] = []
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

class TradeAction(str, Enum):
    BUY = "BUY"
    SELL = "SELL"

class InstrumentType(str, Enum):
    EQUITY = "EQUITY"
    OPTIONS = "OPTIONS"
    FUTURES = "FUTURES"

class SignalCategory(str, Enum):
    INDEX = "INDEX"
    EQUITY = "EQUITY"

class SignalStrength(str, Enum):
    STRONG_BUY = "STRONG BUY"
    BUY = "BUY"
    STRONG_SELL = "STRONG SELL"
    SELL = "SELL"
    NEUTRAL = "NEUTRAL"

class TechnicalSummary(BaseModel):
    trend: str
    rsi: float
    rsi_signal: str
    macd_status: str
    ema_alignment: str
    supertrend: str
    vwap_signal: str
    bollinger_signal: str
    support_level: float
    resistance_level: float
    oi_pcr: Optional[float] = None
    max_pain: Optional[float] = None
    rationale: str

class FNODetails(BaseModel):
    contract_name: str
    derivative_type: str # "OPTIONS" or "FUTURES"
    option_type: Optional[str] = None # "CE", "PE"
    strike_price: Optional[float] = None
    expiry_date: str
    premium_entry: float
    premium_target: float
    premium_stop_loss: float
    lot_size: int
    strategy_type: str

class TradeSuggestion(BaseModel):
    id: str
    symbol: str
    name: str
    category: SignalCategory
    instrument_type: InstrumentType
    action: TradeAction
    signal_strength: SignalStrength
    current_price: float
    entry_price: float
    target_1: float
    target_2: float
    stop_loss: float
    risk_reward_ratio: str
    potential_gain_pct: float
    timeframe: str # "Intraday (15-60m)" or "Swing (1-3 Days)"
    confidence_pct: float
    fno_details: Optional[FNODetails] = None
    technical_analysis: TechnicalSummary
    timestamp: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

class TradeSuggestionsResponse(BaseModel):
    suggestions: List[TradeSuggestion]
    market_sentiment: str
    total_signals: int
    disclaimer: str = (
        "TRADE RECOMMENDATION DISCLAIMER: Market suggestions are derived from statistical quantitative "
        "and technical analysis algorithms. Derivatives and equities trading involve financial risk. "
        "Always adhere to strict risk management and position sizing."
    )

class CandleData(BaseModel):
    timestamp: int
    time_str: str
    open: float
    high: float
    low: float
    close: float
    volume: int
    ema_20: Optional[float] = None
    ema_50: Optional[float] = None
    sma_200: Optional[float] = None
    vwap: Optional[float] = None
    bb_upper: Optional[float] = None
    bb_middle: Optional[float] = None
    bb_lower: Optional[float] = None
    supertrend: Optional[float] = None
    supertrend_direction: Optional[str] = None # "UP" or "DOWN"
    rsi: Optional[float] = None
    macd: Optional[float] = None
    macd_signal: Optional[float] = None
    macd_hist: Optional[float] = None

class ChartDataResponse(BaseModel):
    symbol: str
    interval: str
    spot_price: float
    change: float
    change_pct: float
    candles: List[CandleData]
    technical_call: Optional[str] = "BUY" # STRONG BUY, BUY, NEUTRAL, SELL, STRONG SELL
    technical_score: Optional[float] = 0.0
    target_price: Optional[float] = None
    stop_loss: Optional[float] = None
    strategy_signals: List[StrategySignalItem] = []
    is_market_open: bool = False
    market_status: str = "MARKET CLOSED"
    session_label: str = "Official Close"
    last_close_time: Optional[str] = None

class MarketMoverItem(BaseModel):
    symbol: str
    name: str
    spot_price: float
    change: float
    change_percent: float
    exchange: str = "NSE"
    is_positive: bool = True
    volume: Optional[int] = 0
    volume_str: Optional[str] = "0"
    turnover_cr: Optional[float] = 0.0
    turnover_str: Optional[str] = "₹0 Cr"

class GainersLosersResponse(BaseModel):
    exchange: str = "NSE"
    timestamp: str
    gainers: List[MarketMoverItem] = []
    losers: List[MarketMoverItem] = []
    most_active_volume: List[MarketMoverItem] = []
    most_active_value: List[MarketMoverItem] = []

class CorporateActionItem(BaseModel):
    id: str
    symbol: str
    company_name: str
    action_type: str # "DIVIDEND", "SPLIT", "BONUS", "RIGHTS", "AGM", "BUYBACK"
    ex_date: str
    record_date: str
    details: str
    amount: Optional[float] = None
    ratio: Optional[str] = None
    impact: str = "Neutral"

class CorporateActionsResponse(BaseModel):
    symbol: Optional[str] = None
    total_actions: int
    actions: List[CorporateActionItem]

class PeerStockMetrics(BaseModel):
    symbol: str
    name: str
    cmp: float
    change_pct: float
    pe_ratio: float
    pb_ratio: float
    market_cap_cr: float
    market_cap_str: str
    roe: float
    roce: float
    dividend_yield: float
    net_profit_margin: float
    high_52w: float
    low_52w: float
    is_target_stock: bool = False

class PeerComparisonResponse(BaseModel):
    symbol: str
    company_name: str
    sector: str
    industry: str
    peers: List[PeerStockMetrics]

class MovingAverageItem(BaseModel):
    period: int
    sma_value: float
    sma_call: str # "BUY" or "SELL"
    ema_value: float
    ema_call: str # "BUY" or "SELL"

class OscillatorItem(BaseModel):
    name: str
    value: float
    signal: str # "BUY", "SELL", "NEUTRAL", "OVERBOUGHT", "OVERSOLD"
    interpretation: str

class PivotPointLevel(BaseModel):
    name: str
    p: float
    s1: float
    s2: float
    s3: float
    r1: float
    r2: float
    r3: float
    s4: Optional[float] = None
    r4: Optional[float] = None

class DetailedTechnicalAnalysisResponse(BaseModel):
    symbol: str
    spot_price: float
    change: float
    change_pct: float
    overall_call: str # "STRONG BUY", "BUY", "NEUTRAL", "SELL", "STRONG SELL"
    technical_score: float # -100 to +100
    bullish_count: int
    bearish_count: int
    neutral_count: int
    moving_averages: List[MovingAverageItem]
    oscillators: List[OscillatorItem]
    pivot_points: Dict[str, PivotPointLevel]
    summary_rationale: str

class QuarterlyResultItem(BaseModel):
    id: str
    symbol: str
    company_name: str
    result_date: str
    quarter: str
    revenue_actual_cr: Optional[float] = None
    revenue_estimate_cr: Optional[float] = None
    pat_actual_cr: Optional[float] = None
    pat_estimate_cr: Optional[float] = None
    yoy_growth_pct: Optional[float] = None
    status: str # "UPCOMING", "DECLARED"
    purpose: str = "Financial Results"

class CorporateEventItem(BaseModel):
    id: str
    symbol: str
    company_name: str
    event_date: str
    category: str # "RESULTS", "DIVIDEND", "BONUS/SPLIT", "FUND_RAISING", "AGM"
    title: str
    description: str

class EconomicEventItem(BaseModel):
    event_id: str
    title: str
    date: str
    impact_level: str # "HIGH", "MEDIUM", "LOW"
    forecast: Optional[str] = None
    previous: Optional[str] = None
    actual: Optional[str] = None
    sector_impact: str

class ResultsAndEventsResponse(BaseModel):
    symbol: Optional[str] = None
    quarterly_results: List[QuarterlyResultItem] = []
    corporate_events: List[CorporateEventItem] = []
    economic_events: List[EconomicEventItem] = []

class PatternDetectionItem(BaseModel):
    pattern_id: str
    pattern_name: str # e.g. "Hammer", "Bullish Marubozu", "Dragonfly Doji", "Morning Star", "Evening Star"
    pattern_type: str # "BUY" or "SELL"
    category: str = "CANDLESTICK PATTERN" # "CANDLESTICK PATTERN" or "PRICE ACTION TECHNIQUE"
    symbol: str
    company_name: str
    timeframe: str = "15m"
    reliability_pct: float = 85.0
    status: str = "CONFIRMED" # "CONFIRMED", "FORMING", "TRIGGERED"
    current_price: float
    trigger_price: float
    stop_loss: float
    target1: float
    target2: float
    risk_reward_ratio: str = "1:2.5"
    candle_anatomy: str
    description: str
    trading_rule: str
    timestamp: str = ""

class PatternScannerResponse(BaseModel):
    symbol: Optional[str] = None
    total_detected: int
    buy_patterns: List[PatternDetectionItem] = []
    sell_patterns: List[PatternDetectionItem] = []
    all_patterns: List[PatternDetectionItem] = []

# =============================================================================
# ALL ROUNDER 2 (OCTOBER EDITION) PRO STRATEGIES & OPTIONS SUITE
# =============================================================================

class DailyIntradayTechniqueItem(BaseModel):
    symbol: str
    timeframe: str = "5 MIN"
    supertrend_status: str # "BULLISH (UPTREND)" or "BEARISH (DOWNTREND)"
    supertrend_value: float
    supertrend_inputs: str = "Length: 20, Factor: 2"
    vwap_status: str # "PRICE ABOVE VWAP (UPTREND)" or "PRICE BELOW VWAP (DOWNTREND)"
    vwap_value: float
    macd_status: str # "MACD BULLISH CROSSOVER" or "MACD BEARISH CROSSOVER"
    macd_signal: str
    tri_confirmation_signal: str # "CONFIRMED BUY / CALL ENTRY", "CONFIRMED SELL / PUT ENTRY", "WAITING FOR CONFIRMATION"
    is_confirmed: bool = False
    entry_price: float
    target_price: float
    stop_loss: float
    target_sl_ratio: str = "1:1"
    execution_rules: List[str] = [
        "1. Candlestick Body should not be weak / small.",
        "2. Trend should be reversal only, if not avoid it.",
        "3. Take Entry on before half minute of (B) Candle closing.",
        "4. Target & Stop Loss should be 1:1 (e.g. 20 pts target = 20 pts SL).",
        "5. Place Target order immediately after Transaction Order."
    ]

class GoldenCrossStrategyItem(BaseModel):
    symbol: str
    ema50_value: float # Blue Line
    ema200_value: float # Red Line
    crossover_status: str # "GOLDEN CROSS (BULLISH)", "DEATH CROSS (BEARISH)", "IN UPTREND (50 > 200)", "IN DOWNTREND (50 < 200)"
    reenter_opportunity: bool = False
    reenter_type: str = "NONE" # "BUY RE-ENTER (Near 200/50 EMA Support)", "SELL RE-ENTER (Near 200/50 EMA Resistance)", "NONE"
    reenter_explanation: str
    trade_action: str # "BUY / CALL", "SHORT SELL / PUT", "HOLD / WAIT"

class BollingerBandTechniqueItem(BaseModel):
    symbol: str
    upper_band: float
    median_line: float # SMA 20
    lower_band: float
    band_status: str # "TOUCHING/BREAKING UPPER LINE", "TOUCHING/BREAKING LOWER LINE", "PASSING THROUGH MEDIAN LINE", "INSIDE BANDS"
    trade_call: str # "CALL BUYING SETUP (STRONG UPTREND)", "PUT BUYING SETUP (STRONG DOWNTREND)", "TREND REVERSAL ALERT", "NEUTRAL"
    explanation: str

class ConsolidationBreakoutItem(BaseModel):
    symbol: str
    range_high: float
    range_low: float
    is_consolidating: bool
    status: str # "CONSOLIDATING (SIDEWAYS - AVOID)", "BREAKOUT ABOVE RESISTANCE (BIGGER MOVE UP)", "BREAKDOWN BELOW SUPPORT (BIGGER MOVE DOWN)"
    guidance: str = "Once this range is broken, it may lead to bigger moves; until range is intact, avoid trading."

class OpenInterestInterpretationItem(BaseModel):
    symbol: str
    spot_price: float
    price_change_pct: float
    total_oi: int
    oi_change_pct: float
    interpretation: str # "LONG BUILDUP", "SHORT BUILDUP", "SHORT COVERING", "LONG UNWINDING"
    market_sentiment: str # "Strong Bullish", "Strong Bearish", "Bullish Pullback", "Bearish Profit Booking"
    put_call_ratio_oi: float
    max_pain_strike: float
    highest_oi_call_resistance: float
    highest_oi_put_support: float
    action_advice: str

class HedgingStrategyItem(BaseModel):
    strategy_name: str # "Protective Put (Uptrend Strategy)", "Protective Call (Downtrend Strategy)"
    view: str # "WHEN MARKET IS IN UPTREND", "WHEN MARKET IS IN DOWNTREND"
    rule: str = "Works only when market in uptrend/downtrend; Avoid trade if market is sideways."
    future_leg: str # "Buy Future - 1 lot", "Short sell Future - 1 lot"
    option_leg: str # "Buy ATM Put - 1 lot", "Buy ATM Call - 1 lot"
    risk_profile: str
    underlying_symbol: str
    atm_strike: float
    estimated_premium: float
    net_protection: str

class OptionTerminologyItem(BaseModel):
    id: int
    name: str
    definition: str
    best_market_condition: str

class ProStrategiesResponse(BaseModel):
    symbol: str
    company_name: str
    daily_intraday: DailyIntradayTechniqueItem
    golden_cross: GoldenCrossStrategyItem
    bollinger_bands: BollingerBandTechniqueItem
    consolidation: ConsolidationBreakoutItem
    open_interest: OpenInterestInterpretationItem
    hedging_strategies: List[HedgingStrategyItem] = []
    terminologies: List[OptionTerminologyItem] = []

# =============================================================================
# FRONTPAGE & DEMO TRADING AUTO-EXECUTION ENGINE SCHEMAS
# =============================================================================

class AutoTradeSettings(BaseModel):
    is_auto_trade_enabled: bool = False
    broker_destination: str = "FRONTPAGE"  # "FRONTPAGE", "IN_APP_PAPER", "TRADINGVIEW_WEBHOOK", "SENSIBULL_VIRTUAL"
    frontpage_user_token: Optional[str] = ""
    frontpage_account_id: Optional[str] = "FP_VIRTUAL_01"
    webhook_url: Optional[str] = ""
    scan_interval_seconds: int = 15
    min_reliability_pct: float = 80.0
    allowed_actions: str = "ALL"  # "ALL", "BUY_ONLY", "SELL_ONLY"
    max_capital_per_trade: float = 50000.0
    max_concurrent_positions: int = 5
    auto_trailing_stop_loss: bool = True
    notify_on_execution: bool = True

class AutoTradeOrder(BaseModel):
    order_id: str
    symbol: str
    company_name: str
    exchange: str = "NSE"
    action: str  # "BUY", "SELL"
    product_type: str = "INTRADAY"  # "INTRADAY", "DELIVERY", "FNO"
    pattern_trigger: str
    order_price: float
    quantity: int
    total_value: float
    stop_loss: float
    target_price: float
    status: str = "EXECUTED"  # "PENDING", "EXECUTED", "REJECTED", "CANCELLED"
    broker: str = "FrontPage"
    broker_order_id: str
    timestamp: str
    execution_message: str

class VirtualPosition(BaseModel):
    position_id: str
    order_id: str
    symbol: str
    company_name: str
    action: str  # "BUY", "SELL"
    quantity: int
    avg_price: float
    current_price: float
    stop_loss: float
    target_price: float
    pnl: float
    pnl_percent: float
    pattern_trigger: str
    broker: str = "FrontPage"
    status: str = "OPEN"  # "OPEN", "CLOSED_TARGET", "CLOSED_SL", "MANUALLY_CLOSED"
    entry_time: str
    exit_time: Optional[str] = None
    exit_price: Optional[float] = None

class VirtualPortfolio(BaseModel):
    total_capital: float = 1000000.0
    available_margin: float = 1000000.0
    used_margin: float = 0.0
    unrealized_pnl: float = 0.0
    realized_pnl: float = 0.0
    total_trades: int = 0
    winning_trades: int = 0
    losing_trades: int = 0
    win_rate_pct: float = 0.0
    broker_name: str = "FrontPage Virtual Account"
    positions: List[VirtualPosition] = []
    order_history: List[AutoTradeOrder] = []

class ScanAndExecuteResponse(BaseModel):
    stocks_scanned_count: int
    patterns_detected_count: int
    orders_executed: List[AutoTradeOrder] = []
    portfolio: VirtualPortfolio
    summary: str
    timestamp: str

class ManualOrderRequest(BaseModel):
    symbol: str
    action: str  # "BUY" or "SELL"
    quantity: Optional[int] = None
    product_type: str = "INTRADAY"
    price: Optional[float] = None
    stop_loss: Optional[float] = None
    target_price: Optional[float] = None
    pattern_trigger: str = "Manual User Order"

class SquareOffRequest(BaseModel):
    position_id: Optional[str] = None  # None or "ALL" closes all open positions




