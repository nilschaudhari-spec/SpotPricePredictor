package com.spotpricepredictor.pro.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.domain.usecase.GetPredictionHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getPredictionHistoryUseCase: GetPredictionHistoryUseCase
) : ViewModel() {

    val historyList: StateFlow<List<PredictionHistoryEntity>> = getPredictionHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
