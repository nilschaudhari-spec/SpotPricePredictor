package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class FeaturesEnhancementTest {

    @Test
    fun `corporate action item maintains full details integrity`() {
        val dividendAction = CorporateActionItem(
            id = "CA_TCS_DIV",
            symbol = "TCS",
            companyName = "Tata Consultancy Services Ltd",
            actionType = "DIVIDEND",
            exDate = "18-Nov-2026",
            recordDate = "20-Nov-2026",
            details = "Second Interim Dividend of ₹28.00 per share (2800%)",
            amount = 28.0,
            ratio = null,
            impact = "High Cash Payout"
        )

        assertEquals("TCS", dividendAction.symbol)
        assertEquals("DIVIDEND", dividendAction.actionType)
        assertEquals(28.0, dividendAction.amount ?: 0.0, 0.01)
        assertEquals("18-Nov-2026", dividendAction.exDate)
        assertEquals("20-Nov-2026", dividendAction.recordDate)
        assertTrue(dividendAction.details.contains("₹28.00"))

        val bonusAction = CorporateActionItem(
            id = "CA_RELIANCE_BONUS",
            symbol = "RELIANCE",
            companyName = "Reliance Industries Limited",
            actionType = "BONUS",
            exDate = "28-Oct-2026",
            recordDate = "29-Oct-2026",
            details = "Bonus Issue in the ratio of 1:1",
            amount = null,
            ratio = "1:1",
            impact = "Liquidity Expansion"
        )

        assertEquals("BONUS", bonusAction.actionType)
        assertEquals("1:1", bonusAction.ratio)
        assertNull(bonusAction.amount)
    }

    @Test
    fun `option chain data updates spot price and strike LTP with delta drift`() {
        val baseSpot = 23000.0
        val strikes = listOf(
            StrikeRowData(
                strikePrice = 23000.0,
                call = OptionLeg(
                    ltp = 150.0,
                    volume = 50000L,
                    oi = 200000L,
                    changeInOi = 5000L,
                    iv = 13.5,
                    greeks = GreekMetrics(delta = 0.52, gamma = 0.002, theta = -12.0, vega = 18.0),
                    buildup = "Long Buildup"
                ),
                put = OptionLeg(
                    ltp = 140.0,
                    volume = 45000L,
                    oi = 250000L,
                    changeInOi = 8000L,
                    iv = 13.8,
                    greeks = GreekMetrics(delta = -0.48, gamma = 0.002, theta = -11.5, vega = 17.5),
                    buildup = "Short Buildup"
                )
            )
        )

        val optionChain = OptionChainData(
            symbol = "NIFTY",
            spotPrice = baseSpot,
            pcr = 1.25,
            maxPain = 23000.0,
            immediateSupport = 22800.0,
            immediateResistance = 23200.0,
            strikes = strikes
        )

        // Simulate micro-tick drift: Spot moves up by +10.0 points
        val spotDelta = 10.0
        val newSpot = baseSpot + spotDelta
        val updatedStrikes = optionChain.strikes.map { strike ->
            val newCallLtp = strike.call.ltp + spotDelta * strike.call.greeks.delta
            val newPutLtp = strike.put.ltp - spotDelta * kotlin.math.abs(strike.put.greeks.delta)
            strike.copy(
                call = strike.call.copy(ltp = newCallLtp),
                put = strike.put.copy(ltp = newPutLtp)
            )
        }

        val updatedChain = optionChain.copy(spotPrice = newSpot, strikes = updatedStrikes)

        assertEquals(23010.0, updatedChain.spotPrice, 0.001)
        // Call LTP should increase by 10 * 0.52 = 5.2 points -> 155.2
        assertEquals(155.2, updatedChain.strikes[0].call.ltp, 0.01)
        // Put LTP should decrease by 10 * 0.48 = 4.8 points -> 135.2
        assertEquals(135.2, updatedChain.strikes[0].put.ltp, 0.01)
    }

    @Test
    fun `market news item contains full reading payload and valid sentiment`() {
        val newsItem = MarketNewsItem(
            id = "news-1",
            section = NewsSectionType.COMPANY_SPECIFIC,
            title = "Reliance Industries Board Recommends 1:1 Bonus Issue",
            symbols = listOf("RELIANCE", "NIFTY"),
            whatHappened = "Board of Directors approved issue of bonus shares in 1:1 ratio to reward long-term equity shareholders.",
            whyItMatters = "Doubles liquidity and makes counter accessible to broader base of retail participants.",
            likelyImpact = "Strong bullish impetus on index heavyweight; expected to anchor benchmark Nifty above 23,000.",
            sentiment = NewsImpactSentiment.POSITIVE,
            source = "The Economic Times",
            sourceUrl = "https://economictimes.indiatimes.com",
            timestamp = "08-Nov-2026, 02:30 PM IST"
        )

        assertEquals(NewsImpactSentiment.POSITIVE, newsItem.sentiment)
        assertEquals("The Economic Times", newsItem.source)
        assertTrue(newsItem.symbols.contains("RELIANCE"))
        assertTrue(newsItem.whatHappened.length > 20)
        assertTrue(newsItem.whyItMatters.length > 20)
        assertTrue(newsItem.likelyImpact.length > 20)
    }
}
