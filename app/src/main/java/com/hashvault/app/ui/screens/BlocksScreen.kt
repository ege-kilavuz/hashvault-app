@file:OptIn(ExperimentalMaterial3Api::class)

package com.hashvault.app.ui.screens

import com.hashvault.app.LocalStrings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.model.Block
import com.hashvault.app.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksScreen() {
    val scope = rememberCoroutineScope()
    var blocks by remember { mutableStateOf<List<Block>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var poolType by remember { mutableStateOf("collective") }

    fun loadBlocks() {
        scope.launch {
            isLoading = true
            error = null
            runCatching {
                ApiClient.api.getPoolBlocks(poolType = poolType, page = 0, limit = 20)
            }.onSuccess {
                blocks = it
            }.onFailure {
                error = it.message
            }
            isLoading = false
        }
    }

    LaunchedEffect(poolType) { loadBlocks() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blocks") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filter tabs
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = poolType == "collective",
                    onClick = { poolType = "collective" },
                    label = { Text("Collective") }
                )
                FilterChip(
                    selected = poolType == "solo",
                    onClick = { poolType = "solo" },
                    label = { Text("Solo") }
                )
                Spacer(modifier = Modifier.weight(1f))
                if (error != null) {
                    TextButton(onClick = { loadBlocks() }) { Text("Retry") }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                ErrorCard(message = error!!, onRetry = { loadBlocks() })
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(blocks) { block ->
                        BlockCard(block)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun BlockCard(block: Block) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#${block.height}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Effort: %.1f%%".format(block.effort),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = (block.poolType ?: "unknown").uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            block.foundBy?.let { finder ->
                Text(
                    text = "Found by: $finder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
