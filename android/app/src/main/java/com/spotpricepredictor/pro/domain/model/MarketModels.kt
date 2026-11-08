package com.spotpricepredictor.pro.domain.model

enum class Exchange {
    NSE, BSE
}

enum class AssetType {
    INDEX, STOCK
}

enum class OptionType {
    CE, PE
}

enum class TrendDirection(val label: String) {
    STRONG_BULLISH("Strong Bullish"),
    BULLISH("Bullish"),
    SIDEWAYS("Sideways"),
    BEARISH("Bearish"),
    STRONG_BEARISH("Strong Bearish"),
    AUTO("Auto Detect")
}

enum class PredictionHorizon(val label: String, val minutes: Int) {
    MIN_5("5 Min", 5),
    MIN_10("10 Min", 10),
    MIN_15("15 Min", 15),
    MIN_30("30 Min", 30),
    HOUR_1("1 Hour", 60),
    CUSTOM("Custom", 45)
}

enum class StrategyType(val label: String) {
    BUY_CALL("BUY CALL"),
    BUY_PUT("BUY PUT"),
    CALL_SPREAD("CALL SPREAD"),
    PUT_SPREAD("PUT SPREAD"),
    IRON_CONDOR("IRON CONDOR"),
    STRADDLE("STRADDLE"),
    WAIT("WAIT"),
    NO_TRADE("NO TRADE")
}

data class GreekMetrics(
    val delta: Double = 0.0,
    val gamma: Double = 0.0,
    val theta: Double = 0.0,
    val vega: Double = 0.0,
    val rho: Double = 0.0
)

data class OptionLeg(
    val ltp: Double,
    val volume: Long,
    val oi: Long,
    val changeInOi: Long,
    val iv: Double,
    val greeks: GreekMetrics,
    val buildup: String
)

data class StrikeRowData(
    val strikePrice: Double,
    val call: OptionLeg,
    val put: OptionLeg
)

data class ExpiryInfo(
    val date: String,
    val daysToExpiry: Double,
    val type: String
)

data class LiveQuote(
    val symbol: String,
    val spotPrice: Double,
    val previousClose: Double,
    val change: Double,
    val changePercent: Double,
    val isRealtime: Boolean,
    val step: Double? = null,
    val lotSize: Int? = null,
    val isMarketOpen: Boolean = false,
    val marketStatus: String = "MARKET CLOSED",
    val sessionLabel: String = "Official Close",
    val lastCloseTime: String = "",
    val nextSession: String = ""
)

data class OptionChainData(
    val symbol: String,
    val spotPrice: Double,
    val pcr: Double,
    val maxPain: Double,
    val immediateSupport: Double,
    val immediateResistance: Double,
    val strikes: List<StrikeRowData>,
    val selectedDaysToExpiry: Double = 3.5,
    val selectedExpiryDate: String? = null,
    val availableExpiries: List<ExpiryInfo> = emptyList()
)

data class MarketMoverItem(
    val symbol: String,
    val name: String,
    val spotPrice: Double,
    val change: Double,
    val changePercent: Double,
    val exchange: String = "NSE",
    val isPositive: Boolean = true,
    val volume: Long = 0L,
    val volumeStr: String = "",
    val turnoverCr: Double = 0.0,
    val turnoverStr: String = ""
)

data class GainersLosersData(
    val exchange: String = "NSE",
    val timestamp: String = "",
    val gainers: List<MarketMoverItem> = emptyList(),
    val losers: List<MarketMoverItem> = emptyList(),
    val mostActiveVolume: List<MarketMoverItem> = emptyList(),
    val mostActiveValue: List<MarketMoverItem> = emptyList()
)

data class CorporateActionItem(
    val id: String,
    val symbol: String,
    val companyName: String,
    val actionType: String,
    val exDate: String,
    val recordDate: String,
    val details: String,
    val amount: Double? = null,
    val ratio: String? = null,
    val impact: String = "Neutral"
)

data class CorporateActionsData(
    val symbol: String? = null,
    val totalActions: Int = 0,
    val actions: List<CorporateActionItem> = emptyList()
)

data class PeerStockMetrics(
    val symbol: String,
    val name: String,
    val cmp: Double,
    val changePct: Double,
    val peRatio: Double,
    val pbRatio: Double,
    val marketCapCr: Double,
    val marketCapStr: String,
    val roe: Double,
    val roce: Double,
    val dividendYield: Double,
    val netProfitMargin: Double,
    val high52w: Double,
    val low52w: Double,
    val isTargetStock: Boolean = false
)

data class PeerComparisonData(
    val symbol: String,
    val companyName: String,
    val sector: String,
    val industry: String,
    val peers: List<PeerStockMetrics> = emptyList()
)

data class MovingAverageItem(
    val period: Int,
    val smaValue: Double,
    val smaCall: String,
    val emaValue: Double,
    val emaCall: String
)

data class OscillatorItem(
    val name: String,
    val value: Double,
    val signal: String,
    val interpretation: String
)

data class PivotPointLevel(
    val name: String,
    val p: Double,
    val s1: Double,
    val s2: Double,
    val s3: Double,
    val r1: Double,
    val r2: Double,
    val r3: Double,
    val s4: Double? = null,
    val r4: Double? = null
)

data class DetailedTechnicalsData(
    val symbol: String,
    val spotPrice: Double,
    val change: Double,
    val changePct: Double,
    val overallCall: String,
    val technicalScore: Double,
    val bullishCount: Int,
    val bearishCount: Int,
    val neutralCount: Int,
    val movingAverages: List<MovingAverageItem> = emptyList(),
    val oscillators: List<OscillatorItem> = emptyList(),
    val pivotPoints: Map<String, PivotPointLevel> = emptyMap(),
    val summaryRationale: String = ""
)

data class QuarterlyResultItem(
    val id: String,
    val symbol: String,
    val companyName: String,
    val resultDate: String,
    val quarter: String,
    val revenueActualCr: Double? = null,
    val revenueEstimateCr: Double? = null,
    val patActualCr: Double? = null,
    val patEstimateCr: Double? = null,
    val yoyGrowthPct: Double? = null,
    val status: String,
    val purpose: String = "Financial Results"
)

data class CorporateEventItem(
    val id: String,
    val symbol: String,
    val companyName: String,
    val eventDate: String,
    val category: String,
    val title: String,
    val description: String
)

data class EconomicEventItem(
    val eventId: String,
    val title: String,
    val date: String,
    val impactLevel: String,
    val forecast: String? = null,
    val previous: String? = null,
    val actual: String? = null,
    val sectorImpact: String
)

data class ResultsAndEventsData(
    val symbol: String? = null,
    val quarterlyResults: List<QuarterlyResultItem> = emptyList(),
    val corporateEvents: List<CorporateEventItem> = emptyList(),
    val economicEvents: List<EconomicEventItem> = emptyList()
)

data class PatternDetectionItem(
    val patternId: String,
    val patternName: String,
    val patternType: String, // "BUY" or "SELL"
    val category: String = "CANDLESTICK PATTERN", // "CANDLESTICK PATTERN" or "PRICE ACTION TECHNIQUE"
    val symbol: String,
    val companyName: String,
    val timeframe: String = "15m",
    val reliabilityPct: Double = 85.0,
    val status: String = "CONFIRMED",
    val currentPrice: Double,
    val triggerPrice: Double,
    val stopLoss: Double,
    val target1: Double,
    val target2: Double,
    val riskRewardRatio: String = "1:2.5",
    val candleAnatomy: String,
    val description: String,
    val tradingRule: String,
    val timestamp: String = ""
)

data class PatternScannerData(
    val symbol: String? = null,
    val totalDetected: Int = 0,
    val buyPatterns: List<PatternDetectionItem> = emptyList(),
    val sellPatterns: List<PatternDetectionItem> = emptyList(),
    val allPatterns: List<PatternDetectionItem> = emptyList()
)

data class DailyIntradayTechnique(
    val symbol: String,
    val timeframe: String = "5 MIN",
    val supertrendStatus: String,
    val supertrendValue: Double,
    val supertrendInputs: String = "Length: 20, Factor: 2",
    val vwapStatus: String,
    val vwapValue: Double,
    val macdStatus: String,
    val macdSignal: String,
    val triConfirmationSignal: String,
    val isConfirmed: Boolean = false,
    val entryPrice: Double,
    val targetPrice: Double,
    val stopLoss: Double,
    val targetSlRatio: String = "1:1",
    val executionRules: List<String> = emptyList()
)

data class GoldenCrossData(
    val symbol: String,
    val ema50Value: Double,
    val ema200Value: Double,
    val crossoverStatus: String,
    val reenterOpportunity: Boolean = false,
    val reenterType: String = "NONE",
    val reenterExplanation: String,
    val tradeAction: String
)

data class BollingerBandTechnique(
    val symbol: String,
    val upperBand: Double,
    val medianLine: Double,
    val lowerBand: Double,
    val bandStatus: String,
    val tradeCall: String,
    val explanation: String
)

data class ConsolidationBreakout(
    val symbol: String,
    val rangeHigh: Double,
    val rangeLow: Double,
    val isConsolidating: Boolean,
    val status: String,
    val guidance: String
)

data class OpenInterestInterpretation(
    val symbol: String,
    val spotPrice: Double,
    val priceChangePct: Double,
    val totalOi: Long,
    val oiChangePct: Double,
    val interpretation: String,
    val marketSentiment: String,
    val putCallRatioOi: Double,
    val maxPainStrike: Double,
    val highestOiCallResistance: Double,
    val highestOiPutSupport: Double,
    val actionAdvice: String
)

data class HedgingStrategyItem(
    val strategyName: String,
    val view: String,
    val rule: String,
    val futureLeg: String,
    val optionLeg: String,
    val riskProfile: String,
    val underlyingSymbol: String,
    val atmStrike: Double,
    val estimatedPremium: Double,
    val netProtection: String
)

data class OptionTerminologyItem(
    val id: Int,
    val name: String,
    val definition: String,
    val bestMarketCondition: String
)

data class ProStrategiesData(
    val symbol: String,
    val companyName: String,
    val dailyIntraday: DailyIntradayTechnique,
    val goldenCross: GoldenCrossData,
    val bollingerBands: BollingerBandTechnique,
    val consolidation: ConsolidationBreakout,
    val openInterest: OpenInterestInterpretation,
    val hedgingStrategies: List<HedgingStrategyItem> = emptyList(),
    val terminologies: List<OptionTerminologyItem> = emptyList()
)
