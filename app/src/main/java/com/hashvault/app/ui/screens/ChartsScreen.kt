@file:OptIn(ExperimentalMaterial3Api::class)

package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hashvault.app.LocalStrings
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.model.ChartPoint
import com.hashvault.app.data.model.DifficultyChartPoint
import com.hashvault.app.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen() {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()
    val api = ApiClient.api

    var hashrateChart by remember { mutableStateOf<List<ChartPoint>>(emptyList()) }
    var difficultyChart by remember { mutableStateOf<List<DifficultyChartPoint>>(emptyList()) }
    var period by remember { mutableStateOf("hourly") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        scope.launch {
            isLoading = true
            error = null
            runCatching { api.getHashrateChart(period) }
                .onSuccess { hashrateChart = it }
                .onFailure { error = it.message }
            runCatching { api.getDifficultyChart(period) }
                .onSuccess { difficultyChart = it }
                .onFailure { error = it.message }
            isLoading = false
        }
    }

    LaunchedEffect(period) { load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.chartsTitle) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val periodLabels = mapOf(
                        "hourly" to s.periodHourly,
                        "daily" to s.periodDaily,
                        "weekly" to s.periodWeekly,
                        "monthly" to s.periodMonthly
                    )
                    periodLabels.forEach { (key, label) ->
                        FilterChip(
                            selected = period == key,
                            onClick = { period = key },
                            label = { Text(label) }
                        )
                    }
                }
            }

            if (error != null) {
                item { ErrorCard(message = error!!) }
            }

            item { SectionHeader(s.poolHrMiners) }
            if (isLoading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            } else {
                hashrateChart.firstOrNull()?.let { latest ->
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(title = s.collectiveHr, value = FormatHashrate(latest.ch ?: 0), modifier = Modifier.weight(1f))
                            StatCard(title = s.soloHr, value = FormatHashrate(latest.sh ?: 0), modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(title = "Miners", value = (latest.m ?: 0).toString(), modifier = Modifier.weight(1f))
                            StatCard(title = "Workers", value = (latest.w ?: 0).toString(), modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (hashrateChart.isNotEmpty()) {
                    item { Text("${s.recentData} ${hashrateChart.size})", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    items(hashrateChart.take(20)) { point ->
                        ChartPointItem(point)
                    }
                }
            }

            item { SectionHeader(s.networkDifficulty) }
            if (isLoading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            } else {
                difficultyChart.firstOrNull()?.let { latest ->
                    item { StatCard(title = s.currentDifficulty, value = FormatCompact(latest.diff)) }
                }
                if (difficultyChart.isNotEmpty()) {
                    item { Text("${s.recentDifficulty} ${difficultyChart.size})", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    items(difficultyChart.take(20)) { point -> DifficultyPointItem(point) }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ChartPointItem(point: ChartPoint) {
    val tsText = java.text.SimpleDateFormat("dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(point.ts))
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(tsText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(80.dp))
            Text("CH: ${hashrateStr(point.ch ?: 0)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text("SH: ${hashrateStr(point.sh ?: 0)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text("M: ${point.m ?: 0}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DifficultyPointItem(point: DifficultyChartPoint) {
    val tsText = java.text.SimpleDateFormat("dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(point.ts))
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(tsText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("Diff: ${FormatCompact(point.diff)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun hashrateStr(hr: Long): String = when {
    hr >= 1_000_000_000 -> "%.2f GH/s".format(hr / 1_000_000_000.0)
    hr >= 1_000_000 -> "%.2f MH/s".format(hr / 1_000_000.0)
    hr >= 1_000 -> "%.2f kH/s".format(hr / 1_000.0)
    else -> "$hr H/s"
}
