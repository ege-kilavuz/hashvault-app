package com.hashvault.app.data.api

import com.hashvault.app.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Complete HashVault.pro API v3 interface for Monero.
 * Base URL: https://api.hashvault.pro/v3/monero/
 *
 * All available endpoints documented and implemented.
 */
interface HashVaultApi {

    // ====================================================================
    // POOL ENDPOINTS
    // ====================================================================

    /** Pool stats — returns everything: pool stats, network, market, config, block template */
    @GET("pool/stats")
    suspend fun getPoolStats(): PoolStatsResponse

    /** Pool blocks */
    @GET("pool/blocks")
    suspend fun getPoolBlocks(
        @Query("pooltype") poolType: String = "collective",
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Block>

    /** Pool payments */
    @GET("pool/payments")
    suspend fun getPoolPayments(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Payment>

    /** Top miners — returns object with collective/solo arrays + stats */
    @GET("pool/topminers")
    suspend fun getTopMiners(): TopMinersResponse

    /** Pool ports (connection info for miners) */
    @GET("pool/ports")
    suspend fun getPorts(): PoolPorts

    /** Pool hashrate & miners chart
     * @param period hourly, daily, weekly, monthly
     */
    @GET("pool/chart/hashrateAndMiners")
    suspend fun getHashrateChart(
        @Query("period") period: String = "hourly"
    ): List<ChartPoint>

    // ====================================================================
    // NETWORK ENDPOINTS
    // ====================================================================

    /** Network stats */
    @GET("network/stats")
    suspend fun getNetworkStats(): NetworkStats

    /** Network difficulty chart
     * @param period hourly, daily, weekly, monthly
     */
    @GET("network/chart/difficulty")
    suspend fun getDifficultyChart(
        @Query("period") period: String = "hourly"
    ): List<DifficultyChartPoint>

    // ====================================================================
    // MARKET ENDPOINTS
    // ====================================================================

    /** Market stats for XMR */
    @GET("market/stats")
    suspend fun getMarketStats(): MarketStats

    // ====================================================================
    // WALLET ENDPOINTS
    // ====================================================================

    /** Wallet statistics
     * @param address Monero wallet address
     * @param period daily / weekly / monthly / all
     * @param workers include worker details
     * @param chart include chart data
     * @param inactivityThreshold minutes before worker marked inactive
     */
    @GET("wallet/{address}/stats")
    suspend fun getWalletStats(
        @Path("address") address: String,
        @Query("period") period: String = "daily",
        @Query("workers") workers: Boolean = true,
        @Query("chart") chart: Boolean = false,
        @Query("inactivityThreshold") inactivityThreshold: Int = 10
    ): WalletStatsResponse

    /** Wallet blocks found */
    @GET("wallet/{address}/blocks")
    suspend fun getWalletBlocks(
        @Path("address") address: String,
        @Query("pooltype") poolType: String = "collective",
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Block>

    /** Wallet payments received */
    @GET("wallet/{address}/payments")
    suspend fun getWalletPayments(
        @Path("address") address: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Payment>

    /** Wallet rewards breakdown */
    @GET("wallet/{address}/rewards")
    suspend fun getWalletRewards(
        @Path("address") address: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 15
    ): List<Reward>

    /** Wallet top shares */
    @GET("wallet/{address}/topshares")
    suspend fun getWalletTopShares(
        @Path("address") address: String
    ): ShareResponse
}
