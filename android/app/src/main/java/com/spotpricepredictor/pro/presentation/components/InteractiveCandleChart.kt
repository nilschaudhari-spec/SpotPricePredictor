package com.spotpricepredictor.pro.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.CandleBar
import com.spotpricepredictor.pro.domain.model.ChartData
import com.spotpricepredictor.pro.domain.model.IndicatorConfig
import com.spotpricepredictor.pro.domain.model.StrategySignal
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun InteractiveCandleChart(
    chartData: ChartData,
    onIntervalChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var indicatorConfig by remember { mutableStateOf(IndicatorConfig()) }
    var selectedCandleIndex by remember { mutableStateOf<Int?>(null) }
    var showIndicatorToggles by remember { mutableStateOf(false) }
    var showStrategyBreakdown by remember { mutableStateOf(false) }

    // Pan and Zoom controls
    var visibleCandlesCount by remember { mutableStateOf(40) }
    var panIndexOffset by remember { mutableStateOf(0) }

    val rawCandles = chartData.candles
    if (rawCandles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No Chart Data Available", color = TextSecondary)
        }
        return
    }

    // Windowed candles based on pan & zoom
    val safeCount = visibleCandlesCount.coerceIn(15, rawCandles.size)
    val maxPan = (rawCandles.size - safeCount).coerceAtLeast(0)
    val safePan = panIndexOffset.coerceIn(0, maxPan)
    val startIdx = (rawCandles.size - safeCount - safePan).coerceAtLeast(0)
    val endIdx = (startIdx + safeCount).coerceAtMost(rawCandles.size)
    val candles = rawCandles.subList(startIdx, endIdx)

    // Reset selected candle index when timeframe or symbol changes
    LaunchedEffect(chartData.interval, chartData.symbol) {
        selectedCandleIndex = null
        panIndexOffset = 0
    }

    val selectedCandle = selectedCandleIndex?.let { candles.getOrNull(it) } ?: candles.lastOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // 1. Top Header: Symbol, Live Spot, 1ms Badge, Technical Call & Indicator Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chartData.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${"%,.2f".format(chartData.spotPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (chartData.change >= 0) NeonEmerald else CrimsonAlert
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (chartData.change >= 0) "+" else ""}${"%.2f".format(chartData.changePct)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (chartData.change >= 0) NeonEmerald else CrimsonAlert
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .background(NeonEmerald.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.8.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "● 1ms LIVE TICKS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }

                    val call = chartData.technicalCall.uppercase()
                    val (callColor, callBg) = when {
                        call.contains("BUY") -> NeonEmerald to NeonEmerald.copy(alpha = 0.15f)
                        call.contains("SELL") -> CrimsonAlert to CrimsonAlert.copy(alpha = 0.15f)
                        else -> AmberWarning to AmberWarning.copy(alpha = 0.15f)
                    }
                    Box(
                        modifier = Modifier
                            .clickable { showStrategyBreakdown = !showStrategyBreakdown }
                            .background(callBg, RoundedCornerShape(4.dp))
                            .border(0.8.dp, callColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "CALL: $call",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = callColor
                        )
                    }
                }
            }

            // Indicator Controls & Zoom Tools
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { visibleCandlesCount = (visibleCandlesCount - 5).coerceAtLeast(15) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { visibleCandlesCount = (visibleCandlesCount + 10).coerceAtMost(rawCandles.size) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                OutlinedButton(
                    onClick = { showIndicatorToggles = !showIndicatorToggles },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (showIndicatorToggles) ElectricCyan else TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (showIndicatorToggles) ElectricCyan else CardBorder
                    )
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Indicators", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Indicators", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Technical Analysis Call & Strategy Target Banner
        val call = chartData.technicalCall.uppercase()
        val (callColor, callBg) = when {
            call.contains("BUY") -> NeonEmerald to NeonEmerald.copy(alpha = 0.12f)
            call.contains("SELL") -> CrimsonAlert to CrimsonAlert.copy(alpha = 0.12f)
            else -> AmberWarning to AmberWarning.copy(alpha = 0.12f)
        }
        val callIcon = if (call.contains("BUY")) Icons.AutoMirrored.Filled.TrendingUp else if (call.contains("SELL")) Icons.AutoMirrored.Filled.TrendingDown else Icons.Default.PauseCircle

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = callBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, callColor.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showStrategyBreakdown = !showStrategyBreakdown }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(callIcon, contentDescription = null, tint = callColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "TECHNICAL ANALYSIS: $call",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = callColor
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            chartData.targetPrice?.let {
                                Text("Tgt: ₹${"%,.2f".format(it)}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            chartData.stopLoss?.let {
                                Text("SL: ₹${"%,.2f".format(it)}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = CrimsonAlert)
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (showStrategyBreakdown) "Hide Strategies ▲" else "View Strategies ▼",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
            }
        }

        // Expandable Strategy Signals Breakdown Panel
        AnimatedVisibility(visible = showStrategyBreakdown) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .background(SurfaceDark, RoundedCornerShape(8.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "TECHNICAL STRATEGY SIGNALS (6 MODELS)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                chartData.strategySignals.forEach { sig ->
                    val sColor = when (sig.signal.uppercase()) {
                        "BUY" -> NeonEmerald
                        "SELL" -> CrimsonAlert
                        else -> AmberWarning
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sig.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(sig.rationale, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                        }
                        Box(
                            modifier = Modifier
                                .background(sColor.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                                .border(0.5.dp, sColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(sig.signal, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = sColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Multi-Timeframe Selector
        val intervals = listOf(
            ChartIntervalItem("15s", "15s", "SEC", "15 SECONDS"),
            ChartIntervalItem("30s", "30s", "SEC", "30 SECONDS"),
            ChartIntervalItem("1m", "1m", "MIN", "1 MINUTE"),
            ChartIntervalItem("5m", "5m", "MIN", "5 MINUTES"),
            ChartIntervalItem("15m", "15m", "MIN", "15 MINUTES"),
            ChartIntervalItem("30m", "30m", "MIN", "30 MINUTES"),
            ChartIntervalItem("1h", "1h", "HOUR", "1 HOUR"),
            ChartIntervalItem("4h", "4h", "HOUR", "4 HOURS"),
            ChartIntervalItem("1D", "1D", "DAY", "1 DAY"),
            ChartIntervalItem("1W", "1W", "WEEK", "1 WEEK"),
            ChartIntervalItem("1M", "1M", "MONTH", "1 MONTH"),
            ChartIntervalItem("1Y", "1Y", "YEAR", "1 YEAR")
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(intervals) { item ->
                val isSelected = chartData.interval.equals(item.id, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clickable { onIntervalChange(item.id) }
                        .background(
                            if (isSelected) ElectricCyan.copy(alpha = 0.22f) else SurfaceDark,
                            RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else CardBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.label,
                        color = if (isSelected) ElectricCyan else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Indicator Toggle Chips Panel (Collapsible)
        AnimatedVisibility(visible = showIndicatorToggles) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(SurfaceDark, RoundedCornerShape(8.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "APPLY INDICATORS ON CHART",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        IndicatorChip(
                            label = "EMA 20",
                            color = ElectricCyan,
                            isSelected = indicatorConfig.showEma20,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showEma20 = !indicatorConfig.showEma20) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "EMA 50",
                            color = AmberWarning,
                            isSelected = indicatorConfig.showEma50,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showEma50 = !indicatorConfig.showEma50) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "SMA 200",
                            color = RoyalViolet,
                            isSelected = indicatorConfig.showSma200,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showSma200 = !indicatorConfig.showSma200) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "VWAP",
                            color = Color(0xFFFFEA00),
                            isSelected = indicatorConfig.showVwap,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showVwap = !indicatorConfig.showVwap) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "Bollinger",
                            color = Color(0xFF64B5F6),
                            isSelected = indicatorConfig.showBollinger,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showBollinger = !indicatorConfig.showBollinger) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "Supertrend",
                            color = NeonEmerald,
                            isSelected = indicatorConfig.showSupertrend,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showSupertrend = !indicatorConfig.showSupertrend) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "RSI Pane",
                            color = Color(0xFFFF80AB),
                            isSelected = indicatorConfig.showRsi,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showRsi = !indicatorConfig.showRsi) }
                        )
                    }
                    item {
                        IndicatorChip(
                            label = "MACD Pane",
                            color = Color(0xFF80DEEA),
                            isSelected = indicatorConfig.showMacd,
                            onToggle = { indicatorConfig = indicatorConfig.copy(showMacd = !indicatorConfig.showMacd) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Selected Candle Inspector Data Bar
        selectedCandle?.let { c ->
            val candleDiff = c.close - c.open
            val isGreen = candleDiff >= 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("T: ${c.timeStr}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = ElectricCyan)
                Text("O: ${c.open}", fontSize = 10.sp, color = TextPrimary)
                Text("H: ${c.high}", fontSize = 10.sp, color = NeonEmerald)
                Text("L: ${c.low}", fontSize = 10.sp, color = CrimsonAlert)
                Text("C: ${c.close}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isGreen) NeonEmerald else CrimsonAlert)
                Text("Vol: ${c.volume / 1000}K", fontSize = 10.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Main Interactive Candlestick Canvas with Pan, Zoom & Crosshair
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .pointerInput(candles.size) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (zoom != 1f) {
                            val nextCount = (visibleCandlesCount / zoom).toInt().coerceIn(15, rawCandles.size)
                            visibleCandlesCount = nextCount
                        }
                        if (pan.x != 0f) {
                            val candleWidth = size.width / candles.size
                            val shift = (-pan.x / candleWidth).toInt()
                            panIndexOffset = (panIndexOffset + shift).coerceIn(0, (rawCandles.size - visibleCandlesCount).coerceAtLeast(0))
                        }
                    }
                }
                .pointerInput(candles.size) {
                    detectTapGestures(
                        onTap = { offset ->
                            val candleWidth = size.width / candles.size
                            val index = (offset.x / candleWidth).toInt().coerceIn(0, candles.size - 1)
                            selectedCandleIndex = index
                        }
                    )
                }
                .pointerInput(candles.size) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val candleWidth = size.width / candles.size
                            val index = (offset.x / candleWidth).toInt().coerceIn(0, candles.size - 1)
                            selectedCandleIndex = index
                        },
                        onDrag = { change, _ ->
                            val candleWidth = size.width / candles.size
                            val index = (change.position.x / candleWidth).toInt().coerceIn(0, candles.size - 1)
                            selectedCandleIndex = index
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val n = candles.size
                if (n == 0) return@Canvas

                // Find price scaling min/max
                var minPrice = candles.minOf { it.low }
                var maxPrice = candles.maxOf { it.high }

                if (indicatorConfig.showBollinger) {
                    candles.mapNotNull { it.bbLower }.minOrNull()?.let { minPrice = minOf(minPrice, it) }
                    candles.mapNotNull { it.bbUpper }.maxOrNull()?.let { maxPrice = maxOf(maxPrice, it) }
                }

                val priceRange = maxOf(1e-4, maxPrice - minPrice)
                val paddedMin = minPrice - (priceRange * 0.04)
                val paddedMax = maxPrice + (priceRange * 0.04)
                val totalRange = paddedMax - paddedMin

                val chartHeight = size.height * 0.82f
                val volumeBaseY = size.height
                val volumeHeight = size.height * 0.16f

                fun priceToY(p: Double): Float {
                    val normalized = (p - paddedMin) / totalRange
                    return (chartHeight - (normalized * chartHeight)).toFloat()
                }

                val maxVol = maxOf(1L, candles.maxOf { it.volume })
                fun volToHeight(v: Long): Float {
                    return ((v.toFloat() / maxVol) * volumeHeight)
                }

                val candleSlotWidth = size.width / n
                val candleBodyWidth = maxOf(3f, candleSlotWidth * 0.68f)

                // Draw Grid Lines (Horizontal price levels)
                val gridSteps = 4
                for (g in 0..gridSteps) {
                    val gPrice = paddedMin + (totalRange * (g.toFloat() / gridSteps))
                    val y = priceToY(gPrice)
                    drawLine(
                        color = CardBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }

                // Draw Bollinger Bands Shading
                if (indicatorConfig.showBollinger) {
                    val upperPath = Path()
                    val lowerPath = Path()
                    var started = false

                    candles.forEachIndexed { i, c ->
                        if (c.bbUpper != null && c.bbLower != null) {
                            val x = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val yUp = priceToY(c.bbUpper)
                            val yLow = priceToY(c.bbLower)
                            if (!started) {
                                upperPath.moveTo(x, yUp)
                                lowerPath.moveTo(x, yLow)
                                started = true
                            } else {
                                upperPath.lineTo(x, yUp)
                                lowerPath.lineTo(x, yLow)
                            }
                        }
                    }

                    drawPath(
                        path = upperPath,
                        color = Color(0xFF64B5F6).copy(alpha = 0.7f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawPath(
                        path = lowerPath,
                        color = Color(0xFF64B5F6).copy(alpha = 0.7f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Draw Proper Candlesticks & Volume Bars
                candles.forEachIndexed { i, c ->
                    val xCenter = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                    val isBullish = c.close >= c.open
                    val candleColor = if (isBullish) NeonEmerald else CrimsonAlert

                    // 1. Volume Bar
                    val vH = volToHeight(c.volume)
                    drawRect(
                        color = candleColor.copy(alpha = 0.28f),
                        topLeft = Offset(xCenter - (candleBodyWidth / 2f), volumeBaseY - vH),
                        size = Size(candleBodyWidth, vH)
                    )

                    // 2. High-Low Wick
                    val yHigh = priceToY(c.high)
                    val yLow = priceToY(c.low)
                    drawLine(
                        color = candleColor,
                        start = Offset(xCenter, yHigh),
                        end = Offset(xCenter, yLow),
                        strokeWidth = 1.6.dp.toPx()
                    )

                    // 3. Candle Body
                    val yOpen = priceToY(c.open)
                    val yClose = priceToY(c.close)
                    val bodyTop = minOf(yOpen, yClose)
                    val bodyHeight = maxOf(2.5f, kotlin.math.abs(yClose - yOpen))
                    drawRect(
                        color = candleColor,
                        topLeft = Offset(xCenter - (candleBodyWidth / 2f), bodyTop),
                        size = Size(candleBodyWidth, bodyHeight)
                    )
                }

                // Helper to draw indicator lines
                fun drawIndicatorLine(values: List<Double?>, color: Color, strokeW: Float = 1.8f) {
                    val path = Path()
                    var hasStarted = false
                    values.forEachIndexed { i, v ->
                        if (v != null) {
                            val x = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val y = priceToY(v)
                            if (!hasStarted) {
                                path.moveTo(x, y)
                                hasStarted = true
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                    }
                    if (hasStarted) {
                        drawPath(path = path, color = color, style = Stroke(width = strokeW.dp.toPx()))
                    }
                }

                // Draw Overlays
                if (indicatorConfig.showEma20) {
                    drawIndicatorLine(candles.map { it.ema20 }, ElectricCyan, 1.8f)
                }
                if (indicatorConfig.showEma50) {
                    drawIndicatorLine(candles.map { it.ema50 }, AmberWarning, 1.8f)
                }
                if (indicatorConfig.showSma200) {
                    drawIndicatorLine(candles.map { it.sma200 }, RoyalViolet, 1.8f)
                }
                if (indicatorConfig.showVwap) {
                    drawIndicatorLine(candles.map { it.vwap }, Color(0xFFFFEA00), 2.0f)
                }

                // Draw Supertrend (segmented by UP / DOWN)
                if (indicatorConfig.showSupertrend) {
                    candles.forEachIndexed { i, c ->
                        val currentSt = c.supertrend
                        val prevSt = if (i > 0) candles[i - 1].supertrend else null
                        if (i > 0 && currentSt != null && prevSt != null) {
                            val prevX = ((i - 1) * candleSlotWidth) + (candleSlotWidth / 2f)
                            val prevY = priceToY(prevSt)
                            val curX = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val curY = priceToY(currentSt)
                            val stColor = if (c.supertrendDirection == "UP") NeonEmerald else CrimsonAlert
                            drawLine(
                                color = stColor,
                                start = Offset(prevX, prevY),
                                end = Offset(curX, curY),
                                strokeWidth = 2.2.dp.toPx()
                            )
                        }
                    }
                }

                // Horizontal Live Price Guide Line & Pulsing Beacon on Current Candle
                val lastCandle = candles.last()
                val liveY = priceToY(lastCandle.close)
                val liveColor = if (lastCandle.close >= lastCandle.open) NeonEmerald else CrimsonAlert
                drawLine(
                    color = liveColor.copy(alpha = 0.7f),
                    start = Offset(0f, liveY),
                    end = Offset(size.width, liveY),
                    strokeWidth = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                )

                val lastX = ((candles.size - 1) * candleSlotWidth) + (candleSlotWidth / 2f)
                drawCircle(color = liveColor, radius = 4.dp.toPx(), center = Offset(lastX, liveY))
                drawCircle(color = liveColor.copy(alpha = 0.35f), radius = 8.dp.toPx(), center = Offset(lastX, liveY))

                // Interactive Crosshair on Touch/Scrub
                selectedCandleIndex?.let { idx ->
                    val touchedCandle = candles.getOrNull(idx)
                    if (touchedCandle != null) {
                        val x = (idx * candleSlotWidth) + (candleSlotWidth / 2f)
                        val y = priceToY(touchedCandle.close)

                        // Vertical Time Line
                        drawLine(
                            color = ElectricCyan.copy(alpha = 0.8f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                        )

                        // Horizontal Price Line
                        drawLine(
                            color = ElectricCyan.copy(alpha = 0.8f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                        )

                        // Crosshair intersection node
                        drawCircle(color = ElectricCyan, radius = 4.dp.toPx(), center = Offset(x, y))
                    }
                }
            }
        }

        // 5. X-Axis Time Labels Row
        if (candles.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val step = maxOf(1, (candles.size - 1) / 4)
                val sampleIndices = listOf(0, step, step * 2, step * 3, candles.size - 1).distinct()
                sampleIndices.forEach { idx ->
                    val candle = candles.getOrNull(idx)
                    if (candle != null) {
                        Text(
                            text = candle.timeStr,
                            fontSize = 9.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Sub-pane 1: RSI Sub-chart (14)
        if (indicatorConfig.showRsi) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("RSI (14)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF80AB))
                selectedCandle?.rsi?.let { r ->
                    Text(
                        text = "$r ${if (r >= 70) "(Overbought)" else if (r <= 30) "(Oversold)" else ""}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (r >= 70) CrimsonAlert else if (r <= 30) NeonEmerald else Color(0xFFFF80AB)
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(SurfaceDark, RoundedCornerShape(6.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val n = candles.size
                    if (n == 0) return@Canvas
                    val candleSlotWidth = size.width / n

                    fun rsiToY(v: Double): Float {
                        val norm = (v.coerceIn(0.0, 100.0) / 100.0)
                        return (size.height - (norm * size.height)).toFloat()
                    }

                    // 70 & 30 reference lines
                    val y70 = rsiToY(70.0)
                    val y30 = rsiToY(30.0)
                    drawLine(CardBorder, Offset(0f, y70), Offset(size.width, y70), strokeWidth = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
                    drawLine(CardBorder, Offset(0f, y30), Offset(size.width, y30), strokeWidth = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))

                    val path = Path()
                    var started = false
                    candles.forEachIndexed { i, c ->
                        if (c.rsi != null) {
                            val x = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val y = rsiToY(c.rsi)
                            if (!started) {
                                path.moveTo(x, y)
                                started = true
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                    }
                    if (started) {
                        drawPath(path, color = Color(0xFFFF80AB), style = Stroke(width = 1.5.dp.toPx()))
                    }
                }
            }
        }

        // Sub-pane 2: MACD Sub-chart (12, 26, 9)
        if (indicatorConfig.showMacd) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MACD (12, 26, 9)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF80DEEA))
                selectedCandle?.let { c ->
                    if (c.macd != null && c.macdSignal != null) {
                        Text(
                            text = "MACD: ${c.macd} | Sig: ${c.macdSignal} | Hist: ${c.macdHist ?: 0.0}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(SurfaceDark, RoundedCornerShape(6.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val n = candles.size
                    if (n == 0) return@Canvas
                    val candleSlotWidth = size.width / n
                    val barWidth = maxOf(2f, candleSlotWidth * 0.6f)

                    val maxAbs = maxOf(
                        0.5,
                        candles.mapNotNull { it.macd }.map { kotlin.math.abs(it) }.maxOrNull() ?: 1.0,
                        candles.mapNotNull { it.macdHist }.map { kotlin.math.abs(it) }.maxOrNull() ?: 1.0
                    )
                    val centerY = size.height / 2f

                    fun valToY(v: Double): Float {
                        val norm = (v / maxAbs).coerceIn(-1.0, 1.0)
                        return (centerY - (norm * (size.height * 0.45f))).toFloat()
                    }

                    // Center 0 line
                    drawLine(CardBorder, Offset(0f, centerY), Offset(size.width, centerY), strokeWidth = 1f)

                    // Histogram bars
                    candles.forEachIndexed { i, c ->
                        if (c.macdHist != null) {
                            val xCenter = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val y = valToY(c.macdHist)
                            val h = kotlin.math.abs(y - centerY)
                            val isPos = c.macdHist >= 0
                            drawRect(
                                color = if (isPos) NeonEmerald.copy(alpha = 0.7f) else CrimsonAlert.copy(alpha = 0.7f),
                                topLeft = Offset(xCenter - (barWidth / 2f), if (isPos) y else centerY),
                                size = Size(barWidth, maxOf(1f, h))
                            )
                        }
                    }

                    // MACD Line
                    val macdPath = Path()
                    var startedMacd = false
                    candles.forEachIndexed { i, c ->
                        if (c.macd != null) {
                            val x = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val y = valToY(c.macd)
                            if (!startedMacd) {
                                macdPath.moveTo(x, y)
                                startedMacd = true
                            } else {
                                macdPath.lineTo(x, y)
                            }
                        }
                    }
                    if (startedMacd) {
                        drawPath(macdPath, color = Color(0xFF00E5FF), style = Stroke(width = 1.5.dp.toPx()))
                    }

                    // Signal Line
                    val sigPath = Path()
                    var startedSig = false
                    candles.forEachIndexed { i, c ->
                        if (c.macdSignal != null) {
                            val x = (i * candleSlotWidth) + (candleSlotWidth / 2f)
                            val y = valToY(c.macdSignal)
                            if (!startedSig) {
                                sigPath.moveTo(x, y)
                                startedSig = true
                            } else {
                                sigPath.lineTo(x, y)
                            }
                        }
                    }
                    if (startedSig) {
                        drawPath(sigPath, color = AmberWarning, style = Stroke(width = 1.2.dp.toPx()))
                    }
                }
            }
        }
    }
}

@Composable
fun IndicatorChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onToggle() }
            .background(
                if (isSelected) color.copy(alpha = 0.22f) else DeepObsidian,
                RoundedCornerShape(6.dp)
            )
            .border(
                1.dp,
                if (isSelected) color else CardBorder,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) TextPrimary else TextSecondary
            )
        }
    }
}

data class ChartIntervalItem(
    val id: String,
    val label: String,
    val category: String,
    val description: String
)
