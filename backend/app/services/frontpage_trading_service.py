import uuid
import httpx
from datetime import datetime, timezone, timedelta
from typing import List, Dict, Any, Optional

from app.models.schemas import (
    AutoTradeSettings, AutoTradeOrder, VirtualPosition,
    VirtualPortfolio, ManualOrderRequest
)
from app.services.market_data_fetcher import MarketDataFetcher

class FrontPageTradingService:
    """
    Frontpage & Demo Trading Integration Service.
    Connects with Frontpage (frontpage.app) virtual trading bridge,
    TradingView / custom webhooks, and maintains an in-memory high-precision
    Virtual Trading Engine with ₹10,00,000 margin, live MTM P&L, and auto SL/Target triggers.
    """

    _SETTINGS = AutoTradeSettings()

    _PORTFOLIO = VirtualPortfolio(
        total_capital=1000000.0,
        available_margin=1000000.0,
        used_margin=0.0,
        unrealized_pnl=0.0,
        realized_pnl=0.0,
        total_trades=0,
        winning_trades=0,
        losing_trades=0,
        win_rate_pct=0.0,
        broker_name="FrontPage Virtual Account",
        positions=[],
        order_history=[]
    )

    @classmethod
    def get_settings(cls) -> AutoTradeSettings:
        return cls._SETTINGS

    @classmethod
    def update_settings(cls, new_settings: AutoTradeSettings) -> AutoTradeSettings:
        cls._SETTINGS = new_settings
        if new_settings.broker_destination == "FRONTPAGE":
            cls._PORTFOLIO.broker_name = "FrontPage Virtual Account"
        elif new_settings.broker_destination == "TRADINGVIEW_WEBHOOK":
            cls._PORTFOLIO.broker_name = "TradingView Webhook Bridge"
        elif new_settings.broker_destination == "SENSIBULL_VIRTUAL":
            cls._PORTFOLIO.broker_name = "Sensibull Virtual Sandbox"
        else:
            cls._PORTFOLIO.broker_name = "In-App Pro Virtual Broker"
        return cls._SETTINGS

    @classmethod
    def get_portfolio(cls) -> VirtualPortfolio:
        cls.update_positions_market_mtm()
        return cls._PORTFOLIO

    @classmethod
    def _now_ist_str(cls) -> str:
        ist_now = datetime.now(timezone(timedelta(hours=5, minutes=30)))
        return ist_now.strftime("%d-%b-%Y, %I:%M:%S %p IST")

    @classmethod
    def _calculate_lot_quantity(cls, symbol: str, price: float, max_capital: float) -> int:
        sym_u = symbol.upper()
        if sym_u in ["NIFTY", "NIFTY 50"]:
            return 50  # 1 standard lot
        elif sym_u in ["BANKNIFTY", "NIFTY BANK"]:
            return 15  # 1 standard lot
        elif sym_u in ["FINNIFTY"]:
            return 25  # 1 standard lot
        elif sym_u in ["SENSEX"]:
            return 10  # 1 standard lot
        else:
            if price <= 0:
                return 10
            qty = int(max_capital / price)
            return max(1, min(qty, 500))

    @classmethod
    def execute_order(
        cls,
        symbol: str,
        action: str,  # "BUY" or "SELL"
        price: float,
        stop_loss: float,
        target_price: float,
        pattern_trigger: str,
        quantity: Optional[int] = None,
        product_type: str = "INTRADAY",
        company_name: Optional[str] = None
    ) -> AutoTradeOrder:
        """
        Executes a Buy/Sell order on FrontPage or the virtual demo broker.
        Dispatches HTTP webhook to Frontpage if configured, and updates portfolio.
        """
        cls.update_positions_market_mtm()
        now_str = cls._now_ist_str()
        order_id = f"FP-{uuid.uuid4().hex[:8].upper()}"

        # Resolve company name
        co_name = company_name or f"{symbol} Limited"

        # Calculate quantity
        qty = quantity if (quantity and quantity > 0) else cls._calculate_lot_quantity(
            symbol=symbol,
            price=price,
            max_capital=cls._SETTINGS.max_capital_per_trade
        )

        total_val = round(price * qty, 2)
        required_margin = round(total_val * 0.20, 2) if product_type == "INTRADAY" else total_val

        # Risk Validation: Max positions check
        open_positions = [p for p in cls._PORTFOLIO.positions if p.status == "OPEN"]
        if len(open_positions) >= cls._SETTINGS.max_concurrent_positions:
            rejected_order = AutoTradeOrder(
                order_id=order_id,
                symbol=symbol.upper(),
                company_name=co_name,
                exchange="NSE",
                action=action.upper(),
                product_type=product_type,
                pattern_trigger=pattern_trigger,
                order_price=price,
                quantity=qty,
                total_value=total_val,
                stop_loss=stop_loss,
                target_price=target_price,
                status="REJECTED",
                broker=cls._PORTFOLIO.broker_name,
                broker_order_id="N/A",
                timestamp=now_str,
                execution_message=f"Order rejected: Max concurrent positions limit ({cls._SETTINGS.max_concurrent_positions}) reached."
            )
            cls._PORTFOLIO.order_history.insert(0, rejected_order)
            return rejected_order

        # Margin check
        if required_margin > cls._PORTFOLIO.available_margin:
            # Scale down quantity to fit available margin
            scale_factor = cls._PORTFOLIO.available_margin / max(1.0, required_margin)
            qty = max(1, int(qty * scale_factor))
            total_val = round(price * qty, 2)
            required_margin = round(total_val * 0.20, 2) if product_type == "INTRADAY" else total_val

            if required_margin > cls._PORTFOLIO.available_margin:
                rejected_order = AutoTradeOrder(
                    order_id=order_id,
                    symbol=symbol.upper(),
                    company_name=co_name,
                    exchange="NSE",
                    action=action.upper(),
                    product_type=product_type,
                    pattern_trigger=pattern_trigger,
                    order_price=price,
                    quantity=qty,
                    total_value=total_val,
                    stop_loss=stop_loss,
                    target_price=target_price,
                    status="REJECTED",
                    broker=cls._PORTFOLIO.broker_name,
                    broker_order_id="N/A",
                    timestamp=now_str,
                    execution_message="Order rejected: Insufficient virtual margin."
                )
                cls._PORTFOLIO.order_history.insert(0, rejected_order)
                return rejected_order

        # Dispatch to FrontPage or Webhook if endpoint provided
        broker_order_id = f"FRONTPAGE-ORD-{uuid.uuid4().hex[:6].upper()}"
        broker_status = "Executed on FrontPage Virtual Paper Desk"

        if cls._SETTINGS.broker_destination == "FRONTPAGE" and cls._SETTINGS.frontpage_user_token:
            try:
                # Dispatch order to FrontPage API bridge
                headers = {
                    "Authorization": f"Bearer {cls._SETTINGS.frontpage_user_token}",
                    "Content-Type": "application/json"
                }
                payload = {
                    "account_id": cls._SETTINGS.frontpage_account_id,
                    "symbol": symbol.upper(),
                    "action": action.upper(),
                    "quantity": qty,
                    "price": price,
                    "stop_loss": stop_loss,
                    "target_price": target_price,
                    "order_type": "MARKET",
                    "product": product_type,
                    "pattern": pattern_trigger,
                    "source": "SpotPricePredictor_AutoTrader"
                }
                with httpx.Client(timeout=2.0) as client:
                    resp = client.post("https://api.frontpage.app/v1/virtual/orders", json=payload, headers=headers)
                    if resp.status_code in [200, 201]:
                        broker_status = "Frontpage Cloud Virtual Desk Confirmed"
            except Exception:
                broker_status = "FrontPage Local Simulated Fallback"

        elif cls._SETTINGS.broker_destination == "TRADINGVIEW_WEBHOOK" and cls._SETTINGS.webhook_url:
            try:
                webhook_payload = {
                    "event": "AUTO_TRADE_ORDER",
                    "symbol": symbol.upper(),
                    "action": action.upper(),
                    "quantity": qty,
                    "price": price,
                    "stop_loss": stop_loss,
                    "target": target_price,
                    "pattern": pattern_trigger,
                    "timestamp": now_str
                }
                with httpx.Client(timeout=2.0) as client:
                    client.post(cls._SETTINGS.webhook_url, json=webhook_payload)
                broker_status = "Webhook Alert Dispatched"
            except Exception:
                broker_status = "Webhook Dispatch Failed - Logged Locally"

        # Create executed order
        executed_order = AutoTradeOrder(
            order_id=order_id,
            symbol=symbol.upper(),
            company_name=co_name,
            exchange="NSE",
            action=action.upper(),
            product_type=product_type,
            pattern_trigger=pattern_trigger,
            order_price=price,
            quantity=qty,
            total_value=total_val,
            stop_loss=stop_loss,
            target_price=target_price,
            status="EXECUTED",
            broker=cls._PORTFOLIO.broker_name,
            broker_order_id=broker_order_id,
            timestamp=now_str,
            execution_message=f"{broker_status}: {action.upper()} {qty} shares of {symbol.upper()} @ ₹{price:.2f} [Pattern: {pattern_trigger}]"
        )

        # Create Virtual Position
        pos_id = f"POS-{uuid.uuid4().hex[:8].upper()}"
        new_pos = VirtualPosition(
            position_id=pos_id,
            order_id=order_id,
            symbol=symbol.upper(),
            company_name=co_name,
            action=action.upper(),
            quantity=qty,
            avg_price=price,
            current_price=price,
            stop_loss=stop_loss,
            target_price=target_price,
            pnl=0.0,
            pnl_percent=0.0,
            pattern_trigger=pattern_trigger,
            broker=cls._PORTFOLIO.broker_name,
            status="OPEN",
            entry_time=now_str
        )

        # Update Portfolio Margins
        cls._PORTFOLIO.used_margin = round(cls._PORTFOLIO.used_margin + required_margin, 2)
        cls._PORTFOLIO.available_margin = round(cls._PORTFOLIO.total_capital - cls._PORTFOLIO.used_margin + cls._PORTFOLIO.realized_pnl, 2)
        cls._PORTFOLIO.total_trades += 1

        cls._PORTFOLIO.positions.insert(0, new_pos)
        cls._PORTFOLIO.order_history.insert(0, executed_order)

        return executed_order

    @classmethod
    def update_positions_market_mtm(cls):
        """
        Refreshes MTM (Mark-to-Market) P&L for all open positions based on live spot quotes.
        Automatically triggers exit if Stop Loss or Target price is breached.
        """
        now_str = cls._now_ist_str()
        total_unrealized = 0.0

        for pos in cls._PORTFOLIO.positions:
            if pos.status != "OPEN":
                continue

            # Fetch live spot price
            quote = MarketDataFetcher.get_realtime_quote(pos.symbol)
            live_price = quote.get("spot_price", pos.avg_price)
            pos.current_price = live_price

            # Calculate P&L
            if pos.action == "BUY":
                pnl = (live_price - pos.avg_price) * pos.quantity
                pnl_pct = ((live_price - pos.avg_price) / pos.avg_price) * 100.0 if pos.avg_price > 0 else 0.0
            else:  # SELL / SHORT
                pnl = (pos.avg_price - live_price) * pos.quantity
                pnl_pct = ((pos.avg_price - live_price) / pos.avg_price) * 100.0 if pos.avg_price > 0 else 0.0

            pos.pnl = round(pnl, 2)
            pos.pnl_percent = round(pnl_pct, 2)

            # Auto Target Trigger Check
            target_hit = False
            sl_hit = False

            if pos.action == "BUY":
                if live_price >= pos.target_price:
                    target_hit = True
                elif live_price <= pos.stop_loss:
                    sl_hit = True
            else:  # SELL
                if live_price <= pos.target_price:
                    target_hit = True
                elif live_price >= pos.stop_loss:
                    sl_hit = True

            if target_hit:
                pos.status = "CLOSED_TARGET"
                pos.exit_time = now_str
                pos.exit_price = live_price
                cls._PORTFOLIO.realized_pnl = round(cls._PORTFOLIO.realized_pnl + pos.pnl, 2)
                cls._PORTFOLIO.winning_trades += 1
                required_margin = round((pos.avg_price * pos.quantity) * 0.20, 2)
                cls._PORTFOLIO.used_margin = max(0.0, round(cls._PORTFOLIO.used_margin - required_margin, 2))

                # Record exit order in order history
                exit_order = AutoTradeOrder(
                    order_id=f"EXIT-{uuid.uuid4().hex[:6].upper()}",
                    symbol=pos.symbol,
                    company_name=pos.company_name,
                    exchange="NSE",
                    action="SELL" if pos.action == "BUY" else "BUY",
                    product_type="INTRADAY",
                    pattern_trigger=f"TARGET REACHED ({pos.pattern_trigger})",
                    order_price=live_price,
                    quantity=pos.quantity,
                    total_value=round(live_price * pos.quantity, 2),
                    stop_loss=pos.stop_loss,
                    target_price=pos.target_price,
                    status="EXECUTED",
                    broker=pos.broker,
                    broker_order_id=f"TARGET-HIT-{uuid.uuid4().hex[:6].upper()}",
                    timestamp=now_str,
                    execution_message=f"🎯 Target Reached! Closed {pos.symbol} @ ₹{live_price:.2f} with profit of +₹{pos.pnl:.2f}"
                )
                cls._PORTFOLIO.order_history.insert(0, exit_order)

            elif sl_hit:
                pos.status = "CLOSED_SL"
                pos.exit_time = now_str
                pos.exit_price = live_price
                cls._PORTFOLIO.realized_pnl = round(cls._PORTFOLIO.realized_pnl + pos.pnl, 2)
                cls._PORTFOLIO.losing_trades += 1
                required_margin = round((pos.avg_price * pos.quantity) * 0.20, 2)
                cls._PORTFOLIO.used_margin = max(0.0, round(cls._PORTFOLIO.used_margin - required_margin, 2))

                # Record exit order in order history
                exit_order = AutoTradeOrder(
                    order_id=f"EXIT-{uuid.uuid4().hex[:6].upper()}",
                    symbol=pos.symbol,
                    company_name=pos.company_name,
                    exchange="NSE",
                    action="SELL" if pos.action == "BUY" else "BUY",
                    product_type="INTRADAY",
                    pattern_trigger=f"STOP LOSS HIT ({pos.pattern_trigger})",
                    order_price=live_price,
                    quantity=pos.quantity,
                    total_value=round(live_price * pos.quantity, 2),
                    stop_loss=pos.stop_loss,
                    target_price=pos.target_price,
                    status="EXECUTED",
                    broker=pos.broker,
                    broker_order_id=f"SL-HIT-{uuid.uuid4().hex[:6].upper()}",
                    timestamp=now_str,
                    execution_message=f"🛡️ Stop Loss Protected! Closed {pos.symbol} @ ₹{live_price:.2f} with loss of ₹{pos.pnl:.2f}"
                )
                cls._PORTFOLIO.order_history.insert(0, exit_order)
            else:
                total_unrealized += pos.pnl

        cls._PORTFOLIO.unrealized_pnl = round(total_unrealized, 2)
        cls._PORTFOLIO.available_margin = round(cls._PORTFOLIO.total_capital - cls._PORTFOLIO.used_margin + cls._PORTFOLIO.realized_pnl, 2)

        # Win Rate calculation
        closed_trades = cls._PORTFOLIO.winning_trades + cls._PORTFOLIO.losing_trades
        if closed_trades > 0:
            cls._PORTFOLIO.win_rate_pct = round((cls._PORTFOLIO.winning_trades / closed_trades) * 100.0, 1)

    @classmethod
    def square_off_position(cls, position_id: Optional[str] = None) -> List[VirtualPosition]:
        """
        Manually squares off a specific position or all open positions.
        """
        cls.update_positions_market_mtm()
        now_str = cls._now_ist_str()
        closed: List[VirtualPosition] = []

        for pos in cls._PORTFOLIO.positions:
            if pos.status == "OPEN" and (position_id is None or position_id == "ALL" or pos.position_id == position_id):
                pos.status = "MANUALLY_CLOSED"
                pos.exit_time = now_str
                pos.exit_price = pos.current_price
                cls._PORTFOLIO.realized_pnl = round(cls._PORTFOLIO.realized_pnl + pos.pnl, 2)
                if pos.pnl >= 0:
                    cls._PORTFOLIO.winning_trades += 1
                else:
                    cls._PORTFOLIO.losing_trades += 1

                required_margin = round((pos.avg_price * pos.quantity) * 0.20, 2)
                cls._PORTFOLIO.used_margin = max(0.0, round(cls._PORTFOLIO.used_margin - required_margin, 2))

                # Add exit order record
                exit_order = AutoTradeOrder(
                    order_id=f"SQ-{uuid.uuid4().hex[:6].upper()}",
                    symbol=pos.symbol,
                    company_name=pos.company_name,
                    exchange="NSE",
                    action="SELL" if pos.action == "BUY" else "BUY",
                    product_type="INTRADAY",
                    pattern_trigger=f"MANUAL SQUARE OFF ({pos.pattern_trigger})",
                    order_price=pos.current_price,
                    quantity=pos.quantity,
                    total_value=round(pos.current_price * pos.quantity, 2),
                    stop_loss=pos.stop_loss,
                    target_price=pos.target_price,
                    status="EXECUTED",
                    broker=pos.broker,
                    broker_order_id=f"SQOFF-{uuid.uuid4().hex[:6].upper()}",
                    timestamp=now_str,
                    execution_message=f"Square Off Executed: Closed {pos.symbol} @ ₹{pos.current_price:.2f} (P&L: ₹{pos.pnl:+.2f})"
                )
                cls._PORTFOLIO.order_history.insert(0, exit_order)
                closed.append(pos)

        cls.update_positions_market_mtm()
        return closed

    @classmethod
    def test_broker_connection(cls) -> Dict[str, Any]:
        """
        Tests connectivity to FrontPage API or Webhook bridge.
        """
        dest = cls._SETTINGS.broker_destination
        if dest == "FRONTPAGE":
            token = cls._SETTINGS.frontpage_user_token
            if not token:
                return {
                    "broker": "FrontPage Virtual Trading",
                    "status": "ONLINE (Simulated FrontPage Desk)",
                    "message": "FrontPage Virtual Engine is ready. Virtual Margin: ₹10,00,000 available. Enter API token for cloud sync.",
                    "code": 200
                }
            try:
                headers = {"Authorization": f"Bearer {token}"}
                with httpx.Client(timeout=3.0) as client:
                    resp = client.get("https://api.frontpage.app/v1/ping", headers=headers)
                    return {
                        "broker": "FrontPage Cloud API",
                        "status": "CONNECTED" if resp.status_code == 200 else "MOCK_SYNC_ACTIVE",
                        "message": f"FrontPage response status: {resp.status_code}",
                        "code": resp.status_code
                    }
            except Exception as e:
                return {
                    "broker": "FrontPage Virtual Bridge",
                    "status": "ONLINE (In-App Sandbox)",
                    "message": f"FrontPage cloud bridge unreachable, operating in high-fidelity local sandbox mode: {str(e)}",
                    "code": 200
                }

        elif dest == "TRADINGVIEW_WEBHOOK":
            url = cls._SETTINGS.webhook_url
            if not url:
                return {
                    "broker": "TradingView Webhook",
                    "status": "CONFIG_REQUIRED",
                    "message": "Enter your Webhook URL to receive automated trade alerts.",
                    "code": 400
                }
            try:
                with httpx.Client(timeout=3.0) as client:
                    resp = client.post(url, json={"event": "PING_TEST", "source": "SpotPricePredictor", "time": cls._now_ist_str()})
                    return {
                        "broker": "TradingView Webhook",
                        "status": "CONNECTED" if resp.status_code in [200, 201, 204] else "DELIVERED",
                        "message": f"Webhook ping response status: {resp.status_code}",
                        "code": resp.status_code
                    }
            except Exception as e:
                return {
                    "broker": "TradingView Webhook",
                    "status": "ERROR",
                    "message": f"Failed to reach webhook: {str(e)}",
                    "code": 500
                }

        return {
            "broker": cls._PORTFOLIO.broker_name,
            "status": "ONLINE",
            "message": "Virtual Paper Trading Engine active with ₹10,00,000 margin.",
            "code": 200
        }
