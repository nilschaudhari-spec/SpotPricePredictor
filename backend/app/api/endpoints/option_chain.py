from fastapi import APIRouter, Query, HTTPException
from typing import Optional
from app.models.schemas import OptionChainResponse
from app.services.market_data_fetcher import MarketDataFetcher
from app.services.oi_analyzer import OIAnalyzer

router = APIRouter(prefix="/option-chain", tags=["Option Chain"])

@router.get("/{symbol}", response_model=OptionChainResponse)
async def get_option_chain(
    symbol: str,
    days_to_expiry: float = Query(3.5, ge=0.1, le=180.0),
    num_strikes: int = Query(15, ge=5, le=51),
    custom_spot: Optional[float] = Query(None, gt=0),
    expiry_date: Optional[str] = Query(None)
):
    """
    Fetches strike-by-strike option chain for symbol (e.g. NIFTY, BANKNIFTY, RELIANCE, SBIN)
    with analytical Greeks (Delta, Gamma, Theta, Vega), Implied Volatility,
    PCR, Max Pain, upcoming expiry dates, and Immediate Support/Resistance.
    """
    try:
        chain_data = MarketDataFetcher.get_spot_and_chain(
            symbol=symbol,
            days_to_expiry=days_to_expiry,
            num_strikes=num_strikes,
            custom_spot=custom_spot
        )

        strikes = chain_data["strikes"]
        spot = chain_data["spot_price"]

        pcr, _ = OIAnalyzer.calculate_pcr(strikes)
        max_pain = OIAnalyzer.calculate_max_pain(strikes)
        supports, resistances = OIAnalyzer.identify_support_resistance(strikes, spot)

        available_expiries = MarketDataFetcher.get_upcoming_expiries(symbol)
        selected_label = expiry_date or (available_expiries[0].date if available_expiries else None)

        return OptionChainResponse(
            symbol=symbol.upper(),
            spot_price=spot,
            pcr=pcr,
            max_pain=max_pain,
            immediate_support=supports[-1] if supports else spot * 0.99,
            immediate_resistance=resistances[0] if resistances else spot * 1.01,
            strikes=strikes,
            selected_days_to_expiry=days_to_expiry,
            selected_expiry_date=selected_label,
            available_expiries=available_expiries
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Option chain retrieval error: {str(e)}")
