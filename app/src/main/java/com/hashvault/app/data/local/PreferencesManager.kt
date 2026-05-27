package com.hashvault.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hashvault_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_WALLET_ADDRESS = stringPreferencesKey("wallet_address")
        private val KEY_LAST_BLOCK_INDEX = longPreferencesKey("last_block_index")
        private val KEY_LAST_PAYMENT_ID = longPreferencesKey("last_payment_id")
        private val KEY_LAST_REWARD_TS = longPreferencesKey("last_reward_ts")
        private val KEY_LAST_HASHRATE = longPreferencesKey("last_hashrate")
        private val KEY_CHECK_INTERVAL = longPreferencesKey("check_interval_minutes")

        // Individual notification toggles
        private val KEY_NOTIFY_BLOCK = stringPreferencesKey("notify_block")
        private val KEY_NOTIFY_PAYMENT = stringPreferencesKey("notify_payment")
        private val KEY_NOTIFY_REWARD = stringPreferencesKey("notify_reward")
        private val KEY_NOTIFY_HASHRATE_DROP = stringPreferencesKey("notify_hashrate_drop")
        private val KEY_NOTIFY_WORKER_OFFLINE = stringPreferencesKey("notify_worker_offline")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    // ================================================================
    // FLOWS
    // ================================================================

    val walletAddressFlow: Flow<String?> = context.dataStore.data.map { it[KEY_WALLET_ADDRESS] }

    val lastBlockIndexFlow: Flow<Long> = context.dataStore.data.map {
        it[KEY_LAST_BLOCK_INDEX] ?: 0L
    }

    val checkIntervalFlow: Flow<Long> = context.dataStore.data.map {
        it[KEY_CHECK_INTERVAL] ?: 15L
    }

    val notifyBlockFlow: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_NOTIFY_BLOCK] == "true"
    }

    val notifyPaymentFlow: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_NOTIFY_PAYMENT] == "true"
    }

    val notifyRewardFlow: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_NOTIFY_REWARD] == "true"
    }

    val notifyHashrateDropFlow: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_NOTIFY_HASHRATE_DROP] == "true"
    }

    val notifyWorkerOfflineFlow: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_NOTIFY_WORKER_OFFLINE] == "true"
    }

    // ================================================================
    // WALLET ADDRESS
    // ================================================================

    suspend fun getWalletAddress(): String? =
        context.dataStore.data.first()[KEY_WALLET_ADDRESS]

    suspend fun saveWalletAddress(address: String) {
        context.dataStore.edit { it[KEY_WALLET_ADDRESS] = address }
    }

    // ================================================================
    // BLOCK TRACKING
    // ================================================================

    suspend fun getLastBlockIndex(): Long =
        context.dataStore.data.first()[KEY_LAST_BLOCK_INDEX] ?: 0L

    suspend fun saveLastBlockIndex(index: Long) {
        context.dataStore.edit { it[KEY_LAST_BLOCK_INDEX] = index }
    }

    // ================================================================
    // PAYMENT TRACKING
    // ================================================================

    suspend fun getLastPaymentId(): Long =
        context.dataStore.data.first()[KEY_LAST_PAYMENT_ID] ?: 0L

    suspend fun saveLastPaymentId(id: Long) {
        context.dataStore.edit { it[KEY_LAST_PAYMENT_ID] = id }
    }

    // ================================================================
    // REWARD TRACKING
    // ================================================================

    suspend fun getLastRewardTs(): Long =
        context.dataStore.data.first()[KEY_LAST_REWARD_TS] ?: 0L

    suspend fun saveLastRewardTs(ts: Long) {
        context.dataStore.edit { it[KEY_LAST_REWARD_TS] = ts }
    }

    // ================================================================
    // HASHRATE TRACKING
    // ================================================================

    suspend fun getLastHashrate(): Long =
        context.dataStore.data.first()[KEY_LAST_HASHRATE] ?: 0L

    suspend fun saveLastHashrate(hr: Long) {
        context.dataStore.edit { it[KEY_LAST_HASHRATE] = hr }
    }

    // ================================================================
    // CHECK INTERVAL
    // ================================================================

    suspend fun getCheckInterval(): Long =
        context.dataStore.data.first()[KEY_CHECK_INTERVAL] ?: 15L

    suspend fun saveCheckInterval(minutes: Long) {
        context.dataStore.edit { it[KEY_CHECK_INTERVAL] = minutes }
    }

    // ================================================================
    // INDIVIDUAL NOTIFICATION TOGGLES
    // ================================================================

    suspend fun getNotifyBlock(): Boolean =
        context.dataStore.data.first()[KEY_NOTIFY_BLOCK] == "true"

    suspend fun saveNotifyBlock(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_BLOCK] = enabled.toString() }
    }

    suspend fun getNotifyPayment(): Boolean =
        context.dataStore.data.first()[KEY_NOTIFY_PAYMENT] == "true"

    suspend fun saveNotifyPayment(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_PAYMENT] = enabled.toString() }
    }

    suspend fun getNotifyReward(): Boolean =
        context.dataStore.data.first()[KEY_NOTIFY_REWARD] == "true"

    suspend fun saveNotifyReward(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_REWARD] = enabled.toString() }
    }

    suspend fun getNotifyHashrateDrop(): Boolean =
        context.dataStore.data.first()[KEY_NOTIFY_HASHRATE_DROP] == "true"

    suspend fun saveNotifyHashrateDrop(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_HASHRATE_DROP] = enabled.toString() }
    }

    suspend fun getNotifyWorkerOffline(): Boolean =
        context.dataStore.data.first()[KEY_NOTIFY_WORKER_OFFLINE] == "true"

    suspend fun saveNotifyWorkerOffline(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_WORKER_OFFLINE] = enabled.toString() }
    }

    // ================================================================
    // LANGUAGE
    // ================================================================

    suspend fun getLanguage(): com.hashvault.app.AppLanguage {
        val code = context.dataStore.data.first()[KEY_LANGUAGE]
        return if (code == "TR") com.hashvault.app.AppLanguage.TR else com.hashvault.app.AppLanguage.EN
    }

    suspend fun saveLanguage(lang: com.hashvault.app.AppLanguage) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang.name }
    }
}
