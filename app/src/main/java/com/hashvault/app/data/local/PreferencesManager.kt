package com.hashvault.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hashvault_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_WALLET_ADDRESS = stringPreferencesKey("wallet_address")
        private val KEY_LAST_BLOCK_INDEX = longPreferencesKey("last_block_index")
        private val KEY_CHECK_INTERVAL = longPreferencesKey("check_interval_minutes")
        private val KEY_NOTIFICATIONS_ENABLED = longPreferencesKey("notifications_enabled")
    }

    val walletAddressFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_WALLET_ADDRESS]
    }

    val lastBlockIndexFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_BLOCK_INDEX] ?: 0L
    }

    val checkIntervalFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_CHECK_INTERVAL] ?: 15L
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        (prefs[KEY_NOTIFICATIONS_ENABLED] ?: 1L) == 1L
    }

    suspend fun getWalletAddress(): String? {
        return context.dataStore.data.first()[KEY_WALLET_ADDRESS]
    }

    suspend fun saveWalletAddress(address: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_WALLET_ADDRESS] = address
        }
    }

    suspend fun getLastBlockIndex(): Long {
        return context.dataStore.data.first()[KEY_LAST_BLOCK_INDEX] ?: 0L
    }

    suspend fun saveLastBlockIndex(index: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_BLOCK_INDEX] = index
        }
    }

    suspend fun getCheckInterval(): Long {
        return context.dataStore.data.first()[KEY_CHECK_INTERVAL] ?: 15L
    }

    suspend fun saveCheckInterval(minutes: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CHECK_INTERVAL] = minutes
        }
    }

    suspend fun getNotificationsEnabled(): Boolean {
        return context.dataStore.data.first()[KEY_NOTIFICATIONS_ENABLED] ?: 1L == 1L
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] = if (enabled) 1L else 0L
        }
    }
}
