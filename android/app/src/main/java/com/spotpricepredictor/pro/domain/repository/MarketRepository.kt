package com.spotpricepredictor.pro.domain.repository

import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.domain.model.LiveQuote
import com.spotpricepredictor.pro.domain.model.OptionChainData
import com.spotpricepredictor.pro.domain.model.PredictionHorizon
import com.spotpricepredictor.pro.domain.model.PredictionResult
import com.spotpricepredictor.pro.domain.model.TrendDirection
import kotlinx.coroutines.flow.Flow

interface MarketRepository {

    suspend fun getOptionChain(
        symbol: String,
        daysToExpiry: Double = 3.5,
        customSpot: Double? = null,
        expiryDate: String? = null
    ): Result<OptionChainData>

    suspend fun getRealtimeQuote(
        symbol: String
    ): Result<LiveQuote>

    suspend fun predictSpotPrice(
        symbol: String,
        currentSpot: Double,
        horizon: PredictionHorizon,
        customMinutes: Int? = null,
        trendMode: TrendDirection = TrendDirection.AUTO
    ): Result<PredictionResult>

    fun getPredictionHistory(): Flow<List<PredictionHistoryEntity>>

    suspend fun savePrediction(prediction: PredictionResult, horizonLabel: String): Long

    suspend fun recordActualOutcome(id: Long, actualSpot: Double, status: String)
}
