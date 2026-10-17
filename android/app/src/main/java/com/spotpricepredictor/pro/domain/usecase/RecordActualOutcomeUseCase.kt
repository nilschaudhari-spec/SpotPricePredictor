package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class RecordActualOutcomeUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(id: Long, actualSpot: Double, status: String) {
        repository.recordActualOutcome(id, actualSpot, status)
    }
}
