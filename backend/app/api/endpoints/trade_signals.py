from fastapi import APIRouter, Query, HTTPException
from typing import Optional
from app.models.schemas import TradeSuggestionsResponse, ChartDataResponse
from app.services.trade_suggestion_engine import TradeSuggestionEngine
from app.services.chart_service import ChartService

router = APIRouter(prefix="/trade-signals", tags=["Trade Suggestions & Charts"])

@router.get("/suggestions", response_model=TradeSuggestionsResponse)
async def get_trade_suggestions(
    category: Optional[str] = Query("ALL", description="Filter by ALL, INDEX, or EQUITY"),
    instrument: Optional[str] = Query("ALL", description="Filter by ALL, EQUITY, or FNO"),
    action: Optional[str] = Query("ALL", description="Filter by ALL, BUY, or SELL"),
    symbol: Optional[str] = Query(None, description="Optional single ticker query (e.g. NIFTY, RELIANCE)")
):
    """
    Returns high-conviction algorithmic Buy/Sell trade suggestions across Indices and Equities,
    spanning both Cash (Equity) and Derivatives (F&O: Futures & Options).
    Every trade idea is supported by comprehensive Technical Analysis (EMA, Supertrend, VWAP, RSI, MACD)
    and Multi-Model Quantitative Prediction confluence.
    """
    try:
        return TradeSuggestionEngine.get_suggestions(
            category=category,
            instrument=instrument,
            action=action,
            symbol=symbol
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate trade suggestions: {str(e)}")

@router.get("/chart/{symbol}", response_model=ChartDataResponse)
async def get_chart_with_indicators(
    symbol: str,
    interval: str = Query("15m", description="Candle interval: 15s, 30s, 1m, 5m, 15m, 30m, 1h, 4h, 1D, 1W, 1M, 1Y"),
    limit: int = Query(60, ge=10, le=200, description="Number of candles")
):
    """
    Returns multi-timeframe OHLCV Candlestick data with pre-calculated Technical Indicators:
    - Moving Averages (EMA 20, EMA 50, SMA 200)
    - Volume Weighted Average Price (VWAP)
    - Bollinger Bands (20, 2)
    - Supertrend (10, 3)
    - Relative Strength Index (RSI 14)
    - Moving Average Convergence Divergence (MACD 12, 26, 9)
    """
    try:
        return ChartService.get_chart_data(symbol=symbol, interval=interval, limit=limit)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to fetch chart data for {symbol}: {str(e)}")
