package com.spotpricepredictor.pro.domain.model

data class TickerItem(
    val symbol: String,
    val name: String,
    val spotPrice: Double,
    val change: Double,
    val changePercent: Double,
    val isPositive: Boolean
) {
    companion object {
        /**
         * Default ticker quotes for major market benchmarks and bellwethers.
         * Displayed on the marquee ticker tape.
         */
        val DEFAULT_TICKERS: List<TickerItem> = listOf(
            TickerItem("NIFTY", "Nifty 50 Index", 22520.45, 288.65, 1.30, true),
            TickerItem("BANKNIFTY", "Bank Nifty Index", 55256.65, 741.60, 1.36, true),
            TickerItem("FINNIFTY", "Nifty Financial", 24968.50, 328.05, 1.33, true),
            TickerItem("SENSEX", "BSE Sensex Index", 72472.33, 879.09, 1.23, true),
            TickerItem("RELIANCE", "Reliance Industries", 1170.30, -7.70, -0.65, false),
            TickerItem("TCS", "Tata Consultancy Services", 2156.00, 80.00, 3.85, true),
            TickerItem("HDFCBANK", "HDFC Bank Ltd", 707.25, 15.00, 2.17, true),
            TickerItem("INFY", "Infosys Ltd", 1023.40, 26.40, 2.65, true),
            TickerItem("SBIN", "State Bank of India", 959.10, 19.10, 2.03, true),
            TickerItem("TATAMOTORS", "Tata Motors Ltd", 980.00, 14.50, 1.50, true),
            TickerItem("ITC", "ITC Limited", 266.00, 11.00, 4.31, true),
            TickerItem("BHARTIARTL", "Bharti Airtel", 1805.10, 0.50, 0.03, true),
            TickerItem("BAJFINANCE", "Bajaj Finance", 960.00, 4.05, 0.42, true),
            TickerItem("LT", "Larsen & Toubro", 3703.30, 78.20, 2.16, true),
            TickerItem("MARUTI", "Maruti Suzuki", 11420.00, 192.00, 1.71, true),
            TickerItem("ZOMATO", "Zomato Limited", 260.00, 5.40, 2.12, true),
            TickerItem("SUNPHARMA", "Sun Pharma", 1760.00, 0.20, 0.01, true),
            TickerItem("TATASTEEL", "Tata Steel", 173.80, 1.84, 1.07, true),
            TickerItem("ADANIENT", "Adani Enterprises", 2634.40, 38.40, 1.48, true),
            TickerItem("TITAN", "Titan Company", 4410.00, 50.50, 1.16, true),
            TickerItem("ICICIBANK", "ICICI Bank Ltd", 1355.00, 6.00, 0.44, true),
            TickerItem("AXISBANK", "Axis Bank Ltd", 1259.10, 14.10, 1.13, true),
            TickerItem("BEL", "Bharat Electronics", 368.15, 0.85, 0.23, true),
            TickerItem("HAL", "Hindustan Aeronautics", 4655.60, 8.20, 0.18, true)
        )
    }
}
