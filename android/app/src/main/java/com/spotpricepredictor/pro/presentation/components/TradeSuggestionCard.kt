package com.spotpricepredictor.pro.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.InstrumentType
import com.spotpricepredictor.pro.domain.model.SignalCategory
import com.spotpricepredictor.pro.domain.model.TradeAction
import com.spotpricepredictor.pro.domain.model.TradeSuggestion
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun TradeSuggestionCard(
    suggestion: TradeSuggestion,
    onViewChart: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val isBuy = suggestion.action == TradeAction.BUY
    val actionColor = if (isBuy) NeonEmerald else CrimsonAlert
    val actionBg = if (isBuy) NeonEmerald.copy(alpha = 0.15f) else CrimsonAlert.copy(alpha = 0.15f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Top Row: Symbol, Category/Instrument Tag & Action Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = suggestion.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Instrument / Category Chip
                        Box(
                            modifier = Modifier
                                .background(SurfaceDark, RoundedCornerShape(6.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (suggestion.instrumentType == InstrumentType.EQUITY) "EQUITY" else "F&O: ${suggestion.fnoDetails?.optionType ?: "OPT"}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (suggestion.instrumentType == InstrumentType.EQUITY) ElectricCyan else RoyalViolet
                            )
                        }
                    }
                    Text(
                        text = suggestion.name,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                // Buy / Sell Badge
                Box(
                    modifier = Modifier
                        .background(actionBg, RoundedCornerShape(8.dp))
                        .border(1.5.dp, actionColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = actionColor,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = suggestion.signalStrength.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = actionColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Targets Grid (Entry, T1, T2, SL, R:R)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ENTRY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(
                        text = "₹${suggestion.entryPrice}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text("Spot: ₹${suggestion.currentPrice}", fontSize = 9.sp, color = TextSecondary)
                }

                Column {
                    Text("TARGET 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                    Text(
                        text = "₹${suggestion.target1}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                    Text("+${suggestion.potentialGainPct}%", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = NeonEmerald)
                }

                Column {
                    Text("STOP LOSS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                    Text(
                        text = "₹${suggestion.stopLoss}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonAlert
                    )
                    Text("R:R ${suggestion.riskRewardRatio}", fontSize = 9.sp, color = AmberWarning)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("AI CONF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    Text(
                        text = "${suggestion.confidencePct.toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                    Text(suggestion.timeframe, fontSize = 9.sp, color = TextSecondary)
                }
            }

            // F&O Specific Derivatives Box
            suggestion.fnoDetails?.let { fno ->
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoyalViolet.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .border(1.dp, RoyalViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fno.contractName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Exp: ${fno.expiryDate} (Lot: ${fno.lotSize})",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Premium: ₹${fno.premiumEntry}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElectricCyan
                        )
                        Text(
                            text = "Target: ₹${fno.premiumTarget}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonEmerald
                        )
                        Text(
                            text = "SL: ₹${fno.premiumStopLoss}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CrimsonAlert
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Technical Analysis Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Technical Analysis & Indicators Rationale",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberWarning
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .background(DeepObsidian, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    // Quick Indicator Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IndicatorPill("RSI", "${suggestion.technicalAnalysis.rsi.toInt()} (${suggestion.technicalAnalysis.rsiSignal})", ElectricCyan)
                        IndicatorPill("VWAP", suggestion.technicalAnalysis.vwapSignal, AmberWarning)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IndicatorPill("Supertrend", suggestion.technicalAnalysis.supertrend, NeonEmerald)
                        IndicatorPill("MACD", suggestion.technicalAnalysis.macdStatus, RoyalViolet)
                    }

                    if (suggestion.technicalAnalysis.oiPcr != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IndicatorPill("OI PCR", "${suggestion.technicalAnalysis.oiPcr}", ElectricCyan)
                            IndicatorPill("Max Pain", "₹${suggestion.technicalAnalysis.maxPain?.toInt()}", CrimsonAlert)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = suggestion.technicalAnalysis.rationale,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: View Interactive Graph Button
            Button(
                onClick = { onViewChart(suggestion.symbol) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan.copy(alpha = 0.2f),
                    contentColor = ElectricCyan
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.AutoGraph, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "View Graph & Apply Indicators (${suggestion.symbol})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun IndicatorPill(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .background(SurfaceDark, RoundedCornerShape(4.dp))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$label: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 9.sp, color = TextPrimary, maxLines = 1)
        }
    }
}
