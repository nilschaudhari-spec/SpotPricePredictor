package com.spotpricepredictor.pro.presentation.screens.signals

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.InstrumentType
import com.spotpricepredictor.pro.domain.model.SignalCategory
import com.spotpricepredictor.pro.presentation.components.InteractiveCandleChart
import com.spotpricepredictor.pro.presentation.components.TradeSuggestionCard
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun SignalsScreen(
    viewModel: SignalsViewModel,
    modifier: Modifier = Modifier
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val marketSentiment by viewModel.marketSentiment.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedInstrument by viewModel.selectedInstrument.collectAsState()
    val selectedAction by viewModel.selectedAction.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val activeChartSymbol by viewModel.activeChartSymbol.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val isChartLoading by viewModel.isChartLoading.collectAsState()

    // Chart Dialog
    if (activeChartSymbol != null) {
        Dialog(
            onDismissRequest = { viewModel.closeChart() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepObsidian.copy(alpha = 0.95f))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${activeChartSymbol} TECHNICAL GRAPH",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { viewModel.closeChart() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isChartLoading && chartData == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ElectricCyan)
                        }
                    } else if (chartData != null) {
                        InteractiveCandleChart(
                            chartData = chartData!!,
                            onIntervalChange = { viewModel.changeChartInterval(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Filter suggestions by search query locally
    val displayedSuggestions = remember(suggestions, searchQuery) {
        if (searchQuery.isBlank()) {
            suggestions
        } else {
            val q = searchQuery.trim().uppercase()
            suggestions.filter {
                it.symbol.uppercase().contains(q) || it.name.uppercase().contains(q)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header & Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TRADE SIGNALS",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Stocks & Indices • Equity & F&O",
                    fontSize = 12.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { viewModel.loadTradeSuggestions() },
                modifier = Modifier
                    .background(CardBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = ElectricCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Market Sentiment & Open/Closed Status Banner
        val mStatus = com.spotpricepredictor.pro.domain.model.MarketStatusHelper.getMarketStatus()
        val statusDotColor = if (mStatus.isOpen) NeonEmerald else CrimsonAlert
        val statusText = if (mStatus.isOpen) "🟢 LIVE MARKET" else "🔴 MARKET CLOSED"

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusDotColor, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$statusText • $marketSentiment",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Instrument & Category Filter Tabs
        val instrumentTabs = listOf(
            "ALL" to "All Signals",
            "INDEX" to "Indices",
            "EQUITY" to "Equity Cash",
            "FNO" to "F&O Derivatives"
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(instrumentTabs) { (key, label) ->
                val isSelected = when (key) {
                    "ALL" -> selectedInstrument == "ALL" && selectedCategory == "ALL"
                    "INDEX" -> selectedCategory == "INDEX"
                    "EQUITY" -> selectedInstrument == "EQUITY" && selectedCategory != "INDEX"
                    "FNO" -> selectedInstrument == "FNO"
                    else -> false
                }

                Box(
                    modifier = Modifier
                        .clickable {
                            when (key) {
                                "ALL" -> {
                                    viewModel.setCategory("ALL")
                                    viewModel.setInstrument("ALL")
                                }
                                "INDEX" -> {
                                    viewModel.setCategory("INDEX")
                                    viewModel.setInstrument("ALL")
                                }
                                "EQUITY" -> {
                                    viewModel.setCategory("EQUITY")
                                    viewModel.setInstrument("EQUITY")
                                }
                                "FNO" -> {
                                    viewModel.setCategory("ALL")
                                    viewModel.setInstrument("FNO")
                                }
                            }
                        }
                        .background(
                            if (isSelected) ElectricCyan.copy(alpha = 0.2f) else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else CardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) ElectricCyan else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Filter Chips (All, Buy Signals, Sell Signals)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val actions = listOf(
                "ALL" to "All Actions",
                "BUY" to "Buy Signals",
                "SELL" to "Sell Signals"
            )
            actions.forEach { (act, label) ->
                val isSelected = selectedAction == act
                val color = when (act) {
                    "BUY" -> NeonEmerald
                    "SELL" -> CrimsonAlert
                    else -> TextPrimary
                }
                Box(
                    modifier = Modifier
                        .clickable { viewModel.setAction(act) }
                        .background(
                            if (isSelected) color.copy(alpha = 0.15f) else SurfaceDark,
                            RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) color else CardBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) color else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Filter by ticker or name...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Main List of Suggestions
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = ElectricCyan)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Scanning Quantitative Technical Setups...", fontSize = 12.sp, color = TextSecondary)
                }
            }
        } else if (displayedSuggestions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No trade signals match current filters.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(displayedSuggestions, key = { it.id }) { suggestion ->
                    TradeSuggestionCard(
                        suggestion = suggestion,
                        onViewChart = { symbol ->
                            viewModel.openChart(symbol)
                        }
                    )
                }
            }
        }
    }
}
