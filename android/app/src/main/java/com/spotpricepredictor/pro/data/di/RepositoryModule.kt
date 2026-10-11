package com.spotpricepredictor.pro.data.di

import com.spotpricepredictor.pro.data.repository.MarketRepositoryImpl
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMarketRepository(
        marketRepositoryImpl: MarketRepositoryImpl
    ): MarketRepository
}
