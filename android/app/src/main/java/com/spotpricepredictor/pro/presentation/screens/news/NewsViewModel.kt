package com.spotpricepredictor.pro.presentation.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.domain.model.MarketNewsBriefing
import com.spotpricepredictor.pro.domain.model.NewsSectionType
import com.spotpricepredictor.pro.domain.usecase.GetMarketNewsUseCase
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

sealed interface NewsUiState {
    object Loading : NewsUiState
    data class Success(val briefing: MarketNewsBriefing) : NewsUiState
    data class Error(val message: String) : NewsUiState
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getMarketNewsUseCase: GetMarketNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    val selectedSection = MutableStateFlow(NewsSectionType.ALL)
    val searchQuery = MutableStateFlow("")

    val lastSyncTime = MutableStateFlow("")
    val isRefreshing = MutableStateFlow(false)

    private var pollingJob: Job? = null

    init {
        loadNews()
        startRealtimeNewsPolling()
    }

    fun loadNews() {
        viewModelScope.launch {
            if (_uiState.value !is NewsUiState.Success) {
                _uiState.value = NewsUiState.Loading
            } else {
                isRefreshing.value = true
            }
            val result = getMarketNewsUseCase()
            result.fold(
                onSuccess = { briefing ->
                    _uiState.value = NewsUiState.Success(briefing)
                    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                    lastSyncTime.value = timeFormat.format(Date())
                },
                onFailure = { error ->
                    if (_uiState.value !is NewsUiState.Success) {
                        _uiState.value = NewsUiState.Error(error.localizedMessage ?: "Failed to load market news")
                    }
                }
            )
            isRefreshing.value = false
        }
    }

    private fun startRealtimeNewsPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000L) // Real-time feed refresh every 60 seconds
                val result = getMarketNewsUseCase()
                result.onSuccess { briefing ->
                    _uiState.value = NewsUiState.Success(briefing)
                    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                    lastSyncTime.value = timeFormat.format(Date())
                }
            }
        }
    }

    fun onSectionSelected(section: NewsSectionType) {
        selectedSection.value = section
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
