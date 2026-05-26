package com.hashvault.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashvault.app.ui.navigation.AppNavGraph
import com.hashvault.app.ui.navigation.Screen
import com.hashvault.app.ui.theme.HashVaultTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            installSplashScreen()
        } catch (_: Exception) {
            // SplashScreen API might not be available
        }
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (_: Exception) {
            // Edge-to-edge might fail on some devices
        }

        setContent {
            HashVaultTheme {
                HashVaultMain()
            }
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashVaultMain() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem(Screen.Home, androidx.compose.material.icons.Icons.Filled.Dashboard),
        BottomNavItem(Screen.Wallet, androidx.compose.material.icons.Icons.Filled.AccountBalanceWallet),
        BottomNavItem(Screen.Blocks, androidx.compose.material.icons.Icons.Filled.Inventory2),
        BottomNavItem(Screen.Pool, androidx.compose.material.icons.Icons.Filled.BarChart),
        BottomNavItem(Screen.Settings, androidx.compose.material.icons.Icons.Filled.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.screen.route
                    } == true

                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.screen.label) },
                        label = { Text(item.screen.label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
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
        AppNavGraph(
            navController = navController,
            onWalletCheck = { },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
