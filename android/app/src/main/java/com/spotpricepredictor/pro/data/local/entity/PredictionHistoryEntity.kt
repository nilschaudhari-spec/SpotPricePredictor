package com.spotpricepredictor.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class PredictionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val timestamp: Long = System.currentTimeMillis(),
    val horizon: String,
    val currentSpot: Double,
    val predictedSpot: Double,
    val actualSpot: Double? = null,
    val errorPoints: Double? = null,
    val targetPrice: Double,
    val stopLoss: Double,
    val confidencePct: Double,
    val suggestedStrategy: String,
    val probBullish: Double,
    val probBearish: Double,
    val status: String = "PENDING" // "PENDING", "TARGET_HIT", "STOP_LOSS_HIT", "EXPIRED"
)
