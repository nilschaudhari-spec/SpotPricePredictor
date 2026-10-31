package com.spotpricepredictor.pro.domain.model

enum class TradeAction(val label: String) {
    BUY("BUY"),
    SELL("SELL")
}

enum class InstrumentType(val label: String) {
    EQUITY("EQUITY (CASH)"),
    OPTIONS("OPTIONS"),
    FUTURES("FUTURES")
}

enum class SignalCategory(val label: String) {
    INDEX("INDEX"),
    EQUITY("EQUITY")
}

enum class SignalStrength(val label: String) {
    STRONG_BUY("STRONG BUY"),
    BUY("BUY"),
    STRONG_SELL("STRONG SELL"),
    SELL("SELL"),
    NEUTRAL("NEUTRAL")
}

data class TechnicalSummary(
    val trend: String,
    val rsi: Double,
    val rsiSignal: String,
    val macdStatus: String,
    val emaAlignment: String,
    val supertrend: String,
    val vwapSignal: String,
    val bollingerSignal: String,
    val supportLevel: Double,
    val resistanceLevel: Double,
    val oiPcr: Double? = null,
    val maxPain: Double? = null,
    val rationale: String
)

data class FNODetails(
    val contractName: String,
    val derivativeType: String,
    val optionType: String? = null,
    val strikePrice: Double? = null,
    val expiryDate: String,
    val premiumEntry: Double,
    val premiumTarget: Double,
    val premiumStopLoss: Double,
    val lotSize: Int,
    val strategyType: String
)

data class TradeSuggestion(
    val id: String,
    val symbol: String,
    val name: String,
    val category: SignalCategory,
    val instrumentType: InstrumentType,
    val action: TradeAction,
    val signalStrength: SignalStrength,
    val currentPrice: Double,
    val entryPrice: Double,
    val target1: Double,
    val target2: Double,
    val stopLoss: Double,
    val riskRewardRatio: String,
    val potentialGainPct: Double,
    val timeframe: String,
    val confidencePct: Double,
    val fnoDetails: FNODetails? = null,
    val technicalAnalysis: TechnicalSummary,
    val timestamp: String? = null
)

data class TradeSuggestionsResponse(
    val suggestions: List<TradeSuggestion>,
    val marketSentiment: String,
    val totalSignals: Int,
    val disclaimer: String
)
