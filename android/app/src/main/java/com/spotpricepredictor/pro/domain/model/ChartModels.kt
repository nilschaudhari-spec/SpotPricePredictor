package com.spotpricepredictor.pro.domain.model

data class StrategySignal(
    val name: String,
    val signal: String, // "BUY", "SELL", "NEUTRAL"
    val strength: Double = 80.0,
    val rationale: String
)

data class CandleBar(
    val timestamp: Long,
    val timeStr: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val ema20: Double? = null,
    val ema50: Double? = null,
    val sma200: Double? = null,
    val vwap: Double? = null,
    val bbUpper: Double? = null,
    val bbMiddle: Double? = null,
    val bbLower: Double? = null,
    val supertrend: Double? = null,
    val supertrendDirection: String? = null,
    val rsi: Double? = null,
    val macd: Double? = null,
    val macdSignal: Double? = null,
    val macdHist: Double? = null
)

data class ChartData(
    val symbol: String,
    val interval: String,
    val spotPrice: Double,
    val change: Double,
    val changePct: Double,
    val candles: List<CandleBar>,
    val technicalCall: String = "BUY", // STRONG BUY, BUY, NEUTRAL, SELL, STRONG SELL
    val technicalScore: Double = 0.0,
    val targetPrice: Double? = null,
    val stopLoss: Double? = null,
    val strategySignals: List<StrategySignal> = emptyList(),
    val isMarketOpen: Boolean = false,
    val marketStatus: String = "MARKET CLOSED",
    val sessionLabel: String = "Official Close",
    val lastCloseTime: String = ""
)

data class IndicatorConfig(
    val showEma20: Boolean = true,
    val showEma50: Boolean = true,
    val showSma200: Boolean = false,
    val showVwap: Boolean = true,
    val showBollinger: Boolean = false,
    val showSupertrend: Boolean = true,
    val showRsi: Boolean = true,
    val showMacd: Boolean = true
)
