package com.spotpricepredictor.pro.data.api.models

import com.google.gson.annotations.SerializedName
import com.spotpricepredictor.pro.domain.model.*

data class AutoTradeSettingsDto(
    @SerializedName("is_auto_trade_enabled") val isAutoTradeEnabled: Boolean = false,
    @SerializedName("broker_destination") val brokerDestination: String = "ANVEST_PAPER",
    @SerializedName("anvest_user_token") val anvestUserToken: String? = "",
    @SerializedName("anvest_account_id") val anvestAccountId: String? = "ANVEST_PAPER_01",
    @SerializedName("anvest_webhook_url") val anvestWebhookUrl: String? = "https://anvest.in/paper-trading",
    @SerializedName("frontpage_user_token") val frontpageUserToken: String? = "",
    @SerializedName("frontpage_account_id") val frontpageAccountId: String? = "FP_VIRTUAL_01",
    @SerializedName("frontpage_webhook_url") val frontpageWebhookUrl: String? = "https://front.page/",
    @SerializedName("webhook_url") val webhookUrl: String? = "",
    @SerializedName("scan_interval_seconds") val scanIntervalSeconds: Int = 15,
    @SerializedName("min_reliability_pct") val minReliabilityPct: Double = 75.0,
    @SerializedName("allowed_actions") val allowedActions: String = "ALL",
    @SerializedName("max_capital_per_trade") val maxCapitalPerTrade: Double = 50000.0,
    @SerializedName("max_concurrent_positions") val maxConcurrentPositions: Int = 5,
    @SerializedName("auto_trailing_stop_loss") val autoTrailingStopLoss: Boolean = true,
    @SerializedName("notify_on_execution") val notifyOnExecution: Boolean = true,
    @SerializedName("enabled_strategies") val enabledStrategies: List<String> = emptyList()
) {
    fun toDomain(): AutoTradeSettings {
        val dest = when (brokerDestination.uppercase()) {
            "ANVEST_PAPER" -> BrokerDestination.ANVEST_PAPER
            "FRONTPAGE" -> BrokerDestination.FRONTPAGE
            "IN_APP_PAPER" -> BrokerDestination.IN_APP_PAPER
            "TRADINGVIEW_WEBHOOK" -> BrokerDestination.TRADINGVIEW_WEBHOOK
            "SENSIBULL_VIRTUAL" -> BrokerDestination.SENSIBULL_VIRTUAL
            else -> BrokerDestination.ANVEST_PAPER
        }
        return AutoTradeSettings(
            isAutoTradeEnabled = isAutoTradeEnabled,
            brokerDestination = dest,
            anvestUserToken = anvestUserToken ?: "",
            anvestAccountId = anvestAccountId ?: "ANVEST_PAPER_01",
            anvestWebhookUrl = anvestWebhookUrl ?: "https://anvest.in/paper-trading",
            frontpageUserToken = frontpageUserToken ?: "",
            frontpageAccountId = frontpageAccountId ?: "FP_VIRTUAL_01",
            frontpageWebhookUrl = frontpageWebhookUrl ?: "https://front.page/",
            webhookUrl = webhookUrl ?: "",
            scanIntervalSeconds = scanIntervalSeconds,
            minReliabilityPct = minReliabilityPct,
            allowedActions = allowedActions,
            maxCapitalPerTrade = maxCapitalPerTrade,
            maxConcurrentPositions = maxConcurrentPositions,
            autoTrailingStopLoss = autoTrailingStopLoss,
            notifyOnExecution = notifyOnExecution,
            enabledStrategies = if (enabledStrategies.isNotEmpty()) enabledStrategies else listOf(
                "CANDLESTICK_PATTERNS",
                "INTRADAY_TRI_CONFIRMATION",
                "GOLDEN_CROSS",
                "BOLLINGER_BREAKOUT",
                "CONSOLIDATION_BREAKOUT",
                "OI_BUILDUP"
            )
        )
    }
}

fun AutoTradeSettings.toDto(): AutoTradeSettingsDto = AutoTradeSettingsDto(
    isAutoTradeEnabled = isAutoTradeEnabled,
    brokerDestination = brokerDestination.key,
    anvestUserToken = anvestUserToken,
    anvestAccountId = anvestAccountId,
    anvestWebhookUrl = anvestWebhookUrl,
    frontpageUserToken = frontpageUserToken,
    frontpageAccountId = frontpageAccountId,
    frontpageWebhookUrl = frontpageWebhookUrl,
    webhookUrl = webhookUrl,
    scanIntervalSeconds = scanIntervalSeconds,
    minReliabilityPct = minReliabilityPct,
    allowedActions = allowedActions,
    maxCapitalPerTrade = maxCapitalPerTrade,
    maxConcurrentPositions = maxConcurrentPositions,
    autoTrailingStopLoss = autoTrailingStopLoss,
    notifyOnExecution = notifyOnExecution,
    enabledStrategies = enabledStrategies
)

data class AutoTradeOrderDto(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("exchange") val exchange: String = "NSE",
    @SerializedName("action") val action: String,
    @SerializedName("product_type") val productType: String = "INTRADAY",
    @SerializedName("pattern_trigger") val patternTrigger: String,
    @SerializedName("order_price") val orderPrice: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("total_value") val totalValue: Double,
    @SerializedName("stop_loss") val stopLoss: Double,
    @SerializedName("target_price") val targetPrice: Double,
    @SerializedName("status") val status: String = "EXECUTED",
    @SerializedName("broker") val broker: String = "FrontPage",
    @SerializedName("broker_order_id") val brokerOrderId: String = "",
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("execution_message") val executionMessage: String
) {
    fun toDomain(): AutoTradeOrder = AutoTradeOrder(
        orderId = orderId,
        symbol = symbol,
        companyName = companyName,
        exchange = exchange,
        action = action,
        productType = productType,
        patternTrigger = patternTrigger,
        orderPrice = orderPrice,
        quantity = quantity,
        totalValue = totalValue,
        stopLoss = stopLoss,
        targetPrice = targetPrice,
        status = status,
        broker = broker,
        brokerOrderId = brokerOrderId,
        timestamp = timestamp,
        executionMessage = executionMessage
    )
}

fun AutoTradeOrder.toDto(): AutoTradeOrderDto = AutoTradeOrderDto(
    orderId = orderId,
    symbol = symbol,
    companyName = companyName,
    exchange = exchange,
    action = action,
    productType = productType,
    patternTrigger = patternTrigger,
    orderPrice = orderPrice,
    quantity = quantity,
    totalValue = totalValue,
    stopLoss = stopLoss,
    targetPrice = targetPrice,
    status = status,
    broker = broker,
    brokerOrderId = brokerOrderId,
    timestamp = timestamp,
    executionMessage = executionMessage
)

data class VirtualPositionDto(
    @SerializedName("position_id") val positionId: String,
    @SerializedName("order_id") val orderId: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("action") val action: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("avg_price") val avgPrice: Double,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("stop_loss") val stopLoss: Double,
    @SerializedName("target_price") val targetPrice: Double,
    @SerializedName("pnl") val pnl: Double,
    @SerializedName("pnl_percent") val pnlPercent: Double,
    @SerializedName("pattern_trigger") val patternTrigger: String,
    @SerializedName("broker") val broker: String = "FrontPage",
    @SerializedName("status") val status: String = "OPEN",
    @SerializedName("entry_time") val entryTime: String,
    @SerializedName("exit_time") val exitTime: String? = null,
    @SerializedName("exit_price") val exitPrice: Double? = null
) {
    fun toDomain(): VirtualPosition = VirtualPosition(
        positionId = positionId,
        orderId = orderId,
        symbol = symbol,
        companyName = companyName,
        action = action,
        quantity = quantity,
        avgPrice = avgPrice,
        currentPrice = currentPrice,
        stopLoss = stopLoss,
        targetPrice = targetPrice,
        pnl = pnl,
        pnlPercent = pnlPercent,
        patternTrigger = patternTrigger,
        broker = broker,
        status = status,
        entryTime = entryTime,
        exitTime = exitTime,
        exitPrice = exitPrice
    )
}

data class VirtualPortfolioDto(
    @SerializedName("total_capital") val totalCapital: Double = 1000000.0,
    @SerializedName("available_margin") val availableMargin: Double = 1000000.0,
    @SerializedName("used_margin") val usedMargin: Double = 0.0,
    @SerializedName("unrealized_pnl") val unrealizedPnl: Double = 0.0,
    @SerializedName("realized_pnl") val realizedPnl: Double = 0.0,
    @SerializedName("total_trades") val totalTrades: Int = 0,
    @SerializedName("winning_trades") val winningTrades: Int = 0,
    @SerializedName("losing_trades") val losingTrades: Int = 0,
    @SerializedName("win_rate_pct") val winRatePct: Double = 0.0,
    @SerializedName("broker_name") val brokerName: String = "FrontPage Virtual Account",
    @SerializedName("positions") val positions: List<VirtualPositionDto> = emptyList(),
    @SerializedName("order_history") val orderHistory: List<AutoTradeOrderDto> = emptyList()
) {
    fun toDomain(): VirtualPortfolio = VirtualPortfolio(
        totalCapital = totalCapital,
        availableMargin = availableMargin,
        usedMargin = usedMargin,
        unrealizedPnl = unrealizedPnl,
        realizedPnl = realizedPnl,
        totalTrades = totalTrades,
        winningTrades = winningTrades,
        losingTrades = losingTrades,
        winRatePct = winRatePct,
        brokerName = brokerName,
        positions = positions.map { it.toDomain() },
        orderHistory = orderHistory.map { it.toDomain() }
    )
}

data class StrategyConditionItemDto(
    @SerializedName("strategy_id") val strategyId: String,
    @SerializedName("strategy_name") val strategyName: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("timeframe") val timeframe: String = "5 MIN",
    @SerializedName("condition_met") val conditionMet: Boolean = false,
    @SerializedName("action") val action: String = "WAIT",
    @SerializedName("trigger_condition") val triggerCondition: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("entry_price") val entryPrice: Double,
    @SerializedName("stop_loss") val stopLoss: Double,
    @SerializedName("target_price") val targetPrice: Double,
    @SerializedName("risk_reward") val riskReward: String = "1:2",
    @SerializedName("confidence_score") val confidenceScore: Double = 85.0,
    @SerializedName("execution_advice") val executionAdvice: String,
    @SerializedName("timestamp") val timestamp: String = ""
) {
    fun toDomain(): StrategyConditionItem = StrategyConditionItem(
        strategyId = strategyId,
        strategyName = strategyName,
        symbol = symbol,
        companyName = companyName,
        timeframe = timeframe,
        conditionMet = conditionMet,
        action = action,
        triggerCondition = triggerCondition,
        currentPrice = currentPrice,
        entryPrice = entryPrice,
        stopLoss = stopLoss,
        targetPrice = targetPrice,
        riskReward = riskReward,
        confidenceScore = confidenceScore,
        executionAdvice = executionAdvice,
        timestamp = timestamp
    )
}

data class ScanAndExecuteResponseDto(
    @SerializedName("stocks_scanned_count") val stocksScannedCount: Int,
    @SerializedName("patterns_detected_count") val patternsDetectedCount: Int,
    @SerializedName("strategy_conditions_count") val strategyConditionsCount: Int = 0,
    @SerializedName("orders_executed") val ordersExecuted: List<AutoTradeOrderDto> = emptyList(),
    @SerializedName("strategy_conditions_evaluated") val strategyConditionsEvaluated: List<StrategyConditionItemDto> = emptyList(),
    @SerializedName("portfolio") val portfolio: VirtualPortfolioDto,
    @SerializedName("summary") val summary: String,
    @SerializedName("timestamp") val timestamp: String
) {
    fun toDomain(): ScanAndExecuteResponse = ScanAndExecuteResponse(
        stocksScannedCount = stocksScannedCount,
        patternsDetectedCount = patternsDetectedCount,
        strategyConditionsCount = strategyConditionsCount,
        ordersExecuted = ordersExecuted.map { it.toDomain() },
        strategyConditionsEvaluated = strategyConditionsEvaluated.map { it.toDomain() },
        portfolio = portfolio.toDomain(),
        summary = summary,
        timestamp = timestamp
    )
}

data class ManualOrderRequestDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("action") val action: String,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("product_type") val productType: String = "INTRADAY",
    @SerializedName("price") val price: Double? = null,
    @SerializedName("stop_loss") val stopLoss: Double? = null,
    @SerializedName("target_price") val targetPrice: Double? = null,
    @SerializedName("pattern_trigger") val patternTrigger: String = "Manual User Order"
)

data class SquareOffRequestDto(
    @SerializedName("position_id") val positionId: String? = null
)

data class BrokerTestResponseDto(
    @SerializedName("broker") val broker: String,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int
)
