package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.AdvancedAnalysis
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun AdvancedMetricsCard(
    analysis: AdvancedAnalysis,
    modifier: Modifier = Modifier
) {
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
                .padding(18.dp)
        ) {
            Text(
                text = "QUANT & DERIVATIVE ANALYTICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: PCR & Max Pain
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricItem(label = "PCR (OI)", value = "%.3f".format(analysis.pcr), modifier = Modifier.weight(1f))
                MetricItem(label = "MAX PAIN", value = "₹${analysis.maxPain.toInt()}", modifier = Modifier.weight(1f))
                MetricItem(label = "SENTIMENT", value = "${analysis.marketSentimentScore.toInt()}/100", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DividerColor, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Dealer Positioning & Gamma Exposure
            Text(
                text = "DEALER POSITIONING",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = analysis.dealerPositioning,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if ("Long Gamma" in analysis.dealerPositioning) ElectricCyan else CrimsonAlert
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DividerColor, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: Support & Resistance levels
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "KEY SUPPORTS (PUT OI)", fontSize = 10.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                    Text(
                        text = analysis.supportLevels.joinToString("  •  ") { "₹${it.toInt()}" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "KEY RESISTANCES (CALL OI)", fontSize = 10.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                    Text(
                        text = analysis.resistanceLevels.joinToString("  •  ") { "₹${it.toInt()}" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DividerColor, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Row 4: Technicals & Momentum
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricItem(label = "VWAP POSITION", value = analysis.vwapPosition, modifier = Modifier.weight(1.2f))
                MetricItem(label = "ATR VOLATILITY", value = "±${analysis.atrVolatility.toInt()} pts", modifier = Modifier.weight(1f))
                MetricItem(label = "MOMENTUM SCORE", value = "${analysis.momentumScore.toInt()}", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
