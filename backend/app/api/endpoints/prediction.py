from fastapi import APIRouter, HTTPException
from app.models.schemas import PredictionRequest, PredictionResponse
from app.services.prediction_orchestrator import PredictionOrchestrator

router = APIRouter(prefix="/predict", tags=["Prediction Engine"])

@router.post("", response_model=PredictionResponse)
async def predict_spot_price(request: PredictionRequest):
    """
    Executes multi-model AI & Quantitative prediction for expected future spot price.
    Fuses:
    - Model 1: Open Interest & Max Pain Dynamics
    - Model 2: Option Greeks & Implied Volatility Surface
    - Model 3: ML Regression Ensemble (XGBoost + LightGBM + Random Forest)
    - Model 4: LSTM Time Series / Path Simulation
    - Model 5: Price Action, Liquidity Zones & Breakout Retest
    """
    try:
        response = PredictionOrchestrator.run_prediction(request)
        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction pipeline error: {str(e)}")
