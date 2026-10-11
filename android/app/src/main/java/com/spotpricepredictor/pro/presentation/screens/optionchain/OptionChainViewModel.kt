package com.spotpricepredictor.pro.presentation.screens.optionchain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.data.preferences.WatchlistManager
import com.spotpricepredictor.pro.domain.model.ExpiryInfo
import com.spotpricepredictor.pro.domain.model.OptionChainData
import com.spotpricepredictor.pro.domain.usecase.GetOptionChainUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OptionChainUiState {
    object Loading : OptionChainUiState
    data class Success(val data: OptionChainData) : OptionChainUiState
    data class Error(val message: String) : OptionChainUiState
}

@HiltViewModel
class OptionChainViewModel @Inject constructor(
    private val getOptionChainUseCase: GetOptionChainUseCase,
    private val watchlistManager: WatchlistManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<OptionChainUiState>(OptionChainUiState.Loading)
    val uiState: StateFlow<OptionChainUiState> = _uiState.asStateFlow()

    val symbols: StateFlow<List<String>> = watchlistManager.symbolsFlow
    var selectedSymbol = MutableStateFlow("NIFTY")

    var selectedDaysToExpiry = MutableStateFlow(3.5)
    var selectedExpiryDate = MutableStateFlow<String?>("15-Oct-2026")
    var availableExpiries = MutableStateFlow<List<ExpiryInfo>>(emptyList())

    init {
        loadOptionChain("NIFTY")
    }

    fun addStock(symbol: String) {
        val clean = watchlistManager.addSymbol(symbol)
        if (clean.isNotBlank()) {
            selectedSymbol.value = clean
            loadOptionChain(clean)
        }
    }

    fun onSymbolSelected(symbol: String) {
        selectedSymbol.value = symbol
        loadOptionChain(symbol)
    }

    fun onExpirySelected(expiry: ExpiryInfo) {
        selectedDaysToExpiry.value = expiry.daysToExpiry
        selectedExpiryDate.value = expiry.date
        loadOptionChain(
            symbol = selectedSymbol.value,
            days = expiry.daysToExpiry,
            dateLabel = expiry.date
        )
    }

    fun loadOptionChain(
        symbol: String = selectedSymbol.value,
        days: Double = selectedDaysToExpiry.value,
        dateLabel: String? = selectedExpiryDate.value
    ) {
        viewModelScope.launch {
            _uiState.value = OptionChainUiState.Loading
            val result = getOptionChainUseCase(
                symbol = symbol,
                daysToExpiry = days,
                expiryDate = dateLabel
            )
            result.fold(
                onSuccess = { data ->
                    _uiState.value = OptionChainUiState.Success(data)
                    selectedDaysToExpiry.value = data.selectedDaysToExpiry
                    selectedExpiryDate.value = data.selectedExpiryDate
                    if (data.availableExpiries.isNotEmpty()) {
                        availableExpiries.value = data.availableExpiries
                    }
                },
                onFailure = { error ->
                    _uiState.value = OptionChainUiState.Error(error.localizedMessage ?: "Failed to fetch option chain")
                }
            )
        }
    }
}
