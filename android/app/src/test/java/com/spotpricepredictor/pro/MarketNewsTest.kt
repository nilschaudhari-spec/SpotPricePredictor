package com.spotpricepredictor.pro

import com.spotpricepredictor.pro.domain.model.DefaultMarketNews
import com.spotpricepredictor.pro.domain.model.NewsImpactSentiment
import com.spotpricepredictor.pro.domain.model.NewsSectionType
import org.junit.Assert.*
import org.junit.Test

class MarketNewsTest {

    @Test
    fun `default briefing contains items across all required sections`() {
        val briefing = DefaultMarketNews.getFallbackBriefing()
        assertNotNull(briefing)
        assertFalse(briefing.items.isEmpty())
        assertTrue(briefing.totalItems >= 10)

        val sections = briefing.items.map { it.section }.toSet()
        assertTrue("Must include national & global macro", sections.contains(NewsSectionType.NATIONAL_GLOBAL))
        assertTrue("Must include Indian indices and sectors", sections.contains(NewsSectionType.INDICES_SECTORS))
        assertTrue("Must include company specific news", sections.contains(NewsSectionType.COMPANY_SPECIFIC))
        assertTrue("Must include global commodities and FX", sections.contains(NewsSectionType.GLOBAL_COMMODITIES_FX))
        assertTrue("Must include upcoming events", sections.contains(NewsSectionType.UPCOMING_EVENTS))
    }

    @Test
    fun `each news item contains required impact analysis fields`() {
        val briefing = DefaultMarketNews.getFallbackBriefing()
        for (item in briefing.items) {
            assertTrue("Title must not be blank", item.title.isNotBlank())
            assertTrue("What happened must not be blank", item.whatHappened.isNotBlank())
            assertTrue("Why it matters must not be blank", item.whyItMatters.isNotBlank())
            assertTrue("Likely impact must not be blank", item.likelyImpact.isNotBlank())
            assertNotNull("Sentiment must be defined", item.sentiment)
            assertTrue("Source must not be blank", item.source.isNotBlank())
        }
    }

    @Test
    fun `briefing contains diverse impact sentiments`() {
        val briefing = DefaultMarketNews.getFallbackBriefing()
        val sentiments = briefing.items.map { it.sentiment }.toSet()
        assertTrue("Should include positive impact items", sentiments.contains(NewsImpactSentiment.POSITIVE))
        assertTrue("Should include negative or mixed items", sentiments.contains(NewsImpactSentiment.NEGATIVE) || sentiments.contains(NewsImpactSentiment.MIXED))
    }

    @Test
    fun `briefing contains informative disclaimer`() {
        val briefing = DefaultMarketNews.getFallbackBriefing()
        assertTrue("Must contain disclaimer", briefing.disclaimer.contains("informational", ignoreCase = true))
    }
}
