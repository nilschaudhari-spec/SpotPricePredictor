package com.spotpricepredictor.pro.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.MarketNewsItem
import com.spotpricepredictor.pro.domain.model.NewsImpactSentiment
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun NewsDetailDialog(
    item: MarketNewsItem,
    onDismiss: () -> Unit,
    onSelectSymbol: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    val (sentimentColor, sentimentBg, sentimentText) = when (item.sentiment) {
        NewsImpactSentiment.POSITIVE -> Triple(NeonEmerald, NeonEmerald.copy(alpha = 0.15f), "🟢 BULLISH / POSITIVE IMPACT")
        NewsImpactSentiment.NEGATIVE -> Triple(CrimsonAlert, CrimsonAlert.copy(alpha = 0.15f), "🔴 BEARISH / NEGATIVE IMPACT")
        NewsImpactSentiment.MIXED -> Triple(AmberWarning, AmberWarning.copy(alpha = 0.15f), "🟡 MIXED / VOLATILE IMPACT")
        NewsImpactSentiment.UNCERTAIN -> Triple(ElectricCyan, ElectricCyan.copy(alpha = 0.15f), "🔵 NEUTRAL / AWAITING CUES")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepObsidian.copy(alpha = 0.98f))
                .padding(14.dp)
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
                    // Top Navigation Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(CardBackground, RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "FULL NEWS STORY",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${item.source} • ${item.timestamp}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
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

                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Category & Sentiment Banner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(SurfaceDark, RoundedCornerShape(6.dp))
                                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.section.label.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(sentimentBg, RoundedCornerShape(6.dp))
                                    .border(1.dp, sentimentColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = sentimentText,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = sentimentColor
                                )
                            }
                        }

                        // 2. Headline
                        Text(
                            text = item.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            lineHeight = 23.sp
                        )

                        // 3. Related Asset Chips
                        if (item.symbols.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AFFECTED ASSETS:",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                item.symbols.forEach { sym ->
                                    Box(
                                        modifier = Modifier
                                            .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .clickable { onSelectSymbol?.invoke(sym) }
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "$sym ➔",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ElectricCyan
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Section 1: What Happened
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📌 WHAT HAPPENED (IN-DEPTH REPORT)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.whatHappened,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        // 5. Section 2: Why It Matters
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 WHY IT MATTERS (INSTITUTIONAL ANALYSIS)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.whyItMatters,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        // 6. Section 3: Likely Market & Volatility Impact
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🎯 LIKELY MARKET & SECTOR IMPACT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sentimentColor,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.likelyImpact,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        // 7. Section 4: Quant Trading Playbook (Aligned with PPT)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ QUANT TRADING & EXECUTION PLAYBOOK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalViolet,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val strategies = when (item.sentiment) {
                                    NewsImpactSentiment.POSITIVE -> listOf(
                                        "Intraday 5M Setup: Watch for Supertrend (20,2) green buy signal + VWAP cross on 5M timeframe.",
                                        "Bull Call Spread: PPT Chapter 4 - Buy ATM Call and sell OTM Call to capture upside with capped volatility risk.",
                                        "Resistance Target: Watch immediate pivot resistance levels before adding aggressive long leverage."
                                    )
                                    NewsImpactSentiment.NEGATIVE -> listOf(
                                        "Protective Put Hedge: PPT Chapter 5 - Buy ATM Put on active futures or holding to guard against drawdown.",
                                        "Bear Put Spread: Buy ATM Put and sell OTM Put for low-cost high-probability downside capture.",
                                        "Intraday Reversal: Wait for Hammer or Morning Star pattern near key support before attempting counter-trend trades."
                                    )
                                    else -> listOf(
                                        "Range Breakout: PPT Consolidation Strategy - trade only on confirmed 15M breakout beyond the range bounds.",
                                        "Option Selling / Iron Condor: Exploit theta decay while market consolidates around high open interest strikes.",
                                        "Strict Risk Limits: Maintain 1:2 Risk/Reward ratio with tight stop loss below session VWAP."
                                    )
                                }

                                strategies.forEach { strat ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "▸", color = RoyalViolet, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = strat,
                                            fontSize = 10.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (item.sourceUrl.isNotBlank()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Source Link", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        val primarySym = item.symbols.firstOrNull() ?: "NIFTY"
                        Button(
                            onClick = {
                                onSelectSymbol?.invoke(primarySym)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = DeepObsidian,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Predict $primarySym",
                                color = DeepObsidian,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
