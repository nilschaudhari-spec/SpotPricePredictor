package com.spotpricepredictor.pro.domain.model

object StockCatalog {

    val STOCKS: List<StockItem> = listOf(
        // Indices
        StockItem("NIFTY", "Nifty 50 Index", "Benchmark Index"),
        StockItem("BANKNIFTY", "Nifty Bank Index", "Banking Index"),
        StockItem("FINNIFTY", "Nifty Financial Services Index", "Financial Index"),
        StockItem("MIDCPNIFTY", "Nifty Midcap Select Index", "Midcap Index"),
        StockItem("SENSEX", "BSE Sensex Benchmark Index", "Benchmark Index", "BSE"),

        // Tata Group
        StockItem("TATAMOTORS", "Tata Motors Limited", "Automobile"),
        StockItem("TCS", "Tata Consultancy Services Limited", "IT Services"),
        StockItem("TATASTEEL", "Tata Steel Limited", "Metals & Mining"),
        StockItem("TATACONSUM", "Tata Consumer Products Limited", "FMCG"),
        StockItem("TATACHEM", "Tata Chemicals Limited", "Chemicals"),
        StockItem("TATAPOWER", "Tata Power Company Limited", "Power & Energy"),
        StockItem("TATACOMM", "Tata Communications Limited", "Telecom"),
        StockItem("TATAELXSI", "Tata Elxsi Limited", "IT & Design"),
        StockItem("TRENT", "Trent Limited (Westside/Zudio Tata Retail)", "Retail"),
        StockItem("VOLTAS", "Voltas Limited (Tata Group)", "Consumer Durables"),
        StockItem("TITAN", "Titan Company Limited (Tanishq/Fastrack)", "Consumer Lifestyle"),

        // Banking
        StockItem("HDFCBANK", "HDFC Bank Limited", "Private Banking"),
        StockItem("ICICIBANK", "ICICI Bank Limited", "Private Banking"),
        StockItem("SBIN", "State Bank of India", "Public Banking"),
        StockItem("KOTAKBANK", "Kotak Mahindra Bank Limited", "Private Banking"),
        StockItem("AXISBANK", "Axis Bank Limited", "Private Banking"),
        StockItem("INDUSINDBK", "IndusInd Bank Limited", "Private Banking"),
        StockItem("BANKBARODA", "Bank of Baroda", "Public Banking"),
        StockItem("PNB", "Punjab National Bank", "Public Banking"),
        StockItem("CANBK", "Canara Bank", "Public Banking"),
        StockItem("UNIONBANK", "Union Bank of India", "Public Banking"),
        StockItem("FEDERALBNK", "Federal Bank Limited", "Private Banking"),
        StockItem("IDFCFIRSTB", "IDFC First Bank Limited", "Private Banking"),
        StockItem("AUBANK", "AU Small Finance Bank Limited", "Banking"),
        StockItem("BANDHANBNK", "Bandhan Bank Limited", "Private Banking"),

        // Financial Services & Insurance
        StockItem("BAJFINANCE", "Bajaj Finance Limited", "NBFC / Financial Services"),
        StockItem("BAJAJFINSV", "Bajaj Finserv Limited", "Financial Services"),
        StockItem("CHOLAFIN", "Cholamandalam Investment and Finance", "NBFC"),
        StockItem("MUTHOOTFIN", "Muthoot Finance Limited", "Gold Loans / NBFC"),
        StockItem("JIOFIN", "Jio Financial Services Limited", "Financial Services"),
        StockItem("HDFCLIFE", "HDFC Life Insurance Company Limited", "Life Insurance"),
        StockItem("SBILIFE", "SBI Life Insurance Company Limited", "Life Insurance"),
        StockItem("ICICIPRULI", "ICICI Prudential Life Insurance", "Life Insurance"),
        StockItem("ICICIGI", "ICICI Lombard General Insurance", "General Insurance"),
        StockItem("SBICARD", "SBI Cards and Payment Services Limited", "Credit Cards"),
        StockItem("HDFCAMC", "HDFC Asset Management Company Limited", "Asset Management"),
        StockItem("LICI", "Life Insurance Corporation of India (LIC)", "Life Insurance"),

        // IT & Software
        StockItem("INFY", "Infosys Limited", "IT Services"),
        StockItem("WIPRO", "Wipro Limited", "IT Services"),
        StockItem("HCLTECH", "HCL Technologies Limited", "IT Services"),
        StockItem("TECHM", "Tech Mahindra Limited", "IT Services"),
        StockItem("LTIM", "LTIMindtree Limited", "IT Services"),
        StockItem("PERSISTENT", "Persistent Systems Limited", "IT Services"),
        StockItem("COFORGE", "Coforge Limited", "IT Services"),
        StockItem("MPHASIS", "Mphasis Limited", "IT Services"),
        StockItem("LTTS", "L&T Technology Services Limited", "Engineering R&D"),
        StockItem("OFSS", "Oracle Financial Services Software", "IT Services"),
        StockItem("NAUKRI", "Info Edge (India) Limited (Naukri.com)", "Internet & Tech"),
        StockItem("ZOMATO", "Zomato Limited (Blinkit)", "Food Delivery & Tech"),
        StockItem("PAYTM", "One97 Communications Limited (Paytm)", "Fintech"),
        StockItem("NYKAA", "FSN E-Commerce Ventures (Nykaa)", "E-Commerce"),
        StockItem("POLICYBZR", "PB Fintech Limited (PolicyBazaar)", "Fintech"),

        // Oil, Gas & Energy
        StockItem("RELIANCE", "Reliance Industries Limited", "Conglomerate & Energy"),
        StockItem("ONGC", "Oil and Natural Gas Corporation Limited", "Oil & Gas Exploration"),
        StockItem("BPCL", "Bharat Petroleum Corporation Limited", "Oil & Gas Refining"),
        StockItem("IOC", "Indian Oil Corporation Limited", "Oil & Gas Refining"),
        StockItem("HPCL", "Hindustan Petroleum Corporation Limited", "Oil & Gas Refining"),
        StockItem("GAIL", "GAIL (India) Limited", "Natural Gas Transmission"),
        StockItem("PETRONET", "Petronet LNG Limited", "Natural Gas LNG"),
        StockItem("IGL", "Indraprastha Gas Limited", "City Gas Distribution"),
        StockItem("MGL", "Mahanagar Gas Limited", "City Gas Distribution"),
        StockItem("COALINDIA", "Coal India Limited", "Mining & Energy"),
        StockItem("NTPC", "NTPC Limited", "Power Generation"),
        StockItem("POWERGRID", "Power Grid Corporation of India", "Power Transmission"),
        StockItem("ADANIENT", "Adani Enterprises Limited", "Conglomerate & Mining"),
        StockItem("ADANIPORTS", "Adani Ports and Special Economic Zone", "Ports & Logistics"),
        StockItem("ADANIPOWER", "Adani Power Limited", "Power Generation"),
        StockItem("ADANIGREEN", "Adani Green Energy Limited", "Renewable Energy"),
        StockItem("SUZLON", "Suzlon Energy Limited", "Wind Power / Renewable"),
        StockItem("NHPC", "NHPC Limited", "Hydro Power"),

        // Automobile & Auto Components
        StockItem("MARUTI", "Maruti Suzuki India Limited", "Passenger Vehicles"),
        StockItem("M&M", "Mahindra & Mahindra Limited", "Automobile & Tractors"),
        StockItem("BAJAJ-AUTO", "Bajaj Auto Limited", "Two & Three Wheelers"),
        StockItem("HEROMOTOCO", "Hero MotoCorp Limited", "Two Wheelers"),
        StockItem("EICHERMOT", "Eicher Motors Limited (Royal Enfield)", "Automobile"),
        StockItem("TVSMOTOR", "TVS Motor Company Limited", "Two Wheelers"),
        StockItem("ASHOKLEY", "Ashok Leyland Limited", "Commercial Vehicles"),
        StockItem("ESCORTS", "Escorts Kubota Limited", "Agricultural Machinery"),
        StockItem("MOTHERSON", "Samvardhana Motherson International", "Auto Components"),
        StockItem("BOSCHLTD", "Bosch Limited", "Auto Components & Tech"),
        StockItem("BALKRISIND", "Balkrishna Industries Limited (BKT)", "Tyres & Off-Highway"),
        StockItem("MRF", "MRF Limited", "Tyres"),
        StockItem("APOLLOTYRE", "Apollo Tyres Limited", "Tyres"),
        StockItem("BHARATFORG", "Bharat Forge Limited", "Auto Forging & Defense"),

        // Pharma & Healthcare
        StockItem("SUNPHARMA", "Sun Pharmaceutical Industries Limited", "Pharmaceuticals"),
        StockItem("CIPLA", "Cipla Limited", "Pharmaceuticals"),
        StockItem("DRREDDY", "Dr. Reddy's Laboratories Limited", "Pharmaceuticals"),
        StockItem("DIVISLAB", "Divi's Laboratories Limited", "Pharmaceuticals & API"),
        StockItem("APOLLOHOSP", "Apollo Hospitals Enterprise Limited", "Healthcare & Hospitals"),
        StockItem("LUPIN", "Lupin Limited", "Pharmaceuticals"),
        StockItem("AUROPHARMA", "Aurobindo Pharma Limited", "Pharmaceuticals"),
        StockItem("BIOCON", "Biocon Limited", "Biotechnology & Biosimilars"),
        StockItem("TORNTPHARM", "Torrent Pharmaceuticals Limited", "Pharmaceuticals"),
        StockItem("ALKEM", "Alkem Laboratories Limited", "Pharmaceuticals"),
        StockItem("MANKIND", "Mankind Pharma Limited", "Pharmaceuticals"),
        StockItem("MAXHEALTH", "Max Healthcare Institute Limited", "Hospitals"),
        StockItem("FORTIS", "Fortis Healthcare Limited", "Hospitals"),
        StockItem("GLENMARK", "Glenmark Pharmaceuticals Limited", "Pharmaceuticals"),

        // FMCG & Consumer Goods
        StockItem("HINDUNILVR", "Hindustan Unilever Limited", "FMCG / Home & Personal"),
        StockItem("ITC", "ITC Limited", "FMCG & Diversified"),
        StockItem("NESTLEIND", "Nestle India Limited", "FMCG / Food & Beverage"),
        StockItem("BRITANNIA", "Britannia Industries Limited", "FMCG / Bakery & Dairy"),
        StockItem("GODREJCP", "Godrej Consumer Products Limited", "FMCG / Household"),
        StockItem("DABUR", "Dabur India Limited", "FMCG / Ayurveda"),
        StockItem("MARICO", "Marico Limited (Parachute/Saffola)", "FMCG / Consumer Care"),
        StockItem("COLPAL", "Colgate-Palmolive (India) Limited", "FMCG / Oral Care"),
        StockItem("VBL", "Varun Beverages Limited (PepsiCo)", "Beverages"),
        StockItem("UBL", "United Breweries Limited (Kingfisher)", "Beverages"),
        StockItem("UNITDSPR", "United Spirits Limited (Diageo)", "Beverages"),
        StockItem("PAGEIND", "Page Industries Limited (Jockey)", "Apparel & Textiles"),
        StockItem("BATAINDIA", "Bata India Limited", "Footwear"),

        // Metals & Mining
        StockItem("JSWSTEEL", "JSW Steel Limited", "Iron & Steel"),
        StockItem("HINDALCO", "Hindalco Industries Limited (Novelis)", "Aluminium & Copper"),
        StockItem("VEDL", "Vedanta Limited", "Diversified Metals & Mining"),
        StockItem("JINDALSTEL", "Jindal Steel & Power Limited", "Iron & Steel"),
        StockItem("NMDC", "NMDC Limited", "Iron Ore Mining"),
        StockItem("SAIL", "Steel Authority of India Limited", "Steel Manufacturing"),
        StockItem("NATIONALUM", "National Aluminium Company (NALCO)", "Aluminium"),

        // Infrastructure, Cement & Capital Goods
        StockItem("LT", "Larsen & Toubro Limited", "Engineering & Infra"),
        StockItem("ULTRACEMCO", "UltraTech Cement Limited", "Cement"),
        StockItem("GRASIM", "Grasim Industries Limited", "Cement & VSF"),
        StockItem("AMBUJACEM", "Ambuja Cements Limited", "Cement"),
        StockItem("ACC", "ACC Limited", "Cement"),
        StockItem("SHREECEM", "Shree Cement Limited", "Cement"),
        StockItem("DALBHARAT", "Dalmia Bharat Limited", "Cement"),
        StockItem("SIEMENS", "Siemens Limited", "Capital Goods & Automation"),
        StockItem("ABB", "ABB India Limited", "Power & Automation"),
        StockItem("HAVELLS", "Havells India Limited", "Consumer Electricals"),
        StockItem("POLYCAB", "Polycab India Limited", "Wires & Cables"),
        StockItem("CUMMINSIND", "Cummins India Limited", "Engines & Power Gen"),
        StockItem("BHEL", "Bharat Heavy Electricals Limited", "Power Equipment"),
        StockItem("BEL", "Bharat Electronics Limited", "Defense Electronics"),
        StockItem("HAL", "Hindustan Aeronautics Limited", "Defense Aerospace"),
        StockItem("MAZDOCK", "Mazagon Dock Shipbuilders Limited", "Defense Shipbuilding"),
        StockItem("COCHINSHIP", "Cochin Shipyard Limited", "Shipbuilding & Marine"),
        StockItem("BDL", "Bharat Dynamics Limited", "Defense Missiles"),

        // Real Estate & Logistics
        StockItem("DLF", "DLF Limited", "Real Estate"),
        StockItem("GODREJPROP", "Godrej Properties Limited", "Real Estate"),
        StockItem("OBEROIRLTY", "Oberoi Realty Limited", "Real Estate"),
        StockItem("PHOENIXLTD", "The Phoenix Mills Limited", "Real Estate Retail Malls"),
        StockItem("CONCOR", "Container Corporation of India", "Logistics"),
        StockItem("DELHIVERY", "Delhivery Limited", "Express Logistics"),
        StockItem("GMRINFRA", "GMR Airports Infrastructure Limited", "Airports & Infra"),

        // Railways & PSU Finance
        StockItem("IRCTC", "Indian Railway Catering & Tourism Corp", "Railways & Tourism"),
        StockItem("IRFC", "Indian Railway Finance Corporation", "Railways Financing"),
        StockItem("RVNL", "Rail Vikas Nigam Limited", "Railway Infrastructure"),
        StockItem("RECLTD", "REC Limited", "Power Infrastructure Finance"),
        StockItem("PFC", "Power Finance Corporation Limited", "Power Financing"),
        StockItem("IREDA", "Indian Renewable Energy Dev Agency", "Green Energy Finance"),

        // Telecom & Media
        StockItem("BHARTIARTL", "Bharti Airtel Limited", "Telecommunications"),
        StockItem("IDEA", "Vodafone Idea Limited", "Telecommunications"),
        StockItem("PVRINOX", "PVR INOX Limited", "Multiplex & Cinema"),
        StockItem("SUNTV", "Sun TV Network Limited", "Broadcasting & Media"),
        StockItem("ZEEL", "Zee Entertainment Enterprises", "Media & Entertainment"),

        // Chemicals & Paints
        StockItem("ASIANPAINT", "Asian Paints Limited", "Paints & Coatings"),
        StockItem("BERGEPAINT", "Berger Paints India Limited", "Paints"),
        StockItem("PIDILITIND", "Pidilite Industries Limited (Fevicol)", "Adhesives & Sealants"),
        StockItem("SRF", "SRF Limited", "Specialty Chemicals"),
        StockItem("DEEPAKNTR", "Deepak Nitrite Limited", "Chemical Intermediates"),
        StockItem("AARTIIND", "Aarti Industries Limited", "Specialty Chemicals"),
        StockItem("PIIND", "PI Industries Limited", "Agrochemicals"),
        StockItem("UPL", "UPL Limited", "Agrochemicals")
    )

    /**
     * Search stocks when 3 or more letters are typed.
     * Matches symbol prefix, company name prefix, individual name word prefixes,
     * and substring occurrences with intelligent relevance ranking.
     */
    fun search(query: String, limit: Int = 15): List<StockItem> {
        val q = query.trim().uppercase()
        if (q.length < 3) return emptyList()

        val exactMatch = mutableListOf<StockItem>()
        val symbolStartsWith = mutableListOf<StockItem>()
        val nameStartsWith = mutableListOf<StockItem>()
        val nameWordStartsWith = mutableListOf<StockItem>()
        val containsMatch = mutableListOf<StockItem>()

        for (item in STOCKS) {
            val sym = item.symbol.uppercase()
            val name = item.name.uppercase()

            val words = name.split(" ", "-", "(", ")", "/", ".").filter { it.isNotBlank() }

            when {
                sym == q -> exactMatch.add(item)
                sym.startsWith(q) -> symbolStartsWith.add(item)
                name.startsWith(q) -> nameStartsWith.add(item)
                words.any { it.startsWith(q) } -> nameWordStartsWith.add(item)
                sym.contains(q) || name.contains(q) -> containsMatch.add(item)
            }
        }

        val orderedSet = LinkedHashSet<StockItem>()
        orderedSet.addAll(exactMatch)
        orderedSet.addAll(symbolStartsWith)
        orderedSet.addAll(nameStartsWith)
        orderedSet.addAll(nameWordStartsWith)
        orderedSet.addAll(containsMatch)

        return orderedSet.take(limit).toList()
    }
}
