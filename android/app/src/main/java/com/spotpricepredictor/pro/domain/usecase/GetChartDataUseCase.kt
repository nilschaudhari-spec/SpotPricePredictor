package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.ChartData
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class GetChartDataUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(symbol: String, interval: String = "15m", limit: Int = 60): Result<ChartData> {
        return repository.getChartData(symbol, interval, limit)
    }
}
