package com.spotpricepredictor.pro.domain.model

enum class NewsImpactSentiment(val label: String) {
    POSITIVE("Positive"),
    NEGATIVE("Negative"),
    MIXED("Mixed"),
    UNCERTAIN("Uncertain")
}

enum class NewsSectionType(val key: String, val label: String) {
    ALL("ALL", "All News"),
    NATIONAL_GLOBAL("NATIONAL_GLOBAL", "Macro & Global"),
    INDICES_SECTORS("INDICES_SECTORS", "Nifty & Sectors"),
    COMPANY_SPECIFIC("COMPANY_SPECIFIC", "Company News"),
    GLOBAL_COMMODITIES_FX("GLOBAL_COMMODITIES_FX", "Crude, Gold & FX"),
    UPCOMING_EVENTS("UPCOMING_EVENTS", "Upcoming Events")
}

data class MarketNewsItem(
    val id: String,
    val section: NewsSectionType,
    val title: String,
    val symbols: List<String>,
    val whatHappened: String,
    val whyItMatters: String,
    val likelyImpact: String,
    val sentiment: NewsImpactSentiment,
    val source: String,
    val sourceUrl: String,
    val timestamp: String
)

data class MarketNewsBriefing(
    val title: String,
    val lastUpdated: String,
    val macroSummary: String,
    val totalItems: Int,
    val items: List<MarketNewsItem>,
    val disclaimer: String
)
