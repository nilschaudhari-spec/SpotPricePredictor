package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.MarketNewsBriefing
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class GetMarketNewsUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(): Result<MarketNewsBriefing> {
        return repository.getMarketNews()
    }
}
