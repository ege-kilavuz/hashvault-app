@file:OptIn(ExperimentalMaterial3Api::class)

package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hashvault.app.*
import com.hashvault.app.notification.BlockCheckWorker
import com.hashvault.app.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController? = null,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val app = context.applicationContext as HashVaultApp
    val prefs = app.prefs
    val scope = rememberCoroutineScope()

    var walletAddress by remember { mutableStateOf("") }
    var checkInterval by remember { mutableStateOf(15L) }

    // Notification toggles
    var notifyBlock by remember { mutableStateOf(true) }
    var notifyPayment by remember { mutableStateOf(true) }
    var notifyReward by remember { mutableStateOf(true) }
    var notifyHashrateDrop by remember { mutableStateOf(false) }
    var notifyWorkerOffline by remember { mutableStateOf(false) }

    var currentLang by remember { mutableStateOf(AppLanguage.EN) }

    LaunchedEffect(Unit) {
        walletAddress = prefs.getWalletAddress() ?: ""
        checkInterval = prefs.getCheckInterval()
        notifyBlock = prefs.getNotifyBlock()
        notifyPayment = prefs.getNotifyPayment()
        notifyReward = prefs.getNotifyReward()
        notifyHashrateDrop = prefs.getNotifyHashrateDrop()
        notifyWorkerOffline = prefs.getNotifyWorkerOffline()
        currentLang = prefs.getLanguage()
    }

    fun rescheduleWorker() {
        BlockCheckWorker.schedule(context, checkInterval)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.settingsTitle) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ============================================================
            // ⚙️ WALLET
            // ============================================================
            SectionCard(s.secWallet, s.secWalletDesc) {
                OutlinedTextField(
                    value = walletAddress,
                    onValueChange = { walletAddress = it },
                    label = { Text(s.yourWallet) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("4...") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            prefs.saveWalletAddress(walletAddress.trim())
                            rescheduleWorker()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(s.saveBtn)
                }
            }

            // ============================================================
            // 🌐 LANGUAGE
            // ============================================================
            SectionCard(s.language, "") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentLang == AppLanguage.TR,
                        onClick = {
                            currentLang = AppLanguage.TR
                            onLanguageChange(AppLanguage.TR)
                            scope.launch { prefs.saveLanguage(AppLanguage.TR) }
                        },
                        label = { Text(s.turkish) }
                    )
                    FilterChip(
                        selected = currentLang == AppLanguage.EN,
                        onClick = {
                            currentLang = AppLanguage.EN
                            onLanguageChange(AppLanguage.EN)
                            scope.launch { prefs.saveLanguage(AppLanguage.EN) }
                        },
                        label = { Text(s.english) }
                    )
                }
            }

            // ============================================================
            // 🔔 NOTIFICATIONS
            // ============================================================
            SectionCard(s.secNotifications, s.secNotifDesc) {
                NotifToggle(
                    title = s.notifBlock,
                    desc = s.notifBlockDesc,
                    checked = notifyBlock,
                    onCheck = {
                        notifyBlock = it
                        scope.launch { prefs.saveNotifyBlock(it) }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                NotifToggle(
                    title = s.notifPayment,
                    desc = s.notifPaymentDesc,
                    checked = notifyPayment,
                    onCheck = {
                        notifyPayment = it
                        scope.launch { prefs.saveNotifyPayment(it) }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                NotifToggle(
                    title = s.notifReward,
                    desc = s.notifRewardDesc,
                    checked = notifyReward,
                    onCheck = {
                        notifyReward = it
                        scope.launch { prefs.saveNotifyReward(it) }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                NotifToggle(
                    title = s.notifHashrateDrop,
                    desc = s.notifHashrateDropDesc,
                    checked = notifyHashrateDrop,
                    onCheck = {
                        notifyHashrateDrop = it
                        scope.launch { prefs.saveNotifyHashrateDrop(it) }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                NotifToggle(
                    title = s.notifWorkerOffline,
                    desc = s.notifWorkerOfflineDesc,
                    checked = notifyWorkerOffline,
                    onCheck = {
                        notifyWorkerOffline = it
                        scope.launch { prefs.saveNotifyWorkerOffline(it) }
                    }
                )
            }

            // ============================================================
            // ⏱️ CHECK INTERVAL
            // ============================================================
            SectionCard(s.secCheckInterval, "") {
                val label = when {
                    checkInterval >= 60 -> s.everyMinutes + " $checkInterval " + s.minutes
                    checkInterval > 0 -> s.everyMinDesc + " $checkInterval" + s.seconds
                    else -> "—"
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Live refresh (seconds)
                Text("Live Refresh (foreground)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(2L, 5L, 30L).forEach { sec ->
                        FilterChip(
                            selected = checkInterval == sec,
                            onClick = {
                                checkInterval = sec
                                scope.launch {
                                    prefs.saveCheckInterval(sec)
                                    rescheduleWorker()
                                }
                            },
                            label = { Text("${sec}s") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Background check (minutes)
                Text("Background Check", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15L, 30L, 60L).forEach { min ->
                        FilterChip(
                            selected = checkInterval == min,
                            onClick = {
                                checkInterval = min
                                scope.launch {
                                    prefs.saveCheckInterval(min)
                                    rescheduleWorker()
                                }
                            },
                            label = { Text("${min}m") }
                        )
                    }
                }
            }

            // ============================================================
            // 🔔 TEST NOTIFICATION
            // ============================================================
            SectionCard("Test Notification", "Send a test notification to verify it works") {
                Button(
                    onClick = {
                        scope.launch {
                            com.hashvault.app.notification.NotificationHelper.showBlockFound(context, 999999, 0.123456)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔔 Send Test Notification")
                }
            }

            // ============================================================
            // 📖 GUIDE
            // ============================================================
            if (navController != null) {
                SectionCard(s.secGuide, s.secGuideDesc) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Guide.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(s.openGuide)
                    }
                }
            }

            // ============================================================
            // ℹ️ ABOUT
            // ============================================================
            SectionCard(s.secAbout, "") {
                Text(
                    text = s.aboutName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = s.aboutDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = s.aboutData,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ====================================================================
// REUSABLE COMPONENTS
// ====================================================================

@Composable
private fun SectionCard(title: String, desc: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (desc.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun NotifToggle(
    title: String,
    desc: String,
    checked: Boolean,
    onCheck: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheck
        )
    }
}
