package com.hashvault.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.hashvault.app.data.local.PreferencesManager

class HashVaultApp : Application() {
    lateinit var prefs: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Block found — high priority
            val blockChannel = NotificationChannel(
                CHANNEL_BLOCK,
                "Block Found",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "New blocks found by your wallet"
                enableVibration(true)
            }
            manager.createNotificationChannel(blockChannel)

            // Payments — default priority
            val paymentChannel = NotificationChannel(
                CHANNEL_PAYMENT,
                "Payments",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "New payments received"
                enableVibration(true)
            }
            manager.createNotificationChannel(paymentChannel)

            // Rewards — default priority
            val rewardChannel = NotificationChannel(
                CHANNEL_REWARD,
                "Rewards",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "New mining rewards credited"
            }
            manager.createNotificationChannel(rewardChannel)

            // Alerts (hashrate drop, worker offline) — low priority
            val alertChannel = NotificationChannel(
                CHANNEL_ALERT,
                "Mining Alerts",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hashrate drops, offline workers, and other alerts"
            }
            manager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        const val CHANNEL_BLOCK = "hashvault_blocks"
        const val CHANNEL_PAYMENT = "hashvault_payments"
        const val CHANNEL_REWARD = "hashvault_rewards"
        const val CHANNEL_ALERT = "hashvault_alerts"
    }
}
