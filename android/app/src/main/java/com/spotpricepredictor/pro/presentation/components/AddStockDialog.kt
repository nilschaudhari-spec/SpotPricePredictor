package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotpricepredictor.pro.domain.model.StockCatalog
import com.spotpricepredictor.pro.domain.model.StockItem
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun AddStockDialog(
    onDismiss: () -> Unit,
    onAddSymbol: (String) -> Unit
) {
    var symbolInput by remember { mutableStateOf("") }
    var selectedExchangeTab by remember { mutableStateOf("ALL") }
    var selectedCategoryTab by remember { mutableStateOf("ALL") }
    val trimmedInput = symbolInput.trim()
    val isMinQueryReached = trimmedInput.isNotEmpty()

    val searchResults = remember(trimmedInput, selectedExchangeTab, selectedCategoryTab) {
        if (isMinQueryReached) {
            StockCatalog.search(trimmedInput, exchange = selectedExchangeTab)
        } else {
            val exFiltered = if (selectedExchangeTab == "ALL") {
                StockCatalog.STOCKS
            } else {
                StockCatalog.STOCKS.filter { it.exchange.equals(selectedExchangeTab, ignoreCase = true) }
            }

            if (selectedCategoryTab != "ALL") {
                when (selectedCategoryTab.uppercase()) {
                    "INDICES" -> exFiltered.filter { it.sector.contains("Index", ignoreCase = true) }
                    "COMMODITIES" -> exFiltered.filter {
                        it.sector.contains("Commodities", ignoreCase = true) ||
                        it.sector.contains("Precious Metals", ignoreCase = true) ||
                        it.sector.contains("Base Metals", ignoreCase = true) ||
                        it.sector.contains("Currency", ignoreCase = true) ||
                        it.sector.contains("Crypto", ignoreCase = true)
                    }
                    "BANKING" -> exFiltered.filter { it.sector.contains("Bank", ignoreCase = true) || it.sector.contains("Financial", ignoreCase = true) || it.sector.contains("NBFC", ignoreCase = true) }
                    "IT" -> exFiltered.filter { it.sector.contains("IT", ignoreCase = true) || it.sector.contains("Tech", ignoreCase = true) || it.sector.contains("Software", ignoreCase = true) }
                    "AUTO" -> exFiltered.filter { it.sector.contains("Auto", ignoreCase = true) }
                    "ENERGY" -> exFiltered.filter { it.sector.contains("Energy", ignoreCase = true) || it.sector.contains("Oil", ignoreCase = true) || it.sector.contains("Power", ignoreCase = true) || it.sector.contains("Gas", ignoreCase = true) }
                    "FMCG" -> exFiltered.filter { it.sector.contains("FMCG", ignoreCase = true) || it.sector.contains("Retail", ignoreCase = true) }
                    "METALS" -> exFiltered.filter { it.sector.contains("Metal", ignoreCase = true) || it.sector.contains("Mining", ignoreCase = true) }
                    else -> exFiltered
                }
            } else {
                exFiltered
            }
        }
    }


    fun submit() {
        val finalSymbol = trimmedInput.uppercase()
        if (finalSymbol.isNotBlank()) {
            onAddSymbol(finalSymbol)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElectricCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Add Stock",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Add Stock / Symbol",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Search NSE/BSE stocks & indices for AI price prediction",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search / Symbol Input Field with leading Search icon and clear button
                OutlinedTextField(
                    value = symbolInput,
                    onValueChange = { symbolInput = it },
                    label = { Text("Stock Symbol or Company Name", fontSize = 12.sp) },
                    placeholder = {
                        Text("Type company name or ticker (e.g. TAT, REL, INF, SBI)...", fontSize = 12.sp, color = TextMuted)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (symbolInput.isNotEmpty()) ElectricCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (symbolInput.isNotEmpty()) {
                            IconButton(onClick = { symbolInput = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Exchange Filter Row (ALL, NSE, BSE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXCHANGE:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    listOf("ALL" to "ALL", "NSE" to "🇮🇳 NSE", "BSE" to "🏛️ BSE").forEach { (code, label) ->
                        val isExSelected = code.equals(selectedExchangeTab, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clickable { selectedExchangeTab = code }
                                .background(
                                    if (isExSelected) ElectricCyan.copy(alpha = 0.25f) else SurfaceDark,
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isExSelected) ElectricCyan else CardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isExSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isExSelected) ElectricCyan else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Sector / Category Filter Chips
                val categories = listOf("ALL", "INDICES", "COMMODITIES", "BANKING", "IT", "AUTO", "ENERGY", "FMCG", "METALS")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    items(categories) { cat ->
                        val isSelected = cat.equals(selectedCategoryTab, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clickable { selectedCategoryTab = cat }
                                .background(
                                    if (isSelected) ElectricCyan.copy(alpha = 0.2f) else SurfaceDark,
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else CardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) ElectricCyan else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Result List Section
                if (searchResults.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMinQueryReached) "MATCHING STOCKS (${searchResults.size})" else "AVAILABLE SCRIPTS (${searchResults.size})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "TAP TO SELECT & ADD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(searchResults, key = { it.symbol }) { stock ->
                            StockResultItem(
                                stock = stock,
                                onSelect = {
                                    onAddSymbol(stock.symbol)
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else {
                    // Empty match state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark, RoundedCornerShape(10.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "No catalog match for \"$trimmedInput\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "You can still add \"${trimmedInput.uppercase()}\" as a custom symbol below.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { submit() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(8.dp),
                        enabled = trimmedInput.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = DeepObsidian,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (trimmedInput.isNotBlank()) "ADD ${trimmedInput.uppercase()}" else "ADD STOCK",
                            color = DeepObsidian,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StockResultItem(
    stock: StockItem,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stock.symbol,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )

                    Box(
                        modifier = Modifier
                            .background(CardBackground, RoundedCornerShape(4.dp))
                            .border(0.5.dp, CardBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stock.exchange,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CardBackground, RoundedCornerShape(4.dp))
                            .border(0.5.dp, CardBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stock.sector,
                            fontSize = 9.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stock.name,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 1-Click Quick Add Badge
            Box(
                modifier = Modifier
                    .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = ElectricCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "ADD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
            }
        }
    }
}
