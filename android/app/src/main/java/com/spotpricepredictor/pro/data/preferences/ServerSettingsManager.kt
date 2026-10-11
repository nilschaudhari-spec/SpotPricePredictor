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
class ServerSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "spot_predictor_server_prefs"
        private const val KEY_BASE_URL = "backend_base_url"
        const val RENDER_CLOUD_URL = "https://spot-price-predictor-api.onrender.com/"
        const val DEFAULT_LAN_URL = "http://192.168.0.141:8000/"
        const val EMULATOR_URL = "http://10.0.2.2:8000/"
        const val ADB_REVERSE_URL = "http://127.0.0.1:8000/"
    }


    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _baseUrlFlow = MutableStateFlow(getBaseUrl())
    val baseUrlFlow: StateFlow<String> = _baseUrlFlow.asStateFlow()

    fun getBaseUrl(): String {
        return prefs.getString(KEY_BASE_URL, DEFAULT_LAN_URL) ?: DEFAULT_LAN_URL
    }

    fun setBaseUrl(url: String) {
        val sanitized = if (!url.endsWith("/")) "$url/" else url
        prefs.edit().putString(KEY_BASE_URL, sanitized).apply()
        _baseUrlFlow.value = sanitized
    }
}
