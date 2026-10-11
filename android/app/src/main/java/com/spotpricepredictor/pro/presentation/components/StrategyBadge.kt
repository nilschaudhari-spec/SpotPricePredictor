package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.StrategyType
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun StrategyBadge(
    strategy: StrategyType,
    modifier: Modifier = Modifier
) {
    val (primaryColor, gradientColors) = when (strategy) {
        StrategyType.BUY_CALL, StrategyType.CALL_SPREAD ->
            Pair(NeonEmerald, listOf(Color(0xFF065F46), Color(0xFF047857)))
        StrategyType.BUY_PUT, StrategyType.PUT_SPREAD ->
            Pair(CrimsonAlert, listOf(Color(0xFF991B1B), Color(0xFFB91C1C)))
        StrategyType.IRON_CONDOR ->
            Pair(ElectricCyan, listOf(Color(0xFF0C4A6E), Color(0xFF0284C7)))
        StrategyType.STRADDLE ->
            Pair(RoyalViolet, listOf(Color(0xFF5B21B6), Color(0xFF7C3AED)))
        StrategyType.WAIT, StrategyType.NO_TRADE ->
            Pair(AmberWarning, listOf(Color(0xFF78350F), Color(0xFFB45309)))
    }

    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, primaryColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = strategy.label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 0.5.sp
        )
    }
}
