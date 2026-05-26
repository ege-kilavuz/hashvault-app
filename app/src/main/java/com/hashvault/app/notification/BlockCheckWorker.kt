package com.hashvault.app.notification

import android.content.Context
import androidx.work.*
import com.hashvault.app.HashVaultApp
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class BlockCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val prefs = PreferencesManager(applicationContext)
                val address = prefs.getWalletAddress() ?: return@withContext Result.success()
                if (!prefs.getNotificationsEnabled()) return@withContext Result.success()

                val lastKnownIndex = prefs.getLastBlockIndex()
                val blocks = ApiClient.api.getWalletBlocks(address, page = 0, limit = 1)

                val latestIndex = blocks.firstOrNull()?.index ?: return@withContext Result.success()

                if (lastKnownIndex > 0 && latestIndex > lastKnownIndex) {
                    val block = blocks.first()
                    val rewardValue = block.value / 1_000_000_000_000.0
                    NotificationHelper.showBlockNotification(
                        applicationContext,
                        block.height,
                        "%.6f".format(rewardValue)
                    )
                }

                prefs.saveLastBlockIndex(latestIndex)
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    companion object {
        fun schedule(context: Context, intervalMinutes: Long = 15) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<BlockCheckWorker>(
                intervalMinutes, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "block_check",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
        }
    }
}
