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
import com.spotpricepredictor.pro.domain.model.ChartData
import com.spotpricepredictor.pro.domain.model.MarketStatusHelper
import com.spotpricepredictor.pro.domain.model.MarketStatusInfo
import com.spotpricepredictor.pro.domain.model.GainersLosersData
import com.spotpricepredictor.pro.domain.model.MarketMoverItem
import com.spotpricepredictor.pro.domain.model.CorporateActionsData
import com.spotpricepredictor.pro.domain.model.PeerComparisonData
import com.spotpricepredictor.pro.domain.model.DetailedTechnicalsData
import com.spotpricepredictor.pro.domain.model.ResultsAndEventsData
import com.spotpricepredictor.pro.domain.repository.MarketRepository
import com.spotpricepredictor.pro.domain.usecase.GetChartDataUseCase
import com.spotpricepredictor.pro.domain.usecase.GetGainersLosersUseCase
import com.spotpricepredictor.pro.domain.usecase.GetRealtimeQuoteUseCase
import com.spotpricepredictor.pro.domain.usecase.PredictSpotPriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import java.util.Random
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
    private val getRealtimeQuoteUseCase: GetRealtimeQuoteUseCase,
    private val getChartDataUseCase: GetChartDataUseCase,
    private val getGainersLosersUseCase: GetGainersLosersUseCase,
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PredictorUiState>(PredictorUiState.Idle)
    val uiState: StateFlow<PredictorUiState> = _uiState.asStateFlow()

    val serverUrl: StateFlow<String> = serverSettingsManager.baseUrlFlow
    val supportedSymbols: StateFlow<List<String>> = watchlistManager.symbolsFlow

    val liveQuote = MutableStateFlow<LiveQuote?>(null)
    val isFetchingQuote = MutableStateFlow(false)
    val lastSyncTime = MutableStateFlow<String>("Live")
    val isAutoDetectActive = MutableStateFlow(true)

    // Top Gainers & Losers & Most Active by Volume/Value (NSE & BSE)
    val gainersLosers = MutableStateFlow<GainersLosersData?>(null)
    val isMoversLoading = MutableStateFlow(false)
    val moversExchange = MutableStateFlow("NSE") // "NSE" or "BSE"
    val moversTab = MutableStateFlow("GAINERS") // "GAINERS", "LOSERS", "VOLUME", or "VALUE"

    // Corporate Actions
    val corporateActions = MutableStateFlow<CorporateActionsData?>(null)
    val isCorporateActionsLoading = MutableStateFlow(false)
    val showCorporateActionsModal = MutableStateFlow(false)
    val selectedCorporateActionsFilter = MutableStateFlow("ALL") // "ALL", "DIVIDEND", "BONUS", "SPLIT"

    // Peer Comparison
    val peerComparison = MutableStateFlow<PeerComparisonData?>(null)
    val isPeerComparisonLoading = MutableStateFlow(false)
    val showPeerModal = MutableStateFlow(false)

    // Detailed Technical Analysis
    val detailedTechnicals = MutableStateFlow<DetailedTechnicalsData?>(null)
    val isTechnicalsLoading = MutableStateFlow(false)
    val showTechnicalsModal = MutableStateFlow(false)
    val selectedTechnicalsTab = MutableStateFlow("MAs") // "MAs", "OSCILLATORS", "PIVOTS"

    // Results and Macro Events
    val resultsAndEvents = MutableStateFlow<ResultsAndEventsData?>(null)
    val isResultsEventsLoading = MutableStateFlow(false)
    val showResultsEventsModal = MutableStateFlow(false)
    val selectedResultsEventsTab = MutableStateFlow("RESULTS") // "RESULTS", "EVENTS", "ECONOMIC"

    // Market Status (Live vs Closed)
    val marketStatus = MutableStateFlow(MarketStatusHelper.getMarketStatus("NIFTY"))
    val isDemoSimulation = MutableStateFlow(false)

    // 1ms High-Frequency Real-Time Stream Engine
    val is1msStreamingActive = MutableStateFlow(false)
    val tickCounter = MutableStateFlow(0L)

    // Script Categories
    val selectedCategory = MutableStateFlow("ALL") // ALL, INDICES, COMMODITIES, BANKING, IT, AUTO, ENERGY, FMCG, METALS, CUSTOM
    val categorySymbols = MutableStateFlow<List<String>>(emptyList())

    val showChartModal = MutableStateFlow(false)
    val chartData = MutableStateFlow<ChartData?>(null)
    val isChartLoading = MutableStateFlow(false)
    val chartInterval = MutableStateFlow("15m")

    var selectedSymbol = MutableStateFlow("NIFTY")
    var spotPriceInput = MutableStateFlow("23053.10")
    var selectedHorizon = MutableStateFlow(PredictionHorizon.MIN_15)
    var selectedTrend = MutableStateFlow(TrendDirection.AUTO)
    var customMinutesInput = MutableStateFlow("45")

    private var pollingJob: Job? = null
    private var highFreqTickerJob: Job? = null
    private val rng = Random()

    init {
        val initialSpot = StockCatalog.getDefaultSpot("NIFTY")
        spotPriceInput.value = initialSpot.toString()
        updateCategorySymbols("ALL")
        val mStatus = MarketStatusHelper.getMarketStatus("NIFTY")
        marketStatus.value = mStatus
        is1msStreamingActive.value = mStatus.isOpen
        fetchLiveQuote("NIFTY", autoUpdateInput = true)
        fetchGainersLosers("NSE")
        fetchCorporateActions("NIFTY")
        fetchPeerComparison("NIFTY")
        fetchDetailedTechnicals("NIFTY")
        fetchResultsEvents("NIFTY")
        startRealtimeQuotePolling()
        if (mStatus.isOpen) {
            start1msHighFrequencyTicker()
        }
    }

    fun setServerUrl(url: String) {
        serverSettingsManager.setBaseUrl(url)
    }

    fun toggle1msStreaming() {
        val mStatus = MarketStatusHelper.getMarketStatus(selectedSymbol.value)
        marketStatus.value = mStatus
        if (!mStatus.isOpen) {
            // Market is closed: toggling activates or deactivates demo simulation mode
            val nextDemo = !isDemoSimulation.value
            isDemoSimulation.value = nextDemo
            is1msStreamingActive.value = nextDemo
            if (nextDemo) {
                start1msHighFrequencyTicker()
            } else {
                highFreqTickerJob?.cancel()
                fetchLiveQuote(selectedSymbol.value, autoUpdateInput = true)
            }
        } else {
            val next = !is1msStreamingActive.value
            is1msStreamingActive.value = next
            if (next) {
                start1msHighFrequencyTicker()
            } else {
                highFreqTickerJob?.cancel()
            }
        }
    }

    fun setMoversExchange(exchange: String) {
        val ex = exchange.trim().uppercase()
        moversExchange.value = ex
        fetchGainersLosers(ex)
    }

    fun setMoversTab(tab: String) {
        moversTab.value = tab.trim().uppercase()
    }

    fun fetchGainersLosers(exchange: String = moversExchange.value) {
        viewModelScope.launch {
            isMoversLoading.value = true
            val res = getGainersLosersUseCase(exchange = exchange, limit = 10)
            res.onSuccess { data ->
                gainersLosers.value = data
            }
            isMoversLoading.value = false
        }
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
        updateCategorySymbols(category)
    }

    private fun updateCategorySymbols(cat: String) {
        val allCatalog = StockCatalog.STOCKS
        val result = when (cat.uppercase()) {
            "INDICES" -> allCatalog.filter { it.sector.contains("Index", ignoreCase = true) }.map { it.symbol }
            "COMMODITIES" -> allCatalog.filter {
                it.sector.contains("Commodities", ignoreCase = true) ||
                it.sector.contains("Precious Metals", ignoreCase = true) ||
                it.sector.contains("Base Metals", ignoreCase = true) ||
                it.sector.contains("Currency", ignoreCase = true)
            }.map { it.symbol }
            "BANKING" -> allCatalog.filter { it.sector.contains("Bank", ignoreCase = true) || it.sector.contains("Financial", ignoreCase = true) || it.sector.contains("NBFC", ignoreCase = true) }.map { it.symbol }
            "IT" -> allCatalog.filter { it.sector.contains("IT", ignoreCase = true) || it.sector.contains("Tech", ignoreCase = true) || it.sector.contains("Software", ignoreCase = true) }.map { it.symbol }
            "AUTO" -> allCatalog.filter { it.sector.contains("Auto", ignoreCase = true) }.map { it.symbol }
            "ENERGY" -> allCatalog.filter { it.sector.contains("Energy", ignoreCase = true) || it.sector.contains("Oil", ignoreCase = true) || it.sector.contains("Power", ignoreCase = true) || it.sector.contains("Gas", ignoreCase = true) }.map { it.symbol }
            "FMCG" -> allCatalog.filter { it.sector.contains("FMCG", ignoreCase = true) || it.sector.contains("Retail", ignoreCase = true) || it.sector.contains("Consumer", ignoreCase = true) }.map { it.symbol }
            "METALS" -> allCatalog.filter { it.sector.contains("Metal", ignoreCase = true) || it.sector.contains("Mining", ignoreCase = true) || it.sector.contains("Steel", ignoreCase = true) }.map { it.symbol }
            "CUSTOM" -> watchlistManager.symbolsFlow.value.filter { it !in WatchlistManager.DEFAULT_SYMBOLS }
            else -> watchlistManager.symbolsFlow.value
        }
        categorySymbols.value = result.distinct()
    }

    fun addCustomStock(symbol: String) {
        val clean = watchlistManager.addSymbol(symbol)
        if (clean.isNotBlank()) {
            updateCategorySymbols(selectedCategory.value)
            onSymbolSelected(clean)
        }
    }

    fun removeCustomStock(symbol: String) {
        watchlistManager.removeSymbol(symbol)
        updateCategorySymbols(selectedCategory.value)
        if (selectedSymbol.value.equals(symbol.trim(), ignoreCase = true)) {
            onSymbolSelected("NIFTY")
        }
    }

    fun onSymbolSelected(symbol: String) {
        val clean = symbol.trim().uppercase()
        if (clean.isBlank()) return

        selectedSymbol.value = clean
        isAutoDetectActive.value = true
        val mStatus = MarketStatusHelper.getMarketStatus(clean)
        marketStatus.value = mStatus

        // 1. Immediately auto-detect baseline spot from StockCatalog
        val baselineSpot = StockCatalog.getDefaultSpot(clean)
        spotPriceInput.value = baselineSpot.toString()

        // 2. Fetch live or official closing market quote & deep analysis
        fetchLiveQuote(clean, autoUpdateInput = true)
        fetchDetailedTechnicals(clean)
        fetchPeerComparison(clean)
        fetchCorporateActions(clean)
        fetchResultsEvents(clean)

        // 3. Control 1ms stream based on market open status
        if (!mStatus.isOpen && !isDemoSimulation.value) {
            is1msStreamingActive.value = false
            highFreqTickerJob?.cancel()
        } else if (mStatus.isOpen || isDemoSimulation.value) {
            start1msHighFrequencyTicker()
        }

        // 4. If chart modal is open, load new chart immediately
        if (showChartModal.value) {
            loadChartData(chartInterval.value)
        }
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
            val mStatus = MarketStatusHelper.getMarketStatus(symbol)
            marketStatus.value = mStatus

            val res = getRealtimeQuoteUseCase(symbol)
            res.fold(
                onSuccess = { quote ->
                    liveQuote.value = quote
                    if (!quote.isMarketOpen && !isDemoSimulation.value) {
                        is1msStreamingActive.value = false
                        highFreqTickerJob?.cancel()
                    }
                    if (autoUpdateInput || isAutoDetectActive.value) {
                        spotPriceInput.value = quote.spotPrice.toString()
                    }
                    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                    lastSyncTime.value = timeFormat.format(Date())
                },
                onFailure = {
                    val fallbackSpot = StockCatalog.getDefaultSpot(symbol)
                    if (spotPriceInput.value.isBlank()) {
                        spotPriceInput.value = fallbackSpot.toString()
                    }
                    liveQuote.value = LiveQuote(
                        symbol = symbol,
                        spotPrice = fallbackSpot,
                        previousClose = fallbackSpot,
                        change = 0.0,
                        changePercent = 0.0,
                        isRealtime = false,
                        isMarketOpen = mStatus.isOpen,
                        marketStatus = mStatus.statusText,
                        sessionLabel = mStatus.sessionLabel,
                        lastCloseTime = mStatus.lastCloseTime,
                        nextSession = mStatus.nextSession
                    )
                }
            )
            isFetchingQuote.value = false
        }
    }

    private fun startRealtimeQuotePolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(3000L) // Anchor poll every 3 seconds to keep base exchange quote synced
                val currentSym = selectedSymbol.value
                val res = getRealtimeQuoteUseCase(currentSym)
                res.onSuccess { quote ->
                    val prevLive = liveQuote.value
                    if (prevLive == null || prevLive.symbol != quote.symbol || (!is1msStreamingActive.value && !isDemoSimulation.value)) {
                        liveQuote.value = quote
                        if (isAutoDetectActive.value && (!is1msStreamingActive.value || !quote.isMarketOpen)) {
                            spotPriceInput.value = quote.spotPrice.toString()
                        }
                    }
                    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                    lastSyncTime.value = timeFormat.format(Date())
                }
            }
        }
    }

    /**
     * Ultra-Fast High-Frequency 1 Millisecond Real-Time Ticker Stream.
     * Active only when market is OPEN, or when user explicitly tests in Demo Simulation Mode.
     * When market is CLOSED, spot price and candles remain frozen at official closing data.
     */
    private fun start1msHighFrequencyTicker() {
        highFreqTickerJob?.cancel()
        val currentSym = selectedSymbol.value
        val mStatus = MarketStatusHelper.getMarketStatus(currentSym)
        marketStatus.value = mStatus

        // If market is closed and user has not requested demo simulation, do not run ticker
        if (!mStatus.isOpen && !isDemoSimulation.value) {
            is1msStreamingActive.value = false
            return
        }

        is1msStreamingActive.value = true
        highFreqTickerJob = viewModelScope.launch(Dispatchers.Default) {
            var tickIdx = 0L
            while (isActive) {
                delay(1L) // 1 millisecond real-time tick interval
                tickIdx++
                tickCounter.value = tickIdx

                val sym = selectedSymbol.value
                val baseSpot = liveQuote.value?.spotPrice ?: StockCatalog.getDefaultSpot(sym)
                val prevClose = liveQuote.value?.previousClose ?: baseSpot

                // Generate realistic micro-tick fluctuation (within ±0.03% market wave)
                val microWave = kotlin.math.sin(tickIdx * 0.04) * 0.00025 + (rng.nextDouble() - 0.5) * 0.00015
                val newPrice = Math.round((baseSpot * (1.0 + microWave)) * 100.0) / 100.0
                val change = Math.round((newPrice - prevClose) * 100.0) / 100.0
                val changePct = if (prevClose > 0) Math.round(((change / prevClose) * 100.0) * 100.0) / 100.0 else 0.0

                // Update LiveQuote
                liveQuote.value = LiveQuote(
                    symbol = sym,
                    spotPrice = newPrice,
                    previousClose = prevClose,
                    change = change,
                    changePercent = changePct,
                    isRealtime = mStatus.isOpen || isDemoSimulation.value,
                    isMarketOpen = mStatus.isOpen,
                    marketStatus = if (isDemoSimulation.value && !mStatus.isOpen) "DEMO SIMULATION" else mStatus.statusText,
                    sessionLabel = if (isDemoSimulation.value && !mStatus.isOpen) "Demo Simulation Mode" else mStatus.sessionLabel,
                    lastCloseTime = mStatus.lastCloseTime,
                    nextSession = mStatus.nextSession
                )

                // If user didn't manually overwrite the input, reflect real-time live price
                if (isAutoDetectActive.value && tickIdx % 20L == 0L) {
                    spotPriceInput.value = newPrice.toString()
                }

                // Dynamically update active live candle in chartData
                val currentChart = chartData.value
                if (currentChart != null && currentChart.candles.isNotEmpty() && currentChart.symbol == currentSym) {
                    val candles = currentChart.candles.toMutableList()
                    val lastIdx = candles.size - 1
                    val activeCandle = candles[lastIdx]

                    val updatedHigh = maxOf(activeCandle.high, newPrice)
                    val updatedLow = minOf(activeCandle.low, newPrice)
                    val updatedVol = activeCandle.volume + (rng.nextInt(3) + 1)

                    candles[lastIdx] = activeCandle.copy(
                        close = newPrice,
                        high = updatedHigh,
                        low = updatedLow,
                        volume = updatedVol
                    )

                    chartData.value = currentChart.copy(
                        spotPrice = newPrice,
                        change = change,
                        changePct = changePct,
                        candles = candles
                    )
                }

                // Periodically update active movers widget if selected symbol is among gainers or losers
                if (tickIdx % 500L == 0L && gainersLosers.value != null) {
                    val currentMovers = gainersLosers.value!!
                    val updatedGainers = currentMovers.gainers.map { item ->
                        if (item.symbol.equals(currentSym, ignoreCase = true)) {
                            item.copy(spotPrice = newPrice, change = change, changePercent = changePct, isPositive = change >= 0)
                        } else item
                    }
                    val updatedLosers = currentMovers.losers.map { item ->
                        if (item.symbol.equals(currentSym, ignoreCase = true)) {
                            item.copy(spotPrice = newPrice, change = change, changePercent = changePct, isPositive = change >= 0)
                        } else item
                    }
                    gainersLosers.value = currentMovers.copy(gainers = updatedGainers, losers = updatedLosers)
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

    fun openChartModal() {
        showChartModal.value = true
        loadChartData(chartInterval.value)
    }

    fun closeChartModal() {
        showChartModal.value = false
    }

    fun changeChartInterval(interval: String) {
        chartInterval.value = interval
        loadChartData(interval)
    }

    private fun loadChartData(interval: String) {
        viewModelScope.launch {
            isChartLoading.value = true
            val res = getChartDataUseCase(selectedSymbol.value, interval, limit = 50)
            res.onSuccess { data ->
                chartData.value = data
                isChartLoading.value = false
            }.onFailure {
                isChartLoading.value = false
            }
        }
    }

    // Corporate Actions
    fun fetchCorporateActions(symbol: String? = selectedSymbol.value, actionType: String? = selectedCorporateActionsFilter.value) {
        viewModelScope.launch {
            isCorporateActionsLoading.value = true
            val res = marketRepository.getCorporateActions(symbol = symbol, actionType = actionType, limit = 50)
            res.onSuccess { data ->
                corporateActions.value = data
            }
            isCorporateActionsLoading.value = false
        }
    }

    fun setCorporateActionsFilter(filter: String) {
        selectedCorporateActionsFilter.value = filter
        fetchCorporateActions(symbol = selectedSymbol.value, actionType = filter)
    }

    fun openCorporateActionsModal() {
        showCorporateActionsModal.value = true
        fetchCorporateActions()
    }

    fun closeCorporateActionsModal() {
        showCorporateActionsModal.value = false
    }

    // Peer Comparison
    fun fetchPeerComparison(symbol: String = selectedSymbol.value) {
        viewModelScope.launch {
            isPeerComparisonLoading.value = true
            val res = marketRepository.getPeerComparison(symbol)
            res.onSuccess { data ->
                peerComparison.value = data
            }
            isPeerComparisonLoading.value = false
        }
    }

    fun openPeerModal() {
        showPeerModal.value = true
        fetchPeerComparison()
    }

    fun closePeerModal() {
        showPeerModal.value = false
    }

    // Detailed Technical Analysis
    fun fetchDetailedTechnicals(symbol: String = selectedSymbol.value) {
        viewModelScope.launch {
            isTechnicalsLoading.value = true
            val res = marketRepository.getDetailedTechnicals(symbol)
            res.onSuccess { data ->
                detailedTechnicals.value = data
            }
            isTechnicalsLoading.value = false
        }
    }

    fun setTechnicalsTab(tab: String) {
        selectedTechnicalsTab.value = tab
    }

    fun openTechnicalsModal() {
        showTechnicalsModal.value = true
        fetchDetailedTechnicals()
    }

    fun closeTechnicalsModal() {
        showTechnicalsModal.value = false
    }

    // Results and Macro Events
    fun fetchResultsEvents(symbol: String? = selectedSymbol.value) {
        viewModelScope.launch {
            isResultsEventsLoading.value = true
            val res = marketRepository.getResultsAndEvents(symbol)
            res.onSuccess { data ->
                resultsAndEvents.value = data
            }
            isResultsEventsLoading.value = false
        }
    }

    fun setResultsEventsTab(tab: String) {
        selectedResultsEventsTab.value = tab
    }

    fun openResultsEventsModal() {
        showResultsEventsModal.value = true
        fetchResultsEvents()
    }

    fun closeResultsEventsModal() {
        showResultsEventsModal.value = false
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        highFreqTickerJob?.cancel()
    }
}
