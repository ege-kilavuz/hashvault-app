@file:OptIn(ExperimentalMaterial3Api::class)

package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hashvault.app.LocalStrings
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.model.TopMinerEntry
import com.hashvault.app.data.model.TopMinersResponse
import com.hashvault.app.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMinersScreen() {
    val scope = rememberCoroutineScope()
    var response by remember { mutableStateOf<TopMinersResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showSolo by remember { mutableStateOf(false) }

    fun load() {
        scope.launch {
            isLoading = true
            error = null
            runCatching { ApiClient.api.getTopMiners() }
                .onSuccess { response = it }
                .onFailure { error = it.message }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text(LocalStrings.current.navTopMiners) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    ErrorCard(message = error!!, onRetry = { load() })
                }
            } else {
                // Pool type toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !showSolo,
                        onClick = { showSolo = false },
                        label = { Text("Collective (${response?.collectiveStats?.total ?: 0})") }
                    )
                    FilterChip(
                        selected = showSolo,
                        onClick = { showSolo = true },
                        label = { Text("Solo (${response?.soloStats?.total ?: 0})") }
                    )
                }

                // Stats bar
                val stats = if (showSolo) response?.soloStats else response?.collectiveStats
                stats?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(title = "Mean HR", value = FormatHashrate(it.mean?.toLong() ?: 0), modifier = Modifier.weight(1f))
                        StatCard(title = "Median HR", value = FormatHashrate(it.median?.toLong() ?: 0), modifier = Modifier.weight(1f))
                        StatCard(title = "P99 HR", value = FormatHashrate(it.percentile99?.toLong() ?: 0), modifier = Modifier.weight(1f))
                    }
                }

                // Miners list
                val miners = if (showSolo) response?.solo ?: emptyList() else response?.collective ?: emptyList()
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(miners.size) { i ->
                        val miner = miners[i]
                        MinerCard(i + 1, miner)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MinerCard(rank: Int, miner: TopMinerEntry) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(40.dp)
            )
            // Wallet
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = miner.wallet?.let { "${it.take(10)}...${it.takeLast(6)}" } ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    miner.currentEffort?.let {
                        Text(
                            text = "Effort: %.1f%%".format(it),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    miner.workers?.let {
                        Text(
                            text = "$it workers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            // Hashrate
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FormatHashrate(miner.hashRate ?: 0),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                miner.shareRate?.let {
                    Text(
                        text = "$it sh/s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
