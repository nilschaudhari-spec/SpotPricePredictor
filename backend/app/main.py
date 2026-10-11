from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.api_router import api_router
from app.core.config import settings

app = FastAPI(
    title="Spot Price Predictor Pro – AI Options Calculator",
    description="Multi-Model Quantitative AI Prediction Engine fusing Option Greeks, Open Interest dynamics, XGBoost/LightGBM/RF ensemble, LSTM, and Price Action.",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS configuration for Android app and Web clients
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router)

@app.get("/", tags=["Health Check"])
async def root():
    return {
        "status": "ONLINE",
        "app": "Spot Price Predictor Pro API",
        "engine": "Multi-Model AI Quant Ensemble (Greeks, OI, ML, LSTM, Price Action)",
        "docs": "/docs"
    }

@app.get("/health", tags=["Health Check"])
async def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import os
    import uvicorn
    port = int(os.environ.get("PORT", 8000))
    uvicorn.run("app.main:app", host="0.0.0.0", port=port, reload=True)
