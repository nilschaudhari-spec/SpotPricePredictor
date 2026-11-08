import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_corporate_actions_endpoint():
    # Test broad fetch
    resp = client.get("/api/v1/market/corporate-actions")
    assert resp.status_code == 200
    data = resp.json()
    assert "actions" in data
    assert len(data["actions"]) > 0
    assert any(a["action_type"] == "DIVIDEND" for a in data["actions"])

    # Test stock specific filter
    resp_rel = client.get("/api/v1/market/corporate-actions?symbol=RELIANCE")
    assert resp_rel.status_code == 200
    data_rel = resp_rel.json()
    assert len(data_rel["actions"]) > 0
    assert data_rel["actions"][0]["symbol"] == "RELIANCE"

def test_peer_comparison_endpoint():
    resp = client.get("/api/v1/market/peer-comparison?symbol=TCS")
    assert resp.status_code == 200
    data = resp.json()
    assert data["symbol"] == "TCS"
    assert data["sector"] == "Information Technology"
    assert len(data["peers"]) >= 4
    # Ensure key metrics are present
    peer = data["peers"][0]
    assert "pe_ratio" in peer
    assert "pb_ratio" in peer
    assert "market_cap_cr" in peer
    assert "roe" in peer
    assert "high_52w" in peer

def test_detailed_technicals_endpoint():
    resp = client.get("/api/v1/market/detailed-technicals?symbol=INFY")
    assert resp.status_code == 200
    data = resp.json()
    assert data["symbol"] == "INFY"
    assert "overall_call" in data
    assert "technical_score" in data
    assert len(data["moving_averages"]) == 6 # 5, 10, 20, 50, 100, 200
    assert len(data["oscillators"]) >= 8
    assert "Classic" in data["pivot_points"]
    assert "Fibonacci" in data["pivot_points"]
    assert "Camarilla" in data["pivot_points"]

def test_results_and_events_endpoint():
    resp = client.get("/api/v1/market/results-events?symbol=HDFCBANK")
    assert resp.status_code == 200
    data = resp.json()
    assert len(data["quarterly_results"]) > 0
    assert len(data["corporate_events"]) > 0
    assert len(data["economic_events"]) > 0
    # Check economic event fields
    eco = data["economic_events"][0]
    assert "title" in eco
    assert "impact_level" in eco
    assert "sector_impact" in eco

def test_movers_volume_and_value():
    resp = client.get("/api/v1/market/gainers-losers?exchange=NSE&limit=5")
    assert resp.status_code == 200
    data = resp.json()
    assert "gainers" in data
    assert "losers" in data
    assert "most_active_volume" in data
    assert "most_active_value" in data
    assert len(data["most_active_volume"]) > 0
    assert len(data["most_active_value"]) > 0
    assert data["most_active_volume"][0]["volume"] > 0
    assert data["most_active_value"][0]["turnover_cr"] > 0

def test_candlestick_patterns_endpoint():
    # Test broad market scan
    resp = client.get("/api/v1/market/patterns")
    assert resp.status_code == 200
    data = resp.json()
    assert "buy_patterns" in data
    assert "sell_patterns" in data
    assert "all_patterns" in data
    assert len(data["buy_patterns"]) > 0
    assert len(data["sell_patterns"]) > 0

    # Verify patterns exist in the catalog
    pattern_names = {p["pattern_name"] for p in data["all_patterns"]}
    assert "Hammer" in pattern_names
    assert "Inverse Hammer" in pattern_names
    assert "Low break close upside" in pattern_names
    assert "Bullish Harami" in pattern_names
    assert "Bullish Marubozu" in pattern_names
    assert "Dragonfly Doji" in pattern_names
    assert "Morning Star" in pattern_names
    assert "Hanging Man" in pattern_names
    assert "Shooting Star" in pattern_names
    assert "High break, close Downside" in pattern_names
    assert "Bearish Harami" in pattern_names
    assert "Bearish Marubozu" in pattern_names
    assert "Gravestone Doji" in pattern_names
    assert "Evening Star" in pattern_names

    # Test single stock scan
    resp_rel = client.get("/api/v1/market/patterns?symbol=RELIANCE")
    assert resp_rel.status_code == 200
    data_rel = resp_rel.json()
    assert len(data_rel["all_patterns"]) == 14
    assert all(p["symbol"] == "RELIANCE" for p in data_rel["all_patterns"])
    assert any(p["category"] == "PRICE ACTION TECHNIQUE" for p in data_rel["all_patterns"])

    # Test filter by BUY
    resp_buy = client.get("/api/v1/market/patterns?pattern_type=BUY")
    assert resp_buy.status_code == 200
    data_buy = resp_buy.json()
    assert all(p["pattern_type"] == "BUY" for p in data_buy["all_patterns"])

    # Test filter by PRICE_ACTION
    resp_pa = client.get("/api/v1/market/patterns?pattern_type=PRICE_ACTION")
    assert resp_pa.status_code == 200
    data_pa = resp_pa.json()
    assert all("PRICE ACTION" in p["category"] for p in data_pa["all_patterns"])


