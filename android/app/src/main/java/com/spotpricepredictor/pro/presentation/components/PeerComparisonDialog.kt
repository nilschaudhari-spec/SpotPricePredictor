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
import androidx.compose.material.icons.filled.Star
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
import com.spotpricepredictor.pro.domain.model.PeerComparisonData
import com.spotpricepredictor.pro.domain.model.PeerStockMetrics
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun PeerComparisonDialog(
    data: PeerComparisonData?,
    isLoading: Boolean,
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
                                    text = "🏢 PEER COMPARISON",
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
                                text = "${data?.sector ?: "Sector"} • ${data?.industry ?: "Industry"}",
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

                    val peers = data?.peers ?: emptyList()
                    if (peers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Loading peer metrics..." else "No peer comparison data available",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(peers) { peer ->
                                PeerCard(
                                    peer = peer,
                                    onSelect = {
                                        onStockSelected(peer.symbol)
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
private fun PeerCard(
    peer: PeerStockMetrics,
    onSelect: () -> Unit
) {
    val isPositive = peer.changePct >= 0
    val chgColor = if (isPositive) NeonEmerald else CrimsonAlert
    val sign = if (isPositive) "+" else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (peer.isTargetStock) RoyalViolet.copy(alpha = 0.12f) else CardBackground
        ),
        border = BorderStroke(
            if (peer.isTargetStock) 1.5.dp else 1.dp,
            if (peer.isTargetStock) RoyalViolet else CardBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = peer.symbol,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (peer.isTargetStock) RoyalViolet else ElectricCyan
                    )
                    if (peer.isTargetStock) {
                        Box(
                            modifier = Modifier
                                .background(RoyalViolet.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "CURRENT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalViolet
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "₹${"%,.2f".format(peer.cmp)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .background(chgColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$sign${"%.2f".format(peer.changePct)}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = chgColor
                        )
                    }
                }
            }

            Text(
                text = peer.name,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Grid (2 rows x 3 columns)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricCell(label = "P/E RATIO", value = "${peer.peRatio}x")
                MetricCell(label = "P/B RATIO", value = "${peer.pbRatio}x")
                MetricCell(label = "MARKET CAP", value = peer.marketCapStr)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricCell(label = "ROE", value = "${peer.roe}%", color = if (peer.roe >= 15.0) NeonEmerald else TextPrimary)
                MetricCell(label = "ROCE", value = "${peer.roce}%", color = if (peer.roce >= 15.0) NeonEmerald else TextPrimary)
                MetricCell(label = "DIV YIELD", value = "${peer.dividendYield}%")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 52W Range Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "52W Low: ₹${"%,.0f".format(peer.low52w)}",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
                Text(
                    text = "NPM: ${peer.netProfitMargin}%",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElectricCyan
                )
                Text(
                    text = "52W High: ₹${"%,.0f".format(peer.high52w)}",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun MetricCell(
    label: String,
    value: String,
    color: Color = TextPrimary
) {
    Column(modifier = Modifier.width(90.dp)) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
