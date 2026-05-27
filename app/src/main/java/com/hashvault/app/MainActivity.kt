@file:OptIn(ExperimentalMaterial3Api::class)

package com.hashvault.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashvault.app.ui.navigation.AppNavGraph
import com.hashvault.app.ui.navigation.Screen
import com.hashvault.app.ui.theme.HashVaultTheme
import kotlinx.coroutines.launch

// ====================================================================
// COMPOSITION LOCAL — provides strings throughout the app
// ====================================================================
val LocalStrings = compositionLocalOf { EnglishStrings() as AppStrings }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try { installSplashScreen() } catch (_: Exception) { }
        super.onCreate(savedInstanceState)
        try { enableEdgeToEdge() } catch (_: Exception) { }

        setContent {
            HashVaultTheme {
                val app = LocalContext.current.applicationContext as HashVaultApp
                val scope = rememberCoroutineScope()
                var lang by remember { mutableStateOf(AppLanguage.EN) }

                // Load saved language preference
                LaunchedEffect(Unit) {
                    val saved = app.prefs.getLanguage()
                    lang = saved
                }

                val strings = remember(lang) {
                    when (lang) {
                        AppLanguage.EN -> EnglishStrings()
                        AppLanguage.TR -> TurkishStrings()
                    }
                }

                CompositionLocalProvider(LocalStrings provides strings) {
                    HashVaultMain(
                        onLanguageChange = { newLang ->
                            lang = newLang
                            scope.launch { app.prefs.saveLanguage(newLang) }
                        }
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(val screen: Screen, val icon: ImageVector)

@Composable
fun HashVaultMain(onLanguageChange: (AppLanguage) -> Unit = {}) {
    val s = LocalStrings.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem(Screen.Home, Icons.Filled.Home),
        BottomNavItem(Screen.Wallet, Icons.Filled.AccountBalanceWallet),
        BottomNavItem(Screen.Blocks, Icons.Filled.Inventory2),
        BottomNavItem(Screen.Pool, Icons.Filled.BarChart),
        BottomNavItem(Screen.Guide, Icons.Filled.MenuBook),
        BottomNavItem(Screen.Settings, Icons.Filled.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.screen.route
                    } == true

                    val label = when (item.screen.route) {
                        Screen.Home.route -> s.navHome
                        Screen.Wallet.route -> s.navWallet
                        Screen.Blocks.route -> s.navBlocks
                        Screen.Pool.route -> s.navPool
                        Screen.Guide.route -> s.navGuide
                        Screen.Settings.route -> s.navSettings
                        else -> item.screen.label
                    }

                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = label) },
                        label = { Text(label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
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
            onLanguageChange = onLanguageChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
