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
                                    text = "🕯️ CANDLESTICK & PRICE ACTION",
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
                                text = "Marubozu • Doji • Morning/Evening Star • Pin Bars • Trap Setups",
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

                    // Main Type Filter Tabs (ALL, BUY, SELL, PRICE ACTION)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "All (14)",
                            "BUY" to "🟢 BUY (7)",
                            "SELL" to "🔴 SELL (7)",
                            "PRICE_ACTION" to "⭐ Price Action"
                        ).forEach { (key, label) ->
                            val isSelected = selectedTypeFilter.equals(key, ignoreCase = true)
                            val activeColor = when (key) {
                                "BUY" -> NeonEmerald
                                "SELL" -> CrimsonAlert
                                "PRICE_ACTION" -> RoyalViolet
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
                                    fontSize = 9.sp,
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
                            "ALL", "Hammer", "Inverse Hammer", "Bullish Marubozu", "Dragonfly Doji", "Morning Star", "Low break close upside", "Bullish Harami"
                        )
                        "SELL" -> listOf(
                            "ALL", "Shooting Star", "Hanging Man", "Bearish Marubozu", "Gravestone Doji", "Evening Star", "High break, close Downside", "Bearish Harami"
                        )
                        "PRICE_ACTION" -> listOf(
                            "ALL", "Morning Star", "Evening Star", "Low break close upside", "High break, close Downside"
                        )
                        else -> listOf(
                            "ALL",
                            "Bullish Marubozu", "Bearish Marubozu",
                            "Dragonfly Doji", "Gravestone Doji",
                            "Morning Star", "Evening Star",
                            "Hammer", "Inverse Hammer", "Shooting Star", "Hanging Man",
                            "Low break close upside", "High break, close Downside",
                            "Bullish Harami", "Bearish Harami"
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
                        "PRICE_ACTION" -> data?.allPatterns?.filter { it.category.contains("PRICE ACTION", ignoreCase = true) } ?: emptyList()
                        else -> data?.allPatterns ?: emptyList()
                    }

                    val filteredList = if (selectedNameFilter.equals("ALL", ignoreCase = true)) {
                        allPatterns
                    } else {
                        allPatterns.filter {
                            it.patternName.equals(selectedNameFilter, ignoreCase = true) ||
                            (selectedNameFilter.equals("Marubozu", ignoreCase = true) && it.patternName.contains("Marubozu", ignoreCase = true)) ||
                            (selectedNameFilter.contains("Doji", ignoreCase = true) && it.patternName.contains("Doji", ignoreCase = true)) ||
                            (selectedNameFilter.contains("Dogi", ignoreCase = true) && it.patternName.contains("Doji", ignoreCase = true))
                        }
                    }

                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Scanning candlestick patterns & price action..." else "No matching pattern formations found",
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
    val isPriceAction = pattern.category.contains("PRICE ACTION", ignoreCase = true)
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBuy) "🟢 BUY" else "🔴 SELL",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = biasColor
                        )
                    }

                    if (isPriceAction) {
                        Box(
                            modifier = Modifier
                                .background(RoyalViolet.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(0.8.dp, RoyalViolet.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⭐ PRICE ACTION",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalViolet
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(SurfaceDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
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
    val yellow = AmberWarning

    Canvas(
        modifier = Modifier
            .size(width = 42.dp, height = 48.dp)
            .background(DeepObsidian, RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        val w = size.width
        val h = size.height
        val midX = w / 2f

        when {
            // 1. Morning Star (3 candles: Tall Red, Star, Tall Green > 50%)
            patternName.contains("Morning Star", ignoreCase = true) -> {
                // 1st Red
                drawLine(red, Offset(w * 0.22f, 4f), Offset(w * 0.22f, h - 14f), strokeWidth = 2f)
                drawRect(red, Offset(w * 0.22f - 4f, 8f), Size(8f, h * 0.48f))
                // 2nd Star / Doji lower
                drawLine(yellow, Offset(w * 0.50f, h - 14f), Offset(w * 0.50f, h - 2f), strokeWidth = 1.8f)
                drawRect(yellow, Offset(w * 0.50f - 3f, h - 10f), Size(6f, 5f))
                // 3rd Green piercing >50%
                drawLine(green, Offset(w * 0.78f, 6f), Offset(w * 0.78f, h - 6f), strokeWidth = 2f)
                drawRect(green, Offset(w * 0.78f - 4f, 10f), Size(8f, h * 0.52f))
            }

            // 2. Evening Star (3 candles: Tall Green, Star, Tall Red > 50%)
            patternName.contains("Evening Star", ignoreCase = true) -> {
                // 1st Green
                drawLine(green, Offset(w * 0.22f, 14f), Offset(w * 0.22f, h - 4f), strokeWidth = 2f)
                drawRect(green, Offset(w * 0.22f - 4f, h * 0.36f), Size(8f, h * 0.48f))
                // 2nd Star / Doji upper
                drawLine(yellow, Offset(w * 0.50f, 2f), Offset(w * 0.50f, 14f), strokeWidth = 1.8f)
                drawRect(yellow, Offset(w * 0.50f - 3f, 5f), Size(6f, 5f))
                // 3rd Red plunging >50%
                drawLine(red, Offset(w * 0.78f, 8f), Offset(w * 0.78f, h - 4f), strokeWidth = 2f)
                drawRect(red, Offset(w * 0.78f - 4f, 12f), Size(8f, h * 0.52f))
            }

            // 3. Bullish Marubozu (Full green candle with no wicks)
            patternName.contains("Bullish Marubozu", ignoreCase = true) || (patternName.contains("Marubozu", ignoreCase = true) && isBuy) -> {
                drawRect(green, Offset(midX - 9f, 4f), Size(18f, h - 8f))
            }

            // 4. Bearish Marubozu (Full red candle with no wicks)
            patternName.contains("Bearish Marubozu", ignoreCase = true) || (patternName.contains("Marubozu", ignoreCase = true) && !isBuy) -> {
                drawRect(red, Offset(midX - 9f, 4f), Size(18f, h - 8f))
            }

            // 5. Dragonfly Doji (Open/High/Close at top, long lower wick)
            patternName.contains("Dragonfly", ignoreCase = true) || (patternName.contains("Doji", ignoreCase = true) && isBuy) || (patternName.contains("Dogi", ignoreCase = true) && isBuy) -> {
                // Long lower wick
                drawLine(green, Offset(midX, 4f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                // Crossbar at top
                drawLine(green, Offset(midX - 10f, 5f), Offset(midX + 10f, 5f), strokeWidth = 3.5f)
            }

            // 6. Gravestone Doji (Open/Low/Close at bottom, long upper wick)
            patternName.contains("Gravestone", ignoreCase = true) || (patternName.contains("Doji", ignoreCase = true) && !isBuy) || (patternName.contains("Dogi", ignoreCase = true) && !isBuy) -> {
                // Long upper wick
                drawLine(red, Offset(midX, 2f), Offset(midX, h - 4f), strokeWidth = 2.5f)
                // Crossbar at bottom
                drawLine(red, Offset(midX - 10f, h - 5f), Offset(midX + 10f, h - 5f), strokeWidth = 3.5f)
            }

            // 7. Hammer: Small upper body, long lower wick
            patternName.contains("Hammer", ignoreCase = true) && !patternName.contains("Inverse", ignoreCase = true) -> {
                drawLine(green, Offset(midX, 4f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(green, Offset(midX - 7f, 4f), Size(14f, 12f))
            }

            // 8. Inverse Hammer: Small lower body, long upper wick
            patternName.contains("Inverse", ignoreCase = true) -> {
                drawLine(green, Offset(midX, 2f), Offset(midX, h - 4f), strokeWidth = 2.5f)
                drawRect(green, Offset(midX - 7f, h - 16f), Size(14f, 12f))
            }

            // 9. Low break close upside: Break below line and reclaim
            patternName.contains("Low break", ignoreCase = true) -> {
                drawLine(Color.Gray.copy(alpha = 0.5f), Offset(2f, h * 0.6f), Offset(w - 2f, h * 0.6f), strokeWidth = 1.5f)
                drawLine(green, Offset(midX, 2f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(green, Offset(midX - 7f, 6f), Size(14f, 16f))
            }

            // 10. Bullish Harami: Tall red + small green inside
            patternName.contains("Bullish Harami", ignoreCase = true) -> {
                drawLine(red, Offset(w * 0.35f, 2f), Offset(w * 0.35f, h - 2f), strokeWidth = 2f)
                drawRect(red, Offset(w * 0.35f - 6f, 6f), Size(12f, h - 14f))
                drawLine(green, Offset(w * 0.72f, h * 0.35f), Offset(w * 0.72f, h * 0.68f), strokeWidth = 2f)
                drawRect(green, Offset(w * 0.72f - 4f, h * 0.42f), Size(8f, 10f))
            }

            // 11. Shooting Star: Long upper wick, small lower body
            patternName.contains("Shooting Star", ignoreCase = true) -> {
                drawLine(red, Offset(midX, 2f), Offset(midX, h - 4f), strokeWidth = 2.5f)
                drawRect(red, Offset(midX - 7f, h - 16f), Size(14f, 12f))
            }

            // 12. Hanging Man: Small upper body, long lower wick at top
            patternName.contains("Hanging Man", ignoreCase = true) -> {
                drawLine(red, Offset(midX, 4f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(red, Offset(midX - 7f, 4f), Size(14f, 12f))
            }

            // 13. High break close downside: Break above line and dump
            patternName.contains("High break", ignoreCase = true) -> {
                drawLine(Color.Gray.copy(alpha = 0.5f), Offset(2f, h * 0.4f), Offset(w - 2f, h * 0.4f), strokeWidth = 1.5f)
                drawLine(red, Offset(midX, 2f), Offset(midX, h - 2f), strokeWidth = 2.5f)
                drawRect(red, Offset(midX - 7f, h * 0.45f), Size(14f, 16f))
            }

            // 14. Bearish Harami: Tall green + small red inside
            patternName.contains("Bearish Harami", ignoreCase = true) -> {
                drawLine(green, Offset(w * 0.35f, 2f), Offset(w * 0.35f, h - 2f), strokeWidth = 2f)
                drawRect(green, Offset(w * 0.35f - 6f, 6f), Size(12f, h - 14f))
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
