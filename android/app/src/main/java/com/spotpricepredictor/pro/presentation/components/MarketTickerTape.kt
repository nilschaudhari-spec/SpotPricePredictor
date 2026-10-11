package com.spotpricepredictor.pro.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.spotpricepredictor.pro.data.preferences.TickerSettingsManager
import com.spotpricepredictor.pro.domain.model.TickerItem
import com.spotpricepredictor.pro.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.DecimalFormat

@Composable
fun MarketTickerTape(
    tickerItems: List<TickerItem> = TickerItem.DEFAULT_TICKERS,
    onSelectSymbol: ((String) -> Unit)? = null,
    tickerSettingsManager: TickerSettingsManager? = null,
    modifier: Modifier = Modifier
) {
    var speedFactor by remember { mutableStateOf(tickerSettingsManager?.getSpeed() ?: 1.0f) }
    var isPaused by remember { mutableStateOf(tickerSettingsManager?.getIsPaused() ?: false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val decimalFormat = remember { DecimalFormat("#,##0.00") }

    // Duplicate list multiple times to enable continuous infinite looping
    val infiniteItems = remember(tickerItems) {
        tickerItems + tickerItems + tickerItems + tickerItems
    }

    // Auto-scroll loop
    LaunchedEffect(speedFactor, isPaused, infiniteItems.size) {
        while (isActive) {
            if (!isPaused && speedFactor > 0.05f) {
                val scrollDelta = (speedFactor * 1.5f)
                val current = scrollState.value
                val max = scrollState.maxValue

                if (max > 0) {
                    if (current >= max - 50) {
                        // Reset to the first cycle seamlessly
                        scrollState.scrollTo(max / 4)
                    } else {
                        scrollState.scrollBy(scrollDelta)
                    }
                }
            }
            delay(16L) // ~60 FPS
        }
    }

    if (showSpeedDialog) {
        TickerSpeedDialog(
            currentSpeed = speedFactor,
            isPaused = isPaused,
            onSpeedChange = { newSpeed ->
                speedFactor = newSpeed
                tickerSettingsManager?.setSpeed(newSpeed)
            },
            onTogglePause = {
                val newPaused = !isPaused
                isPaused = newPaused
                tickerSettingsManager?.setPaused(newPaused)
            },
            onDismiss = { showSpeedDialog = false }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),
        color = SurfaceDark
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Control Badge Button (Pinned Left)
            Box(
                modifier = Modifier
                    .padding(start = 6.dp, end = 4.dp)
                    .background(
                        if (isPaused) CrimsonAlert.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.15f),
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        1.dp,
                        if (isPaused) CrimsonAlert.copy(alpha = 0.5f) else ElectricCyan.copy(alpha = 0.4f),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { showSpeedDialog = true }
                    .padding(horizontal = 7.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.Pause else Icons.Default.Speed,
                        contentDescription = "Adjust Ticker Speed",
                        tint = if (isPaused) CrimsonAlert else ElectricCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isPaused) "PAUSED" else "${String.format("%.1f", speedFactor)}x",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaused) CrimsonAlert else ElectricCyan
                    )
                }
            }

            // Continuous Horizontal Scrolling Items
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    infiniteItems.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onSelectSymbol?.invoke(item.symbol) }
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = item.symbol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "₹${decimalFormat.format(item.spotPrice)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val isPos = item.change >= 0
                            Text(
                                text = if (isPos) "▲ +${String.format("%.2f", item.changePercent)}%" else "▼ ${String.format("%.2f", item.changePercent)}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPos) NeonEmerald else CrimsonAlert
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "•",
                                fontSize = 10.sp,
                                color = TextSecondary.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                // Left fade gradient edge
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(SurfaceDark, Color.Transparent)
                            )
                        )
                )

                // Right fade gradient edge
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, SurfaceDark)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun TickerSpeedDialog(
    currentSpeed: Float,
    isPaused: Boolean,
    onSpeedChange: (Float) -> Unit,
    onTogglePause: () -> Unit,
    onDismiss: () -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(currentSpeed) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Market Ticker Speed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Adjust scrolling rate for default market quotes",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Presets
                Text(
                    text = "SPEED PRESETS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        "0.5x Slow" to 0.5f,
                        "1.0x Normal" to 1.0f,
                        "2.0x Fast" to 2.0f,
                        "3.0x Turbo" to 3.0f
                    )
                    presets.forEach { (label, speed) ->
                        val isSelected = !isPaused && kotlin.math.abs(sliderValue - speed) < 0.05f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) ElectricCyan.copy(alpha = 0.2f) else SurfaceDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else CardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    sliderValue = speed
                                    onSpeedChange(speed)
                                    if (isPaused) onTogglePause()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) ElectricCyan else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Fine-tuning Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM SPEED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${String.format("%.1f", sliderValue)}x",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                }

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onSpeedChange(it)
                    },
                    valueRange = 0.2f..4.0f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricCyan,
                        activeTrackColor = ElectricCyan,
                        inactiveTrackColor = CardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pause / Resume Toggle Button
                Button(
                    onClick = onTogglePause,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaused) NeonEmerald.copy(alpha = 0.2f) else CrimsonAlert.copy(alpha = 0.2f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPaused) NeonEmerald else CrimsonAlert
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = if (isPaused) NeonEmerald else CrimsonAlert,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPaused) "RESUME TICKER (▶)" else "PAUSE TICKER (⏸)",
                        color = if (isPaused) NeonEmerald else CrimsonAlert,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CLOSE", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
