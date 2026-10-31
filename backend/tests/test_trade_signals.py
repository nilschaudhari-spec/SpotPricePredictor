import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_trade_suggestions_all():
    response = client.get("/api/v1/trade-signals/suggestions")
    assert response.status_code == 200
    data = response.json()
    assert "suggestions" in data
    assert "market_sentiment" in data
    assert "disclaimer" in data
    assert len(data["suggestions"]) > 0

    first = data["suggestions"][0]
    assert "symbol" in first
    assert "action" in first
    assert first["action"] in ["BUY", "SELL"]
    assert "current_price" in first
    assert "entry_price" in first
    assert "target_1" in first
    assert "stop_loss" in first
    assert "technical_analysis" in first
    tech = first["technical_analysis"]
    assert "trend" in tech
    assert "rsi" in tech
    assert "supertrend" in tech
    assert "vwap_signal" in tech
    assert "rationale" in tech
    assert len(tech["rationale"]) > 10

def test_trade_suggestions_fno_filter():
    response = client.get("/api/v1/trade-signals/suggestions?instrument=FNO")
    assert response.status_code == 200
    data = response.json()
    for s in data["suggestions"]:
        assert s["instrument_type"] in ["OPTIONS", "FUTURES"]
        if s["instrument_type"] == "OPTIONS":
            assert s["fno_details"] is not None
            assert s["fno_details"]["contract_name"] is not None
            assert s["fno_details"]["premium_entry"] > 0
            assert s["fno_details"]["premium_target"] > 0
            assert s["fno_details"]["premium_stop_loss"] > 0

def test_trade_suggestions_equity_filter():
    response = client.get("/api/v1/trade-signals/suggestions?instrument=EQUITY")
    assert response.status_code == 200
    data = response.json()
    for s in data["suggestions"]:
        assert s["instrument_type"] == "EQUITY"

def test_trade_suggestions_single_symbol():
    response = client.get("/api/v1/trade-signals/suggestions?symbol=NIFTY")
    assert response.status_code == 200
    data = response.json()
    assert len(data["suggestions"]) >= 1
    for s in data["suggestions"]:
        assert s["symbol"] == "NIFTY"

def test_chart_endpoint_nifty():
    response = client.get("/api/v1/trade-signals/chart/NIFTY?interval=15m&limit=30")
    assert response.status_code == 200
    data = response.json()
    assert data["symbol"] == "NIFTY"
    assert data["interval"] == "15m"
    assert len(data["candles"]) >= 15

    candle = data["candles"][-1]
    assert "open" in candle
    assert "high" in candle
    assert "low" in candle
    assert "close" in candle
    assert "volume" in candle
    assert candle["high"] >= candle["low"]
    # Indicators should be calculated
    assert "vwap" in candle
    assert candle["vwap"] is not None
    assert "rsi" in candle

def test_chart_endpoint_reliance():
    response = client.get("/api/v1/trade-signals/chart/RELIANCE?interval=5m&limit=25")
    assert response.status_code == 200
    data = response.json()
    assert data["symbol"] == "RELIANCE"
    assert len(data["candles"]) >= 10

def test_chart_endpoint_multiple_intervals():
    intervals = ["15s", "30s", "1m", "5m", "15m", "30m", "1h", "4h", "1D", "1W", "1M", "1Y"]
    for inv in intervals:
        response = client.get(f"/api/v1/trade-signals/chart/NIFTY?interval={inv}&limit=20")
        assert response.status_code == 200, f"Failed for interval {inv}"
        data = response.json()
        assert data["interval"] == inv
        assert len(data["candles"]) >= 15
        first_candle = data["candles"][0]
        last_candle = data["candles"][-1]
        assert first_candle["timestamp"] < last_candle["timestamp"]
        assert len(last_candle["time_str"]) > 0
        if inv in ["15s", "30s"]:
            # Format should have seconds :ss
            assert len(last_candle["time_str"].split(":")) == 3

