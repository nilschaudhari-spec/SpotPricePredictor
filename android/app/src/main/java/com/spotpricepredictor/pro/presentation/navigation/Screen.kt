package com.spotpricepredictor.pro.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Predictor : Screen("predictor", "Predictor", Icons.Default.TrendingUp)
    object OptionChain : Screen("option_chain", "Option Chain", Icons.Default.TableChart)
    object History : Screen("history", "History", Icons.Default.History)
}
