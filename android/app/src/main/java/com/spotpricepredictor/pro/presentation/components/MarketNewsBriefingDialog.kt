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
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.MarketNewsBriefing
import com.spotpricepredictor.pro.domain.model.MarketNewsItem
import com.spotpricepredictor.pro.domain.model.NewsImpactSentiment
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun MarketNewsBriefingDialog(
    briefing: MarketNewsBriefing?,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onNewsSelected: (MarketNewsItem) -> Unit,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📰 MARKET NEWS & BRIEFINGS",
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

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = onRefresh,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(CardBackground, RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
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
                    }

                    Text(
                        text = "Tap any news card to read the full story and quant impact analysis",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val items = briefing?.items ?: emptyList()
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Fetching breaking market stories..." else "No recent market news found. Tap refresh.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(items, key = { it.id }) { item ->
                                val (sentimentColor, sentimentBg, sentimentLabel) = when (item.sentiment) {
                                    NewsImpactSentiment.POSITIVE -> Triple(NeonEmerald, NeonEmerald.copy(alpha = 0.15f), "BULLISH")
                                    NewsImpactSentiment.NEGATIVE -> Triple(CrimsonAlert, CrimsonAlert.copy(alpha = 0.15f), "BEARISH")
                                    NewsImpactSentiment.MIXED -> Triple(AmberWarning, AmberWarning.copy(alpha = 0.15f), "VOLATILE")
                                    NewsImpactSentiment.UNCERTAIN -> Triple(ElectricCyan, ElectricCyan.copy(alpha = 0.15f), "NEUTRAL")
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNewsSelected(item) },
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
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(sentimentBg, RoundedCornerShape(4.dp))
                                                        .border(0.6.dp, sentimentColor, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = sentimentLabel,
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = sentimentColor
                                                    )
                                                }

                                                Text(
                                                    text = item.source,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextSecondary
                                                )
                                            }

                                            Text(
                                                text = item.timestamp,
                                                fontSize = 9.sp,
                                                color = TextMuted
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = item.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            lineHeight = 17.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = item.whatHappened,
                                            fontSize = 10.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 14.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                item.symbols.take(3).forEach { sym ->
                                                    Box(
                                                        modifier = Modifier
                                                            .background(SurfaceDark, RoundedCornerShape(3.dp))
                                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = sym, fontSize = 8.5.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .border(0.6.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Full Story ➔",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElectricCyan
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
        }
    }
}

