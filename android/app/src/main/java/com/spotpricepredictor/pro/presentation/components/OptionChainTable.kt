package com.spotpricepredictor.pro.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.OptionChainData
import com.spotpricepredictor.pro.domain.model.StrikeRowData
import com.spotpricepredictor.pro.presentation.theme.*

enum class OptionTableViewMode(val label: String) {
    OI_AND_PRICES("OI & Prices"),
    GREEKS_AND_IV("Greeks & IV"),
    BUILDUP_ANALYSIS("Buildup & Flows")
}

@Composable
fun OptionChainTable(
    optionChain: OptionChainData,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(OptionTableViewMode.OI_AND_PRICES) }

    val maxCallOi = remember(optionChain.strikes) {
        optionChain.strikes.maxOfOrNull { it.call.oi }?.coerceAtLeast(1L) ?: 1L
    }
    val maxPutOi = remember(optionChain.strikes) {
        optionChain.strikes.maxOfOrNull { it.put.oi }?.coerceAtLeast(1L) ?: 1L
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .background(CardBackground, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Table View Mode Selector Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OPTIONS DEPTH MATRIX",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OptionTableViewMode.values().forEach { mode ->
                    val isSelected = mode == viewMode
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) ElectricCyan.copy(alpha = 0.2f) else SurfaceDark,
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else CardBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewMode = mode }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ElectricCyan else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .border(0.5.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (viewMode) {
                OptionTableViewMode.OI_AND_PRICES -> {
                    Text(
                        text = "CALL OI",
                        modifier = Modifier.weight(1.3f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Text(
                        text = "CALL LTP",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                    Text(
                        text = "STRIKE",
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWarning
                    )
                    Text(
                        text = "PUT LTP",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonAlert
                    )
                    Text(
                        text = "PUT OI",
                        modifier = Modifier.weight(1.3f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
                OptionTableViewMode.GREEKS_AND_IV -> {
                    Text(
                        text = "CALL DELTA",
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Text(
                        text = "CALL IV",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                    Text(
                        text = "STRIKE",
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWarning
                    )
                    Text(
                        text = "PUT IV",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonAlert
                    )
                    Text(
                        text = "PUT DELTA",
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalViolet
                    )
                }
                OptionTableViewMode.BUILDUP_ANALYSIS -> {
                    Text(
                        text = "CALL FLOW",
                        modifier = Modifier.weight(1.3f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Text(
                        text = "CHG OI",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                    Text(
                        text = "STRIKE",
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWarning
                    )
                    Text(
                        text = "CHG OI",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonAlert
                    )
                    Text(
                        text = "PUT FLOW",
                        modifier = Modifier.weight(1.3f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalViolet
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Table Rows
        Column(modifier = Modifier.fillMaxWidth()) {
            optionChain.strikes.forEach { row ->
                val step = if (optionChain.strikes.size > 1) {
                    kotlin.math.abs(optionChain.strikes[1].strikePrice - optionChain.strikes[0].strikePrice)
                } else 50.0
                val isAtm = kotlin.math.abs(row.strikePrice - optionChain.spotPrice) <= (step / 2.0 + 1.0)
                val isCallItm = row.strikePrice < optionChain.spotPrice
                val isPutItm = row.strikePrice > optionChain.spotPrice

                OptionChainRow(
                    row = row,
                    isAtm = isAtm,
                    isCallItm = isCallItm,
                    isPutItm = isPutItm,
                    viewMode = viewMode,
                    maxCallOi = maxCallOi,
                    maxPutOi = maxPutOi
                )
                HorizontalDivider(color = DividerColor.copy(alpha = 0.35f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun OptionChainRow(
    row: StrikeRowData,
    isAtm: Boolean,
    isCallItm: Boolean,
    isPutItm: Boolean,
    viewMode: OptionTableViewMode,
    maxCallOi: Long,
    maxPutOi: Long
) {
    val callFraction = (row.call.oi.toFloat() / maxCallOi.toFloat()).coerceIn(0f, 1f)
    val putFraction = (row.put.oi.toFloat() / maxPutOi.toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    isAtm -> RoyalViolet.copy(alpha = 0.22f)
                    else -> Color.Transparent
                }
            )
            .padding(vertical = 7.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (viewMode) {
            OptionTableViewMode.OI_AND_PRICES -> {
                // Call OI with visual depth bar
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Depth fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(callFraction)
                            .align(Alignment.CenterEnd)
                            .background(ElectricCyan.copy(alpha = 0.12f), RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = formatCompact(row.call.oi),
                        fontSize = 11.sp,
                        color = if (isCallItm) AmberWarning else TextPrimary,
                        fontWeight = if (isCallItm) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                // Call LTP
                Text(
                    text = "₹${"%.1f".format(row.call.ltp)}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonEmerald
                )

                // Strike
                Box(
                    modifier = Modifier.weight(1.2f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${row.strikePrice.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = if (isAtm) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isAtm) ElectricCyan else AmberWarning
                        )
                        if (isAtm) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .background(ElectricCyan.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                    .border(0.5.dp, ElectricCyan, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                            ) {
                                Text(text = "ATM", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                            }
                        }
                    }
                }

                // Put LTP
                Text(
                    text = "₹${"%.1f".format(row.put.ltp)}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CrimsonAlert
                )

                // Put OI with visual depth bar
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Depth fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(putFraction)
                            .align(Alignment.CenterStart)
                            .background(RoyalViolet.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = formatCompact(row.put.oi),
                        fontSize = 11.sp,
                        color = if (isPutItm) AmberWarning else TextPrimary,
                        fontWeight = if (isPutItm) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            OptionTableViewMode.GREEKS_AND_IV -> {
                // Call Delta
                Text(
                    text = "${"%.2f".format(row.call.greeks.delta)}",
                    modifier = Modifier.weight(1.2f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )

                // Call IV
                Text(
                    text = "${"%.1f".format(row.call.iv)}%",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = NeonEmerald
                )

                // Strike
                Text(
                    text = "${row.strikePrice.toInt()}",
                    modifier = Modifier.weight(1.2f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = if (isAtm) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isAtm) ElectricCyan else AmberWarning
                )

                // Put IV
                Text(
                    text = "${"%.1f".format(row.put.iv)}%",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = CrimsonAlert
                )

                // Put Delta
                Text(
                    text = "${"%.2f".format(row.put.greeks.delta)}",
                    modifier = Modifier.weight(1.2f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = RoyalViolet,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OptionTableViewMode.BUILDUP_ANALYSIS -> {
                // Call Buildup tag
                Text(
                    text = row.call.buildup.take(12),
                    modifier = Modifier.weight(1.3f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (row.call.buildup.contains("Long", ignoreCase = true)) NeonEmerald else CrimsonAlert
                )

                // Call Change in OI
                val isCallOiUp = row.call.changeInOi >= 0
                Text(
                    text = (if (isCallOiUp) "+" else "") + formatCompact(row.call.changeInOi),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCallOiUp) NeonEmerald else CrimsonAlert
                )

                // Strike
                Text(
                    text = "${row.strikePrice.toInt()}",
                    modifier = Modifier.weight(1.2f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = if (isAtm) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isAtm) ElectricCyan else AmberWarning
                )

                // Put Change in OI
                val isPutOiUp = row.put.changeInOi >= 0
                Text(
                    text = (if (isPutOiUp) "+" else "") + formatCompact(row.put.changeInOi),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPutOiUp) NeonEmerald else CrimsonAlert
                )

                // Put Buildup tag
                Text(
                    text = row.put.buildup.take(12),
                    modifier = Modifier.weight(1.3f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (row.put.buildup.contains("Short", ignoreCase = true)) NeonEmerald else CrimsonAlert
                )
            }
        }
    }
}

private fun formatCompact(number: Long): String {
    val absNum = kotlin.math.abs(number)
    val sign = if (number < 0) "-" else ""
    return when {
        absNum >= 10000000 -> "$sign${"%.1f".format(absNum / 10000000.0)}Cr"
        absNum >= 100000 -> "$sign${"%.1f".format(absNum / 100000.0)}L"
        absNum >= 1000 -> "$sign${"%.1f".format(absNum / 1000.0)}k"
        else -> "$sign$absNum"
    }
}
