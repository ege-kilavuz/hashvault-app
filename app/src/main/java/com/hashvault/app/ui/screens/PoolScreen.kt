package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.model.PoolStatsResponse
import com.hashvault.app.data.model.PoolPorts
import com.hashvault.app.data.model.NetworkStats
import com.hashvault.app.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolScreen() {
    val scope = rememberCoroutineScope()
    var poolStats by remember { mutableStateOf<PoolStatsResponse?>(null) }
    var ports by remember { mutableStateOf<PoolPorts?>(null) }
    var networkStats by remember { mutableStateOf<NetworkStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        scope.launch {
            isLoading = true
            error = null
            runCatching { ApiClient.api.getPoolStats() }
                .onSuccess { poolStats = it }
                .onFailure { error = it.message }

            runCatching { ApiClient.api.getPorts() }
                .onSuccess { ports = it }

            runCatching { ApiClient.api.getNetworkStats() }
                .onSuccess { networkStats = it }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pool Stats") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (error != null) {
                item { ErrorCard(message = error!!, onRetry = { load() }) }
            }

            // Pool collective stats
            poolStats?.pool?.collective?.let { c ->
                item { SectionHeader("Collective Pool") }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Hashrate",
                            value = FormatHashrate(c.hashRate),
                            modifier = Modifier.weight(1f),
                            isLoading = isLoading
                        )
                        StatCard(
                            title = "Miners",
                            value = c.miners.toString(),
                            modifier = Modifier.weight(1f),
                            isLoading = isLoading
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "1h Avg",
                            value = FormatHashrate(c.avg1hHashRate),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "24h Avg",
                            value = FormatHashrate(c.avg24hHashRate),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Total Blocks",
                            value = c.totalBlocksFound.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Orphans",
                            value = c.orphans.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Solo pool stats
            poolStats?.pool?.solo?.let { s ->
                item { SectionHeader("Solo Pool") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Hashrate", value = FormatHashrate(s.hashRate), modifier = Modifier.weight(1f))
                        StatCard(title = "Miners", value = s.miners.toString(), modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Total Blocks", value = s.totalBlocksFound.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = "Orphans", value = s.orphans.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }

            // Network
            networkStats?.let { net ->
                item { SectionHeader("Monero Network") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Height", value = net.height.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = "Difficulty", value = "%.0f".format(net.difficulty.toDouble()), modifier = Modifier.weight(1f))
                    }
                }
            }

            // Network difficulty details from pool stats
            poolStats?.network?.diff?.let { diff ->
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Diff (6h avg)", value = "%.0f".format(diff.avg6h), modifier = Modifier.weight(1f))
                        StatCard(title = "Diff (24h avg)", value = "%.0f".format(diff.avg24h), modifier = Modifier.weight(1f))
                    }
                }
            }

            // Ports
            ports?.global?.let { globalPorts ->
                if (globalPorts.isNotEmpty()) {
                    item { SectionHeader("Pool Ports") }
                    items(globalPorts.size) { i ->
                        val port = globalPorts[i]
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = port.host.hostname + ":" + port.port,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                )
                                Text(
                                    text = port.description + (if (port.tls) " · TLS" else ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Pool Payments
            poolStats?.pool?.payments?.let { pmt ->
                item { SectionHeader("Pool Payments") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Miners Paid", value = pmt.totalMinersPaid.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = "Txs Sent", value = pmt.totalTxSent.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
