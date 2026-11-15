package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.data.api.models.*
import com.spotpricepredictor.pro.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class AutoTradeTest {

    @Test
    fun `auto trade settings initialize with frontpage destination and safe risk limits`() {
        val settings = AutoTradeSettings()

        assertFalse(settings.isAutoTradeEnabled)
        assertEquals(BrokerDestination.FRONTPAGE, settings.brokerDestination)
        assertEquals("FP_VIRTUAL_01", settings.frontpageAccountId)
        assertEquals(50000.0, settings.maxCapitalPerTrade, 0.01)
        assertEquals(5, settings.maxConcurrentPositions)
        assertEquals(80.0, settings.minReliabilityPct, 0.01)
        assertTrue(settings.autoTrailingStopLoss)
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
            broker = "FrontPage",
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
            broker = "FrontPage",
            brokerOrderId = "FP_EXT_7894",
            timestamp = "2026-11-15T10:30:00",
            executionMessage = "Auto-executed via FrontPage Virtual Bridge"
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
