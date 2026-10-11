package com.spotpricepredictor.pro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionHistoryEntity): Long

    @Query("SELECT * FROM prediction_history ORDER BY timestamp DESC")
    fun getAllPredictionsFlow(): Flow<List<PredictionHistoryEntity>>

    @Query("SELECT * FROM prediction_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentPredictions(limit: Int): List<PredictionHistoryEntity>

    @Query("SELECT * FROM prediction_history WHERE id = :id")
    suspend fun getPredictionById(id: Long): PredictionHistoryEntity?

    @Update
    suspend fun updatePrediction(prediction: PredictionHistoryEntity)

    @Query("UPDATE prediction_history SET actualSpot = :actual, errorPoints = abs(:actual - predictedSpot), status = :status WHERE id = :id")
    suspend fun recordActualOutcome(id: Long, actual: Double, status: String)

    @Query("SELECT COUNT(*) FROM prediction_history")
    suspend fun getTotalPredictionsCount(): Int

    @Query("SELECT AVG(abs(errorPoints)) FROM prediction_history WHERE errorPoints IS NOT NULL")
    suspend fun getAverageError(): Double?

    @Query("SELECT (COUNT(CASE WHEN status = 'TARGET_HIT' THEN 1 END) * 100.0) / COUNT(*) FROM prediction_history WHERE status != 'PENDING'")
    suspend fun getSuccessRate(): Double?
}
