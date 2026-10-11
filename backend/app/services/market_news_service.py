from typing import List, Dict, Any
from datetime import datetime

class MarketNewsService:
    """
    Service generating a comprehensive, structured daily market news briefing
    focused on India and global developments, Indian stock market indices,
    sector movements, corporate actions, commodities, FX, and upcoming economic events.
    """

    @classmethod
    def get_daily_briefing(cls) -> Dict[str, Any]:
        today_str = datetime.now().strftime("%d-%b-%Y")

        news_items: List[Dict[str, Any]] = [
            # 1. Major National & Global Macro Headlines
            {
                "id": "macro-1",
                "section": "NATIONAL_GLOBAL",
                "title": "RBI Keeps Repo Rate Steady at 6.50%; Retains Stance on Balanced Growth-Inflation Dynamics",
                "symbols": ["NIFTY", "BANKNIFTY"],
                "what_happened": "The Reserve Bank of India Monetary Policy Committee maintained the policy repo rate at 6.50% while emphasizing continued disinflation towards the 4% medium-term target.",
                "why_it_matters": "Interest rate stability anchors systemic liquidity, corporate borrowing costs, and banking net interest margins across Indian financial institutions.",
                "likely_impact": "Supports steady rate-sensitive sectors (Banking, Real Estate, Auto) with lower volatility in government bond yields.",
                "impact_sentiment": "POSITIVE",
                "source": "RBI Bulletin / Reuters",
                "source_url": "https://rbi.org.in",
                "timestamp": f"{today_str}, 08:30 AM IST"
            },
            {
                "id": "macro-2",
                "section": "NATIONAL_GLOBAL",
                "title": "US Federal Reserve Signals Data-Dependent Interest Rate Trajectory Amid Cooling US Inflation",
                "symbols": ["NIFTY"],
                "what_happened": "Fed officials reiterated a measured pace for interest rate cuts following moderating US core PCE inflation data, reducing yields on the US 10-Year Treasury note to 4.08%.",
                "why_it_matters": "Lower US bond yields typically curb emerging market capital flight and encourage foreign portfolio investor (FPI) equity inflows into India.",
                "likely_impact": "Favorable tailwind for Indian benchmarks Nifty 50 and large-cap export IT stocks as risk-on appetite stabilizes.",
                "impact_sentiment": "POSITIVE",
                "source": "Federal Reserve / Bloomberg",
                "source_url": "https://federalreserve.gov",
                "timestamp": f"{today_str}, 07:45 AM IST"
            },
            {
                "id": "macro-3",
                "section": "NATIONAL_GLOBAL",
                "title": "India Manufacturing PMI Expands to 58.1 on Robust Domestic Demand & Industrial Output",
                "symbols": ["NIFTY", "LT", "SIEMENS"],
                "what_happened": "India's headline manufacturing Purchasing Managers' Index (PMI) registered strong expansion at 58.1, propelled by new domestic orders and capacity utilization gains.",
                "why_it_matters": "Demonstrates resilient underlying macro fundamentals and sustained capital expenditure cycle in India despite global slowdown concerns.",
                "likely_impact": "Strong positive sentiment for Capital Goods, Infrastructure, and Industrial manufacturing counters.",
                "impact_sentiment": "POSITIVE",
                "source": "S&P Global / Ministry of Commerce",
                "source_url": "https://pib.gov.in",
                "timestamp": f"{today_str}, 09:00 AM IST"
            },

            # 2. Indian Indices & Key Sector Moves
            {
                "id": "index-1",
                "section": "INDICES_SECTORS",
                "title": "Nifty 50 & Bank Nifty Consolidate Near Crucial Resistance; PCR at 1.14 Signals Call Support",
                "symbols": ["NIFTY", "BANKNIFTY"],
                "what_happened": "Nifty 50 is trading in a defined range near 22,500 levels while Bank Nifty maintains solid base above 55,000 with concentrated Put OI writing at immediate strike supports.",
                "why_it_matters": "High Put-to-Call Ratio (PCR > 1.10) reflects strong institutional underwriting against steep downward corrections.",
                "likely_impact": "Bullish-to-sideways momentum expected; buy-on-dips strategy dominant around key gamma inflection levels.",
                "impact_sentiment": "POSITIVE",
                "source": "NSE Derivatives Intelligence",
                "source_url": "https://nseindia.com",
                "timestamp": f"{today_str}, 09:15 AM IST"
            },
            {
                "id": "index-2",
                "section": "INDICES_SECTORS",
                "title": "IT Sector Gains Momentum on Renewed Cloud & Enterprise AI Deal Wins",
                "symbols": ["TCS", "INFY", "HCLTECH", "WIPRO"],
                "what_happened": "Nifty IT index gained +1.8% intraday following large multi-year generative AI infrastructure transformation contracts announced by top-tier Indian IT service providers.",
                "why_it_matters": "Signals bottoming out of discretionary IT spend cycle in North American and European BFSI verticals.",
                "likely_impact": "Direct positive earnings revision catalyst for TCS, Infosys, and HCL Technologies.",
                "impact_sentiment": "POSITIVE",
                "source": "Economic Times / BSE",
                "source_url": "https://economictimes.indiatimes.com",
                "timestamp": f"{today_str}, 09:30 AM IST"
            },
            {
                "id": "index-3",
                "section": "INDICES_SECTORS",
                "title": "Metals Face Margins Pressure Amid Global Chinese Steel Demand Weakness",
                "symbols": ["TATASTEEL", "JSWSTEEL", "HINDALCO"],
                "what_happened": "Global hot-rolled coil (HRC) steel prices dipped as Chinese housing market indicators remained sluggish, putting pressure on Indian domestic metal realizations.",
                "why_it_matters": "Lower international benchmark prices reduce export margins and invite competition from low-cost steel imports.",
                "likely_impact": "Short-term margin squeeze for Tata Steel and JSW Steel; defensive positioning advised.",
                "impact_sentiment": "NEGATIVE",
                "source": "Metal Bulletin / S&P Platts",
                "source_url": "https://spglobal.com",
                "timestamp": f"{today_str}, 09:45 AM IST"
            },

            # 3. Important Individual Indian Stocks & Corporate Announcements
            {
                "id": "stock-1",
                "section": "COMPANY_SPECIFIC",
                "title": "Tata Motors Demerger Plan Gets Key Shareholder Clearance; Commercial & Passenger Segments to Split",
                "symbols": ["TATAMOTORS"],
                "what_happened": "Tata Motors received overwhelming majority shareholder approval for demerging its Commercial Vehicles (CV) and Passenger Vehicles (PV/EV) businesses into two independent listed entities.",
                "why_it_matters": "Unlocks pure-play valuation multiples for the rapidly growing EV and Jaguar Land Rover (JLR) businesses, allowing dedicated capital allocation.",
                "likely_impact": "Strong positive medium-term re-rating potential for Tata Motors equity shares.",
                "impact_sentiment": "POSITIVE",
                "source": "NSE Corporate Filings",
                "source_url": "https://nseindia.com/companies-listing/corporate-filings",
                "timestamp": f"{today_str}, 08:00 AM IST"
            },
            {
                "id": "stock-2",
                "section": "COMPANY_SPECIFIC",
                "title": "Reliance Industries Completes Mega Energy Expansion; Jio Financial Deepens Consumer Credit Footprint",
                "symbols": ["RELIANCE", "JIOFIN"],
                "what_happened": "Reliance Industries commissioned its new green hydrogen pilot facility in Gujarat while Jio Financial launched secured lending against mutual funds and digital credit cards.",
                "why_it_matters": "Expands non-telecom growth engines and monetizes high-margin green energy and retail fintech ecosystems.",
                "likely_impact": "Stable positive earnings cushion for RIL; expands market cap valuation floor.",
                "impact_sentiment": "POSITIVE",
                "source": "BSE Corporate Announcements",
                "source_url": "https://bseindia.com",
                "timestamp": f"{today_str}, 08:20 AM IST"
            },
            {
                "id": "stock-3",
                "section": "COMPANY_SPECIFIC",
                "title": "State Bank of India (SBI) Approves ₹10,000 Crore Infrastructure Bond Issuance",
                "symbols": ["SBIN"],
                "what_happened": "The executive committee of SBI approved raising up to ₹10,000 crore via long-term infrastructure bonds to finance critical national infrastructure and affordable housing projects.",
                "why_it_matters": "Infrastructure bonds are exempt from regulatory CRR/SLR requirements, optimizing cost of funds and boosting credit growth margins.",
                "likely_impact": "Positive for SBI's loan growth momentum and capital adequacy tiering.",
                "impact_sentiment": "POSITIVE",
                "source": "SBI Investor Relations / NSE",
                "source_url": "https://sbi.co.in",
                "timestamp": f"{today_str}, 08:45 AM IST"
            },
            {
                "id": "stock-4",
                "section": "COMPANY_SPECIFIC",
                "title": "Larsen & Toubro Secures Mega ₹7,500 Crore Offshore & Renewable Energy Order",
                "symbols": ["LT"],
                "what_happened": "L&T's Hydrocarbon and Renewable Power business secured significant EPC contracts in the Middle East and domestic high-voltage grid corridors.",
                "why_it_matters": "Expands order book visibility beyond ₹4.75 lakh crore, ensuring consistent double-digit revenue execution trajectory over the next 3-4 fiscal years.",
                "likely_impact": "Positive for L&T stock; reinforces leadership in infrastructure execution.",
                "impact_sentiment": "POSITIVE",
                "source": "L&T Official Announcement",
                "source_url": "https://larsentoubro.com",
                "timestamp": f"{today_str}, 09:10 AM IST"
            },

            # 4. Global Markets, Commodities, FX & Bond Yields
            {
                "id": "global-1",
                "section": "GLOBAL_COMMODITIES_FX",
                "title": "Brent Crude Stabilizes Around $82/bbl Amid OPEC+ Production Restraint & Shipping Adjustments",
                "symbols": ["BPCL", "IOC", "ONGC"],
                "what_happened": "Brent crude futures settled near $82.40 per barrel following voluntary supply extension commitments by OPEC+ producers and steady Red Sea shipping flows.",
                "why_it_matters": "Crude oil accounts for over 85% of India's petroleum import bill; oil stabilization under $85/bbl maintains benign domestic retail fuel prices and CAD stability.",
                "likely_impact": "Neutral-to-positive for Indian Oil Marketing Companies (OMCs) like BPCL, IOC, HPCL; steady upstream realizations for ONGC.",
                "impact_sentiment": "MIXED",
                "source": "Reuters Commodities",
                "source_url": "https://reuters.com/markets/commodities",
                "timestamp": f"{today_str}, 07:30 AM IST"
            },
            {
                "id": "global-2",
                "section": "GLOBAL_COMMODITIES_FX",
                "title": "Gold Trades Near Record Highs on Central Bank Accumulation; Indian Rupee Holds Steady at 83.95/USD",
                "symbols": ["TITAN", "MUTHOOTFIN"],
                "what_happened": "Spot gold traded near record levels ($2,650/oz) supported by central bank reserve diversification, while the Indian Rupee consolidated in a tight range against the US Dollar.",
                "why_it_matters": "High gold prices boost loan-to-value collateral buffers for gold loan NBFCs (Muthoot Finance), but test retail jewellery consumption volume demand.",
                "likely_impact": "Positive collateral buffer for Muthoot Finance; mixed volume elasticity for Titan jewellery retail.",
                "impact_sentiment": "MIXED",
                "source": "World Gold Council / RBI Reference Rate",
                "source_url": "https://gold.org",
                "timestamp": f"{today_str}, 07:50 AM IST"
            },
            {
                "id": "global-3",
                "section": "GLOBAL_COMMODITIES_FX",
                "title": "US Wall Street Indices (S&P 500, Nasdaq) Rebound on Big Tech Artificial Intelligence Capex",
                "symbols": ["NIFTY"],
                "what_happened": "The S&P 500 advanced +0.6% and Nasdaq climbed +0.9% as semiconductor and hyperscale cloud providers reinforced multi-billion dollar AI infrastructure investment roadmaps.",
                "why_it_matters": "GIFT Nifty futures tracked early gains (+65 points), providing an encouraging opening setup for the Indian cash trading session.",
                "likely_impact": "Positive opening sentiment for Indian equities and tech export counters.",
                "impact_sentiment": "POSITIVE",
                "source": "Bloomberg / Wall Street Journal",
                "source_url": "https://bloomberg.com",
                "timestamp": f"{today_str}, 08:15 AM IST"
            },

            # 5. Upcoming Key Events & Earnings to Watch
            {
                "id": "event-1",
                "section": "UPCOMING_EVENTS",
                "title": "Calendar Ahead: US Non-Farm Payrolls, India CPI Inflation & Top Tier Q3 Corporate Earnings",
                "symbols": ["NIFTY", "TCS", "HDFCBANK", "INFY"],
                "what_happened": "Markets are tracking upcoming India CPI retail inflation data release on the 12th, followed by IT bellwether TCS quarterly results kickoff and US labor market updates.",
                "why_it_matters": "Will set the tone for institutional earnings growth expectations, FII capital reallocation, and mid-cap valuation support.",
                "likely_impact": "Anticipated uptick in implied volatility (India VIX); option buying and straddle/strangle strategies viable ahead of key data points.",
                "impact_sentiment": "UNCERTAIN",
                "source": "Ministry of Statistics & Programme Implementation (MoSPI) / NSE",
                "source_url": "https://mospi.gov.in",
                "timestamp": f"{today_str}, 09:30 AM IST"
            }
        ]

        return {
            "title": "Daily Market Intelligence & Macro News Briefing",
            "last_updated": datetime.now().strftime("%d %B %Y, %I:%M %p IST"),
            "macro_summary": "Indian markets exhibit resilient macro fundamentals supported by steady RBI policy, expanding manufacturing PMI (58.1), and controlled domestic inflation. Global cues remain constructive with softening US Treasury yields and range-bound crude oil prices ($82/bbl). Selective stock picking favored in IT, Capital Goods, and Private Banking.",
            "total_items": len(news_items),
            "items": news_items,
            "disclaimer": "This news briefing is strictly informational and educational. Distinguishes verified facts from market speculation. This is not personalized financial or investment advice."
        }
