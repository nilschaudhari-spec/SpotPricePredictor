package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.PredictionHorizon
import com.spotpricepredictor.pro.domain.model.PredictionResult
import com.spotpricepredictor.pro.domain.model.TrendDirection
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class PredictSpotPriceUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(
        symbol: String,
        currentSpot: Double,
        horizon: PredictionHorizon,
        customMinutes: Int? = null,
        trendMode: TrendDirection = TrendDirection.AUTO
    ): Result<PredictionResult> {
        val result = repository.predictSpotPrice(
            symbol = symbol,
            currentSpot = currentSpot,
            horizon = horizon,
            customMinutes = customMinutes,
            trendMode = trendMode
        )

        // Automatically persist successful prediction for historical tracking & AI learning
        result.onSuccess { pred ->
            val label = if (horizon == PredictionHorizon.CUSTOM && customMinutes != null) {
                "Custom (${customMinutes}m)"
            } else {
                horizon.label
            }
            repository.savePrediction(pred, label)
        }

        return result
    }
}
