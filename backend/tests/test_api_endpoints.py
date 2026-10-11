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
