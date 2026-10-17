package com.spotpricepredictor.pro.presentation.screens.history

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsState()
    val metrics by viewModel.performanceMetrics.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var outcomeDialogItem by remember { mutableStateOf<PredictionHistoryEntity?>(null) }

    if (outcomeDialogItem != null) {
        RecordOutcomeDialog(
            item = outcomeDialogItem!!,
            onDismiss = { outcomeDialogItem = null },
            onConfirm = { actualSpot, status ->
                viewModel.recordOutcome(outcomeDialogItem!!.id, actualSpot, status)
                outcomeDialogItem = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(ElectricCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = "Accuracy History",
                        tint = ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Prediction History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .background(NeonEmerald.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACCURACY TRACKER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        }
                    }
                    Text(
                        text = "Real-Time AI Verification & Model Performance Matrix",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Performance KPI Metrics Grid (2x2)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(14.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Win Rate / Accuracy
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(NeonEmerald, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "AI WIN RATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${String.format("%.1f", metrics.winRatePct)}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonEmerald
                    )
                    Text(
                        text = "${metrics.targetHitCount} of ${metrics.totalSignals} Signals Hit",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Metric 2: Total Signals
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(ElectricCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "TOTAL FORECASTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${metrics.totalSignals}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                    Text(
                        text = "Logged Across Horizons",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 3: Active / Pending
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(AmberWarning, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "ACTIVE SIGNALS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${metrics.pendingCount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWarning
                    )
                    Text(
                        text = "Awaiting Verification",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Metric 4: Avg Deviation
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(RoyalViolet, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "AVG DEVIATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "±${String.format("%.1f", metrics.avgErrorPoints)} pts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalViolet
                    )
                    Text(
                        text = "Target Price Precision",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Filter logs by scrip (e.g. NIFTY, RELIANCE)...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (searchQuery.isNotEmpty()) ElectricCyan else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HistoryFilter.values()) { filter ->
                val isSelected = filter == selectedFilter
                val badgeCount = when (filter) {
                    HistoryFilter.ALL -> metrics.totalSignals
                    HistoryFilter.TARGET_HIT -> metrics.targetHitCount
                    HistoryFilter.PENDING -> metrics.pendingCount
                    HistoryFilter.STOP_LOSS_HIT -> metrics.stopLossCount
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) ElectricCyan else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, if (isSelected) ElectricCyan else CardBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.onFilterSelected(filter) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = filter.label,
                            color = if (isSelected) DeepObsidian else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "($badgeCount)",
                            color = if (isSelected) DeepObsidian.copy(alpha = 0.8f) else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History Items List
        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(CardBorder, ElectricCyanDim.copy(alpha = 0.3f)))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(ElectricCyan.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, ElectricCyan.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No Matching Signals" else "No Signals Logged Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try clearing your search query to view all signals." else "Execute an AI prediction from the Predictor tab to start logging signals with automated outcome verification.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    ModernHistoryCard(
                        item = item,
                        onVerifyClick = { outcomeDialogItem = item }
                    )
                }
            }
        }
    }
}

@Composable
fun ModernHistoryCard(
    item: PredictionHistoryEntity,
    onVerifyClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))

    val (statusBg, statusBorder, statusText, statusColor) = when (item.status) {
        "TARGET_HIT" -> Tuple4(
            NeonEmerald.copy(alpha = 0.15f),
            NeonEmerald.copy(alpha = 0.5f),
            "TARGET HIT ✓",
            NeonEmerald
        )
        "STOP_LOSS_HIT" -> Tuple4(
            CrimsonAlert.copy(alpha = 0.15f),
            CrimsonAlert.copy(alpha = 0.5f),
            "STOP LOSS HIT ✗",
            CrimsonAlert
        )
        "EXPIRED" -> Tuple4(
            TextMuted.copy(alpha = 0.15f),
            CardBorder,
            "TIME EXPIRED",
            TextMuted
        )
        else -> Tuple4(
            ElectricCyan.copy(alpha = 0.15f),
            ElectricCyan.copy(alpha = 0.4f),
            "EVALUATING • ACTIVE",
            ElectricCyan
        )
    }

    val deltaPrice = item.predictedSpot - item.currentSpot
    val isBullish = deltaPrice >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(CardBorder, if (item.status == "TARGET_HIT") NeonEmerald.copy(alpha = 0.3f) else CardBorder)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Symbol, Horizon, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(SurfaceDark, RoundedCornerShape(6.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.symbol,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(SurfaceDark, RoundedCornerShape(6.dp))
                            .border(0.5.dp, CardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.horizon,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .border(1.dp, statusBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Details Matrix
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "SPOT AT CALL", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${"%,.1f".format(item.currentSpot)}",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Text(text = "AI PREDICTED", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${"%,.1f".format(item.predictedSpot)}",
                            fontSize = 13.sp,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isBullish) "▲" else "▼",
                            fontSize = 9.sp,
                            color = if (isBullish) NeonEmerald else CrimsonAlert
                        )
                    }
                }

                Column {
                    Text(text = "TARGET", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${"%,.1f".format(item.targetPrice)}",
                        fontSize = 13.sp,
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(text = "STOP LOSS", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${"%,.1f".format(item.stopLoss)}",
                        fontSize = 13.sp,
                        color = CrimsonAlert,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Strategy & Confidence Bar
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
                        text = "Strategy:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = item.suggestedStrategy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalViolet
                    )
                }

                Text(
                    text = "${String.format("%.1f", item.confidencePct)}% Confidence",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
            }

            // Outcome Details if verified
            if (item.actualSpot != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .border(0.5.dp, CardBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Actual Exit Spot: ₹${"%,.1f".format(item.actualSpot)}",
                        fontSize = 10.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (item.errorPoints != null) {
                        Text(
                            text = "Variance: ±${String.format("%.1f", item.errorPoints)} pts",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Footer: Timestamp & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = TextMuted
                )

                if (item.status == "PENDING") {
                    TextButton(
                        onClick = onVerifyClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VERIFY OUTCOME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecordOutcomeDialog(
    item: PredictionHistoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (actualSpot: Double, status: String) -> Unit
) {
    var actualPriceInput by remember { mutableStateOf(item.targetPrice.toString()) }
    var selectedOutcome by remember { mutableStateOf("TARGET_HIT") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(ElectricCyanDim, RoyalViolet.copy(alpha = 0.5f)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Verify Forecast Outcome",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${item.symbol} • Predicted ₹${"%,.1f".format(item.predictedSpot)}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SELECT VERIFICATION OUTCOME",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val outcomes = listOf(
                    "TARGET_HIT" to "Target Hit (✓)",
                    "STOP_LOSS_HIT" to "Stop Loss Hit (✗)",
                    "EXPIRED" to "Time Expired"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    outcomes.forEach { (code, label) ->
                        val isSelected = selectedOutcome == code
                        val (selColor, selBg) = when (code) {
                            "TARGET_HIT" -> NeonEmerald to NeonEmerald.copy(alpha = 0.15f)
                            "STOP_LOSS_HIT" -> CrimsonAlert to CrimsonAlert.copy(alpha = 0.15f)
                            else -> AmberWarning to AmberWarning.copy(alpha = 0.15f)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSelected) selBg else SurfaceDark, RoundedCornerShape(8.dp))
                                .border(1.dp, if (isSelected) selColor else CardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedOutcome = code
                                    if (code == "TARGET_HIT") actualPriceInput = item.targetPrice.toString()
                                    if (code == "STOP_LOSS_HIT") actualPriceInput = item.stopLoss.toString()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) selColor else TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = actualPriceInput,
                    onValueChange = { actualPriceInput = it },
                    label = { Text("Actual Spot Exit Price (₹)", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = actualPriceInput.toDoubleOrNull() ?: item.predictedSpot
                            onConfirm(price, selectedOutcome)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SAVE & UPDATE", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
