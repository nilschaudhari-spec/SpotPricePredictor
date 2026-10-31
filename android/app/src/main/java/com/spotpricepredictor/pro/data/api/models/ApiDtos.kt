package com.spotpricepredictor.pro.data.api.models

import com.google.gson.annotations.SerializedName

data class PredictionRequestDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("exchange") val exchange: String = "NSE",
    @SerializedName("asset_type") val assetType: String = "INDEX",
    @SerializedName("current_spot_price") val currentSpotPrice: Double,
    @SerializedName("prediction_horizon") val predictionHorizon: String,
    @SerializedName("custom_minutes") val customMinutes: Int? = null,
    @SerializedName("trend_mode") val trendMode: String = "AUTO"
)

data class GreekMetricsDto(
    @SerializedName("delta") val delta: Double,
    @SerializedName("gamma") val gamma: Double,
    @SerializedName("theta") val theta: Double,
    @SerializedName("vega") val vega: Double,
    @SerializedName("rho") val rho: Double
)

data class OptionLegDto(
    @SerializedName("ltp") val ltp: Double,
    @SerializedName("volume") val volume: Long,
    @SerializedName("oi") val oi: Long,
    @SerializedName("change_in_oi") val changeInOi: Long,
    @SerializedName("iv") val iv: Double,
    @SerializedName("greeks") val greeks: GreekMetricsDto,
    @SerializedName("buildup") val buildup: String
)

data class StrikeRowDto(
    @SerializedName("strike_price") val strikePrice: Double,
    @SerializedName("call") val call: OptionLegDto,
    @SerializedName("put") val put: OptionLegDto
)

data class ExpiryInfoDto(
    @SerializedName("date") val date: String,
    @SerializedName("days_to_expiry") val daysToExpiry: Double,
    @SerializedName("type") val type: String
)

data class LiveQuoteDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("spot_price") val spotPrice: Double,
    @SerializedName("previous_close") val previousClose: Double,
    @SerializedName("change") val change: Double,
    @SerializedName("change_percent") val changePercent: Double,
    @SerializedName("is_realtime") val isRealtime: Boolean,
    @SerializedName("step") val step: Double? = null,
    @SerializedName("lot_size") val lotSize: Int? = null
)

data class OptionChainResponseDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("spot_price") val spotPrice: Double,
    @SerializedName("pcr") val pcr: Double,
    @SerializedName("max_pain") val maxPain: Double,
    @SerializedName("immediate_support") val immediateSupport: Double,
    @SerializedName("immediate_resistance") val immediateResistance: Double,
    @SerializedName("strikes") val strikes: List<StrikeRowDto>,
    @SerializedName("selected_days_to_expiry") val selectedDaysToExpiry: Double = 3.5,
    @SerializedName("selected_expiry_date") val selectedExpiryDate: String? = null,
    @SerializedName("available_expiries") val availableExpiries: List<ExpiryInfoDto> = emptyList()
)

data class AdvancedAnalyticsDto(
    @SerializedName("pcr") val pcr: Double,
    @SerializedName("oi_shift_bias") val oiShiftBias: String,
    @SerializedName("delta_exposure_dex") val deltaExposureDex: Double,
    @SerializedName("gamma_exposure_gex") val gammaExposureGex: Double,
    @SerializedName("dealer_positioning") val dealerPositioning: String,
    @SerializedName("max_pain") val maxPain: Double,
    @SerializedName("support_levels") val supportLevels: List<Double>,
    @SerializedName("resistance_levels") val resistanceLevels: List<Double>,
    @SerializedName("vwap_position") val vwapPosition: String,
    @SerializedName("atr_volatility") val atrVolatility: Double,
    @SerializedName("momentum_score") val momentumScore: Double,
    @SerializedName("trend_score") val trendScore: Double,
    @SerializedName("market_sentiment_score") val marketSentimentScore: Double
)

data class ModelForecastBreakdownDto(
    @SerializedName("oi_analysis_spot") val oiAnalysisSpot: Double,
    @SerializedName("greeks_spot") val greeksSpot: Double,
    @SerializedName("ml_ensemble_spot") val mlEnsembleSpot: Double,
    @SerializedName("lstm_spot") val lstmSpot: Double,
    @SerializedName("price_action_spot") val priceActionSpot: Double
)

data class StrategyPredictionDto(
    @SerializedName("strategy_name") val strategyName: String,
    @SerializedName("predicted_spot") val predictedSpot: Double,
    @SerializedName("target_price") val targetPrice: Double,
    @SerializedName("stop_loss") val stopLoss: Double,
    @SerializedName("signal") val signal: String,
    @SerializedName("confidence_pct") val confidencePct: Double,
    @SerializedName("rationale") val rationale: String
)

data class PredictionResponseDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("current_spot") val currentSpot: Double,
    @SerializedName("predicted_spot_price") val predictedSpotPrice: Double,
    @SerializedName("expected_high") val expectedHigh: Double,
    @SerializedName("expected_low") val expectedLow: Double,
    @SerializedName("confidence_pct") val confidencePct: Double,
    @SerializedName("prob_bullish") val probBullish: Double,
    @SerializedName("prob_bearish") val probBearish: Double,
    @SerializedName("prob_sideways") val probSideways: Double,
    @SerializedName("expected_range_lower") val expectedRangeLower: Double,
    @SerializedName("expected_range_upper") val expectedRangeUpper: Double,
    @SerializedName("target_price") val targetPrice: Double,
    @SerializedName("stop_loss") val stopLoss: Double,
    @SerializedName("expected_time_to_reach") val expectedTimeToReach: String,
    @SerializedName("market_strength") val marketStrength: String,
    @SerializedName("volatility_level") val volatilityLevel: String,
    @SerializedName("risk_level") val riskLevel: String,
    @SerializedName("suggested_strategy") val suggestedStrategy: String,
    @SerializedName("strategy_rationale") val strategyRationale: String,
    @SerializedName("advanced_analysis") val advancedAnalysis: AdvancedAnalyticsDto,
    @SerializedName("model_breakdown") val modelBreakdown: ModelForecastBreakdownDto,
    @SerializedName("action_recommendation") val actionRecommendation: String? = null,
    @SerializedName("action_rationale") val actionRationale: String? = null,
    @SerializedName("various_strategies") val variousStrategies: List<StrategyPredictionDto> = emptyList(),
    @SerializedName("disclaimer") val disclaimer: String
)

data class MarketNewsItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("section") val section: String,
    @SerializedName("title") val title: String,
    @SerializedName("symbols") val symbols: List<String> = emptyList(),
    @SerializedName("what_happened") val whatHappened: String,
    @SerializedName("why_it_matters") val whyItMatters: String,
    @SerializedName("likely_impact") val likelyImpact: String,
    @SerializedName("impact_sentiment") val impactSentiment: String,
    @SerializedName("source") val source: String,
    @SerializedName("source_url") val sourceUrl: String,
    @SerializedName("timestamp") val timestamp: String
)

data class MarketNewsBriefingDto(
    @SerializedName("title") val title: String,
    @SerializedName("last_updated") val lastUpdated: String,
    @SerializedName("macro_summary") val macroSummary: String,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("items") val items: List<MarketNewsItemDto>,
    @SerializedName("disclaimer") val disclaimer: String
)

data class TechnicalSummaryDto(
    @SerializedName("trend") val trend: String,
    @SerializedName("rsi") val rsi: Double,
    @SerializedName("rsi_signal") val rsiSignal: String,
    @SerializedName("macd_status") val macdStatus: String,
    @SerializedName("ema_alignment") val emaAlignment: String,
    @SerializedName("supertrend") val supertrend: String,
    @SerializedName("vwap_signal") val vwapSignal: String,
    @SerializedName("bollinger_signal") val bollingerSignal: String,
    @SerializedName("support_level") val supportLevel: Double,
    @SerializedName("resistance_level") val resistanceLevel: Double,
    @SerializedName("oi_pcr") val oiPcr: Double? = null,
    @SerializedName("max_pain") val maxPain: Double? = null,
    @SerializedName("rationale") val rationale: String
)

data class FNODetailsDto(
    @SerializedName("contract_name") val contractName: String,
    @SerializedName("derivative_type") val derivativeType: String,
    @SerializedName("option_type") val optionType: String? = null,
    @SerializedName("strike_price") val strikePrice: Double? = null,
    @SerializedName("expiry_date") val expiryDate: String,
    @SerializedName("premium_entry") val premiumEntry: Double,
    @SerializedName("premium_target") val premiumTarget: Double,
    @SerializedName("premium_stop_loss") val premiumStopLoss: Double,
    @SerializedName("lot_size") val lotSize: Int,
    @SerializedName("strategy_type") val strategyType: String
)

data class TradeSuggestionDto(
    @SerializedName("id") val id: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("instrument_type") val instrumentType: String,
    @SerializedName("action") val action: String,
    @SerializedName("signal_strength") val signalStrength: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("entry_price") val entryPrice: Double,
    @SerializedName("target_1") val target1: Double,
    @SerializedName("target_2") val target2: Double,
    @SerializedName("stop_loss") val stopLoss: Double,
    @SerializedName("risk_reward_ratio") val riskRewardRatio: String,
    @SerializedName("potential_gain_pct") val potentialGainPct: Double,
    @SerializedName("timeframe") val timeframe: String,
    @SerializedName("confidence_pct") val confidencePct: Double,
    @SerializedName("fno_details") val fnoDetails: FNODetailsDto? = null,
    @SerializedName("technical_analysis") val technicalAnalysis: TechnicalSummaryDto,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class TradeSuggestionsResponseDto(
    @SerializedName("suggestions") val suggestions: List<TradeSuggestionDto>,
    @SerializedName("market_sentiment") val marketSentiment: String,
    @SerializedName("total_signals") val totalSignals: Int,
    @SerializedName("disclaimer") val disclaimer: String
)

data class CandleDataDto(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("time_str") val timeStr: String,
    @SerializedName("open") val open: Double,
    @SerializedName("high") val high: Double,
    @SerializedName("low") val low: Double,
    @SerializedName("close") val close: Double,
    @SerializedName("volume") val volume: Long,
    @SerializedName("ema_20") val ema20: Double? = null,
    @SerializedName("ema_50") val ema50: Double? = null,
    @SerializedName("sma_200") val sma200: Double? = null,
    @SerializedName("vwap") val vwap: Double? = null,
    @SerializedName("bb_upper") val bbUpper: Double? = null,
    @SerializedName("bb_middle") val bbMiddle: Double? = null,
    @SerializedName("bb_lower") val bbLower: Double? = null,
    @SerializedName("supertrend") val supertrend: Double? = null,
    @SerializedName("supertrend_direction") val supertrendDirection: String? = null,
    @SerializedName("rsi") val rsi: Double? = null,
    @SerializedName("macd") val macd: Double? = null,
    @SerializedName("macd_signal") val macdSignal: Double? = null,
    @SerializedName("macd_hist") val macdHist: Double? = null
)

data class StrategySignalDto(
    @SerializedName("name") val name: String,
    @SerializedName("signal") val signal: String,
    @SerializedName("strength") val strength: Double = 80.0,
    @SerializedName("rationale") val rationale: String
)

data class ChartDataResponseDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("interval") val interval: String,
    @SerializedName("spot_price") val spotPrice: Double,
    @SerializedName("change") val change: Double,
    @SerializedName("change_pct") val changePct: Double,
    @SerializedName("candles") val candles: List<CandleDataDto>,
    @SerializedName("technical_call") val technicalCall: String? = null,
    @SerializedName("technical_score") val technicalScore: Double? = null,
    @SerializedName("target_price") val targetPrice: Double? = null,
    @SerializedName("stop_loss") val stopLoss: Double? = null,
    @SerializedName("strategy_signals") val strategySignals: List<StrategySignalDto> = emptyList()
)

data class MarketMoverItemDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("spot_price") val spotPrice: Double,
    @SerializedName("change") val change: Double,
    @SerializedName("change_percent") val changePercent: Double,
    @SerializedName("exchange") val exchange: String = "NSE",
    @SerializedName("is_positive") val isPositive: Boolean = true
)

data class GainersLosersResponseDto(
    @SerializedName("exchange") val exchange: String = "NSE",
    @SerializedName("timestamp") val timestamp: String = "",
    @SerializedName("gainers") val gainers: List<MarketMoverItemDto> = emptyList(),
    @SerializedName("losers") val losers: List<MarketMoverItemDto> = emptyList()
)


