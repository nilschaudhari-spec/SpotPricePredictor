package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPredictionHistoryUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    operator fun invoke(): Flow<List<PredictionHistoryEntity>> {
        return repository.getPredictionHistory()
    }
}
