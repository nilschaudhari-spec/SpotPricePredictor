package com.spotpricepredictor.pro.data.api

import com.spotpricepredictor.pro.data.api.models.LiveQuoteDto
import com.spotpricepredictor.pro.data.api.models.MarketNewsBriefingDto
import com.spotpricepredictor.pro.data.api.models.OptionChainResponseDto
import com.spotpricepredictor.pro.data.api.models.PredictionRequestDto
import com.spotpricepredictor.pro.data.api.models.PredictionResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

import com.spotpricepredictor.pro.data.api.models.ChartDataResponseDto
import com.spotpricepredictor.pro.data.api.models.CorporateActionsResponseDto
import com.spotpricepredictor.pro.data.api.models.DetailedTechnicalAnalysisResponseDto
import com.spotpricepredictor.pro.data.api.models.GainersLosersResponseDto
import com.spotpricepredictor.pro.data.api.models.PatternScannerResponseDto
import com.spotpricepredictor.pro.data.api.models.PeerComparisonResponseDto
import com.spotpricepredictor.pro.data.api.models.ResultsAndEventsResponseDto
import com.spotpricepredictor.pro.data.api.models.TradeSuggestionsResponseDto
import com.spotpricepredictor.pro.data.api.models.ProStrategiesResponseDto
import com.spotpricepredictor.pro.data.api.models.AutoTradeSettingsDto
import com.spotpricepredictor.pro.data.api.models.AutoTradeOrderDto
import com.spotpricepredictor.pro.data.api.models.VirtualPortfolioDto
import com.spotpricepredictor.pro.data.api.models.ScanAndExecuteResponseDto
import com.spotpricepredictor.pro.data.api.models.ManualOrderRequestDto
import com.spotpricepredictor.pro.data.api.models.SquareOffRequestDto
import com.spotpricepredictor.pro.data.api.models.BrokerTestResponseDto

interface PredictorApiService {

    @POST("api/v1/predict")
    suspend fun predictSpotPrice(
        @Body request: PredictionRequestDto
    ): Response<PredictionResponseDto>

    @GET("api/v1/option-chain/{symbol}")
    suspend fun getOptionChain(
        @Path("symbol") symbol: String,
        @Query("days_to_expiry") daysToExpiry: Double = 3.5,
        @Query("num_strikes") numStrikes: Int = 15,
        @Query("custom_spot") customSpot: Double? = null,
        @Query("expiry_date") expiryDate: String? = null
    ): Response<OptionChainResponseDto>

    @GET("api/v1/market/quote/{symbol}")
    suspend fun getRealtimeQuote(
        @Path("symbol") symbol: String
    ): Response<LiveQuoteDto>

    @GET("api/v1/market/news")
    suspend fun getMarketNews(): Response<MarketNewsBriefingDto>

    @GET("api/v1/trade-signals/suggestions")
    suspend fun getTradeSuggestions(
        @Query("category") category: String? = "ALL",
        @Query("instrument") instrument: String? = "ALL",
        @Query("action") action: String? = "ALL",
        @Query("symbol") symbol: String? = null
    ): Response<TradeSuggestionsResponseDto>

    @GET("api/v1/trade-signals/chart/{symbol}")
    suspend fun getChartWithIndicators(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String = "15m",
        @Query("limit") limit: Int = 60
    ): Response<ChartDataResponseDto>

    @GET("api/v1/market/gainers-losers")
    suspend fun getGainersLosers(
        @Query("exchange") exchange: String = "NSE",
        @Query("limit") limit: Int = 10
    ): Response<GainersLosersResponseDto>

    @GET("api/v1/market/corporate-actions")
    suspend fun getCorporateActions(
        @Query("symbol") symbol: String? = null,
        @Query("action_type") actionType: String? = "ALL",
        @Query("limit") limit: Int = 50
    ): Response<CorporateActionsResponseDto>

    @GET("api/v1/market/peer-comparison")
    suspend fun getPeerComparison(
        @Query("symbol") symbol: String
    ): Response<PeerComparisonResponseDto>

    @GET("api/v1/market/detailed-technicals")
    suspend fun getDetailedTechnicals(
        @Query("symbol") symbol: String
    ): Response<DetailedTechnicalAnalysisResponseDto>

    @GET("api/v1/market/results-events")
    suspend fun getResultsAndEvents(
        @Query("symbol") symbol: String? = null
    ): Response<ResultsAndEventsResponseDto>

    @GET("api/v1/market/patterns")
    suspend fun getCandlestickPatterns(
        @Query("symbol") symbol: String? = null,
        @Query("pattern_type") patternType: String? = "ALL",
        @Query("limit") limit: Int = 50
    ): Response<PatternScannerResponseDto>

    @GET("api/v1/market/pro-strategies")
    suspend fun getProStrategies(
        @Query("symbol") symbol: String = "NIFTY"
    ): Response<ProStrategiesResponseDto>

    @GET("api/v1/auto-trade/settings")
    suspend fun getAutoTradeSettings(): Response<AutoTradeSettingsDto>

    @POST("api/v1/auto-trade/settings")
    suspend fun updateAutoTradeSettings(
        @Body settings: AutoTradeSettingsDto
    ): Response<AutoTradeSettingsDto>

    @GET("api/v1/auto-trade/portfolio")
    suspend fun getVirtualPortfolio(): Response<VirtualPortfolioDto>

    @POST("api/v1/auto-trade/scan-and-execute")
    suspend fun scanAllStocksAndExecute(
        @Query("force_execute") forceExecute: Boolean = true
    ): Response<ScanAndExecuteResponseDto>

    @POST("api/v1/auto-trade/order")
    suspend fun placeManualOrder(
        @Body req: ManualOrderRequestDto
    ): Response<AutoTradeOrderDto>

    @POST("api/v1/auto-trade/square-off")
    suspend fun squareOffPositions(
        @Body req: SquareOffRequestDto
    ): Response<VirtualPortfolioDto>

    @POST("api/v1/auto-trade/test-broker")
    suspend fun testBrokerConnection(): Response<BrokerTestResponseDto>
}


