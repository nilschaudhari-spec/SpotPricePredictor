package com.spotpricepredictor.pro.presentation.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.domain.model.MarketNewsBriefing
import com.spotpricepredictor.pro.domain.model.NewsSectionType
import com.spotpricepredictor.pro.domain.usecase.GetMarketNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            val result = getMarketNewsUseCase()
            result.fold(
                onSuccess = { briefing ->
                    _uiState.value = NewsUiState.Success(briefing)
                },
                onFailure = { error ->
                    _uiState.value = NewsUiState.Error(error.localizedMessage ?: "Failed to load market news")
                }
            )
        }
    }

    fun onSectionSelected(section: NewsSectionType) {
        selectedSection.value = section
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}
