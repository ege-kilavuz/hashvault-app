package com.hashvault.app.data.repository

import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.local.PreferencesManager
import com.hashvault.app.data.model.*

/**
 * Repository for wallet-level API calls.
 * All methods wrap in runCatching for safe error handling.
 */
class WalletRepository(private val prefs: PreferencesManager) {
    private val api = ApiClient.api

    /** Wallet statistics */
    suspend fun getWalletStats(address: String): Result<WalletStatsResponse> = runCatching {
        api.getWalletStats(address)
    }

    /** Wallet blocks found */
    suspend fun getWalletBlocks(
        address: String,
        poolType: String = "collective",
        page: Int = 0,
        limit: Int = 15
    ): Result<List<Block>> = runCatching {
        api.getWalletBlocks(address, poolType, page, limit)
    }

    /** Wallet payments received */
    suspend fun getWalletPayments(address: String, page: Int = 0, limit: Int = 15): Result<List<Payment>> = runCatching {
        api.getWalletPayments(address, page, limit)
    }

    /** Wallet rewards */
    suspend fun getWalletRewards(address: String, page: Int = 0, limit: Int = 15): Result<List<Reward>> = runCatching {
        api.getWalletRewards(address, page, limit)
    }

    /** Wallet top shares */
    suspend fun getWalletTopShares(address: String): Result<ShareResponse> = runCatching {
        api.getWalletTopShares(address)
    }

    // ====================================================================
    // LOCAL PREFERENCES
    // ====================================================================

    suspend fun getLastBlockIndex(): Long = prefs.getLastBlockIndex()

    suspend fun saveLastBlockIndex(index: Long) = prefs.saveLastBlockIndex(index)

    suspend fun getSavedAddress(): String? = prefs.getWalletAddress()

    suspend fun saveAddress(address: String) = prefs.saveWalletAddress(address)

    /** Get the latest block index for notification checking */
    suspend fun getLatestBlockIndex(): Result<Long> = runCatching {
        val address = getSavedAddress() ?: return@runCatching 0L
        val blocks = api.getWalletBlocks(address, page = 0, limit = 1)
        blocks.firstOrNull()?.index ?: 0L
    }
}
