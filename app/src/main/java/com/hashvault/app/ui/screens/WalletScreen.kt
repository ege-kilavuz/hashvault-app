package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashvault.app.HashVaultApp
import com.hashvault.app.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onAddressChanged: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as HashVaultApp
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(app)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddressInput by remember { mutableStateOf(false) }
    var addressInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wallet") },
                actions = {
                    TextButton(onClick = { showAddressInput = !showAddressInput }) {
                        Text(if (showAddressInput) "Cancel" else "Change")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Address input
            if (showAddressInput) {
                item {
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Monero Wallet Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (addressInput.isNotBlank()) {
                                viewModel.saveAddress(addressInput.trim())
                                onAddressChanged(addressInput.trim())
                                showAddressInput = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Address")
                    }
                }
            }

            // Wallet address display
            item {
                if (state.address.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Wallet",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${state.address.take(12)}...${state.address.takeLast(8)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Error
            state.error?.let { error ->
                item { ErrorCard(message = error) }
            }

            // Balance
            state.stats?.collective?.let { col ->
                item {
                    val confirmed = (col.revenue?.confirmedBalance ?: 0L) / 1_000_000_000_000.0
                    val pending = (col.revenue?.unconfirmedBalance?.collective?.total ?: 0L) / 1_000_000_000_000.0
                    val totalPaid = (col.revenue?.totalPaid ?: 0L) / 1_000_000_000_000.0

                    BalanceCard(balanceXmr = confirmed, balanceUsd = null)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Pending",
                            value = "%.6f".format(pending),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Paid",
                            value = "%.4f".format(totalPaid),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Hashrate
            state.stats?.collective?.let { col ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Current Hashrate",
                            value = FormatHashrate(col.hashRate),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "24h Avg",
                            value = FormatHashrate(col.avg24hHashRate),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Workers
            state.stats?.collective?.workers?.let { workers ->
                if (workers.isNotEmpty()) {
                    item {
                        SectionHeader("Workers")
                    }
                    items(workers) { worker ->
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = worker.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = FormatHashrate(worker.hashRate),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Blocks
            if (state.blocks.isNotEmpty()) {
                item { SectionHeader("Recent Blocks") }
                items(state.blocks.take(5)) { block ->
                    BlockItem(block)
                }
            }

            // Payments
            if (state.payments.isNotEmpty()) {
                item { SectionHeader("Recent Payments") }
                items(state.payments.take(5)) { payment ->
                    PaymentItem(payment)
                }
            }

            // Rewards
            if (state.rewards.isNotEmpty()) {
                item { SectionHeader("Rewards") }
            // Shares
            if (state.shares.isNotEmpty()) {
                item { SectionHeader("Top Shares") }
                items(state.shares) { share ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(share.blockHeight?.let { "#$it" } ?: "-", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Diff: ${share.shareDifficulty}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("%.1f%%".format(share.targetRatio ?: 0.0), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
                items(state.rewards.take(5)) { reward ->
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reward",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                reward.block?.let { height ->
                                    Text(
                                        text = "Block #$height",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = FormatXmr(reward.amt),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TimestampText(timestampMs = reward.ts)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun BlockItem(block: com.hashvault.app.data.model.Block) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
            Column(horizontalAlignment = Alignment.End) {
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
private fun PaymentItem(payment: com.hashvault.app.data.model.Payment) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
            Column(horizontalAlignment = Alignment.End) {
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
