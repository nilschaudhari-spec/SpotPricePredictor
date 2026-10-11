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

