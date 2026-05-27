package com.hashvault.app.data.repository

import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.model.*

/**
 * Repository for pool-level API calls.
 * All methods wrap in runCatching for safe error handling.
 */
class PoolRepository {
    private val api = ApiClient.api

    /** Pool stats with embedded network, market, config */
    suspend fun getPoolStats(): Result<PoolStatsResponse> = runCatching {
        api.getPoolStats()
    }

    /** Pool blocks */
    suspend fun getPoolBlocks(poolType: String = "collective", page: Int = 0, limit: Int = 15): Result<List<Block>> = runCatching {
        api.getPoolBlocks(poolType, page, limit)
    }

    /** Pool payments */
    suspend fun getPoolPayments(page: Int = 0, limit: Int = 15): Result<List<Payment>> = runCatching {
        api.getPoolPayments(page, limit)
    }

    /** Top miners */
    suspend fun getTopMiners(): Result<TopMinersResponse> = runCatching {
        api.getTopMiners()
    }

    /** Pool ports */
    suspend fun getPorts(): Result<PoolPorts> = runCatching {
        api.getPorts()
    }

    /** Pool hashrate & miners chart */
    suspend fun getHashrateChart(period: String = "hourly"): Result<List<ChartPoint>> = runCatching {
        api.getHashrateChart(period)
    }

    /** Network stats */
    suspend fun getNetworkStats(): Result<NetworkStats> = runCatching {
        api.getNetworkStats()
    }

    /** Network difficulty chart */
    suspend fun getDifficultyChart(period: String = "hourly"): Result<List<DifficultyChartPoint>> = runCatching {
        api.getDifficultyChart(period)
    }

    /** Market stats */
    suspend fun getMarketStats(): Result<MarketStats> = runCatching {
        api.getMarketStats()
    }
}
