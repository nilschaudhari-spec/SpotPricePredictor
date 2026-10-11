package com.spotpricepredictor.pro.domain.model

object StockCatalog {

    val STOCKS: List<StockItem> = listOf(
        // Indices
        StockItem("NIFTY", "Nifty 50 Index", "Benchmark Index"),
        StockItem("BANKNIFTY", "Nifty Bank Index", "Banking Index"),
        StockItem("FINNIFTY", "Nifty Financial Services Index", "Financial Index"),
        StockItem("MIDCPNIFTY", "Nifty Midcap Select Index", "Midcap Index"),
        StockItem("SENSEX", "BSE Sensex Benchmark Index", "Benchmark Index", "BSE"),
        StockItem("BANKEX", "BSE Bankex Index", "Banking Index", "BSE"),
        StockItem("NIFTYIT", "Nifty IT Sector Index", "IT Index"),
        StockItem("NIFTYAUTO", "Nifty Auto Sector Index", "Auto Index"),
        StockItem("NIFTYPHARMA", "Nifty Pharma Sector Index", "Pharma Index"),
        StockItem("NIFTYFMCG", "Nifty FMCG Sector Index", "FMCG Index"),
        StockItem("NIFTYMETAL", "Nifty Metal Sector Index", "Metal Index"),
        StockItem("NIFTYENERGY", "Nifty Energy Sector Index", "Energy Index"),

        // Tata Group
        StockItem("TATAMOTORS", "Tata Motors Limited", "Automobile"),
        StockItem("TCS", "Tata Consultancy Services Limited", "IT Services"),
        StockItem("TATASTEEL", "Tata Steel Limited", "Metals & Mining"),
        StockItem("TATACONSUM", "Tata Consumer Products Limited", "FMCG"),
        StockItem("TATACHEM", "Tata Chemicals Limited", "Chemicals"),
        StockItem("TATAPOWER", "Tata Power Company Limited", "Power & Energy"),
        StockItem("TATACOMM", "Tata Communications Limited", "Telecom"),
        StockItem("TATAELXSI", "Tata Elxsi Limited", "IT & Design"),
        StockItem("TATATECH", "Tata Technologies Limited", "Engineering & IT"),
        StockItem("TATAINVEST", "Tata Investment Corporation", "Financial Holding"),
        StockItem("TRENT", "Trent Limited (Westside/Zudio Tata Retail)", "Retail"),
        StockItem("VOLTAS", "Voltas Limited (Tata Group)", "Consumer Durables"),
        StockItem("TITAN", "Titan Company Limited (Tanishq/Fastrack)", "Consumer Lifestyle"),

        // Banking (Private & PSU)
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
        StockItem("RBLBANK", "RBL Bank Limited", "Private Banking"),
        StockItem("INDIANB", "Indian Bank", "Public Banking"),
        StockItem("MAHABANK", "Bank of Maharashtra", "Public Banking"),
        StockItem("CENTRALBK", "Central Bank of India", "Public Banking"),
        StockItem("UCOBANK", "UCO Bank", "Public Banking"),
        StockItem("IOB", "Indian Overseas Bank", "Public Banking"),
        StockItem("BANKINDIA", "Bank of India", "Public Banking"),
        StockItem("YESBANK", "Yes Bank Limited", "Private Banking"),
        StockItem("KARURVYSYA", "Karur Vysya Bank Limited", "Private Banking"),
        StockItem("CUB", "City Union Bank Limited", "Private Banking"),

        // Financial Services, NBFC, Insurance & Exchanges
        StockItem("BAJFINANCE", "Bajaj Finance Limited", "NBFC / Financial Services"),
        StockItem("BAJAJFINSV", "Bajaj Finserv Limited", "Financial Services"),
        StockItem("CHOLAFIN", "Cholamandalam Investment and Finance", "NBFC"),
        StockItem("SHRIRAMFIN", "Shriram Finance Limited", "NBFC"),
        StockItem("MUTHOOTFIN", "Muthoot Finance Limited", "Gold Loans / NBFC"),
        StockItem("MANAPPURAM", "Manappuram Finance Limited", "Gold Loans / NBFC"),
        StockItem("POONAWALLA", "Poonawalla Fincorp Limited", "NBFC"),
        StockItem("JIOFIN", "Jio Financial Services Limited", "Financial Services"),
        StockItem("HDFCLIFE", "HDFC Life Insurance Company Limited", "Life Insurance"),
        StockItem("SBILIFE", "SBI Life Insurance Company Limited", "Life Insurance"),
        StockItem("ICICIPRULI", "ICICI Prudential Life Insurance", "Life Insurance"),
        StockItem("ICICIGI", "ICICI Lombard General Insurance", "General Insurance"),
        StockItem("SBICARD", "SBI Cards and Payment Services Limited", "Credit Cards"),
        StockItem("HDFCAMC", "HDFC Asset Management Company Limited", "Asset Management"),
        StockItem("NAM-INDIA", "Nippon Life India Asset Management", "Asset Management"),
        StockItem("UTIAMC", "UTI Asset Management Company Limited", "Asset Management"),
        StockItem("LICI", "Life Insurance Corporation of India (LIC)", "Life Insurance"),
        StockItem("BSE", "BSE Limited (Exchange)", "Exchanges"),
        StockItem("CDSL", "Central Depository Services (India) Limited", "Depositories"),
        StockItem("MCX", "Multi Commodity Exchange of India", "Exchanges"),
        StockItem("CAMS", "Computer Age Management Services", "Fintech / Registrar"),
        StockItem("ANGELONE", "Angel One Limited", "Broking & Fintech"),
        StockItem("MOTILALOFS", "Motilal Oswal Financial Services", "Financial Services"),
        StockItem("IEX", "Indian Energy Exchange Limited", "Energy Exchange"),
        StockItem("CRISIL", "CRISIL Limited (Credit Rating)", "Rating Agencies"),
        StockItem("PFC", "Power Finance Corporation Limited", "NBFC / Power Infra"),
        StockItem("RECLTD", "REC Limited", "NBFC / Infrastructure"),
        StockItem("HUDCO", "Housing & Urban Development Corp", "Housing & Infra NBFC"),
        StockItem("IRFC", "Indian Railway Finance Corporation", "Railway NBFC"),

        // IT, Software & Internet
        StockItem("INFY", "Infosys Limited", "IT Services"),
        StockItem("WIPRO", "Wipro Limited", "IT Services"),
        StockItem("HCLTECH", "HCL Technologies Limited", "IT Services"),
        StockItem("TECHM", "Tech Mahindra Limited", "IT Services"),
        StockItem("LTIM", "LTIMindtree Limited", "IT Services"),
        StockItem("PERSISTENT", "Persistent Systems Limited", "IT Services"),
        StockItem("COFORGE", "Coforge Limited", "IT Services"),
        StockItem("MPHASIS", "Mphasis Limited", "IT Services"),
        StockItem("LTTS", "L&T Technology Services Limited", "IT & Engineering"),
        StockItem("KPITTECH", "KPIT Technologies Limited", "Automotive Software"),
        StockItem("CYIENT", "Cyient Limited", "Engineering IT"),
        StockItem("BSOFT", "Birlasoft Limited", "IT Services"),
        StockItem("ZOMATO", "Zomato Limited", "Internet & Food Tech"),
        StockItem("PAYTM", "One97 Communications (Paytm)", "Fintech"),
        StockItem("NAUKRI", "Info Edge (India) Limited (Naukri.com)", "Internet / Recruitment"),
        StockItem("PBFINTECH", "PB Fintech (Policybazaar / Paisabazaar)", "Fintech / Insurtech"),
        StockItem("MAPMYINDIA", "CE Info Systems (MapMyIndia)", "Geo-Tech & SaaS"),
        StockItem("POLICYBZR", "Policybazaar (PB Fintech)", "Fintech"),
        StockItem("DELHIVERY", "Delhivery Limited", "Logistics Tech"),
        StockItem("NYKAA", "FSN E-Commerce Ventures (Nykaa)", "E-Commerce / Beauty"),
        StockItem("SWIGGY", "Swiggy Limited", "Food Delivery & Quick Commerce"),
        StockItem("HAPPSTMNDS", "Happiest Minds Technologies", "Digital IT Solutions"),
        StockItem("INTELLECT", "Intellect Design Arena Limited", "Banking Tech / SaaS"),
        StockItem("KFINTECH", "KFin Technologies Limited", "Fintech Registrar"),

        // Oil, Gas, Energy & Utilities
        StockItem("RELIANCE", "Reliance Industries Limited", "Conglomerate / Energy"),
        StockItem("ONGC", "Oil and Natural Gas Corporation", "Oil & Gas Exploration"),
        StockItem("BPCL", "Bharat Petroleum Corporation Limited", "Oil Refining & Marketing"),
        StockItem("IOC", "Indian Oil Corporation Limited", "Oil Refining & Marketing"),
        StockItem("HPCL", "Hindustan Petroleum Corporation Limited", "Oil Refining & Marketing"),
        StockItem("COALINDIA", "Coal India Limited", "Coal & Mining"),
        StockItem("NTPC", "NTPC Limited", "Power Generation"),
        StockItem("POWERGRID", "Power Grid Corporation of India", "Power Transmission"),
        StockItem("GAIL", "GAIL (India) Limited", "Natural Gas Transmission"),
        StockItem("PETRONET", "Petronet LNG Limited", "Liquefied Natural Gas"),
        StockItem("IGL", "Indraprastha Gas Limited", "City Gas Distribution"),
        StockItem("MGL", "Mahanagar Gas Limited", "City Gas Distribution"),
        StockItem("GUJGASLTD", "Gujarat Gas Limited", "City Gas Distribution"),
        StockItem("OIL", "Oil India Limited", "Oil Exploration"),
        StockItem("NHPC", "NHPC Limited", "Hydroelectric Power"),
        StockItem("SJVN", "SJVN Limited", "Power Generation"),
        StockItem("IREDA", "Indian Renewable Energy Dev Agency", "Green Energy Financing"),
        StockItem("SUZLON", "Suzlon Energy Limited", "Wind Energy Equipment"),
        StockItem("JSWENERGY", "JSW Energy Limited", "Power Generation"),
        StockItem("TORNTPOWER", "Torrent Power Limited", "Power Generation & Distribution"),

        // Adani Group
        StockItem("ADANIENT", "Adani Enterprises Limited", "Conglomerate"),
        StockItem("ADANIPORTS", "Adani Ports and Special Economic Zone", "Ports & Logistics"),
        StockItem("ADANIPOWER", "Adani Power Limited", "Thermal Power Generation"),
        StockItem("ADANIGREEN", "Adani Green Energy Limited", "Renewable Solar & Wind"),
        StockItem("ADANIENSOL", "Adani Energy Solutions Limited", "Power Transmission"),
        StockItem("ATGL", "Adani Total Gas Limited", "City Gas Distribution"),
        StockItem("AWL", "Adani Wilmar Limited (Fortune)", "Edible Oils & FMCG"),

        // Automobile & Components
        StockItem("MARUTI", "Maruti Suzuki India Limited", "Automobile (Passenger Cars)"),
        StockItem("M&M", "Mahindra & Mahindra Limited", "Automobile (SUVs & Tractors)"),
        StockItem("BAJAJ-AUTO", "Bajaj Auto Limited", "Two & Three Wheelers"),
        StockItem("HEROMOTOCO", "Hero MotoCorp Limited", "Two Wheelers"),
        StockItem("EICHERMOT", "Eicher Motors Limited (Royal Enfield)", "Automobile / Motorcycles"),
        StockItem("TVSMOTOR", "TVS Motor Company Limited", "Two Wheelers"),
        StockItem("MOTHERSON", "Samvardhana Motherson International", "Auto Components"),
        StockItem("BHARATFORG", "Bharat Forge Limited", "Auto Forgings & Defense"),
        StockItem("BOSCHLTD", "Bosch Limited", "Auto Engineering"),
        StockItem("MRF", "MRF Limited", "Tyres & Rubber"),
        StockItem("APOLLOTYRE", "Apollo Tyres Limited", "Tyres"),
        StockItem("BALKRISIND", "Balkrishna Industries Limited", "Off-Highway Tyres"),
        StockItem("EXIDEIND", "Exide Industries Limited", "Storage Batteries"),
        StockItem("AMARAJABAT", "Amara Raja Energy & Mobility", "Batteries & EV Tech"),
        StockItem("SONACOMS", "Sona BLW Precision Forgings", "EV Driveline Components"),
        StockItem("TIINDIA", "Tube Investments of India Limited", "Auto & Engineering"),
        StockItem("ESCORTS", "Escorts Kubota Limited", "Tractors & Agri Machinery"),
        StockItem("ASHOKLEY", "Ashok Leyland Limited", "Commercial Vehicles"),

        // Metals, Mining & Commodities
        StockItem("JINDALSTEL", "Jindal Steel & Power Limited", "Steel & Mining"),
        StockItem("JSWSTEEL", "JSW Steel Limited", "Steel Manufacturing"),
        StockItem("HINDALCO", "Hindalco Industries Limited", "Aluminium & Copper"),
        StockItem("VEDL", "Vedanta Limited", "Diversified Metals & Mining"),
        StockItem("NMDC", "NMDC Limited", "Iron Ore Mining"),
        StockItem("SAIL", "Steel Authority of India Limited", "Steel Manufacturing"),
        StockItem("NATIONALUM", "National Aluminium Company (NALCO)", "Aluminium Mining"),
        StockItem("HINDZINC", "Hindustan Zinc Limited", "Zinc & Silver Mining"),
        StockItem("APLAPOLLO", "APL Apollo Tubes Limited", "Structural Steel Tubes"),
        StockItem("HINDCOPPER", "Hindustan Copper Limited", "Copper Mining"),

        // Pharma, Healthcare & Biotech
        StockItem("SUNPHARMA", "Sun Pharmaceutical Industries", "Pharmaceuticals"),
        StockItem("CIPLA", "Cipla Limited", "Pharmaceuticals"),
        StockItem("DRREDDY", "Dr. Reddy's Laboratories Limited", "Pharmaceuticals"),
        StockItem("DIVISLAB", "Divi's Laboratories Limited", "Active Pharmaceutical Ingredients"),
        StockItem("APOLLOHOSP", "Apollo Hospitals Enterprise", "Healthcare & Hospitals"),
        StockItem("LUPIN", "Lupin Limited", "Pharmaceuticals"),
        StockItem("AUROPHARMA", "Aurobindo Pharma Limited", "Pharmaceuticals"),
        StockItem("TORNTPHARM", "Torrent Pharmaceuticals Limited", "Pharmaceuticals"),
        StockItem("ZYDUSLIFE", "Zydus Lifesciences Limited", "Pharmaceuticals"),
        StockItem("ALKEM", "Alkem Laboratories Limited", "Pharmaceuticals"),
        StockItem("BIOCON", "Biocon Limited", "Biopharmaceuticals"),
        StockItem("MANKIND", "Mankind Pharma Limited", "Consumer Healthcare"),
        StockItem("GLENMARK", "Glenmark Pharmaceuticals Limited", "Pharmaceuticals"),
        StockItem("IPCALAB", "IPCA Laboratories Limited", "Pharmaceuticals"),
        StockItem("LAURUSLABS", "Laurus Labs Limited", "Active Pharmaceutical Ingredients"),
        StockItem("GRANULES", "Granules India Limited", "Pharmaceutical Formulations"),
        StockItem("FORTIS", "Fortis Healthcare Limited", "Hospitals & Diagnostics"),
        StockItem("MAXHEALTH", "Max Healthcare Institute Limited", "Hospitals"),

        // FMCG, Retail & Consumption
        StockItem("HINDUNILVR", "Hindustan Unilever Limited", "FMCG"),
        StockItem("ITC", "ITC Limited", "FMCG / Diversified"),
        StockItem("NESTLEIND", "Nestle India Limited", "Food & Beverages"),
        StockItem("BRITANNIA", "Britannia Industries Limited", "Bakery & FMCG"),
        StockItem("DABUR", "Dabur India Limited", "Ayurvedic & FMCG"),
        StockItem("MARICO", "Marico Limited (Parachute/Saffola)", "FMCG"),
        StockItem("GODREJCP", "Godrej Consumer Products Limited", "Personal Care & FMCG"),
        StockItem("COLPAL", "Colgate-Palmolive (India) Limited", "Oral Care / FMCG"),
        StockItem("VBL", "Varun Beverages Limited (PepsiCo)", "Beverages"),
        StockItem("DEVYANI", "Devyani International (KFC/Pizza Hut)", "QSR & Quick Dining"),
        StockItem("JUBLFOOD", "Jubilant FoodWorks (Domino's Pizza)", "QSR & Food Service"),
        StockItem("DMART", "Avenue Supermarts Limited (DMart)", "Retail Supermarkets"),
        StockItem("RADICO", "Radico Khaitan Limited", "Beverages / Alcohol"),
        StockItem("UNITEDSPR", "United Spirits Limited (Diageo India)", "Beverages / Alcohol"),

        // Infrastructure, Capital Goods, Defence & Engineering
        StockItem("LT", "Larsen & Toubro Limited", "Engineering, Construction & Infra"),
        StockItem("BEL", "Bharat Electronics Limited", "Defense Electronics & Radar"),
        StockItem("HAL", "Hindustan Aeronautics Limited", "Defense Aerospace & Aircraft"),
        StockItem("BHEL", "Bharat Heavy Electricals Limited", "Power Equipment & Turbines"),
        StockItem("ABB", "ABB India Limited", "Electrification & Industrial Automation"),
        StockItem("SIEMENS", "Siemens Limited", "Industrial Engineering & Power"),
        StockItem("CUMMINSIND", "Cummins India Limited", "Diesel & Natural Gas Engines"),
        StockItem("THERMAX", "Thermax Limited", "Clean Air & Energy Solutions"),
        StockItem("CGPOWER", "CG Power and Industrial Solutions", "Electrical Equipment & Motors"),
        StockItem("POLYCAB", "Polycab India Limited", "Wires, Cables & Fasteners"),
        StockItem("KEI", "KEI Industries Limited", "High Voltage Power Cables"),
        StockItem("HAVELLS", "Havells India Limited", "Consumer Electricals & Appliances"),
        StockItem("DIXON", "Dixon Technologies (India) Limited", "Electronic Manufacturing (EMS)"),
        StockItem("KAYNES", "Kaynes Technology India Limited", "Integrated Electronics (EMS)"),
        StockItem("ASTRAL", "Astral Limited", "Plumbing & Drainage Pipes"),
        StockItem("SUPREMEIND", "Supreme Industries Limited", "Plastic Products & Piping"),
        StockItem("MAZDOCK", "Mazagon Dock Shipbuilders Limited", "Defense Warships & Submarines"),
        StockItem("COCHINSHIP", "Cochin Shipyard Limited", "Shipbuilding & Aircraft Carriers"),
        StockItem("GRSE", "Garden Reach Shipbuilders & Engineers", "Defense Naval Vessels"),
        StockItem("BDL", "Bharat Dynamics Limited", "Missile Systems & Ammunition"),
        StockItem("RVNL", "Rail Vikas Nigam Limited", "Rail Infrastructure Projects"),
        StockItem("IRCTC", "Indian Railway Catering and Tourism Corp", "Railways Ticketing & Tourism"),
        StockItem("RAILTEL", "RailTel Corporation of India Limited", "Telecom & Optical Fiber Network"),
        StockItem("RITES", "RITES Limited", "Transport Infrastructure Consultancy"),
        StockItem("NBCC", "NBCC (India) Limited", "Civil Engineering Construction"),
        StockItem("GMRINFRA", "GMR Airports Infrastructure Limited", "Airports & Transportation"),

        // Cement & Building Materials
        StockItem("ULTRACEMCO", "UltraTech Cement Limited", "Cement & Concrete"),
        StockItem("GRASIM", "Grasim Industries Limited", "Cement, Chemicals & Textiles"),
        StockItem("AMBUJACEM", "Ambuja Cements Limited", "Cement"),
        StockItem("ACC", "ACC Limited", "Cement & Ready Mix Concrete"),
        StockItem("SHREECEM", "Shree Cement Limited", "Cement"),
        StockItem("DALBHARAT", "Dalmia Bharat Limited", "Cement"),
        StockItem("JKCEMENT", "JK Cement Limited", "Grey & White Cement"),
        StockItem("RAMCOCEM", "The Ramco Cements Limited", "Cement"),

        // Chemicals, Fertilizers & Paints
        StockItem("PIDILITIND", "Pidilite Industries Limited (Fevicol)", "Adhesives & Sealants"),
        StockItem("SRF", "SRF Limited", "Specialty Chemicals & Packaging"),
        StockItem("DEEPAKNTR", "Deepak Nitrite Limited", "Chemical Intermediates & Phenolics"),
        StockItem("AARTIIND", "Aarti Industries Limited", "Specialty Chemicals"),
        StockItem("NAVINFLUOR", "Navin Fluorine International Limited", "Specialty Fluorochemicals"),
        StockItem("ATUL", "Atul Limited", "Chemicals & Polymers"),
        StockItem("PIIND", "PI Industries Limited", "Agrochemicals & Fine Chemicals"),
        StockItem("UPL", "UPL Limited", "Agrochemicals & Crop Protection"),
        StockItem("COROMANDEL", "Coromandel International Limited", "Fertilizers & Nutrients"),
        StockItem("ASIANPAINT", "Asian Paints Limited", "Paints, Coatings & Home Decor"),
        StockItem("BERGEPAINT", "Berger Paints India Limited", "Paints & Wall Coatings"),

        // Telecom, Media & Entertainment
        StockItem("BHARTIARTL", "Bharti Airtel Limited", "Telecommunications"),
        StockItem("IDEA", "Vodafone Idea Limited (Vi)", "Telecommunications"),
        StockItem("INDUSTOWER", "Indus Towers Limited", "Passive Telecom Infrastructure"),
        StockItem("ZEEL", "Zee Entertainment Enterprises", "Media & Entertainment"),
        StockItem("SUNTV", "Sun TV Network Limited", "Broadcasting & Television"),
        StockItem("PVRINOX", "PVR INOX Limited", "Multiplex Cinema Exhibition"),

        // Real Estate
        StockItem("DLF", "DLF Limited", "Real Estate Development"),
        StockItem("GODREJPROP", "Godrej Properties Limited", "Residential Real Estate"),
        StockItem("OBEROREALTY", "Oberoi Realty Limited", "Premium Real Estate"),
        StockItem("LODHA", "Macrotech Developers Limited (Lodha)", "Real Estate"),
        StockItem("PRESTIGE", "Prestige Estates Projects Limited", "Real Estate & Hospitality")
    )

    /**
     * Search stocks when 1 or more letters are typed.
     * Matches symbol prefix, company name prefix, individual name word prefixes,
     * and substring occurrences with intelligent relevance ranking.
     * Automatically includes dynamic custom symbol entry if not present in catalog,
     * enabling 100% of equities to be searched and predicted.
     */
    fun search(query: String, limit: Int = 20): List<StockItem> {
        val q = query.trim().uppercase()
        if (q.isBlank()) return emptyList()

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

        // If the exact query is not in catalog and user typed >= 2 chars, add as dynamic selectable custom stock
        if (orderedSet.none { it.symbol == q } && q.length >= 2) {
            orderedSet.add(
                StockItem(
                    symbol = q,
                    name = "$q Listed Equity",
                    sector = "Equities / Direct",
                    exchange = "NSE"
                )
            )
        }

        return orderedSet.take(limit).toList()
    }
}
