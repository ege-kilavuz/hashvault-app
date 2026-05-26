package com.hashvault.app.data.repository

import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.local.PreferencesManager
import com.hashvault.app.data.model.*

class WalletRepository(private val prefs: PreferencesManager) {
    private val api = ApiClient.api

    suspend fun getWalletStats(address: String): Result<WalletStatsResponse> = runCatching {
        api.getWalletStats(address)
    }

    suspend fun getWalletBlocks(address: String, poolType: String = "collective", page: Int = 0): Result<List<Block>> = runCatching {
        api.getWalletBlocks(address, poolType, page)
    }

    suspend fun getWalletPayments(address: String, page: Int = 0): Result<List<Payment>> = runCatching {
        api.getWalletPayments(address, page)
    }

    suspend fun getWalletRewards(address: String, page: Int = 0): Result<List<Reward>> = runCatching {
        api.getWalletRewards(address, page)
    }

    suspend fun getLastBlockIndex(): Long = prefs.getLastBlockIndex()

    suspend fun saveLastBlockIndex(index: Long) = prefs.saveLastBlockIndex(index)

    suspend fun getSavedAddress(): String? = prefs.getWalletAddress()

    suspend fun saveAddress(address: String) = prefs.saveWalletAddress(address)

    suspend fun getLatestBlockIndex(): Result<Long> = runCatching {
        val address = getSavedAddress() ?: return@runCatching 0L
        val blocks = api.getWalletBlocks(address, page = 0, limit = 1)
        blocks.firstOrNull()?.index ?: 0L
    }
}
