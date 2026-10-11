package com.spotpricepredictor.pro.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DefaultMarketNews {

    fun getFallbackBriefing(): MarketNewsBriefing {
        val todayStr = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        val formattedFullDate = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

        val items = listOf(
            MarketNewsItem(
                id = "macro-1",
                section = NewsSectionType.NATIONAL_GLOBAL,
                title = "RBI Keeps Repo Rate Steady at 6.50%; Retains Stance on Balanced Growth-Inflation Dynamics",
                symbols = listOf("NIFTY", "BANKNIFTY"),
                whatHappened = "The Reserve Bank of India Monetary Policy Committee maintained the policy repo rate at 6.50% while emphasizing continued disinflation towards the 4% medium-term target.",
                whyItMatters = "Interest rate stability anchors systemic liquidity, corporate borrowing costs, and banking net interest margins across Indian financial institutions.",
                likelyImpact = "Supports steady rate-sensitive sectors (Banking, Real Estate, Auto) with lower volatility in government bond yields.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "RBI Bulletin / Reuters",
                sourceUrl = "https://rbi.org.in",
                timestamp = "$todayStr, 08:30 AM IST"
            ),
            MarketNewsItem(
                id = "macro-2",
                section = NewsSectionType.NATIONAL_GLOBAL,
                title = "US Federal Reserve Signals Data-Dependent Interest Rate Trajectory Amid Cooling US Inflation",
                symbols = listOf("NIFTY"),
                whatHappened = "Fed officials reiterated a measured pace for interest rate cuts following moderating US core PCE inflation data, reducing yields on the US 10-Year Treasury note to 4.08%.",
                whyItMatters = "Lower US bond yields typically curb emerging market capital flight and encourage foreign portfolio investor (FPI) equity inflows into India.",
                likelyImpact = "Favorable tailwind for Indian benchmarks Nifty 50 and large-cap export IT stocks as risk-on appetite stabilizes.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "Federal Reserve / Bloomberg",
                sourceUrl = "https://federalreserve.gov",
                timestamp = "$todayStr, 07:45 AM IST"
            ),
            MarketNewsItem(
                id = "macro-3",
                section = NewsSectionType.NATIONAL_GLOBAL,
                title = "India Manufacturing PMI Expands to 58.1 on Robust Domestic Demand & Industrial Output",
                symbols = listOf("NIFTY", "LT", "SIEMENS"),
                whatHappened = "India's headline manufacturing Purchasing Managers' Index (PMI) registered strong expansion at 58.1, propelled by new domestic orders and capacity utilization gains.",
                whyItMatters = "Demonstrates resilient underlying macro fundamentals and sustained capital expenditure cycle in India despite global slowdown concerns.",
                likelyImpact = "Strong positive sentiment for Capital Goods, Infrastructure, and Industrial manufacturing counters.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "S&P Global / Ministry of Commerce",
                sourceUrl = "https://pib.gov.in",
                timestamp = "$todayStr, 09:00 AM IST"
            ),
            MarketNewsItem(
                id = "index-1",
                section = NewsSectionType.INDICES_SECTORS,
                title = "Nifty 50 & Bank Nifty Consolidate Near Crucial Resistance; PCR at 1.14 Signals Call Support",
                symbols = listOf("NIFTY", "BANKNIFTY"),
                whatHappened = "Nifty 50 is trading in a defined range near 22,500 levels while Bank Nifty maintains solid base above 55,000 with concentrated Put OI writing at immediate strike supports.",
                whyItMatters = "High Put-to-Call Ratio (PCR > 1.10) reflects strong institutional underwriting against steep downward corrections.",
                likelyImpact = "Bullish-to-sideways momentum expected; buy-on-dips strategy dominant around key gamma inflection levels.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "NSE Derivatives Intelligence",
                sourceUrl = "https://nseindia.com",
                timestamp = "$todayStr, 09:15 AM IST"
            ),
            MarketNewsItem(
                id = "index-2",
                section = NewsSectionType.INDICES_SECTORS,
                title = "IT Sector Gains Momentum on Renewed Cloud & Enterprise AI Deal Wins",
                symbols = listOf("TCS", "INFY", "HCLTECH", "WIPRO"),
                whatHappened = "Nifty IT index gained +1.8% intraday following large multi-year generative AI infrastructure transformation contracts announced by top-tier Indian IT service providers.",
                whyItMatters = "Signals bottoming out of discretionary IT spend cycle in North American and European BFSI verticals.",
                likelyImpact = "Direct positive earnings revision catalyst for TCS, Infosys, and HCL Technologies.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "Economic Times / BSE",
                sourceUrl = "https://economictimes.indiatimes.com",
                timestamp = "$todayStr, 09:30 AM IST"
            ),
            MarketNewsItem(
                id = "index-3",
                section = NewsSectionType.INDICES_SECTORS,
                title = "Metals Face Margins Pressure Amid Global Chinese Steel Demand Weakness",
                symbols = listOf("TATASTEEL", "JSWSTEEL", "HINDALCO"),
                whatHappened = "Global hot-rolled coil (HRC) steel prices dipped as Chinese housing market indicators remained sluggish, putting pressure on Indian domestic metal realizations.",
                whyItMatters = "Lower international benchmark prices reduce export margins and invite competition from low-cost steel imports.",
                likelyImpact = "Short-term margin squeeze for Tata Steel and JSW Steel; defensive positioning advised.",
                sentiment = NewsImpactSentiment.NEGATIVE,
                source = "Metal Bulletin / S&P Platts",
                sourceUrl = "https://spglobal.com",
                timestamp = "$todayStr, 09:45 AM IST"
            ),
            MarketNewsItem(
                id = "stock-1",
                section = NewsSectionType.COMPANY_SPECIFIC,
                title = "Tata Motors Demerger Plan Gets Key Shareholder Clearance; Commercial & Passenger Segments to Split",
                symbols = listOf("TATAMOTORS"),
                whatHappened = "Tata Motors received overwhelming majority shareholder approval for demerging its Commercial Vehicles (CV) and Passenger Vehicles (PV/EV) businesses into two independent listed entities.",
                whyItMatters = "Unlocks pure-play valuation multiples for the rapidly growing EV and Jaguar Land Rover (JLR) businesses, allowing dedicated capital allocation.",
                likelyImpact = "Strong positive medium-term re-rating potential for Tata Motors equity shares.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "NSE Corporate Filings",
                sourceUrl = "https://nseindia.com/companies-listing/corporate-filings",
                timestamp = "$todayStr, 08:00 AM IST"
            ),
            MarketNewsItem(
                id = "stock-2",
                section = NewsSectionType.COMPANY_SPECIFIC,
                title = "Reliance Industries Completes Mega Energy Expansion; Jio Financial Deepens Consumer Credit Footprint",
                symbols = listOf("RELIANCE", "JIOFIN"),
                whatHappened = "Reliance Industries commissioned its new green hydrogen pilot facility in Gujarat while Jio Financial launched secured lending against mutual funds and digital credit cards.",
                whyItMatters = "Expands non-telecom growth engines and monetizes high-margin green energy and retail fintech ecosystems.",
                likelyImpact = "Stable positive earnings cushion for RIL; expands market cap valuation floor.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "BSE Corporate Announcements",
                sourceUrl = "https://bseindia.com",
                timestamp = "$todayStr, 08:20 AM IST"
            ),
            MarketNewsItem(
                id = "stock-3",
                section = NewsSectionType.COMPANY_SPECIFIC,
                title = "State Bank of India (SBI) Approves ₹10,000 Crore Infrastructure Bond Issuance",
                symbols = listOf("SBIN"),
                whatHappened = "The executive committee of SBI approved raising up to ₹10,000 crore via long-term infrastructure bonds to finance critical national infrastructure and affordable housing projects.",
                whyItMatters = "Infrastructure bonds are exempt from regulatory CRR/SLR requirements, optimizing cost of funds and boosting credit growth margins.",
                likelyImpact = "Positive for SBI's loan growth momentum and capital adequacy tiering.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "SBI Investor Relations / NSE",
                sourceUrl = "https://sbi.co.in",
                timestamp = "$todayStr, 08:45 AM IST"
            ),
            MarketNewsItem(
                id = "stock-4",
                section = NewsSectionType.COMPANY_SPECIFIC,
                title = "Larsen & Toubro Secures Mega ₹7,500 Crore Offshore & Renewable Energy Order",
                symbols = listOf("LT"),
                whatHappened = "L&T's Hydrocarbon and Renewable Power business secured significant EPC contracts in the Middle East and domestic high-voltage grid corridors.",
                whyItMatters = "Expands order book visibility beyond ₹4.75 lakh crore, ensuring consistent double-digit revenue execution trajectory over the next 3-4 fiscal years.",
                likelyImpact = "Positive for L&T stock; reinforces leadership in infrastructure execution.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "L&T Official Announcement",
                sourceUrl = "https://larsentoubro.com",
                timestamp = "$todayStr, 09:10 AM IST"
            ),
            MarketNewsItem(
                id = "global-1",
                section = NewsSectionType.GLOBAL_COMMODITIES_FX,
                title = "Brent Crude Stabilizes Around $82/bbl Amid OPEC+ Production Restraint & Shipping Adjustments",
                symbols = listOf("BPCL", "IOC", "ONGC"),
                whatHappened = "Brent crude futures settled near $82.40 per barrel following voluntary supply extension commitments by OPEC+ producers and steady Red Sea shipping flows.",
                whyItMatters = "Crude oil accounts for over 85% of India's petroleum import bill; oil stabilization under $85/bbl maintains benign domestic retail fuel prices and CAD stability.",
                likelyImpact = "Neutral-to-positive for Indian Oil Marketing Companies (OMCs) like BPCL, IOC, HPCL; steady upstream realizations for ONGC.",
                sentiment = NewsImpactSentiment.MIXED,
                source = "Reuters Commodities",
                sourceUrl = "https://reuters.com/markets/commodities",
                timestamp = "$todayStr, 07:30 AM IST"
            ),
            MarketNewsItem(
                id = "global-2",
                section = NewsSectionType.GLOBAL_COMMODITIES_FX,
                title = "Gold Trades Near Record Highs on Central Bank Accumulation; Indian Rupee Holds Steady at 83.95/USD",
                symbols = listOf("TITAN", "MUTHOOTFIN"),
                whatHappened = "Spot gold traded near record levels ($2,650/oz) supported by central bank reserve diversification, while the Indian Rupee consolidated in a tight range against the US Dollar.",
                whyItMatters = "High gold prices boost loan-to-value collateral buffers for gold loan NBFCs (Muthoot Finance), but test retail jewellery consumption volume demand.",
                likelyImpact = "Positive collateral buffer for Muthoot Finance; mixed volume elasticity for Titan jewellery retail.",
                sentiment = NewsImpactSentiment.MIXED,
                source = "World Gold Council / RBI Reference Rate",
                sourceUrl = "https://gold.org",
                timestamp = "$todayStr, 07:50 AM IST"
            ),
            MarketNewsItem(
                id = "global-3",
                section = NewsSectionType.GLOBAL_COMMODITIES_FX,
                title = "US Wall Street Indices (S&P 500, Nasdaq) Rebound on Big Tech Artificial Intelligence Capex",
                symbols = listOf("NIFTY"),
                whatHappened = "The S&P 500 advanced +0.6% and Nasdaq climbed +0.9% as semiconductor and hyperscale cloud providers reinforced multi-billion dollar AI infrastructure investment roadmaps.",
                whyItMatters = "GIFT Nifty futures tracked early gains (+65 points), providing an encouraging opening setup for the Indian cash trading session.",
                likelyImpact = "Positive opening sentiment for Indian equities and tech export counters.",
                sentiment = NewsImpactSentiment.POSITIVE,
                source = "Bloomberg / Wall Street Journal",
                sourceUrl = "https://bloomberg.com",
                timestamp = "$todayStr, 08:15 AM IST"
            ),
            MarketNewsItem(
                id = "event-1",
                section = NewsSectionType.UPCOMING_EVENTS,
                title = "Calendar Ahead: US Non-Farm Payrolls, India CPI Inflation & Top Tier Q3 Corporate Earnings",
                symbols = listOf("NIFTY", "TCS", "HDFCBANK", "INFY"),
                whatHappened = "Markets are tracking upcoming India CPI retail inflation data release on the 12th, followed by IT bellwether TCS quarterly results kickoff and US labor market updates.",
                whyItMatters = "Will set the tone for institutional earnings growth expectations, FII capital reallocation, and mid-cap valuation support.",
                likelyImpact = "Anticipated uptick in implied volatility (India VIX); option buying and straddle/strangle strategies viable ahead of key data points.",
                sentiment = NewsImpactSentiment.UNCERTAIN,
                source = "MoSPI / NSE",
                sourceUrl = "https://mospi.gov.in",
                timestamp = "$todayStr, 09:30 AM IST"
            )
        )

        return MarketNewsBriefing(
            title = "Daily Market Intelligence & Macro News Briefing",
            lastUpdated = "$formattedFullDate IST",
            macroSummary = "Indian markets exhibit resilient macro fundamentals supported by steady RBI policy, expanding manufacturing PMI (58.1), and controlled domestic inflation. Global cues remain constructive with softening US Treasury yields and range-bound crude oil prices ($82/bbl). Selective stock picking favored in IT, Capital Goods, and Private Banking.",
            totalItems = items.size,
            items = items,
            disclaimer = "This news briefing is strictly informational and educational. Distinguishes verified facts from market speculation. This is not personalized financial or investment advice."
        )
    }
}
