package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.LiveQuote
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class GetRealtimeQuoteUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(symbol: String): Result<LiveQuote> {
        return repository.getRealtimeQuote(symbol)
    }
}
