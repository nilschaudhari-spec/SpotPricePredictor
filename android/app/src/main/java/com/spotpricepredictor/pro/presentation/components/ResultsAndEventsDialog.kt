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
import com.spotpricepredictor.pro.domain.model.CorporateEventItem
import com.spotpricepredictor.pro.domain.model.EconomicEventItem
import com.spotpricepredictor.pro.domain.model.QuarterlyResultItem
import com.spotpricepredictor.pro.domain.model.ResultsAndEventsData
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun ResultsAndEventsDialog(
    data: ResultsAndEventsData?,
    isLoading: Boolean,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onStockSelected: (String) -> Unit,
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
                                    text = "📅 RESULTS & EVENTS",
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
                                text = "Earnings, Board Meetings & Macro Economic Data",
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

                    // Tab Row
                    val tabs = listOf(
                        "RESULTS" to "📊 Quarterly Results",
                        "EVENTS" to "🏢 Corporate Events",
                        "ECONOMIC" to "🌍 Macro Events"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tabs.forEach { (key, label) ->
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
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) DeepObsidian else TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Content based on tab
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            "RESULTS" -> QuarterlyResultsView(
                                results = data?.quarterlyResults ?: emptyList(),
                                isLoading = isLoading,
                                onSelect = {
                                    onStockSelected(it)
                                    onDismiss()
                                }
                            )
                            "EVENTS" -> CorporateEventsView(
                                events = data?.corporateEvents ?: emptyList(),
                                isLoading = isLoading,
                                onSelect = {
                                    onStockSelected(it)
                                    onDismiss()
                                }
                            )
                            "ECONOMIC" -> EconomicEventsView(
                                events = data?.economicEvents ?: emptyList(),
                                isLoading = isLoading
                            )
                            else -> QuarterlyResultsView(
                                results = data?.quarterlyResults ?: emptyList(),
                                isLoading = isLoading,
                                onSelect = {
                                    onStockSelected(it)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuarterlyResultsView(
    results: List<QuarterlyResultItem>,
    isLoading: Boolean,
    onSelect: (String) -> Unit
) {
    if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (isLoading) "Loading quarterly earnings..." else "No results found",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(results) { res ->
                val isDeclared = res.status.uppercase() == "DECLARED"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(res.symbol) },
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = res.symbol,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isDeclared) NeonEmerald.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${res.quarter} • ${res.status}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDeclared) NeonEmerald else AmberWarning
                                    )
                                }
                            }

                            Text(
                                text = res.resultDate,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = res.companyName,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Revenue & PAT figures
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "REVENUE (₹ CR)", fontSize = 8.sp, color = TextSecondary)
                                val revAct = res.revenueActualCr?.let { "₹${"%,.0f".format(it)}" } ?: "Pending"
                                val revEst = res.revenueEstimateCr?.let { "Est: ₹${"%,.0f".format(it)}" } ?: ""
                                Text(text = revAct, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (revEst.isNotBlank()) {
                                    Text(text = revEst, fontSize = 9.sp, color = TextSecondary)
                                }
                            }

                            Column {
                                Text(text = "NET PROFIT PAT (₹ CR)", fontSize = 8.sp, color = TextSecondary)
                                val patAct = res.patActualCr?.let { "₹${"%,.0f".format(it)}" } ?: "Pending"
                                val patEst = res.patEstimateCr?.let { "Est: ₹${"%,.0f".format(it)}" } ?: ""
                                Text(text = patAct, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (patEst.isNotBlank()) {
                                    Text(text = patEst, fontSize = 9.sp, color = TextSecondary)
                                }
                            }

                            if (res.yoyGrowthPct != null) {
                                val isYoyPos = res.yoyGrowthPct >= 0
                                val yoyColor = if (isYoyPos) NeonEmerald else CrimsonAlert
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "YoY GROWTH", fontSize = 8.sp, color = TextSecondary)
                                    Text(
                                        text = "${if (isYoyPos) "+" else ""}${"%.1f".format(res.yoyGrowthPct)}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = yoyColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CorporateEventsView(
    events: List<CorporateEventItem>,
    isLoading: Boolean,
    onSelect: (String) -> Unit
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (isLoading) "Loading corporate events..." else "No corporate events scheduled",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(events) { evt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(evt.symbol) },
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = evt.symbol,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan
                                )
                                Box(
                                    modifier = Modifier
                                        .background(RoyalViolet.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = evt.category,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalViolet
                                    )
                                }
                            }

                            Text(
                                text = evt.eventDate,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AmberWarning
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = evt.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = evt.description,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EconomicEventsView(
    events: List<EconomicEventItem>,
    isLoading: Boolean
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (isLoading) "Loading macro events..." else "No economic events found",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(events) { ec ->
                val impactColor = when (ec.impactLevel.uppercase()) {
                    "HIGH" -> CrimsonAlert
                    "MEDIUM" -> AmberWarning
                    else -> ElectricCyan
                }

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
                            Box(
                                modifier = Modifier
                                    .background(impactColor.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                                    .border(0.8.dp, impactColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${ec.impactLevel} IMPACT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = impactColor
                                )
                            }

                            Text(
                                text = ec.date,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = ec.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (ec.forecast != null) {
                                Column {
                                    Text(text = "FORECAST", fontSize = 8.sp, color = TextSecondary)
                                    Text(text = ec.forecast, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                            }
                            if (ec.previous != null) {
                                Column {
                                    Text(text = "PREVIOUS", fontSize = 8.sp, color = TextSecondary)
                                    Text(text = ec.previous, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                }
                            }
                            if (ec.actual != null) {
                                Column {
                                    Text(text = "ACTUAL", fontSize = 8.sp, color = TextSecondary)
                                    Text(text = ec.actual, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ElectricCyan)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Affected: ${ec.sectorImpact}",
                            fontSize = 9.sp,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }
    }
}

