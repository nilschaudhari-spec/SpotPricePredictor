package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.PatternDetectionItem
import com.spotpricepredictor.pro.domain.model.PatternScannerData
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun CandlestickPatternsDialog(
    data: PatternScannerData?,
    isLoading: Boolean,
    selectedTypeFilter: String,
    selectedNameFilter: String,
    onTypeFilterSelected: (String) -> Unit,
    onNameFilterSelected: (String) -> Unit,
    onStockSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepObsidian.copy(alpha = 0.96f))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.2.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "🕯️ CANDLESTICK PATTERNS",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = ElectricCyan
                                    )
                                }
                            }
                            Text(
                                text = "High-Probability Buy & Sell Formations • Real-Time Scan",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(CardBackground, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Type Filter Tabs (ALL, BUY, SELL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "ALL" to "All Patterns",
                            "BUY" to "🟢 BUY PATTERNS (4)",
                            "SELL" to "🔴 SELL PATTERNS (4)"
                        ).forEach { (key, label) ->
                            val isSelected = selectedTypeFilter.equals(key, ignoreCase = true)
                            val activeColor = when (key) {
                                "BUY" -> NeonEmerald
                                "SELL" -> CrimsonAlert
                                else -> ElectricCyan
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onTypeFilterSelected(key)
                                        onNameFilterSelected("ALL")
                                    }
                                    .background(
                                        if (isSelected) activeColor.copy(alpha = 0.2f) else CardBackground,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColor else CardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) activeColor else TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Specific Pattern Chips Carousel
                    val patternChips = when (selectedTypeFilter.uppercase()) {
                        "BUY" -> listOf(
                            "ALL", "Hammer", "Inverse Hammer", "Low break close upside", "Bullish Harami"
                        )
                        "SELL" -> listOf(
                            "ALL", "Hanging Man", "Shooting Star", "High break, close Downside", "Bearish Harami"
                        )
                        else -> listOf(
                            "ALL",
                            "Hammer", "Inverse Hammer", "Low break close upside", "Bullish Harami",
                            "Hanging Man", "Shooting Star", "High break, close Downside", "Bearish Harami"
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(patternChips) { name ->
                            val isSelected = selectedNameFilter.equals(name, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clickable { onNameFilterSelected(name) }
                                    .background(
                                        if (isSelected) ElectricCyan else CardBackground,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        0.6.dp,
                                        if (isSelected) ElectricCyan else CardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (name == "ALL") "All Formations" else name,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) DeepObsidian else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Patterns List
                    val allPatterns = when (selectedTypeFilter.uppercase()) {
                        "BUY" -> data?.buyPatterns ?: emptyList()
                        "SELL" -> data?.sellPatterns ?: emptyList()
                        else -> data?.allPatterns ?: emptyList()
                    }

                    val filteredList = if (selectedNameFilter.equals("ALL", ignoreCase = true)) {
                        allPatterns
                    } else {
                        allPatterns.filter { it.patternName.equals(selectedNameFilter, ignoreCase = true) }
                    }

                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Scanning candlestick patterns..." else "No matching pattern formations found",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredList) { pattern ->
                                PatternCard(
                                    pattern = pattern,
                                    onSelectStock = {
                                        onStockSelected(pattern.symbol)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatternCard(
    pattern: PatternDetectionItem,
    onSelectStock: () -> Unit
) {
    val isBuy = pattern.patternType.uppercase() == "BUY"
    val biasColor = if (isBuy) NeonEmerald else CrimsonAlert
    val biasBg = biasColor.copy(alpha = 0.15f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, biasColor.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Symbol, Pattern Name & Bias Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = pattern.symbol,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )

                    Box(
                        modifier = Modifier
                            .background(biasBg, RoundedCornerShape(4.dp))
                            .border(0.8.dp, biasColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBuy) "🟢 BUY SETUP" else "🔴 SELL SETUP",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = biasColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(SurfaceDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = pattern.timeframe,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }

                Text(
                    text = "₹${"%,.2f".format(pattern.currentPrice)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pattern.patternName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Text(
                    text = "Reliability: ${pattern.reliabilityPct}%",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Candlestick Diagram & Anatomy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Candle Icon Visualizer
                CandleVisualizer(patternName = pattern.patternName, isBuy = isBuy)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ANATOMY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = pattern.candleAnatomy,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        lineHeight = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trade Parameters Grid (Trigger, SL, T1, T2, RR)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "TRIGGER", fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = "₹${"%,.1f".format(pattern.triggerPrice)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text(text = "STOP LOSS", fontSize = 8.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                    Text(text = "₹${"%,.1f".format(pattern.stopLoss)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                }
                Column {
                    Text(text = "TARGET 1", fontSize = 8.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                    Text(text = "₹${"%,.1f".format(pattern.target1)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                }
                Column {
                    Text(text = "TARGET 2", fontSize = 8.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                    Text(text = "₹${"%,.1f".format(pattern.target2)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "R : R", fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = pattern.riskRewardRatio, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = RoyalViolet)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation & Execution Advice
            Text(
                text = pattern.description,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "💡 Execution: ${pattern.tradingRule}",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElectricCyan,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button: Select and Analyze this Stock
            Button(
                onClick = onSelectStock,
                modifier = Modifier.fillMaxWidth().height(34.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBuy) NeonEmerald.copy(alpha = 0.22f) else CrimsonAlert.copy(alpha = 0.22f)
                ),
                border = BorderStroke(1.dp, biasColor),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Text(
                    text = "⚡ PREDICT & TRADE ${pattern.symbol} SETUP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = biasColor,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun CandleVisualizer(patternName: String, isBuy: Boolean) {
    val green = NeonEmerald
    val red = CrimsonAlert

    Canvas(
        modifier = Modifier
            .size(width = 38.dp, height = 48.dp)
            .background(DeepObsidian, RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        val w = size.width
        val h = size.height
        val midX = w / 2f

        when {
            // Hammer: Small upper body, long lower wick
            patternName.contains("Hammer", ignoreCase = true) && !patternName.contains("Inverse", ignoreCase = true) -> {
                drawLine(green, Offset(midX, 4f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(green, Offset(midX - 7f, 4f), Size(14f, 12f))
            }

            // Inverse Hammer: Small lower body, long upper wick
            patternName.contains("Inverse", ignoreCase = true) -> {
                drawLine(green, Offset(midX, 2f), Offset(midX, h - 4f), strokeWidth = 2.5f)
                drawRect(green, Offset(midX - 7f, h - 16f), Size(14f, 12f))
            }

            // Low break close upside: Break below line and reclaim
            patternName.contains("Low break", ignoreCase = true) -> {
                // Support line
                drawLine(Color.Gray.copy(alpha = 0.5f), Offset(2f, h * 0.6f), Offset(w - 2f, h * 0.6f), strokeWidth = 1.5f)
                // Reclaiming candle
                drawLine(green, Offset(midX, 2f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(green, Offset(midX - 7f, 6f), Size(14f, 16f))
            }

            // Bullish Harami: Tall red + small green inside
            patternName.contains("Bullish Harami", ignoreCase = true) -> {
                // Tall Red mother
                drawLine(red, Offset(w * 0.35f, 2f), Offset(w * 0.35f, h - 2f), strokeWidth = 2f)
                drawRect(red, Offset(w * 0.35f - 6f, 6f), Size(12f, h - 14f))
                // Small Green baby
                drawLine(green, Offset(w * 0.72f, h * 0.35f), Offset(w * 0.72f, h * 0.68f), strokeWidth = 2f)
                drawRect(green, Offset(w * 0.72f - 4f, h * 0.42f), Size(8f, 10f))
            }

            // Shooting Star: Long upper wick, small lower body
            patternName.contains("Shooting Star", ignoreCase = true) -> {
                drawLine(red, Offset(midX, 2f), Offset(midX, h - 4f), strokeWidth = 2.5f)
                drawRect(red, Offset(midX - 7f, h - 16f), Size(14f, 12f))
            }

            // Hanging Man: Small upper body, long lower wick at top
            patternName.contains("Hanging Man", ignoreCase = true) -> {
                drawLine(red, Offset(midX, 4f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(red, Offset(midX - 7f, 4f), Size(14f, 12f))
            }

            // High break close downside: Break above line and dump
            patternName.contains("High break", ignoreCase = true) -> {
                // Resistance line
                drawLine(Color.Gray.copy(alpha = 0.5f), Offset(2f, h * 0.4f), Offset(w - 2f, h * 0.4f), strokeWidth = 1.5f)
                // Dump candle
                drawLine(red, Offset(midX, 2f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(red, Offset(midX - 7f, h * 0.45f), Size(14f, 16f))
            }

            // Bearish Harami: Tall green + small red inside
            patternName.contains("Bearish Harami", ignoreCase = true) -> {
                // Tall Green mother
                drawLine(green, Offset(w * 0.35f, 2f), Offset(w * 0.35f, h - 2f), strokeWidth = 2f)
                drawRect(green, Offset(w * 0.35f - 6f, 6f), Size(12f, h - 14f))
                // Small Red baby
                drawLine(red, Offset(w * 0.72f, h * 0.35f), Offset(w * 0.72f, h * 0.68f), strokeWidth = 2f)
                drawRect(red, Offset(w * 0.72f - 4f, h * 0.42f), Size(8f, 10f))
            }

            else -> {
                val c = if (isBuy) green else red
                drawLine(c, Offset(midX, 4f), Offset(midX, h - 4f), strokeWidth = 2.5f)
                drawRect(c, Offset(midX - 7f, 10f), Size(14f, h - 22f))
            }
        }
    }
}

