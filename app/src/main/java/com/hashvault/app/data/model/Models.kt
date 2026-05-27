package com.hashvault.app.data.model

import com.google.gson.annotations.SerializedName

// ====================================================================
// POOL STATS (pool/stats — single response with everything bundled)
// ====================================================================
data class PoolStatsResponse(
    @SerializedName("pool_statistics") val pool: PoolStatistics,
    @SerializedName("network_statistics") val network: NetworkStatistics,
    @SerializedName("block_template") val blockTemplate: BlockTemplate?,
    val config: PoolConfig?,
    val market: MarketDetail?,
    @SerializedName("market_btc") val marketBtc: MarketDetail?,
    val online: Int? = null
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
    // Extended fields
    @SerializedName("shareRate") val shareRate: Long? = null,
    @SerializedName("lastShare") val lastShare: Long? = null,
    @SerializedName("roundHashes") val roundHashes: Long? = null,
    @SerializedName("currentEffort") val currentEffort: Double? = null,
    @SerializedName("dailyEffort") val dailyEffort: Double? = null,
    @SerializedName("monthlyEffort") val monthlyEffort: Double? = null,
    @SerializedName("overallEffort") val overallEffort: Double? = null,
    @SerializedName("totalHashes") val totalHashes: Long? = null,
    @SerializedName("validShares") val validShares: Long? = null,
    @SerializedName("invalidShares") val invalidShares: Long? = null,
    @SerializedName("staleShares") val staleShares: Long? = null,
    @SerializedName("totalBlocksFound") val totalBlocksFound: Long,
    @SerializedName("orphans") val orphans: Long,
    @SerializedName("maturingBlocks") val maturingBlocks: Long? = null,
    @SerializedName("lastFoundBlock") val lastFoundBlock: FoundBlock?,
    @SerializedName("pplnsWindowTime") val pplnsWindowTime: Long? = null
)

data class FoundBlock(
    val index: Long,
    val height: Long,
    val ts: Long,
    val hash: String,
    val value: Long,
    @SerializedName("poolType") val poolType: String? = null,
    @SerializedName("foundBy") val foundBy: String? = null,
    val valid: Boolean? = null,
    val effort: Double? = null,
    // Extended fields
    val diff: Long? = null,
    val hashes: Long? = null,
    @SerializedName("walletEffort") val walletEffort: Double? = null,
    @SerializedName("workerEffort") val workerEffort: Double? = null,
    val credited: Boolean? = null,
    val elapsed: Long? = null
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
    val hash: String? = null,
    val difficulty: Long,
    val value: Long,
    val ts: Long,
    val diff: DifficultyInfo?
)

data class DifficultyInfo(
    @SerializedName("average6h") val avg6h: Double? = null,
    @SerializedName("median6h") val median6h: Double? = null,
    @SerializedName("average24h") val avg24h: Double? = null,
    @SerializedName("median24h") val median24h: Double? = null,
    @SerializedName("average7d") val avg7d: Double? = null,
    @SerializedName("median7d") val median7d: Double? = null
)

// ====================================================================
// BLOCK TEMPLATE (from pool/stats)
// ====================================================================
data class BlockTemplate(
    val height: Long,
    val difficulty: Long,
    val ts: Long
)

// ====================================================================
// POOL CONFIG (from pool/stats)
// ====================================================================
data class PoolConfig(
    @SerializedName("pplns_fee") val pplnsFee: Double? = null,
    @SerializedName("sigDivisor") val sigDivisor: Long? = null,
    @SerializedName("coinDiffTarget") val coinDiffTarget: Long? = null,
    @SerializedName("shareTargetTime") val shareTargetTime: Long? = null,
    @SerializedName("hashRateModifier") val hashRateModifier: Long? = null,
    @SerializedName("min_wallet_payout") val minWalletPayout: Long? = null,
    @SerializedName("min_exchange_payout") val minExchangePayout: Long? = null,
    @SerializedName("maturity_depth") val maturityDepth: Long? = null,
    @SerializedName("min_denom") val minDenom: Long? = null,
    val payout: PayoutConfig? = null
)

data class PayoutConfig(
    val minimum: Long? = null,
    @SerializedName("defaultWallet") val defaultWallet: Long? = null,
    @SerializedName("defaultPaymentId") val defaultPaymentId: Long? = null
)

// ====================================================================
// MARKET DATA (from pool/stats)
// ====================================================================
data class MarketDetail(
    val rank: Long? = null,
    @SerializedName("price_btc") val priceBtc: Double? = null,
    @SerializedName("price_usd") val priceUsd: Double? = null,
    @SerializedName("price_eur") val priceEur: Double? = null,
    @SerializedName("price_rub") val priceRub: Double? = null,
    @SerializedName("market_cap_btc") val marketCapBtc: Double? = null,
    @SerializedName("market_cap_usd") val marketCapUsd: Double? = null,
    @SerializedName("market_cap_eur") val marketCapEur: Double? = null,
    @SerializedName("market_cap_rub") val marketCapRub: Double? = null,
    @SerializedName("percent_change_24h") val percentChange24h: Double? = null,
    @SerializedName("percent_change_7d") val percentChange7d: Double? = null,
    @SerializedName("percent_change_14d") val percentChange14d: Double? = null,
    @SerializedName("percent_change_30d") val percentChange30d: Double? = null,
    @SerializedName("24h_volume_btc") val volume24hBtc: Double? = null,
    @SerializedName("24h_volume_usd") val volume24hUsd: Double? = null,
    @SerializedName("24h_volume_eur") val volume24hEur: Double? = null,
    @SerializedName("24h_volume_rub") val volume24hRub: Double? = null,
    @SerializedName("last_updated") val lastUpdated: String? = null
)

// ====================================================================
// WALLET STATS
// ====================================================================
data class WalletStatsResponse(
    val collective: WalletCollective?,
    val solo: WalletSolo?,
    val revenue: Revenue?
)

data class WalletCollective(
    val hashRate: Long,
    @SerializedName("avg1hashRate") val avg1hHashRate: Long,
    @SerializedName("avg3hashRate") val avg3hHashRate: Long? = null,
    @SerializedName("avg6hashRate") val avg6hHashRate: Long? = null,
    @SerializedName("avg24hashRate") val avg24hHashRate: Long,
    val shareRate: Long? = null,
    val lastShare: Long? = null,
    val roundHashes: Long? = null,
    val totalHashes: Long? = null,
    @SerializedName("validShares") val validShares: Long? = null,
    @SerializedName("invalidShares") val invalidShares: Long? = null,
    @SerializedName("staleShares") val staleShares: Long? = null,
    @SerializedName("foundBlocks") val foundBlocks: Long? = null,
    @SerializedName("currentEffort") val currentEffort: Double? = null,
    val workers: List<Worker>? = null
)

data class WalletSolo(
    val hashRate: Long? = null,
    @SerializedName("avg1hashRate") val avg1hHashRate: Long? = null,
    @SerializedName("avg24hashRate") val avg24hHashRate: Long? = null,
    val validShares: Long? = null,
    @SerializedName("foundBlocks") val foundBlocks: Long? = null
)

data class Revenue(
    @SerializedName("confirmedBalance") val confirmedBalance: Long,
    @SerializedName("totalPaid") val totalPaid: Long,
    @SerializedName("dailyPaid") val dailyPaid: Long?,
    @SerializedName("dailyCredited") val dailyCredited: Long?,
    @SerializedName("payoutThreshold") val payoutThreshold: Long?,
    @SerializedName("totalRewardsCredited") val totalRewardsCredited: Long?,
    @SerializedName("totalPaymentsSent") val totalPaymentsSent: Long?,
    @SerializedName("unconfirmedBalance") val unconfirmedBalance: UnconfirmedBalance?,
    @SerializedName("auxConfirmedBalance") val auxConfirmedBalance: Long? = null,
    @SerializedName("auxUnconfirmedBalance") val auxUnconfirmedBalance: UnconfirmedBalance? = null
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

// ====================================================================
// BLOCK
// ====================================================================
data class Block(
    val index: Long,
    val height: Long,
    val ts: Long,
    val hash: String,
    val value: Long,
    val poolType: String? = null,
    @SerializedName("foundBy") val foundBy: String? = null,
    val effort: Double? = null,
    @SerializedName("walletEffort") val walletEffort: Double? = null,
    val valid: Boolean? = null,
    // Extended fields from pool blocks
    val diff: Long? = null,
    val hashes: Long? = null,
    @SerializedName("workerEffort") val workerEffort: Double? = null,
    val credited: Boolean? = null,
    val elapsed: Long? = null
)

// ====================================================================
// PAYMENT
// ====================================================================
data class Payment(
    val id: Long,
    val hash: String? = null,
    val value: Long,
    val fee: Long? = null,
    val ts: Long,
    val payees: Int? = null
)

// ====================================================================
// MARKET (standalone market/stats endpoint)
// ====================================================================
data class MarketStats(
    val price: Double?,
    @SerializedName("priceChange24h") val priceChange24h: Double?,
    @SerializedName("volume24h") val volume24h: Double?
)

// ====================================================================
// CHART DATA
// ====================================================================
data class ChartPoint(
    val ts: Long,
    val ch: Long? = null,
    val sh: Long? = null,
    val m: Long? = null,
    val w: Long? = null  // workers (from pool/chart/hashrateAndMiners)
)

// Difficulty chart point (network/chart/difficulty)
data class DifficultyChartPoint(
    val ts: Long,
    val diff: Long
)

// ====================================================================
// TOP MINER
// ====================================================================
/** Single miner entry inside pool/topminers response */
data class TopMinerEntry(
    val id: Long? = null,
    val wallet: String? = null,
    val hashRate: Long? = null,
    @SerializedName("currentEffort") val currentEffort: Double? = null,
    @SerializedName("shareRate") val shareRate: Long? = null,
    val workers: Int? = null,
    @SerializedName("lastShare") val lastShare: Long? = null
)

/** pool/topminers response — object with collective + solo arrays */
data class TopMinersResponse(
    val collective: List<TopMinerEntry> = emptyList(),
    val collectiveStats: MinerStats? = null,
    val solo: List<TopMinerEntry> = emptyList(),
    val soloStats: MinerStats? = null
)

data class MinerStats(
    val mean: Double? = null,
    val median: Double? = null,
    val variance: Double? = null,
    val deviation: Double? = null,
    @SerializedName("percentile90") val percentile90: Double? = null,
    @SerializedName("percentile95") val percentile95: Double? = null,
    @SerializedName("percentile99") val percentile99: Double? = null,
    val total: Int? = null
)

// ====================================================================
// POOL PORTS
// ====================================================================
data class PoolPorts(
    val global: List<PortEntry>,
    val pplns: List<PortEntry>,
    val solo: List<PortEntry>? = null
)

data class PortEntry(
    val host: PortHost,
    val port: Int,
    val tls: Boolean? = null,
    val difficulty: Long? = null,
    val miners: Long? = null,
    val description: String? = null
)

data class PortHost(
    val hostname: String,
    @SerializedName("blockID") val blockId: Long? = null,
    @SerializedName("blockIDTime") val blockIdTime: Long? = null
)

// ====================================================================
// NETWORK STATS (standalone)
// ====================================================================
data class NetworkStats(
    val height: Long,
    val hash: String? = null,
    val difficulty: Long,
    val value: Long? = null,
    val ts: Long? = null,
    val diff: DifficultyInfo? = null
)

// ====================================================================
// REWARD
// ====================================================================
data class Reward(
    val ts: Long,
    val amt: Long,
    val block: Long?,
    @SerializedName("blockHash") val blockHash: String?
)

// ====================================================================
// SHARE
// ====================================================================
data class ShareResponse(
    @SerializedName("collectiveRound") val collectiveRound: List<ShareEntry>? = null,
    @SerializedName("soloRound") val soloRound: List<ShareEntry>? = null,
    val overall: List<ShareEntry>? = null
)

data class ShareEntry(
    val index: Int? = null,
    val height: Long? = null,
    val worker: String? = null,
    @SerializedName("shareDifficulty") val shareDifficulty: Long,
    @SerializedName("blockDifficulty") val blockDifficulty: Long?,
    @SerializedName("targetDifficulty") val targetDifficulty: Long?
) {
    val targetRatio: Double
        get() = if (targetDifficulty != null && targetDifficulty > 0) 100.0 * shareDifficulty / targetDifficulty else 0.0
    val blockRatio: Double
        get() = if (blockDifficulty != null && blockDifficulty > 0) 100.0 * shareDifficulty / blockDifficulty else 0.0
}
