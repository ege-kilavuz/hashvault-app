package com.hashvault.app.data.api

import com.hashvault.app.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HashVaultApi {

    // === Pool ===
    @GET("pool/stats")
    suspend fun getPoolStats(): PoolStatsResponse

    @GET("pool/blocks")
    suspend fun getPoolBlocks(
        @Query("pooltype") poolType: String = "collective",
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Block>

    @GET("pool/payments")
    suspend fun getPoolPayments(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Payment>

    @GET("pool/topminers")
    suspend fun getTopMiners(): List<TopMiner>

    @GET("pool/ports")
    suspend fun getPorts(): PoolPorts

    @GET("pool/chart/hashrateAndMiners")
    suspend fun getHashrateChart(
        @Query("period") period: String = "hourly"
    ): List<ChartPoint>

    // === Network ===
    @GET("network/stats")
    suspend fun getNetworkStats(): NetworkStats

    // === Market ===
    @GET("market/stats")
    suspend fun getMarketStats(): MarketStats

    // === Wallet ===
    @GET("wallet/{address}/stats")
    suspend fun getWalletStats(
        @Path("address") address: String,
        @Query("period") period: String = "daily",
        @Query("workers") workers: Boolean = true,
        @Query("chart") chart: Boolean = false,
        @Query("inactivityThreshold") inactivityThreshold: Int = 10
    ): WalletStatsResponse

    @GET("wallet/{address}/blocks")
    suspend fun getWalletBlocks(
        @Path("address") address: String,
        @Query("pooltype") poolType: String = "collective",
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Block>

    @GET("wallet/{address}/payments")
    suspend fun getWalletPayments(
        @Path("address") address: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Payment>

    @GET("wallet/{address}/rewards")
    suspend fun getWalletRewards(
        @Path("address") address: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Reward>
}
