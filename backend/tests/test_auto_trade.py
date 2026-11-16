import pytest
from fastapi.testclient import TestClient
from app.main import app
from app.services.frontpage_trading_service import FrontPageTradingService
from app.services.auto_trade_scanner_service import AutoTradeScannerService

client = TestClient(app)

def test_get_auto_trade_settings():
    response = client.get("/api/v1/auto-trade/settings")
    assert response.status_code == 200
    data = response.json()
    assert "is_auto_trade_enabled" in data
    assert "broker_destination" in data
    assert "min_reliability_pct" in data
    assert "max_capital_per_trade" in data

def test_update_auto_trade_settings():
    payload = {
        "is_auto_trade_enabled": True,
        "broker_destination": "FRONTPAGE",
        "frontpage_user_token": "fp_test_token_999",
        "frontpage_account_id": "FP_TEST_USER_01",
        "webhook_url": "https://example.com/webhook",
        "scan_interval_seconds": 15,
        "min_reliability_pct": 82.0,
        "allowed_actions": "ALL",
        "max_capital_per_trade": 60000.0,
        "max_concurrent_positions": 6,
        "auto_trailing_stop_loss": True,
        "notify_on_execution": True
    }
    response = client.post("/api/v1/auto-trade/settings", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["is_auto_trade_enabled"] is True
    assert data["broker_destination"] == "FRONTPAGE"
    assert data["frontpage_user_token"] == "fp_test_token_999"
    assert data["min_reliability_pct"] == 82.0

def test_get_virtual_portfolio():
    response = client.get("/api/v1/auto-trade/portfolio")
    assert response.status_code == 200
    data = response.json()
    assert data["total_capital"] == 1000000.0
    assert "available_margin" in data
    assert "unrealized_pnl" in data
    assert "realized_pnl" in data
    assert isinstance(data["positions"], list)
    assert isinstance(data["order_history"], list)

def test_place_manual_order():
    order_req = {
        "symbol": "RELIANCE",
        "action": "BUY",
        "quantity": 25,
        "product_type": "INTRADAY",
        "price": 1165.0,
        "stop_loss": 1150.0,
        "target_price": 1195.0,
        "pattern_trigger": "Hammer (Bullish Pin Bar)"
    }
    response = client.post("/api/v1/auto-trade/order", json=order_req)
    assert response.status_code == 200
    data = response.json()
    assert data["symbol"] == "RELIANCE"
    assert data["action"] == "BUY"
    assert data["quantity"] == 25
    assert data["status"] == "EXECUTED"
    assert "Hammer" in data["pattern_trigger"]
    assert data["order_id"].startswith("FP-")

def test_scan_and_execute_all_stocks():
    response = client.post("/api/v1/auto-trade/scan-and-execute?force_execute=true")
    assert response.status_code == 200
    data = response.json()
    assert data["stocks_scanned_count"] >= 20
    assert data["patterns_detected_count"] > 0
    assert "portfolio" in data
    assert "summary" in data
    assert len(data["summary"]) > 10

def test_square_off_positions():
    # Place an order first to ensure at least one position exists
    order_req = {
        "symbol": "TCS",
        "action": "BUY",
        "quantity": 10,
        "product_type": "INTRADAY",
        "price": 2500.0,
        "stop_loss": 2450.0,
        "target_price": 2600.0,
        "pattern_trigger": "Morning Star Reversal"
    }
    client.post("/api/v1/auto-trade/order", json=order_req)

    # Square off all positions
    sq_req = {"position_id": "ALL"}
    response = client.post("/api/v1/auto-trade/square-off", json=sq_req)
    assert response.status_code == 200
    data = response.json()
    # All open positions should now be closed
    open_positions = [p for p in data["positions"] if p["status"] == "OPEN"]
    assert len(open_positions) == 0

def test_broker_connection_test():
    response = client.post("/api/v1/auto-trade/test-broker")
    assert response.status_code == 200
    data = response.json()
    assert "broker" in data
    assert "status" in data
    assert "message" in data

def test_anvest_paper_trading_settings_and_execution():
    # Configure Anvest Paper Trading
    payload = {
        "is_auto_trade_enabled": True,
        "broker_destination": "ANVEST_PAPER",
        "anvest_user_token": "anvest_token_secret_123",
        "anvest_account_id": "ANVEST_PAPER_01",
        "anvest_webhook_url": "https://anvest.in/paper-trading",
        "frontpage_user_token": "",
        "frontpage_account_id": "FP_VIRTUAL_01",
        "frontpage_webhook_url": "https://front.page/",
        "webhook_url": "",
        "scan_interval_seconds": 15,
        "min_reliability_pct": 75.0,
        "allowed_actions": "ALL",
        "max_capital_per_trade": 50000.0,
        "max_concurrent_positions": 5,
        "auto_trailing_stop_loss": True,
        "notify_on_execution": True,
        "enabled_strategies": [
            "INTRADAY_TRI_CONFIRMATION",
            "GOLDEN_CROSS",
            "BOLLINGER_BREAKOUT",
            "CONSOLIDATION_BREAKOUT",
            "OI_BUILDUP",
            "CANDLESTICK_PATTERNS"
        ]
    }
    response = client.post("/api/v1/auto-trade/settings", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["broker_destination"] == "ANVEST_PAPER"
    assert data["anvest_account_id"] == "ANVEST_PAPER_01"
    assert "INTRADAY_TRI_CONFIRMATION" in data["enabled_strategies"]

    # Place order on Anvest paper trading desk
    order_req = {
        "symbol": "INFY",
        "action": "BUY",
        "quantity": 30,
        "product_type": "INTRADAY",
        "price": 1025.0,
        "stop_loss": 1010.0,
        "target_price": 1055.0,
        "pattern_trigger": "5M Tri-Confirmation (Supertrend Bullish + VWAP + MACD)"
    }
    order_resp = client.post("/api/v1/auto-trade/order", json=order_req)
    assert order_resp.status_code == 200
    order_data = order_resp.json()
    assert order_data["symbol"] == "INFY"
    assert order_data["status"] == "EXECUTED"
    assert "Anvest" in order_data["broker"] or "Anvest" in order_data["execution_message"]

def test_strategy_conditions_evaluation():
    response = client.get("/api/v1/auto-trade/strategy-conditions?symbol=NIFTY")
    assert response.status_code == 200
    conditions = response.json()
    assert isinstance(conditions, list)
    assert len(conditions) >= 5

    strategy_names = [c["strategy_name"] for c in conditions]
    assert any("Tri-Confirmation" in s for s in strategy_names)
    assert any("Golden Cross" in s for s in strategy_names)
    assert any("Bollinger" in s for s in strategy_names)

    # Check condition structure
    first = conditions[0]
    assert "condition_met" in first
    assert "action" in first
    assert "entry_price" in first
    assert "stop_loss" in first
    assert "target_price" in first
    assert "confidence_score" in first

def test_strategy_condition_scan_and_execute():
    response = client.post("/api/v1/auto-trade/scan-and-execute?force_execute=true")
    assert response.status_code == 200
    data = response.json()
    assert data["stocks_scanned_count"] >= 20
    assert "strategy_conditions_count" in data
    assert "strategy_conditions_evaluated" in data
    assert len(data["strategy_conditions_evaluated"]) > 0
