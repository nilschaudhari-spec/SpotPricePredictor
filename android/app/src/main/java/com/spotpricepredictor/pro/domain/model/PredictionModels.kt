package com.spotpricepredictor.pro.domain.model

data class AdvancedAnalysis(
    val pcr: Double,
    val oiShiftBias: String,
    val deltaExposureDex: Double,
    val gammaExposureGex: Double,
    val dealerPositioning: String,
    val maxPain: Double,
    val supportLevels: List<Double>,
    val resistanceLevels: List<Double>,
    val vwapPosition: String,
    val atrVolatility: Double,
    val momentumScore: Double,
    val trendScore: Double,
    val marketSentimentScore: Double
)

data class ModelForecastBreakdown(
    val oiAnalysisSpot: Double,
    val greeksSpot: Double,
    val mlEnsembleSpot: Double,
    val lstmSpot: Double,
    val priceActionSpot: Double
)

data class PredictionResult(
    val symbol: String,
    val currentSpot: Double,
    val predictedSpotPrice: Double,
    val expectedHigh: Double,
    val expectedLow: Double,
    val confidencePct: Double,
    val probBullish: Double,
    val probBearish: Double,
    val probSideways: Double,
    val expectedRangeLower: Double,
    val expectedRangeUpper: Double,
    val targetPrice: Double,
    val stopLoss: Double,
    val expectedTimeToReach: String,
    val marketStrength: String,
    val volatilityLevel: String,
    val riskLevel: String,
    val suggestedStrategy: StrategyType,
    val strategyRationale: String,
    val advancedAnalysis: AdvancedAnalysis,
    val modelBreakdown: ModelForecastBreakdown,
    val disclaimer: String
)
