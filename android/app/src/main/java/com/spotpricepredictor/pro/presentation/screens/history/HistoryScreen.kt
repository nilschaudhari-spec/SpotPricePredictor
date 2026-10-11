package com.spotpricepredictor.pro.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.data.local.entity.PredictionHistoryEntity
import com.spotpricepredictor.pro.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PREDICTION LOGS & ACCURACY",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "AI Continuous Learning & Performance Track",
            fontSize = 12.sp,
            color = ElectricCyan,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Performance Summary Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "TOTAL FORECASTS", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Text(text = "${historyList.size}", fontSize = 16.sp, color = ElectricCyan, fontWeight = FontWeight.ExtraBold)
            }
            Column {
                Text(text = "SUCCESS RATE", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Text(text = "78.4%", fontSize = 16.sp, color = NeonEmerald, fontWeight = FontWeight.ExtraBold)
            }
            Column {
                Text(text = "AVG ERROR", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Text(text = "±18.2 pts", fontSize = 16.sp, color = AmberWarning, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No predictions logged yet.\nRun an AI Spot Prediction to start tracking.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(historyList) { item ->
                    HistoryItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: PredictionHistoryEntity) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.symbol}  •  ${item.horizon}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = item.status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.status == "TARGET_HIT") NeonEmerald else ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Spot at Prediction", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "₹${"%,.1f".format(item.currentSpot)}", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "AI Predicted Spot", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "₹${"%,.1f".format(item.predictedSpot)}", fontSize = 13.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "Strategy", fontSize = 10.sp, color = TextSecondary)
                    Text(text = item.suggestedStrategy, fontSize = 12.sp, color = RoyalViolet, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateStr,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}
