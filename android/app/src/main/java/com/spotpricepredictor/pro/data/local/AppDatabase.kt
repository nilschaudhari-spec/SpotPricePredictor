package com.spotpricepredictor.pro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spotpricepredictor.pro.data.local.dao.PredictionHistoryDao
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity

@Database(
    entities = [PredictionHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        const val DATABASE_NAME = "spot_price_predictor.db"
    }
}
