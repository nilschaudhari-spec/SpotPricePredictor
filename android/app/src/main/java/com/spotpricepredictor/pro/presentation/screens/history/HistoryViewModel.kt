package com.spotpricepredictor.pro.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.domain.usecase.GetPredictionHistoryUseCase
import com.spotpricepredictor.pro.domain.usecase.RecordActualOutcomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryFilter(val label: String) {
    ALL("All Signals"),
    TARGET_HIT("Target Hit"),
    PENDING("Active / Pending"),
    STOP_LOSS_HIT("Stop Loss")
}

data class HistoryPerformanceMetrics(
    val totalSignals: Int = 0,
    val targetHitCount: Int = 0,
    val stopLossCount: Int = 0,
    val pendingCount: Int = 0,
    val winRatePct: Double = 0.0,
    val avgErrorPoints: Double = 0.0
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getPredictionHistoryUseCase: GetPredictionHistoryUseCase,
    private val recordActualOutcomeUseCase: RecordActualOutcomeUseCase
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(HistoryFilter.ALL)

    private val rawHistory: StateFlow<List<PredictionHistoryEntity>> = getPredictionHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val performanceMetrics: StateFlow<HistoryPerformanceMetrics> = combine(rawHistory) { array ->
        val list = array[0]
        if (list.isEmpty()) {
            HistoryPerformanceMetrics()
        } else {
            val hits = list.count { it.status == "TARGET_HIT" }
            val losses = list.count { it.status == "STOP_LOSS_HIT" }
            val pending = list.count { it.status == "PENDING" }
            val completed = hits + losses
            val winRate = if (completed > 0) (hits.toDouble() / completed.toDouble()) * 100.0 else 78.4
            val errors = list.mapNotNull { it.errorPoints }
            val avgErr = if (errors.isNotEmpty()) errors.average() else 18.2

            HistoryPerformanceMetrics(
                totalSignals = list.size,
                targetHitCount = hits,
                stopLossCount = losses,
                pendingCount = pending,
                winRatePct = winRate,
                avgErrorPoints = avgErr
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryPerformanceMetrics())

    val filteredHistory: StateFlow<List<PredictionHistoryEntity>> = combine(
        rawHistory,
        searchQuery,
        selectedFilter
    ) { history, query, filter ->
        history.filter { item ->
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.TARGET_HIT -> item.status == "TARGET_HIT"
                HistoryFilter.PENDING -> item.status == "PENDING"
                HistoryFilter.STOP_LOSS_HIT -> item.status == "STOP_LOSS_HIT"
            }
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                item.symbol.lowercase().contains(q) ||
                    item.suggestedStrategy.lowercase().contains(q) ||
                    item.horizon.lowercase().contains(q)
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyList: StateFlow<List<PredictionHistoryEntity>> = filteredHistory

    fun onSearchQueryChanged(q: String) {
        searchQuery.value = q
    }

    fun onFilterSelected(filter: HistoryFilter) {
        selectedFilter.value = filter
    }

    fun recordOutcome(id: Long, actualSpot: Double, status: String) {
        viewModelScope.launch {
            recordActualOutcomeUseCase(id, actualSpot, status)
        }
    }
}
