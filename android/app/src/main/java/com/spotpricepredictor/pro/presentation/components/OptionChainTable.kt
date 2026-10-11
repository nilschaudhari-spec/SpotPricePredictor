package com.spotpricepredictor.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spotpricepredictor.pro.domain.model.OptionChainData
import com.spotpricepredictor.pro.domain.model.StrikeRowData
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun OptionChainTable(
    optionChain: OptionChainData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .background(CardBackground, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CALL OI",
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan
            )
            Text(
                text = "CALL LTP",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonEmerald
            )
            Text(
                text = "STRIKE",
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AmberWarning
            )
            Text(
                text = "PUT LTP",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CrimsonAlert
            )
            Text(
                text = "PUT OI",
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Table Rows
        Column(modifier = Modifier.fillMaxWidth()) {
            optionChain.strikes.forEach { row ->
                val isAtm = kotlin.math.abs(row.strikePrice - optionChain.spotPrice) <= 50.0
                OptionChainStrikeItem(
                    row = row,
                    isAtm = isAtm
                )
                Divider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun OptionChainStrikeItem(
    row: StrikeRowData,
    isAtm: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isAtm) RoyalViolet.copy(alpha = 0.2f) else Color.Transparent)
            .padding(vertical = 7.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatCompact(row.call.oi),
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = TextPrimary
        )
        Text(
            text = "₹${"%.1f".format(row.call.ltp)}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = NeonEmerald
        )
        Text(
            text = "${row.strikePrice.toInt()}",
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = if (isAtm) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isAtm) ElectricCyan else AmberWarning
        )
        Text(
            text = "₹${"%.1f".format(row.put.ltp)}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = CrimsonAlert
        )
        Text(
            text = formatCompact(row.put.oi),
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = TextPrimary
        )
    }
}

private fun formatCompact(number: Long): String {
    return when {
        number >= 10000000 -> "${"%.1f".format(number / 10000000.0)}Cr"
        number >= 100000 -> "${"%.1f".format(number / 100000.0)}L"
        number >= 1000 -> "${"%.1f".format(number / 1000.0)}k"
        else -> number.toString()
    }
}
