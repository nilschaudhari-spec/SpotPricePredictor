package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.ProStrategiesData
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun ProStrategiesDialog(
    data: ProStrategiesData?,
    isLoading: Boolean,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
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
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "⚡ PRO QUANT STRATEGIES",
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
                                text = "${data?.symbol ?: "NIFTY"} • All Rounder Pro Techniques Suite",
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

                    // 4 Navigation Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "INTRADAY" to "5M Intraday",
                            "GOLDEN_CROSS" to "Golden Cross",
                            "BOLLINGER" to "Bollinger",
                            "CONSOLIDATION" to "Breakout"
                        ).forEach { (key, label) ->
                            val isSelected = selectedTab.equals(key, ignoreCase = true)
                            val activeColor = when (key) {
                                "INTRADAY" -> NeonEmerald
                                "GOLDEN_CROSS" -> AmberWarning
                                "BOLLINGER" -> ElectricCyan
                                else -> RoyalViolet
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onTabSelected(key) }
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (data == null) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Loading Pro Strategies...", color = TextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        when (selectedTab.uppercase()) {
                            "INTRADAY" -> DailyIntradayTabContent(data)
                            "GOLDEN_CROSS" -> GoldenCrossTabContent(data)
                            "BOLLINGER" -> BollingerBandsTabContent(data)
                            "CONSOLIDATION" -> ConsolidationTabContent(data)
                            else -> DailyIntradayTabContent(data)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyIntradayTabContent(data: ProStrategiesData) {
    val di = data.dailyIntraday
    val isBuy = di.triConfirmationSignal.contains("BUY", ignoreCase = true)
    val isSell = di.triConfirmationSignal.contains("SELL", ignoreCase = true)
    val verdictColor = if (isBuy) NeonEmerald else if (isSell) CrimsonAlert else AmberWarning

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Master Tri-Confirmation Verdict
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = verdictColor.copy(alpha = 0.12f)),
                border = BorderStroke(1.2.dp, verdictColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRIPLE CONFIRMATION STATUS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdictColor,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(verdictColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (di.isConfirmed) "CONFIRMED SIGNAL" else "AWAITING SYNC",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepObsidian
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = di.triConfirmationSignal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = verdictColor
                    )
                    Text(
                        text = "Timeframe: ${di.timeframe} • Supertrend (20,2) + VWAP + MACD alignment required",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Indicators Triad (Supertrend, VWAP, MACD)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Supertrend
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "SUPERTREND (20,2)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(3.dp))
                        val isStUp = di.supertrendStatus.contains("BULLISH", ignoreCase = true)
                        Text(
                            text = if (isStUp) "🟢 BULLISH" else "🔴 BEARISH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isStUp) NeonEmerald else CrimsonAlert
                        )
                        Text(text = "₹${"%,.1f".format(di.supertrendValue)}", fontSize = 9.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // 2. VWAP
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "VWAP", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(3.dp))
                        val isVwapUp = di.vwapStatus.contains("ABOVE", ignoreCase = true)
                        Text(
                            text = if (isVwapUp) "🟢 ABOVE VWAP" else "🔴 BELOW VWAP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isVwapUp) NeonEmerald else CrimsonAlert
                        )
                        Text(text = "₹${"%,.1f".format(di.vwapValue)}", fontSize = 9.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // 3. MACD
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "MACD (12,26,9)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(3.dp))
                        val isMacdUp = di.macdStatus.contains("BULLISH", ignoreCase = true)
                        Text(
                            text = if (isMacdUp) "🟢 BULL CROSS" else "🔴 BEAR CROSS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isMacdUp) NeonEmerald else CrimsonAlert
                        )
                        Text(text = "Hist Expanding", fontSize = 8.5.sp, color = TextSecondary)
                    }
                }
            }
        }

        // 1:1 Target & SL Plan
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "🎯 1:1 RISK-REWARD EXECUTION PLAN",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "ENTRY", fontSize = 8.sp, color = TextSecondary)
                            Text(text = "₹${"%,.1f".format(di.entryPrice)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "TARGET (1:1)", fontSize = 8.sp, color = NeonEmerald)
                            Text(text = "₹${"%,.1f".format(di.targetPrice)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                        }
                        Column {
                            Text(text = "STOP LOSS (1:1)", fontSize = 8.sp, color = CrimsonAlert)
                            Text(text = "₹${"%,.1f".format(di.stopLoss)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "RATIO", fontSize = 8.sp, color = TextSecondary)
                            Text(text = di.targetSlRatio, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalViolet)
                        }
                    }
                }
            }
        }

        // Strict 5 Rules Checklist (Slide 62)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "⚠️ 5 MANDATORY EXECUTION RULES (SLIDE 62)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWarning
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    di.executionRules.forEach { rule ->
                        Text(
                            text = rule,
                            fontSize = 9.sp,
                            color = TextPrimary,
                            lineHeight = 13.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GoldenCrossTabContent(data: ProStrategiesData) {
    val gc = data.goldenCross
    val isUp = gc.crossoverStatus.contains("BULLISH", ignoreCase = true)
    val statusColor = if (isUp) NeonEmerald else CrimsonAlert

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, statusColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "50 & 200 EMA CROSSOVER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = gc.tradeAction, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = statusColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = gc.crossoverStatus, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = statusColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "BLUE LINE (50 EMA)", fontSize = 8.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                            Text(text = "₹${"%,.1f".format(gc.ema50Value)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "RED LINE (200 EMA)", fontSize = 8.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                            Text(text = "₹${"%,.1f".format(gc.ema200Value)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Re-Enter Opportunity (Slides 55-58)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.15f)),
                border = BorderStroke(1.2.dp, AmberWarning)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔄 RE-ENTER OPPORTUNITY (SLIDE 55-58)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberWarning
                        )
                        Box(
                            modifier = Modifier
                                .background(AmberWarning, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (gc.reenterOpportunity) "RE-ENTER ACTIVE" else "WAIT FOR RETEST",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepObsidian
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gc.reenterType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gc.reenterExplanation,
                        fontSize = 9.5.sp,
                        color = TextSecondary,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BollingerBandsTabContent(data: ProStrategiesData) {
    val bb = data.bollingerBands
    val isCall = bb.tradeCall.contains("CALL", ignoreCase = true)
    val isPut = bb.tradeCall.contains("PUT", ignoreCase = true)
    val color = if (isCall) NeonEmerald else if (isPut) CrimsonAlert else ElectricCyan

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
                border = BorderStroke(1.2.dp, color)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "BOLLINGER BANDS (20, 2) VOLATILITY RIDER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = bb.tradeCall, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
                    Text(text = bb.bandStatus, fontSize = 9.5.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = bb.explanation, fontSize = 9.sp, color = TextSecondary, lineHeight = 13.sp)
                }
            }
        }

        // 3 Bands Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "BAND LEVELS & THRESHOLDS (SLIDE 48)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "UPPER LINE (Uptrend)", fontSize = 8.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                            Text(text = "₹${"%,.1f".format(bb.upperBand)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "MEDIAN (Trend Reverse)", fontSize = 8.sp, color = AmberWarning, fontWeight = FontWeight.Bold)
                            Text(text = "₹${"%,.1f".format(bb.medianLine)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "LOWER LINE (Downtrend)", fontSize = 8.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                            Text(text = "₹${"%,.1f".format(bb.lowerBand)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsolidationTabContent(data: ProStrategiesData) {
    val cons = data.consolidation
    val isSideways = cons.isConsolidating
    val color = if (isSideways) AmberWarning else NeonEmerald

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
                border = BorderStroke(1.2.dp, color)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "CONSOLIDATION & BREAKOUT (SLIDES 33-37)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = cons.status, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = cons.guidance, fontSize = 9.5.sp, color = TextPrimary, lineHeight = 13.sp)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "SIDEWAYS CHANNEL BOUNDARIES", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "RANGE CEILING (RESISTANCE)", fontSize = 8.sp, color = CrimsonAlert)
                            Text(text = "₹${"%,.1f".format(cons.rangeHigh)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "RANGE FLOOR (SUPPORT)", fontSize = 8.sp, color = NeonEmerald)
                            Text(text = "₹${"%,.1f".format(cons.rangeLow)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

