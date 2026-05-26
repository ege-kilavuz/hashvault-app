package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashvault.app.HashVaultApp
import com.hashvault.app.data.model.Block
import com.hashvault.app.data.model.Payment
import com.hashvault.app.ui.components.*
import androidx.navigation.NavController
import com.hashvault.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as HashVaultApp
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(app)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HashVault") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Wallet yoksa prompt
            if (uiState.walletAddress == null) {
                item {
                    Card(
                        onClick = { navController.navigate(Screen.Settings.route) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "No Wallet Configured",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter your Monero wallet address in Settings to track your mining stats.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Error
            uiState.error?.let { error ->
                item {
                    ErrorCard(message = error, onRetry = { viewModel.refresh() })
                }
            }

            // Balance
            uiState.walletStats?.collective?.let { collective ->
                item {
                    val balanceXmr = (collective.revenue?.confirmedBalance ?: 0L) / 1_000_000_000_000.0
                    val pendingXmr = (collective.revenue?.unconfirmedBalance?.collective?.total ?: 0L) / 1_000_000_000_000.0
                    BalanceCard(balanceXmr = balanceXmr, balanceUsd = null)
                    if (pendingXmr > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pending: %.6f XMR".format(pendingXmr),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Hashrate grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val walletHashrate = uiState.walletStats?.collective?.hashRate ?: 0L
                    val poolHashrate = uiState.poolStats?.pool?.collective?.hashRate ?: 0L

                    StatCard(
                        title = "Your Hashrate",
                        value = FormatHashrate(walletHashrate),
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading
                    )
                    StatCard(
                        title = "Pool Hashrate",
                        value = FormatHashrate(poolHashrate),
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading
                    )
                }
            }

            // Pool miners
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Miners",
                        value = (uiState.poolStats?.pool?.collective?.miners ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading
                    )
                    StatCard(
                        title = "Total Paid",
                        value = "%d XMR".format((uiState.poolStats?.pool?.payments?.totalMinersPaid ?: 0)),
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading
                    )
                }
            }

            // Network
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val net = uiState.poolStats?.network
                    StatCard(
                        title = "Network Height",
                        value = (net?.height ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading
                    )
                    StatCard(
                        title = "Network Diff",
                        value = "%.0f".format((net?.difficulty ?: 0).toDouble()),
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading
                    )
                }
            }

            // Last Blocks
            if (uiState.latestBlocks.isNotEmpty()) {
                item { SectionHeader("Latest Blocks") }
                items(uiState.latestBlocks) { block ->
                    BlockItem(block = block)
                }
            }

            // Last Payments
            if (uiState.latestPayments.isNotEmpty()) {
                item { SectionHeader("Latest Payments") }
                items(uiState.latestPayments) { payment ->
                    PaymentItem(payment = payment)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun BlockItem(block: Block) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Block #${block.height}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${block.poolType.uppercase()} · %.1f%% effort".format(block.effort),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = FormatXmr(block.value),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                TimestampText(timestampMs = block.ts)
            }
        }
    }
}

@Composable
private fun PaymentItem(payment: Payment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Payment #${payment.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${payment.payees} payees",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = FormatXmr(payment.value),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                TimestampText(timestampMs = payment.ts)
            }
        }
    }
}
