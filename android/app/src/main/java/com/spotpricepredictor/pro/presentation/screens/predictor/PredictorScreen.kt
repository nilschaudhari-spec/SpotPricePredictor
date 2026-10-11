package com.spotpricepredictor.pro.presentation.screens.predictor

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.ModelForecastBreakdown
import com.spotpricepredictor.pro.domain.model.PredictionHorizon
import com.spotpricepredictor.pro.domain.model.TrendDirection
import com.spotpricepredictor.pro.presentation.components.AddStockDialog
import com.spotpricepredictor.pro.presentation.components.AdvancedMetricsCard
import com.spotpricepredictor.pro.presentation.components.DisclaimerBanner
import com.spotpricepredictor.pro.presentation.components.PredictionResultCard
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

    var showServerDialog by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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

        // 1. Symbol Selection Chips + Add Stock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SELECT ASSET / INDEX",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "+ ADD STOCK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                modifier = Modifier.clickable { showAddStockDialog = true }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(supportedSymbols) { symbol ->
                val isSelected = symbol == selectedSymbol
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) ElectricCyan else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, if (isSelected) ElectricCyan else CardBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.onSymbolSelected(symbol) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = symbol,
                        color = if (isSelected) DeepObsidian else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
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

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Current Spot Price Input + Real-Time Live Market Data Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CURRENT SPOT PRICE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            if (liveQuote != null) {
                val isPositive = liveQuote!!.change >= 0
                val color = if (isPositive) NeonEmerald else CrimsonAlert
                val sign = if (isPositive) "+" else ""
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "● LIVE $sign${liveQuote!!.change} ($sign${liveQuote!!.changePercent}%)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = spotPriceInput,
            onValueChange = { viewModel.onSpotPriceChanged(it) },
            label = { Text("Spot Price (₹)") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CUSTOM HORIZON DURATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        val currentMinsInt = customMinutes.toIntOrNull() ?: 45
                        val displayHours = if (currentMinsInt >= 60) " (%.1f hrs)".format(currentMinsInt / 60.0) else ""
                        Text(
                            text = "$currentMinsInt Mins$displayHours",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalViolet
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customMinutes,
                        onValueChange = { viewModel.onCustomMinutesChanged(it) },
                        label = { Text("Custom Prediction Duration (Minutes)", fontSize = 11.sp) },
                        placeholder = { Text("e.g. 10, 20, 45, 90, 120, 240", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalViolet,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "QUICK DURATION PRESETS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(10, 20, 45, 90, 120, 180, 240).forEach { mins ->
                            val isCurrent = (customMinutes.toIntOrNull() ?: 45) == mins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isCurrent) RoyalViolet else CardBackground,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(1.dp, if (isCurrent) RoyalViolet else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.onCustomMinutesChanged(mins.toString()) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "AI quant engine will simulate multi-model price pathing for exactly ${if (customMinutes.isNotBlank()) customMinutes else "45"} minutes.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Trend Direction Selection
        Text(
            text = "TREND BIAS / AUTO-DETECT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TrendDirection.values()) { trend ->
                val isSelected = trend == selectedTrend
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) NeonEmerald.copy(alpha = 0.25f) else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, if (isSelected) NeonEmerald else CardBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.onTrendSelected(trend) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = trend.label,
                        color = if (isSelected) NeonEmerald else TextPrimary,
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
                imageVector = Icons.Default.TrendingUp,
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
