package com.hashvault.app.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hashvault.app.HashVaultApp
import com.hashvault.app.MainActivity

object NotificationHelper {
    private const val ID_BLOCK = 1001
    private const val ID_PAYMENT = 1002
    private const val ID_REWARD = 1003
    private const val ID_HASHRATE_DROP = 1004
    private const val ID_WORKER_OFFLINE = 1005

    private fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun createIntent(context: Context, navigateTo: String = "wallet"): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", navigateTo)
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** 🎉 New block found by your wallet */
    fun showBlockFound(context: Context, blockHeight: Long, rewardXmr: Double) {
        if (!canNotify(context)) return
        val pendingIntent = createIntent(context, "wallet")
        val notification = NotificationCompat.Builder(context, HashVaultApp.CHANNEL_BLOCK)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 New Block Found!")
            .setContentText("Block #$blockHeight — %.6f XMR".format(rewardXmr))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Block #$blockHeight mined by your wallet!\nReward: %.6f XMR".format(rewardXmr)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_BLOCK, notification)
    }

    /** 💰 New payment received */
    fun showPaymentReceived(context: Context, paymentId: Long, amountXmr: Double) {
        if (!canNotify(context)) return
        val pendingIntent = createIntent(context, "payments")
        val notification = NotificationCompat.Builder(context, HashVaultApp.CHANNEL_PAYMENT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💰 Payment Received")
            .setContentText("Payment #$paymentId — %.6f XMR".format(amountXmr))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("New payment received!\nPayment #$paymentId\nAmount: %.6f XMR".format(amountXmr)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_PAYMENT, notification)
    }

    /** ⛏️ New reward credited */
    fun showRewardCredited(context: Context, blockHeight: Long?, amountXmr: Double) {
        if (!canNotify(context)) return
        val pendingIntent = createIntent(context, "wallet")
        val blockText = if (blockHeight != null) "Block #$blockHeight" else "New reward"
        val notification = NotificationCompat.Builder(context, HashVaultApp.CHANNEL_REWARD)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⛏️ New Reward")
            .setContentText("$blockText — %.6f XMR".format(amountXmr))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_REWARD, notification)
    }

    /** ⚠️ Hashrate dropped significantly */
    fun showHashrateDrop(context: Context, oldHr: String, newHr: String, dropPercent: Double) {
        if (!canNotify(context)) return
        val pendingIntent = createIntent(context, "wallet")
        val notification = NotificationCompat.Builder(context, HashVaultApp.CHANNEL_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Hashrate Dropped")
            .setContentText("$oldHr → $newHr (%.1f%%)".format(dropPercent))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Your hashrate dropped significantly!\nBefore: $oldHr\nNow: $newHr\nDrop: %.1f%%".format(dropPercent)))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_HASHRATE_DROP, notification)
    }

    /** 🔴 Worker went offline */
    fun showWorkerOffline(context: Context, workerName: String) {
        if (!canNotify(context)) return
        val pendingIntent = createIntent(context, "wallet")
        val notification = NotificationCompat.Builder(context, HashVaultApp.CHANNEL_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🔴 Worker Offline")
            .setContentText("$workerName stopped submitting shares")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_WORKER_OFFLINE, notification)
    }
}
