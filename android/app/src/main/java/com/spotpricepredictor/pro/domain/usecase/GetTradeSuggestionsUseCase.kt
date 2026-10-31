package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.TradeSuggestionsResponse
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class GetTradeSuggestionsUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(
        category: String? = "ALL",
        instrument: String? = "ALL",
        action: String? = "ALL",
        symbol: String? = null
    ): Result<TradeSuggestionsResponse> {
        return repository.getTradeSuggestions(category, instrument, action, symbol)
    }
}
