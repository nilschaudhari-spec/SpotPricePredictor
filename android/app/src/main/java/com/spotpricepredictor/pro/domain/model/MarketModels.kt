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
    val lotSize: Int? = null
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
