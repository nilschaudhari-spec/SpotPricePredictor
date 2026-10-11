package com.spotpricepredictor.pro.presentation.screens.predictor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.data.preferences.ServerSettingsManager
import com.spotpricepredictor.pro.data.preferences.WatchlistManager
import com.spotpricepredictor.pro.domain.model.LiveQuote
import com.spotpricepredictor.pro.domain.model.PredictionHorizon
import com.spotpricepredictor.pro.domain.model.PredictionResult
import com.spotpricepredictor.pro.domain.model.TrendDirection
import com.spotpricepredictor.pro.domain.usecase.GetRealtimeQuoteUseCase
import com.spotpricepredictor.pro.domain.usecase.PredictSpotPriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    var selectedSymbol = MutableStateFlow("NIFTY")
    var spotPriceInput = MutableStateFlow("22520.0")
    var selectedHorizon = MutableStateFlow(PredictionHorizon.MIN_15)
    var selectedTrend = MutableStateFlow(TrendDirection.AUTO)
    var customMinutesInput = MutableStateFlow("45")

    init {
        fetchLiveQuote("NIFTY")
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
        selectedSymbol.value = symbol
        fetchLiveQuote(symbol)
    }

    fun fetchLiveQuote(symbol: String) {
        viewModelScope.launch {
            isFetchingQuote.value = true
            val res = getRealtimeQuoteUseCase(symbol)
            res.fold(
                onSuccess = { quote ->
                    liveQuote.value = quote
                    spotPriceInput.value = quote.spotPrice.toString()
                },
                onFailure = {
                    // Fallback to existing spot or default
                }
            )
            isFetchingQuote.value = false
        }
    }

    val customMinutes: StateFlow<String> = customMinutesInput.asStateFlow()

    fun onSpotPriceChanged(value: String) {
        spotPriceInput.value = value
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
        val spot = spotPriceInput.value.toDoubleOrNull() ?: liveQuote.value?.spotPrice ?: 22520.0
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
}
