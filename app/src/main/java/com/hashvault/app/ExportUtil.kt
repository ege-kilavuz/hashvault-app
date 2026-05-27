package com.hashvault.app

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ExportUtil {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    data class ExportData(
        val exportTime: Long = System.currentTimeMillis(),
        val walletAddress: String? = null,
        val poolStats: Any? = null,
        val walletStats: Any? = null,
        val walletBlocks: Any? = null,
        val walletPayments: Any? = null,
        val walletRewards: Any? = null,
        val walletShares: Any? = null,
        val topMiners: Any? = null,
        val networkStats: Any? = null,
        val difficultyChart: Any? = null,
        val poolBlocks: Any? = null,
        val poolPayments: Any? = null,
        val ports: Any? = null
    )

    suspend fun exportToJson(context: Context): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val prefs = PreferencesManager(context)
            val address = prefs.getWalletAddress()
            val api = ApiClient.api

            // Fetch all data
            val poolStats = runCatching { api.getPoolStats() }.getOrNull()
            val walletStats = if (address != null) runCatching { api.getWalletStats(address) }.getOrNull() else null
            val walletBlocks = if (address != null) runCatching { api.getWalletBlocks(address, page = 0, limit = 10) }.getOrNull() else null
            val walletPayments = if (address != null) runCatching { api.getWalletPayments(address, page = 0, limit = 10) }.getOrNull() else null
            val walletRewards = if (address != null) runCatching { api.getWalletRewards(address, page = 0, limit = 10) }.getOrNull() else null
            val walletShares = if (address != null) runCatching { api.getWalletTopShares(address) }.getOrNull() else null
            val topMiners = runCatching { api.getTopMiners() }.getOrNull()
            val networkStats = runCatching { api.getNetworkStats() }.getOrNull()
            val difficultyChart = runCatching { api.getDifficultyChart() }.getOrNull()
            val poolBlocks = runCatching { api.getPoolBlocks(page = 0, limit = 10) }.getOrNull()
            val poolPayments = runCatching { api.getPoolPayments(page = 0, limit = 10) }.getOrNull()
            val ports = runCatching { api.getPorts() }.getOrNull()

            val data = ExportData(
                exportTime = System.currentTimeMillis(),
                walletAddress = address,
                poolStats = poolStats,
                walletStats = walletStats,
                walletBlocks = walletBlocks,
                walletPayments = walletPayments,
                walletRewards = walletRewards,
                walletShares = walletShares,
                topMiners = topMiners,
                networkStats = networkStats,
                difficultyChart = difficultyChart,
                poolBlocks = poolBlocks,
                poolPayments = poolPayments,
                ports = ports
            )

            val json = gson.toJson(data)

            // Save to Downloads
            val fileName = "HashVault_export_${java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())}.json"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create file")
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                    ?: throw Exception("Failed to write file")
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                dir.mkdirs()
                val file = File(dir, fileName)
                FileOutputStream(file).use { it.write(json.toByteArray()) }
            }

            fileName
        }
    }
}
