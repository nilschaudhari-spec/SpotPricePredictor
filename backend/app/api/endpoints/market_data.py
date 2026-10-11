from fastapi import APIRouter, Query
from typing import List, Dict, Any
from app.services.market_data_fetcher import MarketDataFetcher
from app.services.technical_analysis import TechnicalAnalysisEngine

router = APIRouter(prefix="/market", tags=["Market Data"])

ALL_SYMBOLS_CATALOG: List[Dict[str, Any]] = [
    # Indices
    {"symbol": "NIFTY", "name": "Nifty 50 Index", "sector": "Benchmark Index", "exchange": "NSE", "default_spot": 22520.0},
    {"symbol": "BANKNIFTY", "name": "Bank Nifty Index", "sector": "Banking Index", "exchange": "NSE", "default_spot": 55250.0},
    {"symbol": "FINNIFTY", "name": "Nifty Financial Services", "sector": "Financial Index", "exchange": "NSE", "default_spot": 23600.0},
    {"symbol": "MIDCPNIFTY", "name": "Nifty Midcap Select", "sector": "Midcap Index", "exchange": "NSE", "default_spot": 12200.0},
    {"symbol": "SENSEX", "name": "BSE Sensex Benchmark Index", "sector": "Benchmark Index", "exchange": "BSE", "default_spot": 74500.0},
    {"symbol": "BANKEX", "name": "BSE Bankex Index", "sector": "Banking Index", "exchange": "BSE", "default_spot": 61200.0},
    {"symbol": "NIFTYIT", "name": "Nifty IT Sector Index", "sector": "IT Index", "exchange": "NSE", "default_spot": 37800.0},
    {"symbol": "NIFTYAUTO", "name": "Nifty Auto Sector Index", "sector": "Auto Index", "exchange": "NSE", "default_spot": 24100.0},
    {"symbol": "NIFTYPHARMA", "name": "Nifty Pharma Sector Index", "sector": "Pharma Index", "exchange": "NSE", "default_spot": 21500.0},
    {"symbol": "NIFTYFMCG", "name": "Nifty FMCG Sector Index", "sector": "FMCG Index", "exchange": "NSE", "default_spot": 56800.0},
    {"symbol": "NIFTYMETAL", "name": "Nifty Metal Sector Index", "sector": "Metal Index", "exchange": "NSE", "default_spot": 8900.0},
    {"symbol": "NIFTYENERGY", "name": "Nifty Energy Sector Index", "sector": "Energy Index", "exchange": "NSE", "default_spot": 39200.0},

    # Tata Group
    {"symbol": "TATAMOTORS", "name": "Tata Motors Limited", "sector": "Automobile", "exchange": "NSE", "default_spot": 980.0},
    {"symbol": "TCS", "name": "Tata Consultancy Services Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 2156.0},
    {"symbol": "TATASTEEL", "name": "Tata Steel Limited", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 158.0},
    {"symbol": "TATACONSUM", "name": "Tata Consumer Products Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 1120.0},
    {"symbol": "TATACHEM", "name": "Tata Chemicals Limited", "sector": "Chemicals", "exchange": "NSE", "default_spot": 1040.0},
    {"symbol": "TATAPOWER", "name": "Tata Power Company Ltd", "sector": "Power & Energy", "exchange": "NSE", "default_spot": 420.0},
    {"symbol": "TATACOMM", "name": "Tata Communications Ltd", "sector": "Telecom", "exchange": "NSE", "default_spot": 1850.0},
    {"symbol": "TATAELXSI", "name": "Tata Elxsi Limited", "sector": "IT & Design", "exchange": "NSE", "default_spot": 6900.0},
    {"symbol": "TATATECH", "name": "Tata Technologies Limited", "sector": "Engineering & IT", "exchange": "NSE", "default_spot": 990.0},
    {"symbol": "TATAINVEST", "name": "Tata Investment Corporation", "sector": "Financial Holding", "exchange": "NSE", "default_spot": 6700.0},
    {"symbol": "TRENT", "name": "Trent Limited (Tata Retail)", "sector": "Retail", "exchange": "NSE", "default_spot": 6200.0},
    {"symbol": "VOLTAS", "name": "Voltas Limited (Tata)", "sector": "Consumer Durables", "exchange": "NSE", "default_spot": 1750.0},
    {"symbol": "TITAN", "name": "Titan Company Limited", "sector": "Consumer Lifestyle", "exchange": "NSE", "default_spot": 3400.0},

    # Banking (Private & PSU)
    {"symbol": "HDFCBANK", "name": "HDFC Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 707.0},
    {"symbol": "ICICIBANK", "name": "ICICI Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1250.0},
    {"symbol": "SBIN", "name": "State Bank of India", "sector": "Public Banking", "exchange": "NSE", "default_spot": 959.0},
    {"symbol": "KOTAKBANK", "name": "Kotak Mahindra Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1780.0},
    {"symbol": "AXISBANK", "name": "Axis Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1180.0},
    {"symbol": "INDUSINDBK", "name": "IndusInd Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 1420.0},
    {"symbol": "BANKBARODA", "name": "Bank of Baroda", "sector": "Public Banking", "exchange": "NSE", "default_spot": 250.0},
    {"symbol": "PNB", "name": "Punjab National Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 115.0},
    {"symbol": "CANBK", "name": "Canara Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 105.0},
    {"symbol": "UNIONBANK", "name": "Union Bank of India", "sector": "Public Banking", "exchange": "NSE", "default_spot": 120.0},
    {"symbol": "FEDERALBNK", "name": "Federal Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 190.0},
    {"symbol": "IDFCFIRSTB", "name": "IDFC First Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 74.0},
    {"symbol": "AUBANK", "name": "AU Small Finance Bank Ltd", "sector": "Banking", "exchange": "NSE", "default_spot": 620.0},
    {"symbol": "BANDHANBNK", "name": "Bandhan Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 195.0},
    {"symbol": "RBLBANK", "name": "RBL Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 220.0},
    {"symbol": "INDIANB", "name": "Indian Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 530.0},
    {"symbol": "MAHABANK", "name": "Bank of Maharashtra", "sector": "Public Banking", "exchange": "NSE", "default_spot": 60.0},
    {"symbol": "CENTRALBK", "name": "Central Bank of India", "sector": "Public Banking", "exchange": "NSE", "default_spot": 62.0},
    {"symbol": "UCOBANK", "name": "UCO Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 48.0},
    {"symbol": "IOB", "name": "Indian Overseas Bank", "sector": "Public Banking", "exchange": "NSE", "default_spot": 58.0},
    {"symbol": "BANKINDIA", "name": "Bank of India", "sector": "Public Banking", "exchange": "NSE", "default_spot": 125.0},
    {"symbol": "YESBANK", "name": "Yes Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 22.0},
    {"symbol": "KARURVYSYA", "name": "Karur Vysya Bank", "sector": "Private Banking", "exchange": "NSE", "default_spot": 210.0},
    {"symbol": "CUB", "name": "City Union Bank Ltd", "sector": "Private Banking", "exchange": "NSE", "default_spot": 165.0},

    # Financial Services, NBFC, Insurance, Exchanges
    {"symbol": "BAJFINANCE", "name": "Bajaj Finance Ltd", "sector": "NBFC", "exchange": "NSE", "default_spot": 960.0},
    {"symbol": "BAJAJFINSV", "name": "Bajaj Finserv Ltd", "sector": "Financial Services", "exchange": "NSE", "default_spot": 1820.0},
    {"symbol": "CHOLAFIN", "name": "Cholamandalam Investment", "sector": "Financial Services", "exchange": "NSE", "default_spot": 1490.0},
    {"symbol": "SHRIRAMFIN", "name": "Shriram Finance Ltd", "sector": "NBFC", "exchange": "NSE", "default_spot": 2980.0},
    {"symbol": "MUTHOOTFIN", "name": "Muthoot Finance Ltd", "sector": "Gold Loans", "exchange": "NSE", "default_spot": 1950.0},
    {"symbol": "MANAPPURAM", "name": "Manappuram Finance Ltd", "sector": "Gold Loans", "exchange": "NSE", "default_spot": 185.0},
    {"symbol": "POONAWALLA", "name": "Poonawalla Fincorp Ltd", "sector": "NBFC", "exchange": "NSE", "default_spot": 370.0},
    {"symbol": "JIOFIN", "name": "Jio Financial Services", "sector": "Financial Services", "exchange": "NSE", "default_spot": 330.0},
    {"symbol": "HDFCLIFE", "name": "HDFC Life Insurance Co", "sector": "Life Insurance", "exchange": "NSE", "default_spot": 710.0},
    {"symbol": "SBILIFE", "name": "SBI Life Insurance Co", "sector": "Life Insurance", "exchange": "NSE", "default_spot": 1680.0},
    {"symbol": "ICICIPRULI", "name": "ICICI Prudential Life Ins", "sector": "Life Insurance", "exchange": "NSE", "default_spot": 680.0},
    {"symbol": "ICICIGI", "name": "ICICI Lombard General Ins", "sector": "General Insurance", "exchange": "NSE", "default_spot": 1950.0},
    {"symbol": "SBICARD", "name": "SBI Cards & Payment", "sector": "Credit Cards", "exchange": "NSE", "default_spot": 720.0},
    {"symbol": "HDFCAMC", "name": "HDFC Asset Management Co", "sector": "Asset Management", "exchange": "NSE", "default_spot": 4200.0},
    {"symbol": "NAM-INDIA", "name": "Nippon Life India AMC", "sector": "Asset Management", "exchange": "NSE", "default_spot": 640.0},
    {"symbol": "UTIAMC", "name": "UTI Asset Management Co", "sector": "Asset Management", "exchange": "NSE", "default_spot": 1150.0},
    {"symbol": "LICI", "name": "Life Insurance Corp of India", "sector": "Life Insurance", "exchange": "NSE", "default_spot": 960.0},
    {"symbol": "BSE", "name": "BSE Limited (Exchange)", "sector": "Exchanges", "exchange": "NSE", "default_spot": 4100.0},
    {"symbol": "CDSL", "name": "Central Depository Services", "sector": "Depositories", "exchange": "NSE", "default_spot": 1450.0},
    {"symbol": "MCX", "name": "Multi Commodity Exchange", "sector": "Exchanges", "exchange": "NSE", "default_spot": 5800.0},
    {"symbol": "CAMS", "name": "Computer Age Management", "sector": "Fintech / Registrar", "exchange": "NSE", "default_spot": 4250.0},
    {"symbol": "ANGELONE", "name": "Angel One Limited", "sector": "Broking & Fintech", "exchange": "NSE", "default_spot": 2600.0},
    {"symbol": "MOTILALOFS", "name": "Motilal Oswal Financial", "sector": "Financial Services", "exchange": "NSE", "default_spot": 740.0},
    {"symbol": "IEX", "name": "Indian Energy Exchange", "sector": "Energy Exchange", "exchange": "NSE", "default_spot": 185.0},
    {"symbol": "CRISIL", "name": "CRISIL Limited (Credit Rating)", "sector": "Rating Agencies", "exchange": "NSE", "default_spot": 4900.0},
    {"symbol": "PFC", "name": "Power Finance Corporation", "sector": "NBFC / Power Infra", "exchange": "NSE", "default_spot": 490.0},
    {"symbol": "RECLTD", "name": "REC Limited", "sector": "NBFC / Infra", "exchange": "NSE", "default_spot": 520.0},
    {"symbol": "HUDCO", "name": "Housing & Urban Dev Corp", "sector": "Housing Infra NBFC", "exchange": "NSE", "default_spot": 230.0},
    {"symbol": "IRFC", "name": "Indian Railway Finance Corp", "sector": "Railway NBFC", "exchange": "NSE", "default_spot": 155.0},

    # IT & Software
    {"symbol": "INFY", "name": "Infosys Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 1023.0},
    {"symbol": "WIPRO", "name": "Wipro Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 162.0},
    {"symbol": "HCLTECH", "name": "HCL Technologies", "sector": "IT Services", "exchange": "NSE", "default_spot": 1780.0},
    {"symbol": "TECHM", "name": "Tech Mahindra Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 1580.0},
    {"symbol": "LTIM", "name": "LTIMindtree Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 5800.0},
    {"symbol": "PERSISTENT", "name": "Persistent Systems Ltd", "sector": "IT Services", "exchange": "NSE", "default_spot": 5200.0},
    {"symbol": "COFORGE", "name": "Coforge Limited", "sector": "IT Services", "exchange": "NSE", "default_spot": 7100.0},
    {"symbol": "MPHASIS", "name": "Mphasis Limited", "sector": "IT Services", "exchange": "NSE", "default_spot": 2850.0},
    {"symbol": "LTTS", "name": "L&T Technology Services", "sector": "IT Engineering", "exchange": "NSE", "default_spot": 5150.0},
    {"symbol": "KPITTECH", "name": "KPIT Technologies Ltd", "sector": "Automotive Software", "exchange": "NSE", "default_spot": 1650.0},
    {"symbol": "CYIENT", "name": "Cyient Limited", "sector": "Engineering IT", "exchange": "NSE", "default_spot": 1850.0},
    {"symbol": "BSOFT", "name": "Birlasoft Limited", "sector": "IT Services", "exchange": "NSE", "default_spot": 610.0},
    {"symbol": "ZOMATO", "name": "Zomato Limited", "sector": "Internet & Tech", "exchange": "NSE", "default_spot": 260.0},
    {"symbol": "PAYTM", "name": "One97 Communications (Paytm)", "sector": "Fintech", "exchange": "NSE", "default_spot": 720.0},
    {"symbol": "NAUKRI", "name": "Info Edge (India) Ltd", "sector": "Internet / Recruitment", "exchange": "NSE", "default_spot": 7300.0},
    {"symbol": "PBFINTECH", "name": "PB Fintech (Policybazaar)", "sector": "Fintech", "exchange": "NSE", "default_spot": 1650.0},
    {"symbol": "MAPMYINDIA", "name": "CE Info Systems (MapMyIndia)", "sector": "Geo-Tech & SaaS", "exchange": "NSE", "default_spot": 1950.0},
    {"symbol": "POLICYBZR", "name": "Policybazaar (PB Fintech)", "sector": "Fintech", "exchange": "NSE", "default_spot": 1650.0},
    {"symbol": "DELHIVERY", "name": "Delhivery Limited", "sector": "Logistics & Tech", "exchange": "NSE", "default_spot": 370.0},
    {"symbol": "NYKAA", "name": "FSN E-Commerce (Nykaa)", "sector": "E-Commerce", "exchange": "NSE", "default_spot": 195.0},
    {"symbol": "SWIGGY", "name": "Swiggy Limited", "sector": "Food Delivery & Quick Commerce", "exchange": "NSE", "default_spot": 420.0},
    {"symbol": "HAPPSTMNDS", "name": "Happiest Minds Technologies", "sector": "Digital IT", "exchange": "NSE", "default_spot": 780.0},
    {"symbol": "INTELLECT", "name": "Intellect Design Arena", "sector": "Fintech Software", "exchange": "NSE", "default_spot": 890.0},
    {"symbol": "KFINTECH", "name": "KFin Technologies Ltd", "sector": "Fintech Registrar", "exchange": "NSE", "default_spot": 1050.0},

    # Oil, Gas & Energy
    {"symbol": "RELIANCE", "name": "Reliance Industries", "sector": "Conglomerate", "exchange": "NSE", "default_spot": 1170.0},
    {"symbol": "ONGC", "name": "Oil & Natural Gas Corporation", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 290.0},
    {"symbol": "BPCL", "name": "Bharat Petroleum Corporation", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 340.0},
    {"symbol": "IOC", "name": "Indian Oil Corporation", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 165.0},
    {"symbol": "HPCL", "name": "Hindustan Petroleum Corp", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 390.0},
    {"symbol": "COALINDIA", "name": "Coal India Limited", "sector": "Mining & Energy", "exchange": "NSE", "default_spot": 480.0},
    {"symbol": "NTPC", "name": "NTPC Limited", "sector": "Power Generation", "exchange": "NSE", "default_spot": 410.0},
    {"symbol": "POWERGRID", "name": "Power Grid Corporation", "sector": "Power Transmission", "exchange": "NSE", "default_spot": 330.0},
    {"symbol": "GAIL", "name": "GAIL (India) Limited", "sector": "Natural Gas", "exchange": "NSE", "default_spot": 220.0},
    {"symbol": "PETRONET", "name": "Petronet LNG Limited", "sector": "Oil & Gas", "exchange": "NSE", "default_spot": 320.0},
    {"symbol": "IGL", "name": "Indraprastha Gas Limited", "sector": "City Gas", "exchange": "NSE", "default_spot": 410.0},
    {"symbol": "MGL", "name": "Mahanagar Gas Limited", "sector": "City Gas", "exchange": "NSE", "default_spot": 1650.0},
    {"symbol": "GUJGASLTD", "name": "Gujarat Gas Limited", "sector": "City Gas", "exchange": "NSE", "default_spot": 560.0},
    {"symbol": "OIL", "name": "Oil India Limited", "sector": "Oil Exploration", "exchange": "NSE", "default_spot": 510.0},
    {"symbol": "NHPC", "name": "NHPC Limited (Hydro Power)", "sector": "Renewable Power", "exchange": "NSE", "default_spot": 88.0},
    {"symbol": "SJVN", "name": "SJVN Limited", "sector": "Power Generation", "exchange": "NSE", "default_spot": 115.0},
    {"symbol": "IREDA", "name": "Indian Renewable Energy Dev", "sector": "Green Energy NBFC", "exchange": "NSE", "default_spot": 210.0},
    {"symbol": "SUZLON", "name": "Suzlon Energy Limited", "sector": "Wind Energy", "exchange": "NSE", "default_spot": 58.0},
    {"symbol": "JSWENERGY", "name": "JSW Energy Limited", "sector": "Power Generation", "exchange": "NSE", "default_spot": 680.0},
    {"symbol": "TORNTPOWER", "name": "Torrent Power Limited", "sector": "Power Generation", "exchange": "NSE", "default_spot": 1780.0},

    # Adani Group
    {"symbol": "ADANIENT", "name": "Adani Enterprises", "sector": "Conglomerate", "exchange": "NSE", "default_spot": 2980.0},
    {"symbol": "ADANIPORTS", "name": "Adani Ports and SEZ", "sector": "Ports & Logistics", "exchange": "NSE", "default_spot": 1380.0},
    {"symbol": "ADANIPOWER", "name": "Adani Power Limited", "sector": "Power Generation", "exchange": "NSE", "default_spot": 640.0},
    {"symbol": "ADANIGREEN", "name": "Adani Green Energy", "sector": "Renewable Energy", "exchange": "NSE", "default_spot": 1750.0},
    {"symbol": "ADANIENSOL", "name": "Adani Energy Solutions", "sector": "Power Transmission", "exchange": "NSE", "default_spot": 980.0},
    {"symbol": "ATGL", "name": "Adani Total Gas Limited", "sector": "Gas Distribution", "exchange": "NSE", "default_spot": 780.0},
    {"symbol": "AWL", "name": "Adani Wilmar Limited", "sector": "Edible Oils & FMCG", "exchange": "NSE", "default_spot": 330.0},

    # Automobile & Components
    {"symbol": "MARUTI", "name": "Maruti Suzuki India", "sector": "Automobile", "exchange": "NSE", "default_spot": 12400.0},
    {"symbol": "M&M", "name": "Mahindra & Mahindra", "sector": "Automobile", "exchange": "NSE", "default_spot": 2950.0},
    {"symbol": "BAJAJ-AUTO", "name": "Bajaj Auto Limited", "sector": "Automobile", "exchange": "NSE", "default_spot": 11500.0},
    {"symbol": "HEROMOTOCO", "name": "Hero MotoCorp Limited", "sector": "Automobile", "exchange": "NSE", "default_spot": 5100.0},
    {"symbol": "EICHERMOT", "name": "Eicher Motors (Royal Enfield)", "sector": "Automobile", "exchange": "NSE", "default_spot": 4800.0},
    {"symbol": "TVSMOTOR", "name": "TVS Motor Company", "sector": "Automobile", "exchange": "NSE", "default_spot": 2400.0},
    {"symbol": "MOTHERSON", "name": "Samvardhana Motherson", "sector": "Auto Components", "exchange": "NSE", "default_spot": 190.0},
    {"symbol": "BHARATFORG", "name": "Bharat Forge Limited", "sector": "Auto Forgings & Defense", "exchange": "NSE", "default_spot": 1450.0},
    {"symbol": "BOSCHLTD", "name": "Bosch Limited", "sector": "Auto Components", "exchange": "NSE", "default_spot": 33500.0},
    {"symbol": "MRF", "name": "MRF Limited (Tyres)", "sector": "Tyres", "exchange": "NSE", "default_spot": 135000.0},
    {"symbol": "APOLLOTYRE", "name": "Apollo Tyres Limited", "sector": "Tyres", "exchange": "NSE", "default_spot": 510.0},
    {"symbol": "BALKRISIND", "name": "Balkrishna Industries Ltd", "sector": "Tyres", "exchange": "NSE", "default_spot": 2950.0},
    {"symbol": "EXIDEIND", "name": "Exide Industries Limited", "sector": "Batteries", "exchange": "NSE", "default_spot": 480.0},
    {"symbol": "AMARAJABAT", "name": "Amara Raja Energy (Batteries)", "sector": "Batteries", "exchange": "NSE", "default_spot": 1350.0},
    {"symbol": "SONACOMS", "name": "Sona BLW Precision Forgings", "sector": "EV Components", "exchange": "NSE", "default_spot": 690.0},
    {"symbol": "TIINDIA", "name": "Tube Investments of India", "sector": "Auto & Engineering", "exchange": "NSE", "default_spot": 4100.0},
    {"symbol": "ESCORTS", "name": "Escorts Kubota Limited", "sector": "Tractors & Agri", "exchange": "NSE", "default_spot": 3800.0},
    {"symbol": "ASHOKLEY", "name": "Ashok Leyland Limited", "sector": "Commercial Vehicles", "exchange": "NSE", "default_spot": 220.0},

    # Metals & Mining
    {"symbol": "JINDALSTEL", "name": "Jindal Steel & Power", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 980.0},
    {"symbol": "JSWSTEEL", "name": "JSW Steel Limited", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 980.0},
    {"symbol": "HINDALCO", "name": "Hindalco Industries Ltd", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 690.0},
    {"symbol": "VEDL", "name": "Vedanta Limited", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 480.0},
    {"symbol": "NMDC", "name": "NMDC Limited", "sector": "Mining", "exchange": "NSE", "default_spot": 225.0},
    {"symbol": "SAIL", "name": "Steel Authority of India", "sector": "Metals & Mining", "exchange": "NSE", "default_spot": 135.0},
    {"symbol": "NATIONALUM", "name": "National Aluminium Co (NALCO)", "sector": "Aluminium", "exchange": "NSE", "default_spot": 220.0},
    {"symbol": "HINDZINC", "name": "Hindustan Zinc Limited", "sector": "Zinc & Silver Mining", "exchange": "NSE", "default_spot": 510.0},
    {"symbol": "APLAPOLLO", "name": "APL Apollo Tubes Ltd", "sector": "Steel Tubes", "exchange": "NSE", "default_spot": 1500.0},
    {"symbol": "HINDCOPPER", "name": "Hindustan Copper Limited", "sector": "Mining", "exchange": "NSE", "default_spot": 310.0},

    # Pharma & Healthcare
    {"symbol": "SUNPHARMA", "name": "Sun Pharmaceutical", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1850.0},
    {"symbol": "CIPLA", "name": "Cipla Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1500.0},
    {"symbol": "DRREDDY", "name": "Dr. Reddy's Laboratories", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 6500.0},
    {"symbol": "DIVISLAB", "name": "Divi's Laboratories", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 5200.0},
    {"symbol": "APOLLOHOSP", "name": "Apollo Hospitals Enterprise", "sector": "Healthcare", "exchange": "NSE", "default_spot": 6800.0},
    {"symbol": "LUPIN", "name": "Lupin Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 2100.0},
    {"symbol": "AUROPHARMA", "name": "Aurobindo Pharma Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1450.0},
    {"symbol": "TORNTPHARM", "name": "Torrent Pharmaceuticals", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 3300.0},
    {"symbol": "ZYDUSLIFE", "name": "Zydus Lifesciences Ltd", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1050.0},
    {"symbol": "ALKEM", "name": "Alkem Laboratories Ltd", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 5600.0},
    {"symbol": "BIOCON", "name": "Biocon Limited", "sector": "Biotech", "exchange": "NSE", "default_spot": 340.0},
    {"symbol": "MANKIND", "name": "Mankind Pharma Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 2600.0},
    {"symbol": "GLENMARK", "name": "Glenmark Pharmaceuticals", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1750.0},
    {"symbol": "IPCALAB", "name": "IPCA Laboratories Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 1550.0},
    {"symbol": "LAURUSLABS", "name": "Laurus Labs Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 460.0},
    {"symbol": "GRANULES", "name": "Granules India Limited", "sector": "Pharmaceuticals", "exchange": "NSE", "default_spot": 560.0},
    {"symbol": "FORTIS", "name": "Fortis Healthcare Limited", "sector": "Hospitals", "exchange": "NSE", "default_spot": 610.0},
    {"symbol": "MAXHEALTH", "name": "Max Healthcare Institute", "sector": "Hospitals", "exchange": "NSE", "default_spot": 980.0},

    # FMCG & Retail
    {"symbol": "HINDUNILVR", "name": "Hindustan Unilever Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 2750.0},
    {"symbol": "ITC", "name": "ITC Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 266.0},
    {"symbol": "NESTLEIND", "name": "Nestle India Limited", "sector": "FMCG", "exchange": "NSE", "default_spot": 2450.0},
    {"symbol": "BRITANNIA", "name": "Britannia Industries Ltd", "sector": "FMCG", "exchange": "NSE", "default_spot": 5800.0},
    {"symbol": "DABUR", "name": "Dabur India Limited", "sector": "FMCG", "exchange": "NSE", "default_spot": 540.0},
    {"symbol": "MARICO", "name": "Marico Limited", "sector": "FMCG", "exchange": "NSE", "default_spot": 640.0},
    {"symbol": "GODREJCP", "name": "Godrej Consumer Products", "sector": "FMCG", "exchange": "NSE", "default_spot": 1280.0},
    {"symbol": "COLPAL", "name": "Colgate-Palmolive India", "sector": "FMCG", "exchange": "NSE", "default_spot": 3300.0},
    {"symbol": "VBL", "name": "Varun Beverages (PepsiCo)", "sector": "Beverages", "exchange": "NSE", "default_spot": 610.0},
    {"symbol": "DEVYANI", "name": "Devyani International (KFC/PizzaHut)", "sector": "QSR / Dining", "exchange": "NSE", "default_spot": 175.0},
    {"symbol": "JUBLFOOD", "name": "Jubilant FoodWorks (Domino's)", "sector": "QSR / Dining", "exchange": "NSE", "default_spot": 620.0},
    {"symbol": "DMART", "name": "Avenue Supermarts (DMart)", "sector": "Retail Supermarkets", "exchange": "NSE", "default_spot": 4100.0},
    {"symbol": "RADICO", "name": "Radico Khaitan Limited", "sector": "Beverages / Alcohol", "exchange": "NSE", "default_spot": 2250.0},
    {"symbol": "UNITEDSPR", "name": "United Spirits (Diageo)", "sector": "Beverages / Alcohol", "exchange": "NSE", "default_spot": 1480.0},

    # Infrastructure, Capital Goods, Defence & Engineering
    {"symbol": "LT", "name": "Larsen & Toubro Ltd", "sector": "Engineering & Infra", "exchange": "NSE", "default_spot": 3600.0},
    {"symbol": "BEL", "name": "Bharat Electronics Ltd", "sector": "Defense & Electronics", "exchange": "NSE", "default_spot": 290.0},
    {"symbol": "HAL", "name": "Hindustan Aeronautics Ltd", "sector": "Defense & Aerospace", "exchange": "NSE", "default_spot": 4400.0},
    {"symbol": "BHEL", "name": "Bharat Heavy Electricals", "sector": "Power Equipment", "exchange": "NSE", "default_spot": 260.0},
    {"symbol": "ABB", "name": "ABB India Limited", "sector": "Electrification & Automation", "exchange": "NSE", "default_spot": 7800.0},
    {"symbol": "SIEMENS", "name": "Siemens Limited", "sector": "Industrial Engineering", "exchange": "NSE", "default_spot": 7100.0},
    {"symbol": "CUMMINSIND", "name": "Cummins India Limited", "sector": "Engines & Power", "exchange": "NSE", "default_spot": 3650.0},
    {"symbol": "THERMAX", "name": "Thermax Limited", "sector": "Energy & Environment", "exchange": "NSE", "default_spot": 4850.0},
    {"symbol": "CGPOWER", "name": "CG Power and Industrial", "sector": "Electrical Equipment", "exchange": "NSE", "default_spot": 740.0},
    {"symbol": "POLYCAB", "name": "Polycab India Limited", "sector": "Wires & Cables", "exchange": "NSE", "default_spot": 6800.0},
    {"symbol": "KEI", "name": "KEI Industries Limited", "sector": "Wires & Cables", "exchange": "NSE", "default_spot": 4400.0},
    {"symbol": "HAVELLS", "name": "Havells India Limited", "sector": "Consumer Electricals", "exchange": "NSE", "default_spot": 1820.0},
    {"symbol": "DIXON", "name": "Dixon Technologies India", "sector": "Electronics Manufacturing", "exchange": "NSE", "default_spot": 14800.0},
    {"symbol": "KAYNES", "name": "Kaynes Technology India", "sector": "Electronics Manufacturing", "exchange": "NSE", "default_spot": 5600.0},
    {"symbol": "ASTRAL", "name": "Astral Limited (Pipes)", "sector": "Building Materials", "exchange": "NSE", "default_spot": 1850.0},
    {"symbol": "SUPREMEIND", "name": "Supreme Industries Ltd", "sector": "Plastics & Pipes", "exchange": "NSE", "default_spot": 4900.0},
    {"symbol": "MAZDOCK", "name": "Mazagon Dock Shipbuilders", "sector": "Defense & Shipyards", "exchange": "NSE", "default_spot": 4200.0},
    {"symbol": "COCHINSHIP", "name": "Cochin Shipyard Limited", "sector": "Defense & Shipbuilding", "exchange": "NSE", "default_spot": 1600.0},
    {"symbol": "GRSE", "name": "Garden Reach Shipbuilders", "sector": "Defense Shipbuilding", "exchange": "NSE", "default_spot": 1750.0},
    {"symbol": "BDL", "name": "Bharat Dynamics Limited", "sector": "Defense Missiles", "exchange": "NSE", "default_spot": 1150.0},
    {"symbol": "RVNL", "name": "Rail Vikas Nigam Limited", "sector": "Rail Infrastructure", "exchange": "NSE", "default_spot": 420.0},
    {"symbol": "IRCTC", "name": "Indian Railway Catering & Tourism", "sector": "Railways", "exchange": "NSE", "default_spot": 880.0},
    {"symbol": "RAILTEL", "name": "RailTel Corporation of India", "sector": "Telecom & Rail Infra", "exchange": "NSE", "default_spot": 390.0},
    {"symbol": "RITES", "name": "RITES Limited", "sector": "Transport Consultancy", "exchange": "NSE", "default_spot": 310.0},
    {"symbol": "NBCC", "name": "NBCC (India) Limited", "sector": "Civil Construction", "exchange": "NSE", "default_spot": 95.0},
    {"symbol": "GMRINFRA", "name": "GMR Airports Infrastructure", "sector": "Airports & Infra", "exchange": "NSE", "default_spot": 85.0},

    # Cement & Building
    {"symbol": "ULTRACEMCO", "name": "UltraTech Cement Limited", "sector": "Cement", "exchange": "NSE", "default_spot": 11200.0},
    {"symbol": "GRASIM", "name": "Grasim Industries Ltd", "sector": "Cement & Chemicals", "exchange": "NSE", "default_spot": 2650.0},
    {"symbol": "AMBUJACEM", "name": "Ambuja Cements Limited", "sector": "Cement", "exchange": "NSE", "default_spot": 560.0},
    {"symbol": "ACC", "name": "ACC Limited", "sector": "Cement", "exchange": "NSE", "default_spot": 2250.0},
    {"symbol": "SHREECEM", "name": "Shree Cement Limited", "sector": "Cement", "exchange": "NSE", "default_spot": 24800.0},
    {"symbol": "DALBHARAT", "name": "Dalmia Bharat Limited", "sector": "Cement", "exchange": "NSE", "default_spot": 1820.0},
    {"symbol": "JKCEMENT", "name": "JK Cement Limited", "sector": "Cement", "exchange": "NSE", "default_spot": 4350.0},
    {"symbol": "RAMCOCEM", "name": "The Ramco Cements Ltd", "sector": "Cement", "exchange": "NSE", "default_spot": 880.0},

    # Chemicals & Paints
    {"symbol": "PIDILITIND", "name": "Pidilite Industries (Fevicol)", "sector": "Specialty Chemicals", "exchange": "NSE", "default_spot": 3100.0},
    {"symbol": "SRF", "name": "SRF Limited", "sector": "Chemicals", "exchange": "NSE", "default_spot": 2350.0},
    {"symbol": "DEEPAKNTR", "name": "Deepak Nitrite Limited", "sector": "Chemicals", "exchange": "NSE", "default_spot": 2850.0},
    {"symbol": "AARTIIND", "name": "Aarti Industries Limited", "sector": "Specialty Chemicals", "exchange": "NSE", "default_spot": 510.0},
    {"symbol": "NAVINFLUOR", "name": "Navin Fluorine International", "sector": "Fluorochemicals", "exchange": "NSE", "default_spot": 3400.0},
    {"symbol": "ATUL", "name": "Atul Limited", "sector": "Chemicals", "exchange": "NSE", "default_spot": 7200.0},
    {"symbol": "PIIND", "name": "PI Industries Limited", "sector": "Agro Chemicals", "exchange": "NSE", "default_spot": 4400.0},
    {"symbol": "UPL", "name": "UPL Limited", "sector": "Agrochemicals", "exchange": "NSE", "default_spot": 540.0},
    {"symbol": "COROMANDEL", "name": "Coromandel International Ltd", "sector": "Fertilizers", "exchange": "NSE", "default_spot": 1750.0},
    {"symbol": "ASIANPAINT", "name": "Asian Paints Limited", "sector": "Paints", "exchange": "NSE", "default_spot": 3100.0},
    {"symbol": "BERGEPAINT", "name": "Berger Paints India Ltd", "sector": "Paints", "exchange": "NSE", "default_spot": 510.0},

    # Telecom, Media & Entertainment
    {"symbol": "BHARTIARTL", "name": "Bharti Airtel Limited", "sector": "Telecom", "exchange": "NSE", "default_spot": 1650.0},
    {"symbol": "IDEA", "name": "Vodafone Idea Limited", "sector": "Telecom", "exchange": "NSE", "default_spot": 11.5},
    {"symbol": "INDUSTOWER", "name": "Indus Towers Limited", "sector": "Telecom Infrastructure", "exchange": "NSE", "default_spot": 380.0},
    {"symbol": "ZEEL", "name": "Zee Entertainment Enterprises", "sector": "Media & Entertainment", "exchange": "NSE", "default_spot": 125.0},
    {"symbol": "SUNTV", "name": "Sun TV Network Limited", "sector": "Broadcasting", "exchange": "NSE", "default_spot": 760.0},
    {"symbol": "PVRINOX", "name": "PVR INOX Limited", "sector": "Film Exhibition", "exchange": "NSE", "default_spot": 1580.0},

    # Real Estate
    {"symbol": "DLF", "name": "DLF Limited", "sector": "Real Estate", "exchange": "NSE", "default_spot": 860.0},
    {"symbol": "GODREJPROP", "name": "Godrej Properties Limited", "sector": "Real Estate", "exchange": "NSE", "default_spot": 3100.0},
    {"symbol": "OBEROREALTY", "name": "Oberoi Realty Limited", "sector": "Real Estate", "exchange": "NSE", "default_spot": 1950.0},
    {"symbol": "LODHA", "name": "Macrotech Developers (Lodha)", "sector": "Real Estate", "exchange": "NSE", "default_spot": 1200.0},
    {"symbol": "PRESTIGE", "name": "Prestige Estates Projects", "sector": "Real Estate", "exchange": "NSE", "default_spot": 1780.0},
]

# Quick map lookup for O(1) spot resolution
CATALOG_MAP: Dict[str, Dict[str, Any]] = {item["symbol"]: item for item in ALL_SYMBOLS_CATALOG}

@router.get("/symbols")
async def get_supported_symbols() -> Dict[str, Any]:
    indices = [s for s in ALL_SYMBOLS_CATALOG if "Index" in s.get("sector", "")]
    stocks = [s for s in ALL_SYMBOLS_CATALOG if "Index" not in s.get("sector", "")]
    return {
        "indices": indices,
        "stocks": stocks,
        "total_supported": len(ALL_SYMBOLS_CATALOG)
    }

@router.get("/search")
async def search_symbols(q: str = Query("", description="Search query string")) -> List[Dict[str, Any]]:
    query = q.strip().upper()
    if not query:
        return []

    exact = []
    symbol_starts = []
    name_starts = []
    name_words = []
    contains = []

    for item in ALL_SYMBOLS_CATALOG:
        sym = item["symbol"].upper()
        name = item.get("name", "").upper()
        words = name.replace("-", " ").replace("(", " ").replace(")", " ").split()

        if sym == query:
            exact.append(item)
        elif sym.startswith(query):
            symbol_starts.append(item)
        elif name.startswith(query):
            name_starts.append(item)
        elif any(w.startswith(query) for w in words):
            name_words.append(item)
        elif query in sym or query in name:
            contains.append(item)

    results = []
    seen = set()
    for category in [exact, symbol_starts, name_starts, name_words, contains]:
        for it in category:
            if it["symbol"] not in seen:
                seen.add(it["symbol"])
                results.append(it)

    # If the user typed an arbitrary ticker that is not directly in catalog,
    # generate a dynamic custom result so 100% of equities are searchable & selectable
    if query not in seen and len(query) >= 2:
        dynamic_item = {
            "symbol": query,
            "name": f"{query} Listed Equity",
            "sector": "Equities",
            "exchange": "NSE",
            "default_spot": 1000.0
        }
        results.append(dynamic_item)

    return results[:20]

@router.get("/ticker")
async def get_default_ticker() -> List[Dict[str, Any]]:
    """
    Returns real-time or cached quotes for the default market ticker tape.
    """
    default_symbols = [
        "NIFTY", "BANKNIFTY", "FINNIFTY", "SENSEX",
        "RELIANCE", "TCS", "HDFCBANK", "INFY", "SBIN",
        "TATAMOTORS", "ITC", "BHARTIARTL", "BAJFINANCE", "LT",
        "MARUTI", "ZOMATO", "SUNPHARMA", "TATASTEEL", "ADANIENT",
        "WIPRO", "TITAN", "ICICIBANK", "AXISBANK", "BEL", "HAL"
    ]

    ticker_items = []
    for sym in default_symbols:
        item = CATALOG_MAP.get(sym, {"name": sym, "default_spot": 1000.0})
        quote = MarketDataFetcher.get_realtime_quote(sym)
        spot = quote.get("spot_price") or item.get("default_spot", 1000.0)
        change = quote.get("change", 0.0)
        change_pct = quote.get("change_percent", 0.0)
        is_pos = change >= 0

        ticker_items.append({
            "symbol": sym,
            "name": item.get("name", sym),
            "spot_price": spot,
            "change": change,
            "change_percent": change_pct,
            "is_positive": is_pos
        })

    return ticker_items

@router.get("/quote/{symbol}")
async def get_quote_and_technicals(symbol: str) -> Dict[str, Any]:
    sym = symbol.upper().strip()
    quote = MarketDataFetcher.get_realtime_quote(sym)
    spot = quote["spot_price"]
    technicals = TechnicalAnalysisEngine.evaluate_technicals(spot_price=spot)

    return {
        "symbol": sym,
        "spot_price": spot,
        "previous_close": quote["previous_close"],
        "change": quote["change"],
        "change_percent": quote["change_percent"],
        "is_realtime": quote["is_realtime"],
        "step": quote["step"],
        "lot_size": quote["lot_size"],
        "technicals": technicals
    }

@router.get("/news")
async def get_daily_market_news() -> Dict[str, Any]:
    from app.services.market_news_service import MarketNewsService
    return MarketNewsService.get_daily_briefing()
