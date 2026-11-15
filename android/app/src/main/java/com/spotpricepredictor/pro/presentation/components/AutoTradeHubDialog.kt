package com.spotpricepredictor.pro.presentation.components

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.*
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun AutoTradeHubDialog(
    settings: AutoTradeSettings,
    portfolio: VirtualPortfolio,
    isScanning: Boolean,
    onToggleAutoTrade: (Boolean) -> Unit,
    onUpdateSettings: (AutoTradeSettings) -> Unit,
    onScanAndExecute: () -> Unit,
    onSquareOff: (String?) -> Unit,
    onTestBroker: () -> Unit,
    brokerTestMessage: String?,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("POSITIONS") } // "POSITIONS", "SCANNER", "SETTINGS"

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
                    // 1. Top Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "🤖 AUTO-TRADING & FRONTPAGE HUB",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (settings.isAutoTradeEnabled) NeonEmerald.copy(alpha = 0.2f) else CardBackground,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            0.8.dp,
                                            if (settings.isAutoTradeEnabled) NeonEmerald else CardBorder,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (settings.isAutoTradeEnabled) "● AUTO-TRADE ACTIVE" else "⏸️ STANDBY",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (settings.isAutoTradeEnabled) NeonEmerald else TextSecondary
                                    )
                                }
                            }
                            Text(
                                text = "${settings.brokerDestination.label} • Auto Buy/Sell on Pattern Detection",
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Capital & Real-Time MTM P&L Card
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
                                Column {
                                    Text(text = "VIRTUAL CAPITAL (FRONTPAGE)", fontSize = 8.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "₹${"%,.0f".format(portfolio.totalCapital)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "LIVE MTM P&L (UNREALIZED)", fontSize = 8.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                    val isPos = portfolio.unrealizedPnl >= 0
                                    Text(
                                        text = "${if (isPos) "+₹" else "-₹"}${"%,.2f".format(kotlin.math.abs(portfolio.unrealizedPnl))}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isPos) NeonEmerald else CrimsonAlert
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "AVAILABLE MARGIN", fontSize = 8.sp, color = TextSecondary)
                                    Text(text = "₹${"%,.0f".format(portfolio.availableMargin)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                                }
                                Column {
                                    Text(text = "USED MARGIN", fontSize = 8.sp, color = TextSecondary)
                                    Text(text = "₹${"%,.0f".format(portfolio.usedMargin)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Column {
                                    Text(text = "REALIZED P&L", fontSize = 8.sp, color = TextSecondary)
                                    val rPos = portfolio.realizedPnl >= 0
                                    Text(
                                        text = "${if (rPos) "+₹" else "-₹"}${"%,.2f".format(kotlin.math.abs(portfolio.realizedPnl))}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rPos) NeonEmerald else CrimsonAlert
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "WIN RATE", fontSize = 8.sp, color = TextSecondary)
                                    Text(text = "${portfolio.winRatePct}% (${portfolio.winningTrades}W / ${portfolio.losingTrades}L)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalViolet)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Navigation Tabs: POSITIONS, SCANNER, SETTINGS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tabs = listOf(
                            "POSITIONS" to "Positions (${portfolio.positions.count { it.status == "OPEN" }})",
                            "SCANNER" to "⚡ Multi-Stock Scanner",
                            "SETTINGS" to "⚙️ Broker Settings"
                        )

                        tabs.forEach { (key, label) ->
                            val isSel = selectedTab == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = key }
                                    .background(
                                        if (isSel) ElectricCyan.copy(alpha = 0.2f) else CardBackground,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSel) ElectricCyan else CardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) ElectricCyan else TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Tab Content
                    when (selectedTab) {
                        "POSITIONS" -> PositionsTabContent(
                            portfolio = portfolio,
                            onSquareOff = onSquareOff
                        )
                        "SCANNER" -> ScannerTabContent(
                            settings = settings,
                            portfolio = portfolio,
                            isScanning = isScanning,
                            onScanAndExecute = onScanAndExecute,
                            onToggleAutoTrade = onToggleAutoTrade
                        )
                        "SETTINGS" -> SettingsTabContent(
                            settings = settings,
                            onUpdateSettings = onUpdateSettings,
                            onTestBroker = onTestBroker,
                            brokerTestMessage = brokerTestMessage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionsTabContent(
    portfolio: VirtualPortfolio,
    onSquareOff: (String?) -> Unit
) {
    val openPositions = portfolio.positions.filter { it.status == "OPEN" }
    val closedPositions = portfolio.positions.filter { it.status != "OPEN" }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (openPositions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE OPEN POSITIONS (${openPositions.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        letterSpacing = 0.5.sp
                    )

                    Button(
                        onClick = { onSquareOff("ALL") },
                        modifier = Modifier.height(26.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert.copy(alpha = 0.2f)),
                        border = BorderStroke(0.8.dp, CrimsonAlert),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = "Square Off All", fontSize = 8.5.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(openPositions) { pos ->
                PositionCard(
                    pos = pos,
                    onSquareOff = { onSquareOff(pos.positionId) }
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No active open positions",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Turn ON Auto-Trading or run Multi-Stock Scanner to execute trades",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        if (closedPositions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CLOSED TRADES (${closedPositions.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
            }

            items(closedPositions.take(10)) { pos ->
                ClosedPositionCard(pos = pos)
            }
        }
    }
}

@Composable
private fun PositionCard(
    pos: VirtualPosition,
    onSquareOff: () -> Unit
) {
    val isBuy = pos.action.uppercase() == "BUY"
    val actionColor = if (isBuy) NeonEmerald else CrimsonAlert
    val actionBg = actionColor.copy(alpha = 0.15f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = pos.symbol, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ElectricCyan)
                    Box(
                        modifier = Modifier
                            .background(actionBg, RoundedCornerShape(4.dp))
                            .border(0.6.dp, actionColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${pos.action} (${pos.quantity} Qty)", fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, color = actionColor)
                    }
                }

                val isPos = pos.pnl >= 0
                Text(
                    text = "${if (isPos) "+₹" else "-₹"}${"%,.2f".format(kotlin.math.abs(pos.pnl))} (${if (isPos) "+" else ""}${"%.2f".format(pos.pnlPercent)}%)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isPos) NeonEmerald else CrimsonAlert
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Triggered by: ${pos.patternTrigger}",
                fontSize = 9.sp,
                color = RoyalViolet,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ENTRY / CMP", fontSize = 8.sp, color = TextSecondary)
                    Text(text = "₹${"%.2f".format(pos.avgPrice)} ➔ ₹${"%.2f".format(pos.currentPrice)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Column {
                    Text(text = "STOP LOSS", fontSize = 8.sp, color = CrimsonAlert)
                    Text(text = "₹${"%.2f".format(pos.stopLoss)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
                }

                Column {
                    Text(text = "TARGET", fontSize = 8.sp, color = NeonEmerald)
                    Text(text = "₹${"%.2f".format(pos.targetPrice)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                }

                Button(
                    onClick = onSquareOff,
                    modifier = Modifier.height(26.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert.copy(alpha = 0.2f)),
                    border = BorderStroke(0.8.dp, CrimsonAlert),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = "Exit", fontSize = 9.sp, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ClosedPositionCard(pos: VirtualPosition) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(0.6.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = pos.symbol, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = pos.action, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (pos.action == "BUY") NeonEmerald else CrimsonAlert)
                    Text(text = "• ${pos.status.replace('_', ' ')}", fontSize = 8.sp, color = TextSecondary)
                }
                Text(text = "${pos.quantity} shares @ ₹${pos.avgPrice} (Exit: ₹${pos.exitPrice ?: pos.currentPrice})", fontSize = 8.5.sp, color = TextSecondary)
            }

            val isPos = pos.pnl >= 0
            Text(
                text = "${if (isPos) "+₹" else "-₹"}${"%,.2f".format(kotlin.math.abs(pos.pnl))}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPos) NeonEmerald else CrimsonAlert
            )
        }
    }
}

@Composable
private fun ScannerTabContent(
    settings: AutoTradeSettings,
    portfolio: VirtualPortfolio,
    isScanning: Boolean,
    onScanAndExecute: () -> Unit,
    onToggleAutoTrade: (Boolean) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Action Button: Scan All Stocks & Auto-Execute Now
            Button(
                onClick = onScanAndExecute,
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DeepObsidian, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Scanning 24 Stocks Across NSE/BSE...", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = DeepObsidian, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡ Scan All Stocks & Auto-Trade on FrontPage",
                        color = DeepObsidian,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        item {
            // Auto-Trade Switch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "BACKGROUND AUTO-TRADING ENGINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        Text(
                            text = if (settings.isAutoTradeEnabled) "Actively scanning market every ${settings.scanIntervalSeconds}s" else "Standing by. Turn ON to auto-execute on pattern find",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = settings.isAutoTradeEnabled,
                        onCheckedChange = { onToggleAutoTrade(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepObsidian,
                            checkedTrackColor = NeonEmerald,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceDark
                        )
                    )
                }
            }
        }

        item {
            // Execution Order Audit History Header
            Text(
                text = "RECENT AUTO-EXECUTED ORDERS (${portfolio.orderHistory.size})",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                letterSpacing = 0.5.sp
            )
        }

        if (portfolio.orderHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No orders executed yet. Tap 'Scan All Stocks' above.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        } else {
            items(portfolio.orderHistory) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = order.symbol, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = ElectricCyan)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (order.action == "BUY") NeonEmerald.copy(alpha = 0.15f) else CrimsonAlert.copy(alpha = 0.15f),
                                            RoundedCornerShape(3.dp)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(text = "${order.action} (${order.quantity})", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (order.action == "BUY") NeonEmerald else CrimsonAlert)
                                }
                                Text(text = "@ ₹${"%.2f".format(order.orderPrice)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Text(
                                text = order.status,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (order.status == "EXECUTED") NeonEmerald else CrimsonAlert
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Pattern: ${order.patternTrigger}", fontSize = 9.sp, color = RoyalViolet, fontWeight = FontWeight.SemiBold)
                        Text(text = order.executionMessage, fontSize = 8.5.sp, color = TextSecondary, lineHeight = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "SL: ₹${order.stopLoss} | TGT: ₹${order.targetPrice}", fontSize = 8.sp, color = TextMuted)
                            Text(text = order.timestamp, fontSize = 8.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTabContent(
    settings: AutoTradeSettings,
    onUpdateSettings: (AutoTradeSettings) -> Unit,
    onTestBroker: () -> Unit,
    brokerTestMessage: String?
) {
    var selectedBroker by remember { mutableStateOf(settings.brokerDestination) }
    var tokenInput by remember { mutableStateOf(settings.frontpageUserToken) }
    var webhookInput by remember { mutableStateOf(settings.webhookUrl) }
    var minReliability by remember { mutableStateOf(settings.minReliabilityPct) }
    var maxCapital by remember { mutableStateOf(settings.maxCapitalPerTrade) }
    var maxPositions by remember { mutableStateOf(settings.maxConcurrentPositions) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = "EXECUTION BROKER INTEGRATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(6.dp))

            // Broker Selector Row
            BrokerDestination.values().forEach { dest ->
                val isSel = selectedBroker == dest
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedBroker = dest
                            onUpdateSettings(settings.copy(brokerDestination = dest))
                        }
                        .background(
                            if (isSel) ElectricCyan.copy(alpha = 0.15f) else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSel) ElectricCyan else CardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = dest.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) ElectricCyan else TextPrimary)
                        Text(
                            text = when (dest) {
                                BrokerDestination.FRONTPAGE -> "India's #1 Virtual Trading Platform (FrontPage paper desk)"
                                BrokerDestination.IN_APP_PAPER -> "High-speed simulated margin engine (₹10 Lakhs)"
                                BrokerDestination.TRADINGVIEW_WEBHOOK -> "Dispatches webhook alerts for automated execution"
                                BrokerDestination.SENSIBULL_VIRTUAL -> "Sensibull Options & Futures paper trading desk"
                            },
                            fontSize = 8.5.sp,
                            color = TextSecondary
                        )
                    }

                    RadioButton(
                        selected = isSel,
                        onClick = {
                            selectedBroker = dest
                            onUpdateSettings(settings.copy(brokerDestination = dest))
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // FrontPage Specific Configuration
        if (selectedBroker == BrokerDestination.FRONTPAGE) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "FRONTPAGE API CREDENTIALS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter your Frontpage account auth token to sync orders directly with frontpage.app cloud. Leave blank to run on simulated FrontPage desk.",
                            fontSize = 8.5.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = tokenInput,
                            onValueChange = {
                                tokenInput = it
                                onUpdateSettings(settings.copy(frontpageUserToken = it))
                            },
                            label = { Text("FrontPage Bearer Token / API Key", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onTestBroker,
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(text = "Test FrontPage Bridge Connection", fontSize = 10.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }

                        if (brokerTestMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = brokerTestMessage, fontSize = 9.sp, color = NeonEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Webhook Configuration
        if (selectedBroker == BrokerDestination.TRADINGVIEW_WEBHOOK) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "CUSTOM WEBHOOK URL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = webhookInput,
                            onValueChange = {
                                webhookInput = it
                                onUpdateSettings(settings.copy(webhookUrl = it))
                            },
                            label = { Text("https://your-webhook-endpoint.com/order", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onTestBroker,
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(text = "Send Test Webhook Ping", fontSize = 10.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Risk Parameters
        item {
            Text(text = "AUTO-TRADE RISK CONTROLS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Min Reliability
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Minimum Pattern Reliability", fontSize = 9.5.sp, color = TextPrimary)
                        Text(text = "${minReliability.toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    }
                    Slider(
                        value = minReliability.toFloat(),
                        onValueChange = {
                            minReliability = it.toDouble()
                            onUpdateSettings(settings.copy(minReliabilityPct = minReliability))
                        },
                        valueRange = 75f..95f,
                        steps = 4,
                        colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                    )

                    // Capital Per Trade
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Max Capital Allocation / Trade", fontSize = 9.5.sp, color = TextPrimary)
                        Text(text = "₹${"%,.0f".format(maxCapital)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(25000.0, 50000.0, 100000.0).forEach { cap ->
                            val isC = maxCapital == cap
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        maxCapital = cap
                                        onUpdateSettings(settings.copy(maxCapitalPerTrade = cap))
                                    }
                                    .background(if (isC) NeonEmerald.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                                    .border(1.dp, if (isC) NeonEmerald else CardBorder, RoundedCornerShape(6.dp))
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "₹${(cap / 1000).toInt()}k", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isC) NeonEmerald else TextSecondary)
                            }
                        }
                    }

                    // Max Concurrent Positions
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Max Open Positions", fontSize = 9.5.sp, color = TextPrimary)
                        Text(text = "$maxPositions Trades", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalViolet)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(2, 4, 6, 8).forEach { count ->
                            val isP = maxPositions == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        maxPositions = count
                                        onUpdateSettings(settings.copy(maxConcurrentPositions = count))
                                    }
                                    .background(if (isP) RoyalViolet.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                                    .border(1.dp, if (isP) RoyalViolet else CardBorder, RoundedCornerShape(6.dp))
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$count", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isP) RoyalViolet else TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
