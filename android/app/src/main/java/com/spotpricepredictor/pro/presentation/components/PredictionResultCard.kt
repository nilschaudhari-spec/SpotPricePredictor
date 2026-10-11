package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.PredictionResult
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun PredictionResultCard(
    prediction: PredictionResult,
    modifier: Modifier = Modifier
) {
    val deltaPoints = prediction.predictedSpotPrice - prediction.currentSpot
    val deltaPct = (deltaPoints / prediction.currentSpot) * 100.0
    val isBullish = deltaPoints >= 0
    val accentColor = if (isBullish) NeonEmerald else CrimsonAlert

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Symbol & Horizon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = prediction.symbol,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Current: ₹${"%,.2f".format(prediction.currentSpot)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                StrategyBadge(strategy = prediction.suggestedStrategy)
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "₹${"%,.2f".format(prediction.predictedSpotPrice)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Text(
                        text = "${if (isBullish) "+" else ""}${"%.2f".format(deltaPoints)} pts (${if (isBullish) "+" else ""}${"%.2f".format(deltaPct)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor
                    )
                }

                ConfidenceGauge(confidencePct = prediction.confidencePct)
            }

            Spacer(modifier = Modifier.height(18.dp))
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Target, Stop Loss, Expected High & Low Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TargetBox(label = "TARGET", value = prediction.targetPrice, color = NeonEmerald)
                TargetBox(label = "STOP LOSS", value = prediction.stopLoss, color = CrimsonAlert)
                TargetBox(label = "EXP. HIGH", value = prediction.expectedHigh, color = TextPrimary)
                TargetBox(label = "EXP. LOW", value = prediction.expectedLow, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Probability Distribution Bar
            Text(
                text = "PROBABILITY DISTRIBUTION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
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

            Spacer(modifier = Modifier.height(14.dp))

            // Strategy Rationale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(8.dp))
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
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Text(
            text = "₹${"%,.0f".format(value)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
