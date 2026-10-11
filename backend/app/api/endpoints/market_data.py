from fastapi import APIRouter
from typing import List, Dict, Any
from app.services.market_data_fetcher import MarketDataFetcher
from app.services.technical_analysis import TechnicalAnalysisEngine

router = APIRouter(prefix="/market", tags=["Market Data"])

@router.get("/symbols")
async def get_supported_symbols() -> Dict[str, Any]:
    return {
        "indices": [
            {"symbol": "NIFTY", "name": "Nifty 50 Index", "exchange": "NSE", "default_spot": 22520.0},
            {"symbol": "BANKNIFTY", "name": "Bank Nifty Index", "exchange": "NSE", "default_spot": 55250.0},
            {"symbol": "FINNIFTY", "name": "Nifty Financial Services", "exchange": "NSE", "default_spot": 23600.0},
        ],
        "stocks": [
            {"symbol": "RELIANCE", "name": "Reliance Industries", "exchange": "NSE", "default_spot": 1170.0},
            {"symbol": "TCS", "name": "Tata Consultancy Services", "exchange": "NSE", "default_spot": 2156.0},
            {"symbol": "HDFCBANK", "name": "HDFC Bank Ltd", "exchange": "NSE", "default_spot": 707.0},
            {"symbol": "INFY", "name": "Infosys Ltd", "exchange": "NSE", "default_spot": 1023.0},
            {"symbol": "SBIN", "name": "State Bank of India", "exchange": "NSE", "default_spot": 959.0},
            {"symbol": "ITC", "name": "ITC Ltd", "exchange": "NSE", "default_spot": 266.0},
            {"symbol": "WIPRO", "name": "Wipro Ltd", "exchange": "NSE", "default_spot": 162.0},
            {"symbol": "BAJFINANCE", "name": "Bajaj Finance Ltd", "exchange": "NSE", "default_spot": 960.0},
        ]
    }

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
