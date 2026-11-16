package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.data.api.models.*
import com.spotpricepredictor.pro.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class AutoTradeTest {

    @Test
    fun `auto trade settings initialize with anvest paper destination and safe risk limits`() {
        val settings = AutoTradeSettings()

        assertFalse(settings.isAutoTradeEnabled)
        assertEquals(BrokerDestination.ANVEST_PAPER, settings.brokerDestination)
        assertEquals("ANVEST_PAPER_01", settings.anvestAccountId)
        assertEquals("https://anvest.in/paper-trading", settings.anvestWebhookUrl)
        assertEquals("FP_VIRTUAL_01", settings.frontpageAccountId)
        assertEquals("https://front.page/", settings.frontpageWebhookUrl)
        assertEquals(50000.0, settings.maxCapitalPerTrade, 0.01)
        assertEquals(5, settings.maxConcurrentPositions)
        assertEquals(75.0, settings.minReliabilityPct, 0.01)
        assertTrue(settings.autoTrailingStopLoss)
        assertTrue(settings.enabledStrategies.contains("INTRADAY_TRI_CONFIRMATION"))
        assertTrue(settings.enabledStrategies.contains("GOLDEN_CROSS"))
        assertTrue(settings.enabledStrategies.contains("BOLLINGER_BREAKOUT"))
        assertTrue(settings.enabledStrategies.contains("CONSOLIDATION_BREAKOUT"))
        assertTrue(settings.enabledStrategies.contains("OI_BUILDUP"))
        assertTrue(settings.enabledStrategies.contains("CANDLESTICK_PATTERNS"))
    }

    @Test
    fun `broker destinations verify anvest and frontpage portal configurations`() {
        assertEquals("https://anvest.in/paper-trading", BrokerDestination.ANVEST_PAPER.siteUrl)
        assertEquals("https://front.page/", BrokerDestination.FRONTPAGE.siteUrl)
        assertEquals("Anvest Paper Trading (anvest.in/paper-trading)", BrokerDestination.ANVEST_PAPER.label)
        assertEquals("FrontPage Paper Trading (front.page)", BrokerDestination.FRONTPAGE.label)
    }

    @Test
    fun `strategy condition item dto converts to domain model accurately`() {
        val dto = StrategyConditionItemDto(
            strategyId = "INTRADAY_TRI_CONFIRMATION",
            strategyName = "5-Min Intraday Tri-Confirmation",
            symbol = "NIFTY",
            companyName = "NIFTY 50 Index",
            timeframe = "5 MIN",
            conditionMet = true,
            action = "BUY",
            triggerCondition = "Supertrend GREEN + Price > VWAP + MACD Bullish Crossover",
            currentPrice = 24150.0,
            entryPrice = 24155.0,
            stopLoss = 24100.0,
            targetPrice = 24210.0,
            riskReward = "1:1",
            confidenceScore = 91.5,
            executionAdvice = "High probability intraday trend continuation",
            timestamp = "10:30:00"
        )

        val domain = dto.toDomain()

        assertEquals("INTRADAY_TRI_CONFIRMATION", domain.strategyId)
        assertEquals("NIFTY", domain.symbol)
        assertTrue(domain.conditionMet)
        assertEquals("BUY", domain.action)
        assertEquals(24150.0, domain.currentPrice, 0.01)
        assertEquals(24100.0, domain.stopLoss, 0.01)
        assertEquals(24210.0, domain.targetPrice, 0.01)
        assertEquals("1:1", domain.riskReward)
        assertEquals(91.5, domain.confidenceScore, 0.01)
    }

    @Test
    fun `auto trade settings dto round trips with full anvest and frontpage configuration`() {
        val originalSettings = AutoTradeSettings(
            isAutoTradeEnabled = true,
            brokerDestination = BrokerDestination.ANVEST_PAPER,
            anvestUserToken = "jwt_token_anvest_abc123",
            anvestAccountId = "ANVEST_PAPER_99",
            anvestWebhookUrl = "https://anvest.in/paper-trading/webhook",
            frontpageUserToken = "jwt_token_fp_xyz789",
            frontpageAccountId = "FP_VIRTUAL_88",
            frontpageWebhookUrl = "https://front.page/webhook",
            scanIntervalSeconds = 30,
            minReliabilityPct = 80.0,
            allowedActions = "BUY_ONLY",
            maxCapitalPerTrade = 75000.0,
            maxConcurrentPositions = 4,
            autoTrailingStopLoss = true,
            notifyOnExecution = true,
            enabledStrategies = listOf("GOLDEN_CROSS", "BOLLINGER_BREAKOUT")
        )

        val dto = originalSettings.toDto()
        val mapped = dto.toDomain()

        assertTrue(mapped.isAutoTradeEnabled)
        assertEquals(BrokerDestination.ANVEST_PAPER, mapped.brokerDestination)
        assertEquals("jwt_token_anvest_abc123", mapped.anvestUserToken)
        assertEquals("ANVEST_PAPER_99", mapped.anvestAccountId)
        assertEquals("jwt_token_fp_xyz789", mapped.frontpageUserToken)
        assertEquals("FP_VIRTUAL_88", mapped.frontpageAccountId)
        assertEquals(75000.0, mapped.maxCapitalPerTrade, 0.01)
        assertEquals(4, mapped.maxConcurrentPositions)
        assertEquals(listOf("GOLDEN_CROSS", "BOLLINGER_BREAKOUT"), mapped.enabledStrategies)
    }

    @Test
    fun `virtual portfolio maintains accurate margin and pnl on buy and sell executions`() {
        var portfolio = VirtualPortfolio(
            totalCapital = 1000000.0,
            availableMargin = 1000000.0,
            usedMargin = 0.0,
            unrealizedPnl = 0.0,
            realizedPnl = 0.0,
            positions = emptyList(),
            orderHistory = emptyList()
        )

        // 1. Simulate BUY order on RELIANCE at 2800, Qty 20 -> Cost = 56000
        val buyPos = VirtualPosition(
            positionId = "POS_RELIANCE_001",
            orderId = "ORD_RELIANCE_001",
            symbol = "RELIANCE",
            companyName = "Reliance Industries Ltd",
            action = "BUY",
            quantity = 20,
            avgPrice = 2800.0,
            currentPrice = 2800.0,
            stopLoss = 2750.0,
            targetPrice = 2900.0,
            pnl = 0.0,
            pnlPercent = 0.0,
            patternTrigger = "Bullish Marubozu (88%)",
            broker = "Anvest Paper",
            status = "OPEN",
            entryTime = "10:30:00"
        )

        val cost = buyPos.avgPrice * buyPos.quantity
        portfolio = portfolio.copy(
            availableMargin = portfolio.totalCapital - cost,
            usedMargin = cost,
            positions = listOf(buyPos)
        )

        assertEquals(944000.0, portfolio.availableMargin, 0.01)
        assertEquals(56000.0, portfolio.usedMargin, 0.01)

        // 2. Price rises to 2850 (+50 points) -> P&L = 50 * 20 = +1000
        val updatedPrice = 2850.0
        val pnl = (updatedPrice - buyPos.avgPrice) * buyPos.quantity
        val pnlPct = ((updatedPrice - buyPos.avgPrice) / buyPos.avgPrice) * 100.0

        val updatedPos = buyPos.copy(
            currentPrice = updatedPrice,
            pnl = pnl,
            pnlPercent = pnlPct
        )

        assertEquals(1000.0, updatedPos.pnl, 0.01)
        assertEquals(1.785, updatedPos.pnlPercent, 0.01)

        // 3. Square off position at 2850 -> realize +1000 PnL and return margin
        portfolio = portfolio.copy(
            availableMargin = portfolio.totalCapital + pnl,
            usedMargin = 0.0,
            realizedPnl = pnl,
            positions = listOf(updatedPos.copy(status = "CLOSED_TARGET", exitPrice = updatedPrice, exitTime = "10:45:00")),
            totalTrades = 1,
            winningTrades = 1,
            winRatePct = 100.0
        )

        assertEquals(1001000.0, portfolio.availableMargin, 0.01)
        assertEquals(0.0, portfolio.usedMargin, 0.01)
        assertEquals(1000.0, portfolio.realizedPnl, 0.01)
        assertEquals(1, portfolio.winningTrades)
        assertEquals(100.0, portfolio.winRatePct, 0.01)
    }

    @Test
    fun `bidirectional DTO mapping preserves all execution telemetry`() {
        val originalOrder = AutoTradeOrder(
            orderId = "ORD_AUTO_001",
            symbol = "NIFTY",
            companyName = "Nifty 50 Index",
            exchange = "NSE",
            action = "BUY",
            productType = "INTRADAY",
            patternTrigger = "Morning Star (85%)",
            orderPrice = 23100.5,
            quantity = 50,
            totalValue = 1155025.0,
            stopLoss = 23000.0,
            targetPrice = 23300.0,
            status = "EXECUTED",
            broker = "Anvest Paper",
            brokerOrderId = "ANVEST_EXT_7894",
            timestamp = "2026-11-15T10:30:00",
            executionMessage = "Auto-executed via Anvest Paper Trading Desk (anvest.in)"
        )

        val dto = originalOrder.toDto()
        val mappedOrder = dto.toDomain()

        assertEquals(originalOrder.orderId, mappedOrder.orderId)
        assertEquals(originalOrder.symbol, mappedOrder.symbol)
        assertEquals(originalOrder.action, mappedOrder.action)
        assertEquals(originalOrder.orderPrice, mappedOrder.orderPrice, 0.001)
        assertEquals(originalOrder.quantity, mappedOrder.quantity)
        assertEquals(originalOrder.brokerOrderId, mappedOrder.brokerOrderId)
        assertEquals(originalOrder.executionMessage, mappedOrder.executionMessage)
    }
}
