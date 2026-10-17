package com.spotpricepredictor.pro.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.domain.model.TickerItem
import com.spotpricepredictor.pro.domain.usecase.GetRealtimeQuoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TickerViewModel @Inject constructor(
    private val getRealtimeQuoteUseCase: GetRealtimeQuoteUseCase
) : ViewModel() {

    private val _tickerItems = MutableStateFlow(TickerItem.DEFAULT_TICKERS)
    val tickerItems: StateFlow<List<TickerItem>> = _tickerItems.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startRealtimeTickerPolling()
    }

    private fun startRealtimeTickerPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            // Key indices and bellwethers to continuously stream in background
            val keySymbols = listOf(
                "NIFTY", "BANKNIFTY", "FINNIFTY", "SENSEX",
                "RELIANCE", "TCS", "HDFCBANK", "INFY", "SBIN", "TATAMOTORS"
            )
            while (isActive) {
                for (sym in keySymbols) {
                    val res = getRealtimeQuoteUseCase(sym)
                    res.onSuccess { quote ->
                        val currentList = _tickerItems.value.toMutableList()
                        val idx = currentList.indexOfFirst { it.symbol.equals(quote.symbol, ignoreCase = true) }
                        if (idx >= 0) {
                            val existing = currentList[idx]
                            currentList[idx] = existing.copy(
                                spotPrice = quote.spotPrice,
                                change = quote.change,
                                changePercent = quote.changePercent,
                                isPositive = quote.change >= 0
                            )
                            _tickerItems.value = currentList
                        }
                    }
                    delay(2500L) // Stagger network requests gently
                }
                delay(10_000L) // Rest between refresh passes
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
