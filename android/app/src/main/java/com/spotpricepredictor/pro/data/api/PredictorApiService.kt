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
}

