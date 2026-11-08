package com.spotpricepredictor.pro.presentation.screens.predictor

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.data.preferences.WatchlistManager
import com.spotpricepredictor.pro.domain.model.GainersLosersData
import com.spotpricepredictor.pro.domain.model.MarketMoverItem
import com.spotpricepredictor.pro.domain.model.ModelForecastBreakdown
import com.spotpricepredictor.pro.domain.model.PredictionHorizon
import com.spotpricepredictor.pro.domain.model.TrendDirection
import com.spotpricepredictor.pro.presentation.components.AddStockDialog
import com.spotpricepredictor.pro.presentation.components.AdvancedMetricsCard
import com.spotpricepredictor.pro.presentation.components.CorporateActionsDialog
import com.spotpricepredictor.pro.presentation.components.DetailedTechnicalsDialog
import com.spotpricepredictor.pro.presentation.components.DisclaimerBanner
import com.spotpricepredictor.pro.presentation.components.InteractiveCandleChart
import com.spotpricepredictor.pro.presentation.components.PeerComparisonDialog
import com.spotpricepredictor.pro.presentation.components.PredictionResultCard
import com.spotpricepredictor.pro.presentation.components.ResultsAndEventsDialog
import com.spotpricepredictor.pro.presentation.components.CandlestickPatternsDialog
import com.spotpricepredictor.pro.presentation.components.ServerSettingsDialog
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun PredictorScreen(
    viewModel: PredictorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val supportedSymbols by viewModel.supportedSymbols.collectAsState()
    val selectedSymbol by viewModel.selectedSymbol.collectAsState()
    val spotPriceInput by viewModel.spotPriceInput.collectAsState()
    val selectedHorizon by viewModel.selectedHorizon.collectAsState()
    val customMinutes by viewModel.customMinutes.collectAsState()
    val selectedTrend by viewModel.selectedTrend.collectAsState()
    val currentServerUrl by viewModel.serverUrl.collectAsState()
    val liveQuote by viewModel.liveQuote.collectAsState()
    val isFetchingQuote by viewModel.isFetchingQuote.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val isAutoDetectActive by viewModel.isAutoDetectActive.collectAsState()
    val is1msStreamingActive by viewModel.is1msStreamingActive.collectAsState()
    val tickCounter by viewModel.tickCounter.collectAsState()
    val marketStatus by viewModel.marketStatus.collectAsState()
    val isDemoSimulation by viewModel.isDemoSimulation.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categorySymbols by viewModel.categorySymbols.collectAsState()

    val gainersLosers by viewModel.gainersLosers.collectAsState()
    val isMoversLoading by viewModel.isMoversLoading.collectAsState()
    val moversExchange by viewModel.moversExchange.collectAsState()
    val moversTab by viewModel.moversTab.collectAsState()

    val corporateActions by viewModel.corporateActions.collectAsState()
    val isCorporateActionsLoading by viewModel.isCorporateActionsLoading.collectAsState()
    val showCorporateActionsModal by viewModel.showCorporateActionsModal.collectAsState()
    val selectedCorporateActionsFilter by viewModel.selectedCorporateActionsFilter.collectAsState()

    val peerComparison by viewModel.peerComparison.collectAsState()
    val isPeerComparisonLoading by viewModel.isPeerComparisonLoading.collectAsState()
    val showPeerModal by viewModel.showPeerModal.collectAsState()

    val detailedTechnicals by viewModel.detailedTechnicals.collectAsState()
    val isTechnicalsLoading by viewModel.isTechnicalsLoading.collectAsState()
    val showTechnicalsModal by viewModel.showTechnicalsModal.collectAsState()
    val selectedTechnicalsTab by viewModel.selectedTechnicalsTab.collectAsState()

    val resultsAndEvents by viewModel.resultsAndEvents.collectAsState()
    val isResultsEventsLoading by viewModel.isResultsEventsLoading.collectAsState()
    val showResultsEventsModal by viewModel.showResultsEventsModal.collectAsState()
    val selectedResultsEventsTab by viewModel.selectedResultsEventsTab.collectAsState()

    val patternScanner by viewModel.patternScanner.collectAsState()
    val isPatternsLoading by viewModel.isPatternsLoading.collectAsState()
    val showPatternsModal by viewModel.showPatternsModal.collectAsState()
    val selectedPatternTypeFilter by viewModel.selectedPatternTypeFilter.collectAsState()
    val selectedPatternNameFilter by viewModel.selectedPatternNameFilter.collectAsState()

    val showChartModal by viewModel.showChartModal.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val isChartLoading by viewModel.isChartLoading.collectAsState()

    var showServerDialog by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showChartModal) {
        Dialog(
            onDismissRequest = { viewModel.closeChartModal() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepObsidian.copy(alpha = 0.96f))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$selectedSymbol TECHNICAL CHART & INDICATORS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { viewModel.closeChartModal() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isChartLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ElectricCyan)
                        }
                    } else if (chartData != null) {
                        InteractiveCandleChart(
                            chartData = chartData!!,
                            onIntervalChange = { viewModel.changeChartInterval(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No Chart Data Available", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }

    if (showServerDialog) {
        ServerSettingsDialog(
            currentUrl = currentServerUrl,
            onSaveUrl = { viewModel.setServerUrl(it) },
            onDismiss = { showServerDialog = false }
        )
    }

    if (showAddStockDialog) {
        AddStockDialog(
            onDismiss = { showAddStockDialog = false },
            onAddSymbol = { viewModel.addCustomStock(it) }
        )
    }

    if (showCorporateActionsModal) {
        CorporateActionsDialog(
            data = corporateActions,
            isLoading = isCorporateActionsLoading,
            selectedFilter = selectedCorporateActionsFilter,
            onFilterSelected = { viewModel.setCorporateActionsFilter(it) },
            onStockSelected = { viewModel.onSymbolSelected(it) },
            onDismiss = { viewModel.closeCorporateActionsModal() }
        )
    }

    if (showPeerModal) {
        PeerComparisonDialog(
            data = peerComparison,
            isLoading = isPeerComparisonLoading,
            onStockSelected = { viewModel.onSymbolSelected(it) },
            onDismiss = { viewModel.closePeerModal() }
        )
    }

    if (showTechnicalsModal) {
        DetailedTechnicalsDialog(
            data = detailedTechnicals,
            isLoading = isTechnicalsLoading,
            selectedTab = selectedTechnicalsTab,
            onTabSelected = { viewModel.setTechnicalsTab(it) },
            onDismiss = { viewModel.closeTechnicalsModal() }
        )
    }

    if (showResultsEventsModal) {
        ResultsAndEventsDialog(
            data = resultsAndEvents,
            isLoading = isResultsEventsLoading,
            selectedTab = selectedResultsEventsTab,
            onTabSelected = { viewModel.setResultsEventsTab(it) },
            onStockSelected = { viewModel.onSymbolSelected(it) },
            onDismiss = { viewModel.closeResultsEventsModal() }
        )
    }

    if (showPatternsModal) {
        CandlestickPatternsDialog(
            data = patternScanner,
            isLoading = isPatternsLoading,
            selectedTypeFilter = selectedPatternTypeFilter,
            selectedNameFilter = selectedPatternNameFilter,
            onTypeFilterSelected = { viewModel.setPatternTypeFilter(it) },
            onNameFilterSelected = { viewModel.setPatternNameFilter(it) },
            onStockSelected = { viewModel.onSymbolSelected(it) },
            onDismiss = { viewModel.closePatternsModal() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI SPOT PREDICTOR",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Multi-Model Quant Options Engine",
                    fontSize = 12.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showServerDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Server Setup",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = ElectricCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Symbol Selection Chips + Category Selector + Add Stock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SELECT ASSET / SCRIPT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                val isOpen = marketStatus.isOpen
                val isDemo = isDemoSimulation
                val badgeText = when {
                    isOpen && is1msStreamingActive -> "⚡ 1ms LIVE TICKS (#$tickCounter)"
                    isOpen -> "⏸️ STREAM PAUSED"
                    isDemo -> "🧪 DEMO TICKS (#$tickCounter)"
                    else -> "🔴 CLOSED (Tap for Demo)"
                }
                val badgeColor = when {
                    isOpen && is1msStreamingActive -> NeonEmerald
                    isDemo -> AmberWarning
                    isOpen -> AmberWarning
                    else -> CrimsonAlert
                }

                Box(
                    modifier = Modifier
                        .clickable { viewModel.toggle1msStreaming() }
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(0.6.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Text(
                text = "+ ADD STOCK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                modifier = Modifier.clickable { showAddStockDialog = true }
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        // Script Categories (ALL, INDICES, COMMODITIES, BANKING, IT, AUTO, ENERGY, FMCG, METALS, CUSTOM)
        val categories = listOf("ALL", "INDICES", "COMMODITIES", "BANKING", "IT", "AUTO", "ENERGY", "FMCG", "METALS", "CUSTOM")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            items(categories) { cat ->
                val isSelected = cat.equals(selectedCategory, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clickable { viewModel.setCategory(cat) }
                        .background(
                            if (isSelected) ElectricCyan.copy(alpha = 0.2f) else SurfaceDark,
                            RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else CardBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) ElectricCyan else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val currentSymbolsList = if (categorySymbols.isNotEmpty()) categorySymbols else supportedSymbols
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(currentSymbolsList) { symbol ->
                val isSelected = symbol == selectedSymbol
                val isCustom = symbol !in WatchlistManager.DEFAULT_SYMBOLS
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) ElectricCyan else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else if (isCustom) RoyalViolet.copy(alpha = 0.6f) else CardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onSymbolSelected(symbol) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isCustom) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isSelected) DeepObsidian else RoyalViolet,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = symbol,
                            color = if (isSelected) DeepObsidian else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { showAddStockDialog = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ADD",
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Custom Stock Active Status Banner
        val isCustomActive = selectedSymbol !in WatchlistManager.DEFAULT_SYMBOLS
        if (isCustomActive) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalViolet.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, RoyalViolet.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(RoyalViolet.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "CUSTOM STOCK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalViolet
                            )
                        }
                        Text(
                            text = "$selectedSymbol • AI Quant Ensemble Ready",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    TextButton(
                        onClick = { viewModel.removeCustomStock(selectedSymbol) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Remove",
                            fontSize = 11.sp,
                            color = CrimsonAlert,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MARKET TOP GAINERS & LOSERS (NSE & BSE)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                // Header with Exchange Toggles & Refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "🔥 TOP GAINERS & LOSERS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            if (isMoversLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = ElectricCyan)
                            }
                        }
                        Text(
                            text = "Live market momentum scanner • Tap to predict",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }

                    // Exchange Selector (NSE vs BSE)
                    Row(
                        modifier = Modifier
                            .background(CardBackground, RoundedCornerShape(8.dp))
                            .border(0.8.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf("NSE", "BSE").forEach { ex ->
                            val isExSelected = ex.equals(moversExchange, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clickable { viewModel.setMoversExchange(ex) }
                                    .background(
                                        if (isExSelected) ElectricCyan else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (ex == "NSE") "🇮🇳 NSE" else "🏛️ BSE",
                                    fontSize = 10.sp,
                                    fontWeight = if (isExSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isExSelected) DeepObsidian else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Movers Tab (GAINERS, LOSERS, VOLUME, VALUE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "GAINERS" to "🟢 GAINERS",
                        "LOSERS" to "🔴 LOSERS",
                        "VOLUME" to "📊 VOLUME",
                        "VALUE" to "💰 VALUE"
                    ).forEach { (tabKey, tabLabel) ->
                        val isSelected = (moversTab == tabKey)
                        val tabColor = when (tabKey) {
                            "GAINERS" -> NeonEmerald
                            "LOSERS" -> CrimsonAlert
                            "VOLUME" -> ElectricCyan
                            else -> RoyalViolet
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setMoversTab(tabKey) }
                                .background(
                                    if (isSelected) tabColor.copy(alpha = 0.2f) else CardBackground,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) tabColor else CardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) tabColor else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cards List
                val moversList = when (moversTab) {
                    "GAINERS" -> gainersLosers?.gainers ?: emptyList()
                    "LOSERS" -> gainersLosers?.losers ?: emptyList()
                    "VOLUME" -> gainersLosers?.mostActiveVolume ?: emptyList()
                    "VALUE" -> gainersLosers?.mostActiveValue ?: emptyList()
                    else -> gainersLosers?.gainers ?: emptyList()
                }

                if (moversList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading $moversExchange ${moversTab.lowercase()}...",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(moversList) { mover ->
                            val isCurrentSelected = mover.symbol.equals(selectedSymbol, ignoreCase = true)
                            val moverColor = if (mover.isPositive) NeonEmerald else CrimsonAlert
                            val arrow = if (mover.isPositive) "▲ +" else "▼ "

                            Card(
                                modifier = Modifier
                                    .width(156.dp)
                                    .clickable { viewModel.onSymbolSelected(mover.symbol) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                border = BorderStroke(
                                    if (isCurrentSelected) 1.5.dp else 1.dp,
                                    if (isCurrentSelected) ElectricCyan else moverColor.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mover.symbol,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isCurrentSelected) ElectricCyan else TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (mover.exchange == "BSE") AmberWarning.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.2f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = mover.exchange,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (mover.exchange == "BSE") AmberWarning else ElectricCyan
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = mover.name,
                                        fontSize = 9.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${"%,.2f".format(mover.spotPrice)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(moverColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .border(0.6.dp, moverColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$arrow${mover.changePercent}%",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = moverColor
                                            )
                                        }
                                    }

                                    if (mover.volumeStr.isNotBlank() || mover.turnoverStr.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (mover.volumeStr.isNotBlank()) {
                                                Text(
                                                    text = "Vol ${mover.volumeStr}",
                                                    fontSize = 8.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                            if (mover.turnoverStr.isNotBlank()) {
                                                Text(
                                                    text = mover.turnoverStr,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = ElectricCyan
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MARKET INTELLIGENCE & RESEARCH ACTION BAR
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🔬 QUANT INTELLIGENCE & RESEARCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "In-depth analytics for $selectedSymbol • Tap to explore",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(RoyalViolet.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                            .border(0.8.dp, RoyalViolet.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalViolet
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Technical Analysis
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openTechnicalsModal() },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "🎯 Technicals",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val techCall = detailedTechnicals?.overallCall ?: "Analysis"
                            Text(
                                text = techCall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (techCall.contains("BUY")) NeonEmerald else if (techCall.contains("SELL")) CrimsonAlert else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "MAs • RSI • Pivots",
                                fontSize = 8.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // 2. Peer Comparison
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openPeerModal() },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "🏢 Peer Bench",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalViolet
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = peerComparison?.sector ?: "Industry Peers",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "P/E • P/B • ROE",
                                fontSize = 8.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 3. Corporate Actions
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openCorporateActionsModal() },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📜 Corp Actions",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val totalCa = corporateActions?.totalActions ?: 0
                            Text(
                                text = if (totalCa > 0) "$totalCa Actions" else "Upcoming",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "Divs • Splits • Bonus",
                                fontSize = 8.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // 4. Results & Events
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openResultsEventsModal() },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📅 Results/Events",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Q2/Q3 Earnings",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "Quarterly • RBI • Fed",
                                fontSize = 8.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CANDLESTICK & PRICE ACTION PATTERNS SECTION (BUY & SELL PATTERNS)
        var homePatternTab by remember { mutableStateOf("BUY") } // "BUY" or "SELL"

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "🕯️ CANDLESTICK & PRICE ACTION PATTERNS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            if (isPatternsLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = ElectricCyan)
                            }
                        }
                        Text(
                            text = "Hammer, Harami, Rejection Wicks & False Breakouts",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }

                    // View All Modal Button
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.openPatternsModal() }
                            .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .border(0.8.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "EXPLORE ALL (8) ↗",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pattern Type Selector (BUY vs SELL)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Buy Patterns Tab
                    val isBuySelected = homePatternTab == "BUY"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { homePatternTab = "BUY" }
                            .background(
                                if (isBuySelected) NeonEmerald.copy(alpha = 0.2f) else CardBackground,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isBuySelected) NeonEmerald else CardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🟢 BUY PATTERNS (4)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBuySelected) NeonEmerald else TextSecondary
                        )
                    }

                    // Sell Patterns Tab
                    val isSellSelected = homePatternTab == "SELL"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { homePatternTab = "SELL" }
                            .background(
                                if (isSellSelected) CrimsonAlert.copy(alpha = 0.2f) else CardBackground,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSellSelected) CrimsonAlert else CardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔴 SELL PATTERNS (4)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSellSelected) CrimsonAlert else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of patterns for selected tab
                val currentList = if (homePatternTab == "BUY") {
                    patternScanner?.buyPatterns ?: emptyList()
                } else {
                    patternScanner?.sellPatterns ?: emptyList()
                }

                if (currentList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading ${homePatternTab.lowercase()} pattern setups...",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(currentList) { pat ->
                            val isSelected = pat.symbol.equals(selectedSymbol, ignoreCase = true)
                            val isBuy = pat.patternType == "BUY"
                            val accentColor = if (isBuy) NeonEmerald else CrimsonAlert

                            Card(
                                modifier = Modifier
                                    .width(185.dp)
                                    .clickable { viewModel.onSymbolSelected(pat.symbol) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) ElectricCyan else accentColor.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = pat.symbol,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) ElectricCyan else TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "${pat.reliabilityPct}% Win",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = pat.patternName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = pat.candleAnatomy,
                                        fontSize = 8.5.sp,
                                        color = TextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 11.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "Entry", fontSize = 7.5.sp, color = TextSecondary)
                                            Text(text = "₹${"%,.1f".format(pat.triggerPrice)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                        Column {
                                            Text(text = "SL", fontSize = 7.5.sp, color = CrimsonAlert)
                                            Text(text = "₹${"%,.1f".format(pat.stopLoss)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = "T1", fontSize = 7.5.sp, color = NeonEmerald)
                                            Text(text = "₹${"%,.1f".format(pat.target1)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(vertical = 3.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Tap to Trade (${pat.riskRewardRatio})",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Current Spot Price Input + Real-Time Live Market Data Badge & Auto-Detect Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "CURRENT SPOT PRICE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                if (isAutoDetectActive) {
                    Box(
                        modifier = Modifier
                            .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ AUTO-DETECTED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                    }
                }
            }

            if (liveQuote != null) {
                val isPositive = liveQuote!!.change >= 0
                val color = if (isPositive) NeonEmerald else CrimsonAlert
                val sign = if (isPositive) "+" else ""
                val quoteStatusText = if (liveQuote!!.isMarketOpen) {
                    "● LIVE $sign${liveQuote!!.change} ($sign${liveQuote!!.changePercent}%) • $lastSyncTime"
                } else if (isDemoSimulation) {
                    "🧪 DEMO $sign${liveQuote!!.change} ($sign${liveQuote!!.changePercent}%)"
                } else {
                    "🔴 CLOSED $sign${liveQuote!!.change} ($sign${liveQuote!!.changePercent}%) • Official Close"
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = quoteStatusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }
        }
        if (liveQuote != null && !liveQuote!!.isMarketOpen && !isDemoSimulation) {
            Text(
                text = "Market Closed • Showing official exchange close (${marketStatus.lastCloseTime}) • ${marketStatus.nextSession}",
                fontSize = 9.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }

        OutlinedTextField(
            value = spotPriceInput,
            onValueChange = { viewModel.onSpotPriceChanged(it) },
            label = { Text("Spot Price (₹)") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(ElectricCyan.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .clickable { viewModel.autoDetectSpotPrice() }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AUTO-DETECT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }

                    IconButton(onClick = { viewModel.fetchLiveQuote(selectedSymbol) }) {
                        if (isFetchingQuote) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ElectricCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Refresh Live Price",
                                tint = ElectricCyan
                            )
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Open Graph & Apply Indicators Button
        Button(
            onClick = { viewModel.openChartModal() },
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricCyan.copy(alpha = 0.15f),
                contentColor = ElectricCyan
            ),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
        ) {
            Icon(
                imageVector = Icons.Default.AutoGraph,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "View $selectedSymbol Interactive Graph & Technical Call" + (if (chartData != null) " (${chartData!!.technicalCall})" else ""),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Prediction Horizon Selection
        Text(
            text = "PREDICTION TIME HORIZON",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PredictionHorizon.values()) { horizon ->
                val isSelected = horizon == selectedHorizon
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) RoyalViolet else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, if (isSelected) RoyalViolet else CardBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.onHorizonSelected(horizon) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (horizon == PredictionHorizon.CUSTOM && isSelected) {
                            "Custom (${if (customMinutes.isNotBlank()) customMinutes else "45"}m)"
                        } else horizon.label,
                        color = if (isSelected) Color.White else TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Custom Horizon Duration Input & Quick Presets
        if (selectedHorizon == PredictionHorizon.CUSTOM) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(RoyalViolet.copy(alpha = 0.5f))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "CUSTOM HORIZON DURATION (MINUTES)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalViolet,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customMinutes,
                            onValueChange = { viewModel.onCustomMinutesChanged(it) },
                            label = { Text("Minutes (1 - 480)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalViolet,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text(
                            text = "${if (customMinutes.isNotBlank()) customMinutes else "0"} min",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalViolet
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Preset Chips (20m, 45m, 90m, 120m, 180m, 240m)
                    Text(
                        text = "QUICK PRESETS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val presets = listOf(20, 45, 90, 120, 180, 240)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(presets) { preset ->
                            val isPresetSelected = customMinutes == preset.toString()
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isPresetSelected) RoyalViolet else CardBackground,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isPresetSelected) RoyalViolet else CardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.onCustomMinutesChanged(preset.toString()) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${preset}m",
                                    color = if (isPresetSelected) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Trend Direction Selection
        Text(
            text = "MARKET TREND BIAS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TrendDirection.values()) { trend ->
                val isSelected = trend == selectedTrend
                val trendColor = when (trend) {
                    TrendDirection.STRONG_BULLISH, TrendDirection.BULLISH -> NeonEmerald
                    TrendDirection.STRONG_BEARISH, TrendDirection.BEARISH -> CrimsonAlert
                    TrendDirection.AUTO -> ElectricCyan
                    TrendDirection.SIDEWAYS -> AmberWarning
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) trendColor.copy(alpha = 0.2f) else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) trendColor else CardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onTrendSelected(trend) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = trend.label,
                        color = if (isSelected) trendColor else TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Predict Button
        Button(
            onClick = { viewModel.runPrediction() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = DeepObsidian
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CALCULATE AI SPOT PREDICTION",
                color = DeepObsidian,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. UI State Rendering
        when (val state = uiState) {
            is PredictorUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ElectricCyan)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Synthesizing Greeks, OI, ML & LSTM Models...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            is PredictorUiState.Success -> {
                PredictionResultCard(prediction = state.prediction)
                Spacer(modifier = Modifier.height(16.dp))
                ModelBreakdownCard(breakdown = state.prediction.modelBreakdown)
                Spacer(modifier = Modifier.height(16.dp))
                AdvancedMetricsCard(analysis = state.prediction.advancedAnalysis)
                Spacer(modifier = Modifier.height(16.dp))
                DisclaimerBanner()
            }

            is PredictorUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CrimsonAlert.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, CrimsonAlert, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Error: ${state.message}",
                            color = CrimsonAlert,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showServerDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "CONFIGURE SERVER IP",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            is PredictorUiState.Idle -> {
                DisclaimerBanner()
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ModelBreakdownCard(
    breakdown: ModelForecastBreakdown,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "5-MODEL ENSEMBLE BREAKDOWN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalViolet,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModelRow(name = "Model 1: Open Interest & Max Pain", spot = breakdown.oiAnalysisSpot)
            ModelRow(name = "Model 2: Option Greeks & Hedging", spot = breakdown.greeksSpot)
            ModelRow(name = "Model 3: ML Ensemble (XGB+LGB+RF)", spot = breakdown.mlEnsembleSpot)
            ModelRow(name = "Model 4: LSTM Time Series Dynamic", spot = breakdown.lstmSpot)
            ModelRow(name = "Model 5: Price Action & Liquidity", spot = breakdown.priceActionSpot)
        }
    }
}

@Composable
private fun ModelRow(name: String, spot: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 11.sp, color = TextSecondary)
        Text(
            text = "₹${"%,.2f".format(spot)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
