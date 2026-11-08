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
import com.spotpricepredictor.pro.domain.model.DetailedTechnicalsData
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun DetailedTechnicalsDialog(
    data: DetailedTechnicalsData?,
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
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "🎯 TECHNICAL SCORECARD",
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
                                text = "${data?.symbol ?: ""} • CMP ₹${"%,.2f".format(data?.spotPrice ?: 0.0)}",
                                fontSize = 11.sp,
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

                    if (data != null) {
                        // Overall Call Meter Card
                        val callColor = when {
                            data.overallCall.contains("BUY") -> NeonEmerald
                            data.overallCall.contains("SELL") -> CrimsonAlert
                            else -> AmberWarning
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, callColor.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .background(callColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .border(1.dp, callColor, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = data.overallCall,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = callColor
                                            )
                                        }

                                        Text(
                                            text = "Score: ${if (data.technicalScore > 0) "+" else ""}${data.technicalScore}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    // Signal counts
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        BadgeCounter(text = "${data.bullishCount} BUY", color = NeonEmerald)
                                        BadgeCounter(text = "${data.bearishCount} SELL", color = CrimsonAlert)
                                        BadgeCounter(text = "${data.neutralCount} NEUT", color = TextSecondary)
                                    }
                                }

                                if (data.summaryRationale.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = data.summaryRationale,
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tab Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "MAs" to "📈 Moving Avgs",
                                "OSCILLATORS" to "⚡ Oscillators",
                                "PIVOTS" to "🎯 Pivot Points"
                            ).forEach { (key, label) ->
                                val isSelected = selectedTab.equals(key, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onTabSelected(key) }
                                    .background(
                                        if (isSelected) ElectricCyan else CardBackground,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        0.8.dp,
                                        if (isSelected) ElectricCyan else CardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) DeepObsidian else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tab Content
                        Box(modifier = Modifier.weight(1f)) {
                            when (selectedTab) {
                                "MAs" -> MovingAveragesList(data)
                                "OSCILLATORS" -> OscillatorsList(data)
                                "PIVOTS" -> PivotPointsList(data)
                                else -> MovingAveragesList(data)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Loading technical analysis..." else "No technical data available",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovingAveragesList(data: DetailedTechnicalsData) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "PERIOD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text(text = "SIMPLE (SMA)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text(text = "EXPONENTIAL (EMA)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }

        items(data.movingAverages) { ma ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MA ${ma.period}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        modifier = Modifier.width(60.dp)
                    )

                    // SMA
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "₹${"%,.1f".format(ma.smaValue)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        CallPill(call = ma.smaCall)
                    }

                    // EMA
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "₹${"%,.1f".format(ma.emaValue)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        CallPill(call = ma.emaCall)
                    }
                }
            }
        }
    }
}

@Composable
private fun OscillatorsList(data: DetailedTechnicalsData) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(data.oscillators) { osc ->
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
                            text = osc.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "${osc.value}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                            CallPill(call = osc.signal)
                        }
                    }

                    if (osc.interpretation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = osc.interpretation,
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PivotPointsList(data: DetailedTechnicalsData) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(data.pivotPoints.entries.toList()) { entry ->
            val lvl = entry.value

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lvl.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                        Text(
                            text = "Pivot: ₹${"%,.2f".format(lvl.p)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "RESISTANCES", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                            Text(text = "R3: ₹${"%,.1f".format(lvl.r3)}", fontSize = 10.sp, color = TextPrimary)
                            Text(text = "R2: ₹${"%,.1f".format(lvl.r2)}", fontSize = 10.sp, color = TextPrimary)
                            Text(text = "R1: ₹${"%,.1f".format(lvl.r1)}", fontSize = 10.sp, color = TextPrimary)
                            if (lvl.r4 != null) {
                                Text(text = "R4: ₹${"%,.1f".format(lvl.r4)}", fontSize = 10.sp, color = TextPrimary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "SUPPORTS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                            Text(text = "S1: ₹${"%,.1f".format(lvl.s1)}", fontSize = 10.sp, color = TextPrimary)
                            Text(text = "S2: ₹${"%,.1f".format(lvl.s2)}", fontSize = 10.sp, color = TextPrimary)
                            Text(text = "S3: ₹${"%,.1f".format(lvl.s3)}", fontSize = 10.sp, color = TextPrimary)
                            if (lvl.s4 != null) {
                                Text(text = "S4: ₹${"%,.1f".format(lvl.s4)}", fontSize = 10.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallPill(call: String) {
    val (color, bg) = when {
        call.contains("BUY") || call.contains("OVERSOLD") -> NeonEmerald to NeonEmerald.copy(alpha = 0.18f)
        call.contains("SELL") || call.contains("OVERBOUGHT") -> CrimsonAlert to CrimsonAlert.copy(alpha = 0.18f)
        call.contains("STRONG") -> RoyalViolet to RoyalViolet.copy(alpha = 0.18f)
        else -> TextSecondary to TextSecondary.copy(alpha = 0.18f)
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = call,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
private fun BadgeCounter(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
