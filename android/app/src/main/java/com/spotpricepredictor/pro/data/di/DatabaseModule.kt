package com.spotpricepredictor.pro.data.di

import android.content.Context
import androidx.room.Room
import com.spotpricepredictor.pro.data.local.AppDatabase
import com.spotpricepredictor.pro.data.local.dao.PredictionHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePredictionHistoryDao(database: AppDatabase): PredictionHistoryDao {
        return database.predictionHistoryDao()
    }
}
