package com.spotpricepredictor.pro.presentation.screens.signals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.domain.model.ChartData
import com.spotpricepredictor.pro.domain.model.TradeSuggestion
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignalsViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<TradeSuggestion>>(emptyList())
    val suggestions: StateFlow<List<TradeSuggestion>> = _suggestions.asStateFlow()

    private val _marketSentiment = MutableStateFlow("Scanning Market...")
    val marketSentiment: StateFlow<String> = _marketSentiment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Filters
    private val _selectedCategory = MutableStateFlow("ALL") // ALL, INDEX, EQUITY
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedInstrument = MutableStateFlow("ALL") // ALL, EQUITY, FNO
    val selectedInstrument: StateFlow<String> = _selectedInstrument.asStateFlow()

    private val _selectedAction = MutableStateFlow("ALL") // ALL, BUY, SELL
    val selectedAction: StateFlow<String> = _selectedAction.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Chart viewer state
    private val _activeChartSymbol = MutableStateFlow<String?>(null)
    val activeChartSymbol: StateFlow<String?> = _activeChartSymbol.asStateFlow()

    private val _chartData = MutableStateFlow<ChartData?>(null)
    val chartData: StateFlow<ChartData?> = _chartData.asStateFlow()

    private val _chartInterval = MutableStateFlow("15m")
    val chartInterval: StateFlow<String> = _chartInterval.asStateFlow()

    private val _isChartLoading = MutableStateFlow(false)
    val isChartLoading: StateFlow<Boolean> = _isChartLoading.asStateFlow()

    init {
        loadTradeSuggestions()
    }

    fun loadTradeSuggestions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getTradeSuggestions(
                category = _selectedCategory.value,
                instrument = _selectedInstrument.value,
                action = _selectedAction.value
            )
            result.onSuccess { res ->
                _suggestions.value = res.suggestions
                _marketSentiment.value = res.marketSentiment
                _isLoading.value = false
            }.onFailure { err ->
                _errorMessage.value = err.message
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        loadTradeSuggestions()
    }

    fun setInstrument(instrument: String) {
        _selectedInstrument.value = instrument
        loadTradeSuggestions()
    }

    fun setAction(action: String) {
        _selectedAction.value = action
        loadTradeSuggestions()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openChart(symbol: String) {
        _activeChartSymbol.value = symbol
        loadChartData(symbol, _chartInterval.value)
    }

    fun closeChart() {
        _activeChartSymbol.value = null
        _chartData.value = null
    }

    fun changeChartInterval(interval: String) {
        _chartInterval.value = interval
        _activeChartSymbol.value?.let { sym ->
            loadChartData(sym, interval)
        }
    }

    private fun loadChartData(symbol: String, interval: String) {
        viewModelScope.launch {
            _isChartLoading.value = true
            val result = repository.getChartData(symbol = symbol, interval = interval, limit = 50)
            result.onSuccess { data ->
                _chartData.value = data
                _isChartLoading.value = false
            }.onFailure {
                _isChartLoading.value = false
            }
        }
    }
}
