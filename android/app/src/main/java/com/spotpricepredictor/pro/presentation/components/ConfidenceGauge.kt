package com.spotpricepredictor.pro.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.presentation.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConfidenceGauge(
    confidencePct: Double,
    modifier: Modifier = Modifier
) {
    val animatedConfidence by animateFloatAsState(
        targetValue = confidencePct.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "ConfidenceAnimation"
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)

            // Background arc (240 degrees: from 150 deg to 390 deg)
            drawArc(
                color = CardBorder,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active gradient arc
            val gradientBrush = Brush.sweepGradient(
                colors = listOf(
                    CrimsonAlert,
                    AmberWarning,
                    ElectricCyan,
                    NeonEmerald
                )
            )

            val sweepAngle = (animatedConfidence / 100f) * 240f

            drawArc(
                brush = gradientBrush,
                startAngle = 150f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Needle pointer endpoint
            val currentAngleRad = Math.toRadians((150.0 + sweepAngle).toDouble())
            val radius = diameter / 2.0
            val center = Offset(size.width / 2f, size.height / 2f)
            val needleEnd = Offset(
                (center.x + radius * cos(currentAngleRad)).toFloat(),
                (center.y + radius * sin(currentAngleRad)).toFloat()
            )

            drawCircle(
                color = ElectricCyan,
                radius = 7.dp.toPx(),
                center = needleEnd
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "${confidencePct.toInt()}%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "CONFIDENCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }
    }
}
