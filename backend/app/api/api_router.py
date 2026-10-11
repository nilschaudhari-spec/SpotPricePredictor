from fastapi import APIRouter
from app.api.endpoints import prediction, option_chain, market_data, admin

api_router = APIRouter(prefix="/api/v1")
api_router.include_router(prediction.router)
api_router.include_router(option_chain.router)
api_router.include_router(market_data.router)
api_router.include_router(admin.router)
