package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.data.preferences.WatchlistManager
import com.spotpricepredictor.pro.domain.model.PredictionResult
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun PredictionResultCard(
    prediction: PredictionResult,
    modifier: Modifier = Modifier
) {
    val deltaPoints = prediction.predictedSpotPrice - prediction.currentSpot
    val deltaPct = if (prediction.currentSpot > 0) (deltaPoints / prediction.currentSpot) * 100.0 else 0.0
    val isBullish = deltaPoints >= 0
    val accentColor = if (isBullish) NeonEmerald else CrimsonAlert

    val riskPoints = kotlin.math.abs(prediction.currentSpot - prediction.stopLoss).coerceAtLeast(0.1)
    val rewardPoints = kotlin.math.abs(prediction.targetPrice - prediction.currentSpot).coerceAtLeast(0.1)
    val rrRatio = rewardPoints / riskPoints

    val cardBorderBrush = Brush.linearGradient(
        if (isBullish) {
            listOf(NeonEmerald.copy(alpha = 0.5f), ElectricCyan.copy(alpha = 0.3f), CardBorder)
        } else {
            listOf(CrimsonAlert.copy(alpha = 0.5f), RoyalViolet.copy(alpha = 0.3f), CardBorder)
        }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, cardBorderBrush, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Symbol, Current Spot & Strategy Badge
            val isCustomStock = prediction.symbol !in WatchlistManager.DEFAULT_SYMBOLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = prediction.symbol,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(SurfaceDark, RoundedCornerShape(6.dp))
                                .border(0.5.dp, CardBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI QUANT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }

                        if (isCustomStock) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(RoyalViolet.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                                    .border(0.5.dp, RoyalViolet.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = RoyalViolet,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "CUSTOM STOCK",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalViolet
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Current: ₹${"%,.2f".format(prediction.currentSpot)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                StrategyBadge(strategy = prediction.suggestedStrategy)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // HERO ACTION RECOMMENDATION BANNER (BUY / HOLD / SELL)
            val action = prediction.actionRecommendation.uppercase()
            val (actionColor, actionIcon, actionBg, actionTitle) = when (action) {
                "BUY" -> ActionConfig(NeonEmerald, Icons.AutoMirrored.Filled.TrendingUp, NeonEmerald.copy(alpha = 0.12f), "BUY RECOMMENDATION")
                "SELL" -> ActionConfig(CrimsonAlert, Icons.AutoMirrored.Filled.TrendingDown, CrimsonAlert.copy(alpha = 0.12f), "SELL RECOMMENDATION")
                else -> ActionConfig(AmberWarning, Icons.Default.PauseCircle, AmberWarning.copy(alpha = 0.12f), "HOLD / NEUTRAL")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(actionBg, RoundedCornerShape(12.dp))
                    .border(1.2.dp, actionColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(actionColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = actionIcon,
                                    contentDescription = action,
                                    tint = actionColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = actionTitle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = actionColor.copy(alpha = 0.9f),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = when (action) {
                                        "BUY" -> "BUY CALL"
                                        "SELL" -> "SELL CALL"
                                        else -> "HOLD POSITION"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = actionColor
                                )
                            }
                        }

                        // Conviction Badge
                        Box(
                            modifier = Modifier
                                .background(actionColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${"%.1f".format(prediction.confidencePct)}% Conviction",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = actionColor
                            )
                        }
                    }

                    prediction.actionRationale?.let { rationale ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rationale,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Spot Prediction & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PREDICTED SPOT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "₹${"%,.2f".format(prediction.predictedSpotPrice)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${if (isBullish) "▲ +" else "▼ "}${"%.2f".format(deltaPoints)} pts (${if (isBullish) "+" else ""}${"%.2f".format(deltaPct)}%)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                ConfidenceGauge(confidencePct = prediction.confidencePct)
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Target, Stop Loss, Expected High & Low Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TargetBox(label = "TARGET", value = prediction.targetPrice, color = NeonEmerald)
                TargetBox(label = "STOP LOSS", value = prediction.stopLoss, color = CrimsonAlert)
                TargetBox(label = "EXP. HIGH", value = prediction.expectedHigh, color = TextPrimary)
                TargetBox(label = "EXP. LOW", value = prediction.expectedLow, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Risk to Reward & Trade Quality Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Risk : Reward", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "1 : ${String.format("%.1f", rrRatio)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                    }
                }

                Text(
                    text = if (rrRatio >= 1.5) "✓ High R:R Profile" else "Fair Risk Ratio",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (rrRatio >= 1.5) NeonEmerald else AmberWarning
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Probability Distribution Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROBABILITY REGIME DISTRIBUTION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(prediction.probBullish.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(NeonEmerald)
                )
                Box(
                    modifier = Modifier
                        .weight(prediction.probSideways.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(AmberWarning)
                )
                Box(
                    modifier = Modifier
                        .weight(prediction.probBearish.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(CrimsonAlert)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bull: ${(prediction.probBullish * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = NeonEmerald,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Sideways: ${(prediction.probSideways * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = AmberWarning,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Bear: ${(prediction.probBearish * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = CrimsonAlert,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (prediction.variousStrategies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "PREDICTION STRATEGIES MATRIX",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectricCyan,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    prediction.variousStrategies.forEach { strat ->
                        val isBuy = strat.signal.contains("BUY", ignoreCase = true)
                        val isSell = strat.signal.contains("SELL", ignoreCase = true)
                        val sColor = if (isBuy) NeonEmerald else if (isSell) CrimsonAlert else AmberWarning

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceDark, RoundedCornerShape(8.dp))
                                .border(0.6.dp, CardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = strat.strategyName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(sColor.copy(alpha = 0.16f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, sColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = strat.signal,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = sColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Forecast: ₹${"%,.0f".format(strat.predictedSpot)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = "Target: ₹${"%,.0f".format(strat.targetPrice)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeonEmerald
                                    )
                                    Text(
                                        text = "SL: ₹${"%,.0f".format(strat.stopLoss)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CrimsonAlert
                                    )
                                    Text(
                                        text = "${strat.confidencePct.toInt()}% Conf",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strat.rationale,
                                    fontSize = 9.sp,
                                    color = TextMuted,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strategy Rationale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(10.dp))
                    .border(0.5.dp, CardBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = prediction.strategyRationale,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun TargetBox(label: String, value: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "₹${"%,.0f".format(value)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

private data class ActionConfig(
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val background: Color,
    val title: String
)

