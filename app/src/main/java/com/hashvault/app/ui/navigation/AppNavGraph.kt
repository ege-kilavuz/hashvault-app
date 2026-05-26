package com.hashvault.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hashvault.app.ui.screens.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onWalletCheck: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Wallet.route) {
            WalletScreen(onAddressChanged = onWalletCheck)
        }
        composable(Screen.Blocks.route) {
            BlocksScreen()
        }
        composable(Screen.Payments.route) {
            PaymentsScreen()
        }
        composable(Screen.Pool.route) {
            PoolScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
