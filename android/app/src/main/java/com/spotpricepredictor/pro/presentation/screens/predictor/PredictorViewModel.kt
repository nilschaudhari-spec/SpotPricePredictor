package com.spotpricepredictor.pro.presentation.screens.predictor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.data.preferences.ServerSettingsManager
import com.spotpricepredictor.pro.data.preferences.WatchlistManager
import com.spotpricepredictor.pro.domain.model.LiveQuote
import com.spotpricepredictor.pro.domain.model.PredictionHorizon
import com.spotpricepredictor.pro.domain.model.PredictionResult
import com.spotpricepredictor.pro.domain.model.StockCatalog
import com.spotpricepredictor.pro.domain.model.TrendDirection
import com.spotpricepredictor.pro.domain.usecase.GetRealtimeQuoteUseCase
import com.spotpricepredictor.pro.domain.usecase.PredictSpotPriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface PredictorUiState {
    object Idle : PredictorUiState
    object Loading : PredictorUiState
    data class Success(val prediction: PredictionResult) : PredictorUiState
    data class Error(val message: String) : PredictorUiState
}

@HiltViewModel
class PredictorViewModel @Inject constructor(
    private val predictUseCase: PredictSpotPriceUseCase,
    private val serverSettingsManager: ServerSettingsManager,
    private val watchlistManager: WatchlistManager,
    private val getRealtimeQuoteUseCase: GetRealtimeQuoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PredictorUiState>(PredictorUiState.Idle)
    val uiState: StateFlow<PredictorUiState> = _uiState.asStateFlow()

    val serverUrl: StateFlow<String> = serverSettingsManager.baseUrlFlow
    val supportedSymbols: StateFlow<List<String>> = watchlistManager.symbolsFlow

    val liveQuote = MutableStateFlow<LiveQuote?>(null)
    val isFetchingQuote = MutableStateFlow(false)
    val lastSyncTime = MutableStateFlow<String>("Live")
    val isAutoDetectActive = MutableStateFlow(true)

    var selectedSymbol = MutableStateFlow("NIFTY")
    var spotPriceInput = MutableStateFlow("22520.0")
    var selectedHorizon = MutableStateFlow(PredictionHorizon.MIN_15)
    var selectedTrend = MutableStateFlow(TrendDirection.AUTO)
    var customMinutesInput = MutableStateFlow("45")

    private var pollingJob: Job? = null

    init {
        val initialSpot = StockCatalog.getDefaultSpot("NIFTY")
        spotPriceInput.value = initialSpot.toString()
        fetchLiveQuote("NIFTY", autoUpdateInput = true)
        startRealtimeQuotePolling()
    }

    fun setServerUrl(url: String) {
        serverSettingsManager.setBaseUrl(url)
    }

    fun addCustomStock(symbol: String) {
        val clean = watchlistManager.addSymbol(symbol)
        if (clean.isNotBlank()) {
            onSymbolSelected(clean)
        }
    }

    fun onSymbolSelected(symbol: String) {
        val clean = symbol.trim().uppercase()
        if (clean.isBlank()) return

        selectedSymbol.value = clean
        isAutoDetectActive.value = true

        // 1. Immediately auto-detect baseline spot from StockCatalog
        val baselineSpot = StockCatalog.getDefaultSpot(clean)
        spotPriceInput.value = baselineSpot.toString()

        // 2. Fetch live real-time market quote
        fetchLiveQuote(clean, autoUpdateInput = true)
    }

    fun autoDetectSpotPrice() {
        isAutoDetectActive.value = true
        val currentQuote = liveQuote.value
        if (currentQuote != null && currentQuote.symbol == selectedSymbol.value) {
            spotPriceInput.value = currentQuote.spotPrice.toString()
        } else {
            val fallback = StockCatalog.getDefaultSpot(selectedSymbol.value)
            spotPriceInput.value = fallback.toString()
        }
        fetchLiveQuote(selectedSymbol.value, autoUpdateInput = true)
    }

    fun fetchLiveQuote(symbol: String, autoUpdateInput: Boolean = true) {
        viewModelScope.launch {
            isFetchingQuote.value = true
            val res = getRealtimeQuoteUseCase(symbol)
            res.fold(
                onSuccess = { quote ->
                    liveQuote.value = quote
                    if (autoUpdateInput || isAutoDetectActive.value) {
                        spotPriceInput.value = quote.spotPrice.toString()
                    }
                    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                    lastSyncTime.value = timeFormat.format(Date())
                },
                onFailure = {
                    // Fallback to stock catalog default if quote fails
                    if (spotPriceInput.value.isBlank()) {
                        spotPriceInput.value = StockCatalog.getDefaultSpot(symbol).toString()
                    }
                }
            )
            isFetchingQuote.value = false
        }
    }

    private fun startRealtimeQuotePolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(8000L) // Poll every 8 seconds for real-time market updates
                val currentSym = selectedSymbol.value
                val res = getRealtimeQuoteUseCase(currentSym)
                res.onSuccess { quote ->
                    liveQuote.value = quote
                    if (isAutoDetectActive.value) {
                        spotPriceInput.value = quote.spotPrice.toString()
                    }
                    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                    lastSyncTime.value = timeFormat.format(Date())
                }
            }
        }
    }

    val customMinutes: StateFlow<String> = customMinutesInput.asStateFlow()

    fun onSpotPriceChanged(value: String) {
        spotPriceInput.value = value
        // User manually typed a price, disable auto-overwrite until next symbol click or auto-detect tap
        isAutoDetectActive.value = false
    }

    fun onHorizonSelected(horizon: PredictionHorizon) {
        selectedHorizon.value = horizon
    }

    fun onCustomMinutesChanged(minutes: String) {
        val digits = minutes.filter { it.isDigit() }.take(4)
        customMinutesInput.value = digits
    }

    fun onTrendSelected(trend: TrendDirection) {
        selectedTrend.value = trend
    }

    fun runPrediction() {
        val spot = spotPriceInput.value.toDoubleOrNull() ?: liveQuote.value?.spotPrice ?: StockCatalog.getDefaultSpot(selectedSymbol.value)
        val isCustom = selectedHorizon.value == PredictionHorizon.CUSTOM
        val customMins = if (isCustom) {
            val parsed = customMinutesInput.value.toIntOrNull() ?: 45
            if (parsed <= 0) 45 else parsed
        } else {
            selectedHorizon.value.minutes
        }

        viewModelScope.launch {
            _uiState.value = PredictorUiState.Loading
            val result = predictUseCase(
                symbol = selectedSymbol.value,
                currentSpot = spot,
                horizon = selectedHorizon.value,
                customMinutes = customMins,
                trendMode = selectedTrend.value
            )

            result.fold(
                onSuccess = { prediction ->
                    _uiState.value = PredictorUiState.Success(prediction)
                },
                onFailure = { error ->
                    _uiState.value = PredictorUiState.Error(error.localizedMessage ?: "Prediction failed")
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
