package com.spotpricepredictor.pro.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spotpricepredictor.pro.data.preferences.TickerSettingsManager
import com.spotpricepredictor.pro.presentation.components.MarketTickerTape
import com.spotpricepredictor.pro.presentation.screens.history.HistoryScreen
import com.spotpricepredictor.pro.presentation.screens.history.HistoryViewModel
import com.spotpricepredictor.pro.presentation.screens.news.NewsScreen
import com.spotpricepredictor.pro.presentation.screens.news.NewsViewModel
import com.spotpricepredictor.pro.presentation.screens.optionchain.OptionChainScreen
import com.spotpricepredictor.pro.presentation.screens.optionchain.OptionChainViewModel
import com.spotpricepredictor.pro.presentation.screens.predictor.PredictorScreen
import com.spotpricepredictor.pro.presentation.screens.predictor.PredictorViewModel
import com.spotpricepredictor.pro.presentation.theme.*

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        Screen.Predictor,
        Screen.OptionChain,
        Screen.News,
        Screen.History
    )

    val context = LocalContext.current
    val tickerSettingsManager = remember { TickerSettingsManager(context) }
    val tickerViewModel: TickerViewModel = hiltViewModel()
    val tickerItems by tickerViewModel.tickerItems.collectAsState()

    Scaffold(
        topBar = {
            MarketTickerTape(
                tickerItems = tickerItems,
                tickerSettingsManager = tickerSettingsManager,
                onSelectSymbol = { _ ->
                    navController.navigate(Screen.Predictor.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepObsidian,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = CardBackground,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Predictor.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Predictor.route) {
                val viewModel: PredictorViewModel = hiltViewModel()
                PredictorScreen(viewModel = viewModel)
            }
            composable(Screen.OptionChain.route) {
                val viewModel: OptionChainViewModel = hiltViewModel()
                OptionChainScreen(viewModel = viewModel)
            }
            composable(Screen.News.route) {
                val viewModel: NewsViewModel = hiltViewModel()
                NewsScreen(viewModel = viewModel)
            }
            composable(Screen.History.route) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(viewModel = viewModel)
            }
        }
    }
}
