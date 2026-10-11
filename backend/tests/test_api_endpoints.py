import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_root_endpoint():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["status"] == "ONLINE"

def test_market_symbols_endpoint():
    response = client.get("/api/v1/market/symbols")
    assert response.status_code == 200
    data = response.json()
    assert "indices" in data
    assert any(i["symbol"] == "NIFTY" for i in data["indices"])

def test_option_chain_endpoint():
    response = client.get("/api/v1/option-chain/NIFTY?days_to_expiry=3.5&num_strikes=11")
    assert response.status_code == 200
    data = response.json()
    assert data["symbol"] == "NIFTY"
    assert data["spot_price"] > 0
    assert len(data["strikes"]) == 11
    assert data["pcr"] > 0
    assert data["max_pain"] > 0

def test_predict_endpoint_e2e():
    payload = {
        "symbol": "BANKNIFTY",
        "exchange": "NSE",
        "asset_type": "INDEX",
        "current_spot_price": 52400.0,
        "prediction_horizon": "15_MIN",
        "trend_mode": "AUTO"
    }
    response = client.post("/api/v1/predict", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["symbol"] == "BANKNIFTY"
    assert data["current_spot"] == 52400.0
    assert data["predicted_spot_price"] > 0
    assert data["confidence_pct"] >= 45.0
    assert "suggested_strategy" in data
    assert "model_breakdown" in data
    assert "disclaimer" in data

def test_market_search_empty_returns_empty():
    response = client.get("/api/v1/market/search?q=")
    assert response.status_code == 200
    assert response.json() == []

def test_market_search_two_letters_lt():
    response = client.get("/api/v1/market/search?q=LT")
    assert response.status_code == 200
    symbols = [s["symbol"] for s in response.json()]
    assert "LT" in symbols

def test_market_search_initial_3_letters_tata():
    response = client.get("/api/v1/market/search?q=TAT")
    assert response.status_code == 200
    data = response.json()
    symbols = [s["symbol"] for s in data]
    assert "TATAMOTORS" in symbols
    assert "TCS" in symbols


def test_market_search_reliance():
    response = client.get("/api/v1/market/search?q=REL")
    assert response.status_code == 200
    data = response.json()
    symbols = [s["symbol"] for s in data]
    assert "RELIANCE" in symbols

def test_market_search_case_insensitive():
    res_upper = client.get("/api/v1/market/search?q=INF")
    res_lower = client.get("/api/v1/market/search?q=inf")
    assert res_upper.status_code == 200
    assert res_lower.status_code == 200
    assert [s["symbol"] for s in res_upper.json()] == [s["symbol"] for s in res_lower.json()]

def test_market_daily_news_endpoint():
    response = client.get("/api/v1/market/news")
    assert response.status_code == 200
    data = response.json()
    assert "items" in data
    assert len(data["items"]) > 0
    assert "macro_summary" in data
    assert "disclaimer" in data
    first_item = data["items"][0]
    assert "what_happened" in first_item
    assert "why_it_matters" in first_item
    assert "likely_impact" in first_item
    assert "impact_sentiment" in first_item

def test_market_ticker_endpoint():
    response = client.get("/api/v1/market/ticker")
    assert response.status_code == 200
    data = response.json()
    assert len(data) >= 10
    first_ticker = data[0]
    assert "symbol" in first_ticker
    assert "spot_price" in first_ticker
    assert "change" in first_ticker
    assert "change_percent" in first_ticker
    assert "is_positive" in first_ticker

def test_predict_custom_arbitrary_stock():
    payload = {
        "symbol": "SUZLON",
        "exchange": "NSE",
        "asset_type": "EQUITY",
        "current_spot_price": 58.5,
        "prediction_horizon": "15_MIN",
        "trend_mode": "AUTO"
    }
    response = client.post("/api/v1/predict", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["symbol"] == "SUZLON"
    assert data["predicted_spot_price"] > 0



