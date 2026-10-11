# Spot Price Predictor Pro – AI Options Calculator

Production-ready, multi-model Quantitative AI Options Analytics and Spot Price Forecasting System combining **Option Chain Open Interest dynamics**, **Analytical Black-Scholes Greeks**, **Machine Learning Regression (XGBoost + LightGBM + Random Forest)**, **LSTM Time-Series Pathing**, and **Price Action Mechanics**.

---

## 🏛 System Architecture Overview

The system is engineered as a decoupled, high-performance client-server ecosystem:

```
Spot Price Predictor Pro/
├── backend/                  # Python FastAPI Quant & ML Inference Engine
│   ├── app/
│   │   ├── api/              # REST Endpoints (Predict, Option Chain, Market Data, Admin)
│   │   ├── core/             # Configuration & Settings
│   │   ├── models/           # Pydantic Schemas & DTOs
│   │   └── services/         # Quantitative Math & Model Services
│   │       ├── greeks_engine.py          # Black-Scholes Greeks & Newton-Raphson IV
│   │       ├── oi_analyzer.py            # PCR, Max Pain, Support/Resistance, GEX/DEX
│   │       ├── technical_analysis.py     # EMA 20/50, Supertrend, VWAP, ADX, RSI, MACD
│   │       ├── ml_ensemble.py            # XGBoost, LightGBM, Random Forest Regressor
│   │       ├── lstm_predictor.py         # Recurrent State Volatility Simulation
│   │       ├── price_action.py           # Candlestick, Breakout & Liquidity Zones
│   │       ├── strategy_recommender.py   # Option Strategies (Spreads, Condor, Straddle)
│   │       └── prediction_orchestrator.py# Master Multi-Model Synthesis Engine
│   └── tests/                # Automated Pytest Suite (100% Passing)
│
└── android/                  # Native Android App (Kotlin + Jetpack Compose)
    ├── app/
    │   ├── build.gradle.kts  # Gradle with Material 3, Hilt, Room, Retrofit
    │   └── src/main/java/com/spotpricepredictor/pro/
    │       ├── data/         # Retrofit API, Room DB, Repository Implementation
    │       ├── domain/       # Clean Architecture Models, Repositories, Use Cases
    │       └── presentation/ # Compose Screens, ViewModels, Theme, Custom Canvas Gauge
    └── gradle/               # Version Catalog (libs.versions.toml)
```

---

## 🧠 The 5 AI Prediction Models

1. **Model 1: Open Interest & Max Pain Engine (`oi_analyzer.py`)**
   - Calculates Put-Call Ratio (PCR) for volume and OI.
   - Computes strike-by-strike monetary payouts to pinpoint the exact **Max Pain Strike**.
   - Determines dealer **Gamma Exposure (GEX)** and **Delta Exposure (DEX)** to identify pinning vs. breakout zones.
2. **Model 2: Option Greeks & Hedging Surface (`greeks_engine.py`)**
   - Analytical closed-form Black-Scholes formulas for $\Delta$, $\Gamma$, $\Theta$, $\mathcal{V}$, $\rho$.
   - Real-time **Implied Volatility (IV)** inversion using the Newton-Raphson algorithm with bisection fallback.
   - Derives market maker delta-hedging drift.
3. **Model 3: Machine Learning Regression Ensemble (`ml_ensemble.py`)**
   - Multi-model regressor blending **XGBoost (40%)**, **LightGBM (35%)**, and **Random Forest (25%)**.
   - Evaluates multi-dimensional features: ATM IV Skew, VWAP Distance, EMA Divergence, Momentum, and ADX.
4. **Model 4: LSTM Time Series Dynamic (`lstm_predictor.py`)**
   - Recurrent memory state modeling momentum inertia, mean-reversion pull, and volatility clustering across chosen prediction horizons (5m, 10m, 15m, 30m, 1h, custom).
5. **Model 5: Price Action & Liquidity Structure (`price_action.py`)**
   - Candlestick structure recognition (Marubozu, Hammer, Shooting Star, Doji).
   - Liquidity grab and fair-value order block retests against dynamic ATR bands.

---

## 🚀 Running the FastAPI Backend

### Prerequisites
- Python 3.10+ (Tested on Python 3.14)

### Quick Start
```bash
cd backend
# 1. Activate virtual environment
.venv\Scripts\activate   # Windows
# source .venv/bin/activate # Linux/macOS

# 2. Run automated test suite
pytest -v tests/

# 3. Launch live FastAPI server
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
# OR simply double-click run_server.bat on Windows
```

Interactive OpenAPI Swagger UI is available at:
👉 `http://localhost:8000/docs`

---

## 📱 Android Application Setup

### Prerequisites
- Android Studio (Hedgehog or newer)
- JDK 17
- Target SDK: 34 (Android 14) | Min SDK: 26 (Android 8.0)

### Key Technologies
- **UI Framework:** 100% Jetpack Compose with Material Design 3
- **Architecture:** Clean Architecture (`data`, `domain`, `presentation`) + MVVM + UDF
- **Dependency Injection:** Dagger Hilt
- **Local Persistence:** Room Database for prediction history and continuous AI accuracy tracking
- **Networking:** Retrofit 2 + OkHttp + Gson
- **Theme:** High-contrast FinTech dark theme with Electric Cyan, Neon Emerald, and Crimson Alert

### Building the Android App
1. Open Android Studio.
2. Select **Open** and choose the `android/` directory (`d:\Nilesh\Nilesh\Spot Price Predictor\android`).
3. Allow Gradle to sync dependencies via `gradle/libs.versions.toml`.
4. Run on an Android Emulator or connected physical device.
*(Note: When running on an emulator, the Retrofit client points to `http://10.0.2.2:8000/` which automatically routes to the host machine's backend).*

---

## ⚖️ Regulatory & Probability Disclaimer
derivatives trading involves significant capital risk. All forecasts produced by this system represent probability-based statistical approximations. They are not guaranteed outcomes or financial advice.
