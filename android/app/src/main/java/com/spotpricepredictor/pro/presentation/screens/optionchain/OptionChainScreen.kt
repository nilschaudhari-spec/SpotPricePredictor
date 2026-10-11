package com.spotpricepredictor.pro.presentation.screens.optionchain

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.ExpiryInfo
import com.spotpricepredictor.pro.presentation.components.AddStockDialog
import com.spotpricepredictor.pro.presentation.components.DisclaimerBanner
import com.spotpricepredictor.pro.presentation.components.OptionChainTable
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun OptionChainScreen(
    viewModel: OptionChainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedSymbol by viewModel.selectedSymbol.collectAsState()
    val symbols by viewModel.symbols.collectAsState()
    val availableExpiries by viewModel.availableExpiries.collectAsState()
    val selectedExpiryDate by viewModel.selectedExpiryDate.collectAsState()
    val selectedDaysToExpiry by viewModel.selectedDaysToExpiry.collectAsState()

    var showAddStockDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showAddStockDialog) {
        AddStockDialog(
            onDismiss = { showAddStockDialog = false },
            onAddSymbol = { viewModel.addStock(it) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIVE OPTION CHAIN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Greeks, Open Interest, Expiries & Max Pain",
                    fontSize = 12.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { viewModel.loadOptionChain(selectedSymbol) },
                modifier = Modifier
                    .background(CardBackground, RoundedCornerShape(10.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = ElectricCyan)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Symbol selector chips + Add Stock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SELECT ASSET / INDEX",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "+ ADD STOCK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                modifier = Modifier.clickable { showAddStockDialog = true }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(symbols) { symbol ->
                val isSelected = symbol == selectedSymbol
                Box(
                    modifier = Modifier
                        .background(if (isSelected) ElectricCyan else CardBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, if (isSelected) ElectricCyan else CardBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.onSymbolSelected(symbol) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = symbol,
                        color = if (isSelected) DeepObsidian else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { showAddStockDialog = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ADD", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Expiry Date Selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "EXPIRY DATE SELECTION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        val expiriesList = if (availableExpiries.isNotEmpty()) {
            availableExpiries
        } else {
            listOf(
                ExpiryInfo("15-Oct-2026", 5.0, "Weekly"),
                ExpiryInfo("22-Oct-2026", 12.0, "Weekly"),
                ExpiryInfo("29-Oct-2026", 19.0, "Monthly"),
                ExpiryInfo("05-Nov-2026", 26.0, "Weekly"),
                ExpiryInfo("26-Nov-2026", 47.0, "Monthly")
            )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(expiriesList) { exp ->
                val isSelected = (selectedExpiryDate == exp.date) ||
                        (selectedDaysToExpiry == exp.daysToExpiry && selectedExpiryDate == null)
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) RoyalViolet else CardBackground,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) RoyalViolet else CardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onExpirySelected(exp) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = exp.date,
                            color = if (isSelected) androidx.compose.ui.graphics.Color.White else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${exp.daysToExpiry.toInt()} DTE • ${exp.type}",
                            color = if (isSelected) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f) else TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when (val state = uiState) {
            is OptionChainUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ElectricCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Fetching live Greeks & Open Interest...", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            is OptionChainUiState.Success -> {
                // Summary bar: Spot, PCR, Max Pain, Selected Expiry
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "LIVE SPOT", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(text = "₹${"%,.1f".format(state.data.spotPrice)}", fontSize = 14.sp, color = ElectricCyan, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text(text = "PCR", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(text = "%.2f".format(state.data.pcr), fontSize = 14.sp, color = NeonEmerald, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text(text = "MAX PAIN", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(text = "₹${state.data.maxPain.toInt()}", fontSize = 14.sp, color = AmberWarning, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text(text = "EXPIRY", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = state.data.selectedExpiryDate ?: "${state.data.selectedDaysToExpiry.toInt()}d",
                            fontSize = 13.sp,
                            color = RoyalViolet,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Option Chain Table
                OptionChainTable(optionChain = state.data)

                Spacer(modifier = Modifier.height(20.dp))
                DisclaimerBanner()
            }

            is OptionChainUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CrimsonAlert.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, CrimsonAlert, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Error: ${state.message}",
                            color = CrimsonAlert,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.loadOptionChain(selectedSymbol) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("RETRY", color = androidx.compose.ui.graphics.Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
