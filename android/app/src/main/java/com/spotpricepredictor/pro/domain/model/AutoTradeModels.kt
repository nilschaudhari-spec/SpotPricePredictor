package com.spotpricepredictor.pro.domain.model

enum class BrokerDestination(val key: String, val label: String) {
    FRONTPAGE("FRONTPAGE", "FrontPage (frontpage.app)"),
    IN_APP_PAPER("IN_APP_PAPER", "In-App Pro Virtual Broker"),
    TRADINGVIEW_WEBHOOK("TRADINGVIEW_WEBHOOK", "TradingView / Custom Webhook"),
    SENSIBULL_VIRTUAL("SENSIBULL_VIRTUAL", "Sensibull Virtual Sandbox")
}

data class AutoTradeSettings(
    val isAutoTradeEnabled: Boolean = false,
    val brokerDestination: BrokerDestination = BrokerDestination.FRONTPAGE,
    val frontpageUserToken: String = "",
    val frontpageAccountId: String = "FP_VIRTUAL_01",
    val webhookUrl: String = "",
    val scanIntervalSeconds: Int = 15,
    val minReliabilityPct: Double = 80.0,
    val allowedActions: String = "ALL", // "ALL", "BUY_ONLY", "SELL_ONLY"
    val maxCapitalPerTrade: Double = 50000.0,
    val maxConcurrentPositions: Int = 5,
    val autoTrailingStopLoss: Boolean = true,
    val notifyOnExecution: Boolean = true
)

data class AutoTradeOrder(
    val orderId: String,
    val symbol: String,
    val companyName: String,
    val exchange: String = "NSE",
    val action: String, // "BUY", "SELL"
    val productType: String = "INTRADAY",
    val patternTrigger: String,
    val orderPrice: Double,
    val quantity: Int,
    val totalValue: Double,
    val stopLoss: Double,
    val targetPrice: Double,
    val status: String = "EXECUTED",
    val broker: String = "FrontPage",
    val brokerOrderId: String = "",
    val timestamp: String,
    val executionMessage: String
)

data class VirtualPosition(
    val positionId: String,
    val orderId: String,
    val symbol: String,
    val companyName: String,
    val action: String, // "BUY", "SELL"
    val quantity: Int,
    val avgPrice: Double,
    var currentPrice: Double,
    val stopLoss: Double,
    val targetPrice: Double,
    var pnl: Double,
    var pnlPercent: Double,
    val patternTrigger: String,
    val broker: String = "FrontPage",
    var status: String = "OPEN", // "OPEN", "CLOSED_TARGET", "CLOSED_SL", "MANUALLY_CLOSED"
    val entryTime: String,
    var exitTime: String? = null,
    var exitPrice: Double? = null
)

data class VirtualPortfolio(
    val totalCapital: Double = 1000000.0,
    val availableMargin: Double = 1000000.0,
    val usedMargin: Double = 0.0,
    val unrealizedPnl: Double = 0.0,
    val realizedPnl: Double = 0.0,
    val totalTrades: Int = 0,
    val winningTrades: Int = 0,
    val losingTrades: Int = 0,
    val winRatePct: Double = 0.0,
    val brokerName: String = "FrontPage Virtual Account",
    val positions: List<VirtualPosition> = emptyList(),
    val orderHistory: List<AutoTradeOrder> = emptyList()
)

data class ScanAndExecuteResponse(
    val stocksScannedCount: Int,
    val patternsDetectedCount: Int,
    val ordersExecuted: List<AutoTradeOrder> = emptyList(),
    val portfolio: VirtualPortfolio,
    val summary: String,
    val timestamp: String
)

data class ManualOrderRequest(
    val symbol: String,
    val action: String,
    val quantity: Int? = null,
    val productType: String = "INTRADAY",
    val price: Double? = null,
    val stopLoss: Double? = null,
    val targetPrice: Double? = null,
    val patternTrigger: String = "Manual User Order"
)

data class SquareOffRequest(
    val positionId: String? = null
)
