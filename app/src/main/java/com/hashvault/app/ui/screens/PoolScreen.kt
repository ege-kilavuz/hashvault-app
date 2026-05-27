package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hashvault.app.LocalStrings
import com.hashvault.app.data.model.*
import com.hashvault.app.data.repository.PoolRepository
import com.hashvault.app.ui.components.*
import com.hashvault.app.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolScreen(navController: NavController? = null) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()
    val repo = remember { PoolRepository() }
    var poolStats by remember { mutableStateOf<PoolStatsResponse?>(null) }
    var ports by remember { mutableStateOf<PoolPorts?>(null) }
    var networkStats by remember { mutableStateOf<NetworkStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        scope.launch {
            isLoading = true
            error = null
            repo.getPoolStats().onSuccess { poolStats = it }.onFailure { error = it.message }
            repo.getPorts().onSuccess { ports = it }
            repo.getNetworkStats().onSuccess { networkStats = it }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.poolStats) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick nav buttons
            if (navController != null) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { navController.navigate(Screen.TopMiners.route) }, modifier = Modifier.weight(1f)) {
                            Text(s.teamTopMiners)
                        }
                        OutlinedButton(onClick = { navController.navigate(Screen.Charts.route) }, modifier = Modifier.weight(1f)) {
                            Text(s.teamCharts)
                        }
                    }
                }
            }

            if (error != null) {
                item { ErrorCard(message = error!!, onRetry = { load() }) }
            }

            // ===== Collective Pool =====
            poolStats?.pool?.collective?.let { c ->
                item { SectionHeader(s.collectivePool) }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = s.hashrate, value = FormatHashrate(c.hashRate), modifier = Modifier.weight(1f), isLoading = isLoading)
                        StatCard(title = s.miners, value = c.miners.toString(), modifier = Modifier.weight(1f), isLoading = isLoading)
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "1h Avg", value = FormatHashrate(c.avg1hHashRate), modifier = Modifier.weight(1f))
                        StatCard(title = "24h Avg", value = FormatHashrate(c.avg24hHashRate), modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = s.totalBlocks, value = c.totalBlocksFound.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = s.orphans, value = c.orphans.toString(), modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Valid Shares", value = c.validShares?.let { FormatCompact(it) } ?: "-", modifier = Modifier.weight(1f))
                        StatCard(title = s.effort, value = c.currentEffort?.let { "%.1f%%".format(it) } ?: "-", modifier = Modifier.weight(1f))
                    }
                }
            }

            // ===== Solo Pool =====
            poolStats?.pool?.solo?.let { sPool ->
                item { SectionHeader(s.soloPool) }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = s.hashrate, value = FormatHashrate(sPool.hashRate), modifier = Modifier.weight(1f))
                        StatCard(title = s.miners, value = sPool.miners.toString(), modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = s.totalBlocks, value = sPool.totalBlocksFound.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = s.orphans, value = sPool.orphans.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }

            // ===== Network =====
            networkStats?.let { net ->
                item { SectionHeader("Monero Network") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = s.networkHeight, value = net.height.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = s.networkDiff, value = "%.0f".format(net.difficulty.toDouble()), modifier = Modifier.weight(1f))
                    }
                }
            }
            poolStats?.network?.diff?.let { diff ->
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        diff.avg6h?.let { StatCard(title = "Diff (6h avg)", value = "%.0f".format(it), modifier = Modifier.weight(1f)) }
                        diff.avg24h?.let { StatCard(title = "Diff (24h avg)", value = "%.0f".format(it), modifier = Modifier.weight(1f)) }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        diff.avg7d?.let { StatCard(title = "Diff (7d avg)", value = "%.0f".format(it), modifier = Modifier.weight(1f)) }
                        StatCard(title = "Block Value", value = FormatXmr(poolStats?.network?.value ?: 0L), modifier = Modifier.weight(1f))
                    }
                }
            }

            // ===== Config =====
            poolStats?.config?.let { cfg ->
                item { SectionHeader("Pool Config") }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            cfg.pplnsFee?.let { Text("PPLNS Fee: %.1f%%".format(it * 100), style = MaterialTheme.typography.bodySmall) }
                            cfg.minWalletPayout?.let { Text("Min Payout: ${FormatXmr(it)}", style = MaterialTheme.typography.bodySmall) }
                            cfg.maturityDepth?.let { Text("Maturity: $it blocks", style = MaterialTheme.typography.bodySmall) }
                            cfg.shareTargetTime?.let { Text("Share Target: ${it}s", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }

            // ===== Block Template =====
            poolStats?.blockTemplate?.let { bt ->
                item { SectionHeader("Block Template") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Next Height", value = bt.height.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = "Next Difficulty", value = "%.0f".format(bt.difficulty.toDouble()), modifier = Modifier.weight(1f))
                    }
                }
            }

            // ===== Market =====
            poolStats?.market?.let { mkt ->
                item { SectionHeader("Market — XMR") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Price (USD)", value = mkt.priceUsd?.let { "$${"%.2f".format(it)}" } ?: "-", modifier = Modifier.weight(1f))
                        StatCard(title = "24h Change", value = mkt.percentChange24h?.let { "%.2f%%".format(it) } ?: "-", modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Market Cap", value = mkt.marketCapUsd?.let { "$${FormatCompact(it.toLong())}" } ?: "-", modifier = Modifier.weight(1f))
                        StatCard(title = "24h Volume", value = mkt.volume24hUsd?.let { "$${FormatCompact(it.toLong())}" } ?: "-", modifier = Modifier.weight(1f))
                    }
                }
            }

            // ===== Ports =====
            ports?.global?.let { gp ->
                if (gp.isNotEmpty()) {
                    item { SectionHeader("Pool Ports") }
                    items(gp.size) { i ->
                        val port = gp[i]
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = port.host.hostname + ":" + port.port, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(text = (port.description ?: "") + (if (port.tls == true) " · TLS" else ""), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                port.miners?.let { Text(text = "$it miners", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                        }
                    }
                }
            }

            // ===== Payments Summary =====
            poolStats?.pool?.payments?.let { pmt ->
                item { SectionHeader("Pool Payments") }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Miners Paid", value = pmt.totalMinersPaid.toString(), modifier = Modifier.weight(1f))
                        StatCard(title = "Txns Sent", value = pmt.totalTxSent.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }

            // ===== Online =====
            poolStats?.online?.let { online ->
                if (online > 0) {
                    item { SectionHeader("Network") }
                    item { StatCard(title = "Online Nodes", value = online.toString()) }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
