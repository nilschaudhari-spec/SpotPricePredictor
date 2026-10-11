package com.spotpricepredictor.pro.domain.usecase

import com.spotpricepredictor.pro.domain.model.OptionChainData
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import javax.inject.Inject

class GetOptionChainUseCase @Inject constructor(
    private val repository: MarketRepository
) {
    suspend operator fun invoke(
        symbol: String,
        daysToExpiry: Double = 3.5,
        customSpot: Double? = null,
        expiryDate: String? = null
    ): Result<OptionChainData> {
        return repository.getOptionChain(
            symbol = symbol,
            daysToExpiry = daysToExpiry,
            customSpot = customSpot,
            expiryDate = expiryDate
        )
    }
}
