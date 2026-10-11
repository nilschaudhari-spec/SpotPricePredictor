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
class WatchlistManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "spot_watchlist_prefs"
        private const val KEY_SYMBOLS = "custom_symbols_list"
        val DEFAULT_SYMBOLS = listOf(
            "NIFTY", "BANKNIFTY", "FINNIFTY",
            "RELIANCE", "TCS", "HDFCBANK", "INFY",
            "SBIN", "ITC", "WIPRO", "BAJFINANCE"
        )
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _symbolsFlow = MutableStateFlow(loadSymbols())
    val symbolsFlow: StateFlow<List<String>> = _symbolsFlow.asStateFlow()

    private fun loadSymbols(): List<String> {
        val saved = prefs.getString(KEY_SYMBOLS, null)
        if (saved.isNullOrBlank()) {
            return DEFAULT_SYMBOLS
        }
        val custom = saved.split(",").map { it.trim().uppercase() }.filter { it.isNotBlank() }
        return (DEFAULT_SYMBOLS + custom).distinct()
    }

    fun addSymbol(symbol: String): String {
        val clean = symbol.trim().uppercase()
        if (clean.isBlank()) return ""
        val current = _symbolsFlow.value.toMutableList()
        if (!current.contains(clean)) {
            current.add(clean)
            saveCustomOnly(current)
            _symbolsFlow.value = current
        }
        return clean
    }

    fun removeSymbol(symbol: String) {
        val clean = symbol.trim().uppercase()
        // Do not allow removing default primary indices
        if (clean in listOf("NIFTY", "BANKNIFTY")) return
        val current = _symbolsFlow.value.toMutableList()
        if (current.remove(clean)) {
            saveCustomOnly(current)
            _symbolsFlow.value = current
        }
    }

    private fun saveCustomOnly(allList: List<String>) {
        val custom = allList.filter { it !in DEFAULT_SYMBOLS }
        prefs.edit().putString(KEY_SYMBOLS, custom.joinToString(",")).apply()
    }
}
