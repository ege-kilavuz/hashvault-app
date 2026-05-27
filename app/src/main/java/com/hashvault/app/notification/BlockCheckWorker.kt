package com.hashvault.app.notification

import android.content.Context
import androidx.work.*
import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Background worker that periodically checks wallet data and fires
 * notifications for events the user opted into.
 */
class BlockCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val prefs = PreferencesManager(applicationContext)
                val address = prefs.getWalletAddress() ?: return@withContext Result.success()

                // Check each notification type only if enabled
                if (prefs.getNotifyBlock()) checkNewBlocks(prefs, address)
                if (prefs.getNotifyPayment()) checkNewPayments(prefs, address)
                if (prefs.getNotifyReward()) checkNewRewards(prefs, address)
                if (prefs.getNotifyHashrateDrop()) checkHashrateDrop(prefs, address)
                if (prefs.getNotifyWorkerOffline()) checkWorkerOffline(prefs, address)

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    /** 🎉 Check for newly found blocks */
    private suspend fun checkNewBlocks(prefs: PreferencesManager, address: String) {
        val lastKnownIndex = prefs.getLastBlockIndex()
        val blocks = ApiClient.api.getWalletBlocks(address, page = 0, limit = 1)
        val latestBlock = blocks.firstOrNull() ?: return

        if (lastKnownIndex > 0 && latestBlock.index > lastKnownIndex) {
            val reward = latestBlock.value / 1_000_000_000_000.0
            NotificationHelper.showBlockFound(applicationContext, latestBlock.height, reward)
        }
        prefs.saveLastBlockIndex(latestBlock.index)
    }

    /** 💰 Check for new payments */
    private suspend fun checkNewPayments(prefs: PreferencesManager, address: String) {
        val lastPaymentId = prefs.getLastPaymentId()
        val payments = ApiClient.api.getWalletPayments(address, page = 0, limit = 1)
        val latest = payments.firstOrNull() ?: return

        if (lastPaymentId > 0 && latest.id > lastPaymentId) {
            val amount = latest.value / 1_000_000_000_000.0
            NotificationHelper.showPaymentReceived(applicationContext, latest.id, amount)
        }
        prefs.saveLastPaymentId(latest.id)
    }

    /** ⛏️ Check for new rewards */
    private suspend fun checkNewRewards(prefs: PreferencesManager, address: String) {
        val lastTs = prefs.getLastRewardTs()
        val rewards = ApiClient.api.getWalletRewards(address, page = 0, limit = 1)
        val latest = rewards.firstOrNull() ?: return

        if (lastTs > 0 && latest.ts > lastTs) {
            val amount = latest.amt / 1_000_000_000_000.0
            NotificationHelper.showRewardCredited(applicationContext, latest.block, amount)
        }
        prefs.saveLastRewardTs(latest.ts)
    }

    /** ⚠️ Check for significant hashrate drop (>50%) */
    private suspend fun checkHashrateDrop(prefs: PreferencesManager, address: String) {
        val stats = runCatching {
            ApiClient.api.getWalletStats(address, workers = false, chart = false)
        }.getOrNull() ?: return

        val currentHr = stats.collective?.hashRate ?: return
        val lastHr = prefs.getLastHashrate()

        if (lastHr > 0 && currentHr < lastHr / 2) {
            val dropPct = (1.0 - currentHr.toDouble() / lastHr) * 100
            NotificationHelper.showHashrateDrop(
                applicationContext,
                formatHr(lastHr),
                formatHr(currentHr),
                dropPct
            )
        }
        prefs.saveLastHashrate(currentHr)
    }

    /** 🔴 Check for workers going offline */
    private suspend fun checkWorkerOffline(prefs: PreferencesManager, address: String) {
        val stats = runCatching {
            ApiClient.api.getWalletStats(address, workers = true, chart = false)
        }.getOrNull() ?: return

        val workers = stats.collective?.workers ?: return
        // Notify if a worker hasn't submitted a share in > 10 minutes
        val cutoff = System.currentTimeMillis() - 600_000
        for (worker in workers) {
            if (worker.lastShare > 0 && worker.lastShare < cutoff) {
                NotificationHelper.showWorkerOffline(applicationContext, worker.name)
            }
        }
    }

    private fun formatHr(hr: Long): String = when {
        hr >= 1_000_000_000 -> "%.2f GH/s".format(hr / 1_000_000_000.0)
        hr >= 1_000_000 -> "%.2f MH/s".format(hr / 1_000_000.0)
        hr >= 1_000 -> "%.2f kH/s".format(hr / 1_000.0)
        else -> "$hr H/s"
    }

    companion object {
        /**
         * Schedule background worker.
         * @param intervalVal interval in minutes if >= 15, or seconds if < 15
         */
        fun schedule(context: Context, intervalVal: Long = 15) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // WorkManager minimum is 15 minutes
            val effectiveMin = if (intervalVal < 15) 15L else intervalVal

            val request = PeriodicWorkRequestBuilder<BlockCheckWorker>(
                effectiveMin, TimeUnit.MINUTES
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
