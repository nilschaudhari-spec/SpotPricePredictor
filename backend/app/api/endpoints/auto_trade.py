from fastapi import APIRouter, HTTPException, Query, Body
from typing import Optional, List, Dict, Any

from app.models.schemas import (
    AutoTradeSettings, AutoTradeOrder, VirtualPortfolio,
    ScanAndExecuteResponse, ManualOrderRequest, SquareOffRequest,
    StrategyConditionItem
)
from app.services.frontpage_trading_service import FrontPageTradingService
from app.services.auto_trade_scanner_service import AutoTradeScannerService
from app.services.strategy_condition_service import StrategyConditionService
from app.services.market_data_fetcher import MarketDataFetcher

router = APIRouter(prefix="/auto-trade", tags=["Anvest & FrontPage Automated Paper Trading"])

@router.get("/settings", response_model=AutoTradeSettings)
async def get_auto_trade_settings() -> AutoTradeSettings:
    """
    Returns current Auto-Trading engine configuration, including FrontPage credentials,
    risk parameters, scan interval, and execution broker destination.
    """
    return FrontPageTradingService.get_settings()

@router.post("/settings", response_model=AutoTradeSettings)
async def update_auto_trade_settings(settings: AutoTradeSettings) -> AutoTradeSettings:
    """
    Updates Auto-Trading engine configuration:
    - Master Switch (is_auto_trade_enabled)
    - Broker destination (FRONTPAGE, IN_APP_PAPER, TRADINGVIEW_WEBHOOK, SENSIBULL_VIRTUAL)
    - FrontPage user token & account ID
    - Min reliability threshold & capital limits
    """
    return FrontPageTradingService.update_settings(settings)

@router.get("/portfolio", response_model=VirtualPortfolio)
async def get_virtual_portfolio() -> VirtualPortfolio:
    """
    Returns the real-time Virtual Portfolio for Frontpage / Demo trading:
    - ₹10,00,000 Virtual Capital
    - Live MTM Unrealized P&L
    - Realized P&L & Win Rate %
    - Active open positions with live CMP and SL/Target levels
    - Chronological order audit history
    """
    return FrontPageTradingService.get_portfolio()

@router.post("/scan-and-execute", response_model=ScanAndExecuteResponse)
async def scan_all_stocks_and_execute(
    force_execute: bool = Query(False, description="Force order placement even if auto-trade switch is off"),
    symbols: Optional[List[str]] = Query(None, description="Optional custom list of symbols to scan")
) -> ScanAndExecuteResponse:
    """
    Scans entire universe of Indian stocks & indices (NSE & BSE) for candlestick & price action patterns.
    Automatically places BUY/SELL orders on FrontPage or the demo broker for qualified patterns.
    """
    try:
        return AutoTradeScannerService.scan_and_execute(force_execute=force_execute, custom_symbols=symbols)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Scanning & execution failed: {str(e)}")

@router.post("/order", response_model=AutoTradeOrder)
async def place_manual_order(req: ManualOrderRequest) -> AutoTradeOrder:
    """
    Manually places a BUY or SELL order for a specific stock/index into FrontPage or the virtual desk.
    """
    try:
        quote = MarketDataFetcher.get_realtime_quote(req.symbol)
        price = req.price or quote.get("spot_price", 1000.0)
        co_name = quote.get("company_name", f"{req.symbol.upper()} Ltd")

        sl = req.stop_loss or round(price * (0.985 if req.action.upper() == "BUY" else 1.015), 2)
        tgt = req.target_price or round(price * (1.03 if req.action.upper() == "BUY" else 0.97), 2)

        return FrontPageTradingService.execute_order(
            symbol=req.symbol.upper(),
            action=req.action.upper(),
            price=price,
            stop_loss=sl,
            target_price=tgt,
            pattern_trigger=req.pattern_trigger,
            quantity=req.quantity,
            product_type=req.product_type,
            company_name=co_name
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Order execution failed: {str(e)}")

@router.post("/square-off", response_model=VirtualPortfolio)
async def square_off_positions(req: SquareOffRequest = Body(...)) -> VirtualPortfolio:
    """
    Squares off a specific open position or all open positions at current market price.
    """
    try:
        FrontPageTradingService.square_off_position(req.position_id)
        return FrontPageTradingService.get_portfolio()
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Square-off failed: {str(e)}")

@router.post("/test-broker")
async def test_broker_connection() -> Dict[str, Any]:
    """
    Tests connectivity to Anvest Paper Trading (anvest.in), FrontPage (front.page), or Webhook endpoint.
    """
    return FrontPageTradingService.test_broker_connection()

@router.get("/strategy-conditions", response_model=List[StrategyConditionItem])
async def get_strategy_conditions(
    symbol: str = Query("NIFTY", description="Stock or Index symbol")
) -> List[StrategyConditionItem]:
    """
    Evaluates all strategy conditions for a specific stock/index:
    - 5M Intraday Tri-Confirmation (ST + VWAP + MACD)
    - Golden Cross 50/200 EMA & Pullback Re-entry
    - Bollinger Bands Breakout
    - Price Action Consolidation Box Breakout
    - Open Interest (OI) Institutional Buildup
    - Candlestick & Price Action Formations
    """
    try:
        quote = MarketDataFetcher.get_realtime_quote(symbol)
        co_name = quote.get("company_name", f"{symbol.upper()} Ltd")
        return StrategyConditionService.evaluate_strategies_for_symbol(symbol, co_name)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to evaluate strategy conditions: {str(e)}")
