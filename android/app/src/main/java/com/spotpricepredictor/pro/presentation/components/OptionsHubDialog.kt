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
fun OptionsHubDialog(
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
                                    text = "📊 OPTIONS & OPEN INTEREST LAB",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalViolet,
                                    letterSpacing = 0.5.sp
                                )
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = RoyalViolet
                                    )
                                }
                            }
                            Text(
                                text = "${data?.symbol ?: "NIFTY"} • Chapters 4 & 5 Open Interest & Hedging",
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

                    // 3 Tabs: OI MATRIX, HEDGING, TERMINOLOGIES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "OI_MATRIX" to "OI Matrix (Ch 4)",
                            "HEDGING" to "Hedging Strats (Ch 5)",
                            "TERMINOLOGIES" to "Option Terms"
                        ).forEach { (key, label) ->
                            val isSelected = selectedTab.equals(key, ignoreCase = true)
                            val activeColor = when (key) {
                                "OI_MATRIX" -> RoyalViolet
                                "HEDGING" -> ElectricCyan
                                else -> AmberWarning
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
                            Text(text = "Loading Options Data...", color = TextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        when (selectedTab.uppercase()) {
                            "OI_MATRIX" -> OpenInterestTabContent(data)
                            "HEDGING" -> HedgingTabContent(data)
                            "TERMINOLOGIES" -> TerminologiesTabContent(data)
                            else -> OpenInterestTabContent(data)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OpenInterestTabContent(data: ProStrategiesData) {
    val oi = data.openInterest
    val isBull = oi.interpretation.contains("BUILDUP", ignoreCase = true) && oi.interpretation.contains("LONG") || oi.interpretation.contains("SHORT COVERING")
    val interpColor = if (isBull) NeonEmerald else CrimsonAlert

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Active OI Classification Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = interpColor.copy(alpha = 0.12f)),
                border = BorderStroke(1.2.dp, interpColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "OI INTERPRETATION (SLIDES 64-65)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = interpColor)
                        Box(
                            modifier = Modifier
                                .background(interpColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = oi.marketSentiment, fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, color = DeepObsidian)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = oi.interpretation, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = interpColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = oi.actionAdvice, fontSize = 9.5.sp, color = TextPrimary, lineHeight = 13.sp)
                }
            }
        }

        // 4 Quadrants Matrix Reference (Slide 65)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "4-QUADRANT OPEN INTEREST MATRIX", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OiMatrixQuadrant(
                            title = "Long Buildup",
                            rule = "Price ▲ + OI ▲",
                            sentiment = "Strong Bullish",
                            isActive = oi.interpretation == "LONG BUILDUP",
                            color = NeonEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        OiMatrixQuadrant(
                            title = "Short Buildup",
                            rule = "Price ▼ + OI ▲",
                            sentiment = "Strong Bearish",
                            isActive = oi.interpretation == "SHORT BUILDUP",
                            color = CrimsonAlert,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OiMatrixQuadrant(
                            title = "Short Covering",
                            rule = "Price ▲ + OI ▼",
                            sentiment = "Bullish Pullback",
                            isActive = oi.interpretation == "SHORT COVERING",
                            color = ElectricCyan,
                            modifier = Modifier.weight(1f)
                        )
                        OiMatrixQuadrant(
                            title = "Long Unwinding",
                            rule = "Price ▼ + OI ▼",
                            sentiment = "Bearish Booking",
                            isActive = oi.interpretation == "LONG UNWINDING",
                            color = AmberWarning,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Strike & PCR Analytics Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "DERIVATIVE METRICS & KEY LEVELS", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "PUT-CALL RATIO (PCR)", fontSize = 8.sp, color = TextSecondary)
                            Text(text = "%.2f".format(oi.putCallRatioOi), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (oi.putCallRatioOi >= 1.0) NeonEmerald else CrimsonAlert)
                        }
                        Column {
                            Text(text = "MAX PAIN STRIKE", fontSize = 8.sp, color = TextSecondary)
                            Text(text = "₹${"%,.0f".format(oi.maxPainStrike)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "CALL RESISTANCE", fontSize = 8.sp, color = CrimsonAlert)
                            Text(text = "₹${"%,.0f".format(oi.highestOiCallResistance)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "PUT SUPPORT", fontSize = 8.sp, color = NeonEmerald)
                            Text(text = "₹${"%,.0f".format(oi.highestOiPutSupport)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OiMatrixQuadrant(
    title: String,
    rule: String,
    sentiment: String,
    isActive: Boolean,
    color: Color,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) color.copy(alpha = 0.22f) else SurfaceDark,
                RoundedCornerShape(8.dp)
            )
            .border(
                if (isActive) 1.2.dp else 0.8.dp,
                if (isActive) color else CardBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold, color = if (isActive) color else TextPrimary)
                if (isActive) {
                    Text(text = "ACTIVE", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = color)
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = rule, fontSize = 8.5.sp, color = color, fontWeight = FontWeight.Bold)
            Text(text = sentiment, fontSize = 8.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun HedgingTabContent(data: ProStrategiesData) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "CHAPTER 5: UPTREND & DOWNTREND STRATEGIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Works only when market is in trend (Uptrend/Downtrend). Avoid if market is sideways.", fontSize = 8.5.sp, color = TextSecondary)
                }
            }
        }

        items(data.hedgingStrategies) { strat ->
            val isPut = strat.strategyName.contains("Put", ignoreCase = true)
            val stratColor = if (isPut) NeonEmerald else CrimsonAlert

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.2.dp, stratColor.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = strat.strategyName, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = stratColor)
                        Box(
                            modifier = Modifier
                                .background(stratColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = if (isPut) "UPTREND" else "DOWNTREND", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = stratColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // 2 Legs Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SurfaceDark, RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(text = "LEG 1 (FUTURE)", fontSize = 7.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text(text = strat.futureLeg, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SurfaceDark, RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(text = "LEG 2 (ATM OPTION)", fontSize = 7.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text(text = strat.optionLeg, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = stratColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "🛡️ ${strat.riskProfile}", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = strat.netProtection, fontSize = 8.5.sp, color = TextSecondary, lineHeight = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TerminologiesTabContent(data: ProStrategiesData) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "CHAPTER 4: OPTION TERMINOLOGIES & STRATEGIES (SLIDE 66)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        items(data.terminologies) { term ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${term.id}. ${term.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                        Box(
                            modifier = Modifier
                                .background(SurfaceDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = term.bestMarketCondition,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = term.definition,
                        fontSize = 9.sp,
                        color = TextPrimary,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

