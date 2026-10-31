package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.GainersLosersData
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class GetGainersLosersUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(exchange: String = "NSE", limit: Int = 10): Result<GainersLosersData> {
        return repository.getGainersLosers(exchange = exchange, limit = limit)
    }
}

