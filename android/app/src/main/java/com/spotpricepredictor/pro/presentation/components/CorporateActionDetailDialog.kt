package com.spotpricepredictor.pro.presentation.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.CorporateActionItem
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun CorporateActionDetailDialog(
    action: CorporateActionItem,
    onDismiss: () -> Unit,
    onPredictStock: (String) -> Unit
) {
    val (typeColor, typeBg) = when (action.actionType.uppercase()) {
        "DIVIDEND" -> NeonEmerald to NeonEmerald.copy(alpha = 0.15f)
        "BONUS" -> RoyalViolet to RoyalViolet.copy(alpha = 0.15f)
        "SPLIT" -> ElectricCyan to ElectricCyan.copy(alpha = 0.15f)
        "RIGHTS" -> AmberWarning to AmberWarning.copy(alpha = 0.15f)
        "BUYBACK" -> Color(0xFF00E5FF) to Color(0xFF00E5FF).copy(alpha = 0.15f)
        else -> TextSecondary to TextSecondary.copy(alpha = 0.15f)
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
                                    text = "CORPORATE ACTION DETAILS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${action.symbol} • NSE & BSE Official Record",
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
                        // 1. Symbol & Action Banner Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = action.symbol,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ElectricCyan
                                        )
                                        Text(
                                            text = action.companyName,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(typeBg, RoundedCornerShape(8.dp))
                                            .border(1.dp, typeColor, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = action.actionType,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = typeColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Main Value Highlight
                                if (action.amount != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(typeBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "CASH PAYOUT VALUE", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "₹${"%.2f".format(action.amount)} / Share",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = NeonEmerald
                                            )
                                        }
                                        Text(
                                            text = "Direct Demat Bank Credit",
                                            fontSize = 10.sp,
                                            color = NeonEmerald,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else if (action.ratio != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(typeBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "ENTITLEMENT RATIO", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = action.ratio,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = RoyalViolet
                                            )
                                        }
                                        Text(
                                            text = if (action.actionType.contains("BONUS", ignoreCase = true)) "Free Bonus Shares" else "Sub-division Split",
                                            fontSize = 10.sp,
                                            color = RoyalViolet,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = action.details,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        // 2. Timeline & Critical Dates Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📅 CRITICAL DATES & SETTLEMENT TIMELINE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Ex-Date
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(SurfaceDark, RoundedCornerShape(8.dp))
                                            .border(1.dp, AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(text = "EX-DATE (CUT-OFF)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = action.exDate, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                        Text(text = "Must buy BEFORE this date", fontSize = 8.sp, color = TextSecondary)
                                    }

                                    // Record Date
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(SurfaceDark, RoundedCornerShape(8.dp))
                                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(text = "RECORD DATE (T+1)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = action.recordDate, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                        Text(text = "Demat ownership verification", fontSize = 8.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }

                        // 3. Shareholder Eligibility & T+1 Settlement Rule
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "SHAREHOLDER ELIGIBILITY GUIDE (T+1 RULE)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonEmerald
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                val eligibilityRules = listOf(
                                    "Cum-Date Requirement: Buy shares on or before the Cum-Date (at least 1 trading day prior to Ex-Date).",
                                    "T+1 Settlement: Indian stock exchanges operate on T+1 rolling settlement. Buying on Ex-Date means shares credit on T+2 and will NOT qualify.",
                                    "Demat Ownership: Shares must be settled and present in your Demat account as of the close of the Record Date.",
                                    "Selling on Ex-Date: If you hold shares on Cum-Date and sell on or after Ex-Date, you REMAIN fully eligible to receive the benefit."
                                )

                                eligibilityRules.forEach { rule ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "•", color = NeonEmerald, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = rule,
                                            fontSize = 10.sp,
                                            color = TextPrimary,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Price Adjustment & Market Impact Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📊 PRICE ADJUSTMENT & TAXATION NOTES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val impactDesc = when (action.actionType.uppercase()) {
                                    "DIVIDEND" -> "On the morning of the Ex-Date, the exchange adjusts the stock opening price downwards by approximately the dividend payout (₹${"%.2f".format(action.amount ?: 0.0)}). Dividends are taxable under Section 194 at personal income tax slabs (10% TDS if exceeding ₹5,000)."
                                    "BONUS" -> "Bonus ratio ${action.ratio ?: "1:1"} increases liquidity and trading volume. Theoretical ex-date price is adjusted down proportionally. Bonus shares have zero cost of acquisition under Section 55 for capital gains tax."
                                    "SPLIT" -> "Stock split reduces face value, making shares more affordable for retail investors. No capital gains tax is triggered upon stock split execution."
                                    "RIGHTS" -> "Existing shareholders receive entitlement to purchase fresh equity at a concessional subscription price. Unused rights entitlements (RE) can be traded or renounced on exchange."
                                    "BUYBACK" -> "Tender buyback offers liquidity return at a premium to CMP. Increases earnings per share (EPS) and Return on Equity (ROE)."
                                    else -> "Corporate action verified under SEBI LODR Regulations. Liquidity and index weighting adjustments apply on Ex-Date."
                                }

                                Text(
                                    text = impactDesc,
                                    fontSize = 10.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SurfaceDark, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Market Impact: ${action.impact}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Strategy & Trading Playbook (Aligned with PPT)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ QUANT TRADING & HEDGING PLAYBOOK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalViolet,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val playBook = listOf(
                                    "Pre-Ex-Date Run-up: Momentum buyers often accumulate 3-5 days before Ex-Date for dividend yield or bonus eligibility.",
                                    "Protective Put Hedge: Slide 66 Hedging - buy ATM Put Option on Futures to protect against post-ex-date discount volatility.",
                                    "Post-Ex-Date Dip Buying: If stock falls beyond the theoretical adjustment, watch for Morning Star or Supertrend bounce for high-probability re-entry."
                                )

                                playBook.forEach { tip ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "▸", color = RoyalViolet, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = tip,
                                            fontSize = 10.sp,
                                            color = TextPrimary,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Text(text = "← Back", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onPredictStock(action.symbol)
                                onDismiss()
                            },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = DeepObsidian,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Predict ${action.symbol}",
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
