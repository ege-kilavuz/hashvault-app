package com.hashvault.app.data.model

import com.google.gson.annotations.SerializedName

// ===== Pool Stats =====
data class PoolStatsResponse(
    @SerializedName("pool_statistics") val pool: PoolStatistics,
    @SerializedName("network_statistics") val network: NetworkStatistics
)

data class PoolStatistics(
    val collective: PoolTypeStats,
    val solo: PoolTypeStats,
    val general: GeneralStats,
    val payments: PaymentStats
)

data class PoolTypeStats(
    val miners: Long,
    val hashRate: Long,
    @SerializedName("avg1hashRate") val avg1hHashRate: Long,
    @SerializedName("avg3hashRate") val avg3hHashRate: Long,
    @SerializedName("avg6hashRate") val avg6hHashRate: Long,
    @SerializedName("avg24hashRate") val avg24hHashRate: Long,
    val totalBlocksFound: Long,
    val orphans: Long,
    @SerializedName("lastFoundBlock") val lastFoundBlock: FoundBlock?
)

data class FoundBlock(
    val index: Long,
    val height: Long,
    val ts: Long,
    val hash: String,
    val value: Long,
    val poolType: String,
    @SerializedName("foundBy") val foundBy: String,
    val valid: Boolean,
    val effort: Double
)

data class GeneralStats(
    @SerializedName("last10blocksAvgReward") val last10AvgReward: Long
)

data class PaymentStats(
    @SerializedName("totalMinersPaid") val totalMinersPaid: Long,
    @SerializedName("totalTransactionsSent") val totalTxSent: Long
)

data class NetworkStatistics(
    val height: Long,
    val difficulty: Long,
    val value: Long,
    val ts: Long,
    val diff: DifficultyInfo
)

data class DifficultyInfo(
    @SerializedName("average6h") val avg6h: Double,
    @SerializedName("median6h") val median6h: Double,
    @SerializedName("average24h") val avg24h: Double,
    @SerializedName("median24h") val median24h: Double
)

// ===== Wallet Stats =====
data class WalletStatsResponse(
    val collective: WalletCollective?
)

data class WalletCollective(
    val hashRate: Long,
    @SerializedName("avg1hashRate") val avg1hHashRate: Long,
    @SerializedName("avg24hashRate") val avg24hHashRate: Long,
    val workers: List<Worker>?,
    val revenue: Revenue?,
    val payments: WalletPayments?
)

data class Revenue(
    @SerializedName("confirmedBalance") val confirmedBalance: Long,
    @SerializedName("totalPaid") val totalPaid: Long,
    @SerializedName("dailyPaid") val dailyPaid: Long?,
    @SerializedName("dailyCredited") val dailyCredited: Long?,
    @SerializedName("payoutThreshold") val payoutThreshold: Long?,
    @SerializedName("totalRewardsCredited") val totalRewardsCredited: Long?,
    @SerializedName("totalPaymentsSent") val totalPaymentsSent: Long?,
    @SerializedName("unconfirmedBalance") val unconfirmedBalance: UnconfirmedBalance?
)

data class UnconfirmedBalance(
    val collective: UnconfirmedDetail?,
    val solo: UnconfirmedDetail?
)

data class UnconfirmedDetail(
    val total: Long,
    val detail: Map<String, Long>?
)

data class WalletPayments(
    @SerializedName("totalTransactions") val totalTransactions: Long,
    @SerializedName("totalPaid") val totalPaid: Long
)

data class Worker(
    val name: String,
    val hashRate: Long,
    val lastShare: Long
)

// ===== Block =====
data class Block(
    val index: Long,
    val height: Long,
    val ts: Long,
    val hash: String,
    val value: Long,
    val poolType: String,
    @SerializedName("foundBy") val foundBy: String?,
    val effort: Double,
    @SerializedName("walletEffort") val walletEffort: Double?,
    val valid: Boolean
)

// ===== Payment =====
data class Payment(
    val id: Long,
    val hash: String,
    val value: Long,
    val fee: Long,
    val ts: Long,
    val payees: Int
)

// ===== Market =====
data class MarketStats(
    val price: Double?,
    @SerializedName("priceChange24h") val priceChange24h: Double?,
    @SerializedName("volume24h") val volume24h: Double?
)

// ===== Chart Data Point =====
data class ChartPoint(
    val ts: Long,
    val ch: Long?,
    val sh: Long?,
    val m: Long?
)

// ===== Top Miner =====
data class TopMiner(
    val name: String,
    val hashRate: Long,
    val validShares: Long,
    val invalidShares: Long
)

// ===== Pool Ports =====
data class PoolPorts(
    val global: List<PortEntry>,
    val pplns: List<PortEntry>,
    val solo: List<PortEntry>
)

data class PortEntry(
    val host: PortHost,
    val port: Int,
    val tls: Boolean,
    val difficulty: Long,
    val miners: Long,
    val description: String
)

data class PortHost(
    val hostname: String
)

// ===== Network Stats (minimal) =====
data class NetworkStats(
    val height: Long,
    val hash: String,
    val difficulty: Long,
    val value: Long,
    val ts: Long
)

// ===== Reward =====
data class Reward(
    val ts: Long,
    val amt: Long,
    val block: Long?,
    val blockHash: String?
)
