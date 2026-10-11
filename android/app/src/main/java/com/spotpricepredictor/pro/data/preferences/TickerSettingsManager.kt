package com.spotpricepredictor.pro.data.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TickerSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "spot_ticker_prefs"
        private const val KEY_SPEED = "ticker_speed_factor"
        private const val KEY_PAUSED = "ticker_is_paused"
        const val DEFAULT_SPEED = 1.0f
        const val SLOW_SPEED = 0.5f
        const val FAST_SPEED = 2.0f
        const val TURBO_SPEED = 3.0f
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _speedFlow = MutableStateFlow(getSpeed())
    val speedFlow: StateFlow<Float> = _speedFlow.asStateFlow()

    private val _isPausedFlow = MutableStateFlow(getIsPaused())
    val isPausedFlow: StateFlow<Boolean> = _isPausedFlow.asStateFlow()

    fun getSpeed(): Float {
        return prefs.getFloat(KEY_SPEED, DEFAULT_SPEED)
    }

    fun setSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.1f, 5.0f)
        prefs.edit().putFloat(KEY_SPEED, clamped).apply()
        _speedFlow.value = clamped
    }

    fun getIsPaused(): Boolean {
        return prefs.getBoolean(KEY_PAUSED, false)
    }

    fun setPaused(paused: Boolean) {
        prefs.edit().putBoolean(KEY_PAUSED, paused).apply()
        _isPausedFlow.value = paused
    }

    fun togglePaused() {
        setPaused(!_isPausedFlow.value)
    }
}
