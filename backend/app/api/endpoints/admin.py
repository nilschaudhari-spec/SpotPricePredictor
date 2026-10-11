from fastapi import APIRouter
from typing import Dict, Any

router = APIRouter(prefix="/admin", tags=["Admin & System Settings"])

@router.get("/config")
async def get_admin_configuration() -> Dict[str, Any]:
    return {
        "model_weights": {
            "model_1_open_interest": 0.25,
            "model_2_option_greeks": 0.20,
            "model_3_ml_ensemble_xgb_lgb_rf": 0.25,
            "model_4_lstm_time_series": 0.15,
            "model_5_price_action": 0.15
        },
        "api_integrations": {
            "zerodha_kite": {"configured": True, "status": "READY"},
            "angel_one_smartapi": {"configured": True, "status": "READY"},
            "upstox": {"configured": False, "status": "STANDBY"},
            "nse_direct_scraper": {"configured": True, "status": "ACTIVE"}
        },
        "monetization": {
            "admob_enabled": True,
            "meta_audience_enabled": True,
            "ad_formats": ["BANNER", "INTERSTITIAL", "REWARDED_VIDEO", "NATIVE", "APP_OPEN"],
            "subscription_tiers": [
                {"tier": "FREE", "ads": True, "predictions_per_day": 5, "horizons": ["15_MIN"]},
                {"tier": "PRO", "ads": False, "predictions_per_day": "UNLIMITED", "horizons": "ALL", "price_inr_month": 499}
            ]
        }
    }
