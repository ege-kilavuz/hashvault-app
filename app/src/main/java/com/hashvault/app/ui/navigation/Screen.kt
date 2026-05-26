package com.hashvault.app.ui.navigation

sealed class Screen(val route: String, val label: String, val icon: String) {
    data object Home : Screen("home", "Dashboard", "dashboard")
    data object Wallet : Screen("wallet", "Wallet", "account_balance_wallet")
    data object Blocks : Screen("blocks", "Blocks", "inventory_2")
    data object Payments : Screen("payments", "Payments", "payments")
    data object Pool : Screen("pool", "Pool", "bar_chart")
    data object Settings : Screen("settings", "Settings", "settings")
}
