package com.hashvault.app.data.repository

import com.hashvault.app.data.api.ApiClient
import com.hashvault.app.data.model.*

class PoolRepository {
    private val api = ApiClient.api

    suspend fun getPoolStats(): Result<PoolStatsResponse> = runCatching {
        api.getPoolStats()
    }

    suspend fun getPoolBlocks(poolType: String = "collective", page: Int = 0, limit: Int = 15): Result<List<Block>> = runCatching {
        api.getPoolBlocks(poolType, page, limit)
    }

    suspend fun getPoolPayments(page: Int = 0, limit: Int = 15): Result<List<Payment>> = runCatching {
        api.getPoolPayments(page, limit)
    }

    suspend fun getTopMiners(): Result<List<TopMiner>> = runCatching {
        api.getTopMiners()
    }

    suspend fun getPorts(): Result<PoolPorts> = runCatching {
        api.getPorts()
    }

    suspend fun getHashrateChart(period: String = "hourly"): Result<List<ChartPoint>> = runCatching {
        api.getHashrateChart(period)
    }

    suspend fun getNetworkStats(): Result<NetworkStats> = runCatching {
        api.getNetworkStats()
    }
}
