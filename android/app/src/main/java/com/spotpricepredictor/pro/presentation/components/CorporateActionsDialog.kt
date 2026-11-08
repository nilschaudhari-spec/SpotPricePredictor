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
import androidx.compose.material.icons.filled.Refresh
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
import com.spotpricepredictor.pro.domain.model.CorporateActionItem
import com.spotpricepredictor.pro.domain.model.CorporateActionsData
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun CorporateActionsDialog(
    data: CorporateActionsData?,
    isLoading: Boolean,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
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
                                    text = "📜 CORPORATE ACTIONS",
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
                                text = "Dividends, Bonus, Stock Splits & AGMs • NSE & BSE",
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

                    // Filter Chips Row
                    val filters = listOf(
                        "ALL" to "All Actions",
                        "DIVIDEND" to "💰 Dividends",
                        "BONUS" to "🎁 Bonus",
                        "SPLIT" to "✂️ Splits",
                        "RIGHTS" to "⚖️ Rights",
                        "AGM" to "🏛️ AGMs"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filters.take(4).forEach { (key, label) ->
                            val isSelected = selectedFilter.equals(key, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onFilterSelected(key) }
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

                    // Actions List
                    val actions = data?.actions ?: emptyList()
                    if (actions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Loading corporate actions..." else "No corporate actions found for $selectedFilter filter",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(actions) { action ->
                                CorporateActionCard(
                                    action = action,
                                    onSelectStock = {
                                        onStockSelected(action.symbol)
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
}

@Composable
private fun CorporateActionCard(
    action: CorporateActionItem,
    onSelectStock: () -> Unit
) {
    val (typeColor, typeBg) = when (action.actionType.uppercase()) {
        "DIVIDEND" -> NeonEmerald to NeonEmerald.copy(alpha = 0.15f)
        "BONUS" -> RoyalViolet to RoyalViolet.copy(alpha = 0.15f)
        "SPLIT" -> ElectricCyan to ElectricCyan.copy(alpha = 0.15f)
        "RIGHTS" -> AmberWarning to AmberWarning.copy(alpha = 0.15f)
        else -> TextSecondary to TextSecondary.copy(alpha = 0.15f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectStock() },
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
                        text = action.symbol,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                    Box(
                        modifier = Modifier
                            .background(typeBg, RoundedCornerShape(4.dp))
                            .border(0.6.dp, typeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = action.actionType,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = typeColor
                        )
                    }
                }

                if (action.amount != null) {
                    Text(
                        text = "₹${"%.2f".format(action.amount)} / share",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                } else if (action.ratio != null) {
                    Text(
                        text = "Ratio: ${action.ratio}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalViolet
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = action.companyName,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = action.details,
                fontSize = 11.sp,
                color = TextPrimary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ex: ${action.exDate}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberWarning
                    )
                    Text(
                        text = "Record: ${action.recordDate}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = action.impact,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
