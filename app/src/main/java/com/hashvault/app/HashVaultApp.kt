package com.hashvault.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.*
import com.hashvault.app.data.local.PreferencesManager
import com.hashvault.app.notification.BlockCheckWorker
import java.util.concurrent.TimeUnit

class HashVaultApp : Application() {
    lateinit var prefs: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager(this)
        createNotificationChannel()
        scheduleBlockCheck()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Block Found",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "New blocks found by your wallet"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleBlockCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<BlockCheckWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1, TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "block_check",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "hashvault_blocks"
    }
}
