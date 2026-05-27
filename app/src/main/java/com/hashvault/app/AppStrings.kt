package com.hashvault.app

/**
 * App-wide string localization.
 * Usage: val s = LocalStrings.current  →  s.wallet / s.poolStats etc.
 */

enum class AppLanguage { EN, TR }

/** Guide entry model used by both languages */

sealed class AppStrings(val lang: AppLanguage) {

    // ================================================================
    // NAVIGATION
    // ================================================================
    abstract val navHome: String
    abstract val navWallet: String
    abstract val navBlocks: String
    abstract val navPayments: String
    abstract val navPool: String
    abstract val navTopMiners: String
    abstract val navCharts: String
    abstract val navGuide: String
    abstract val navSettings: String

    // ================================================================
    // HOME / DASHBOARD
    // ================================================================
    abstract val homeTitle: String
    abstract val noWalletTitle: String
    abstract val noWalletDesc: String
    abstract val yourHashrate: String
    abstract val poolHashrate: String
    abstract val miners: String
    abstract val totalPaid: String
    abstract val networkHeight: String
    abstract val networkDiff: String
    abstract val latestBlocks: String
    abstract val latestPayments: String
    abstract val pending: String

    // ================================================================
    // WALLET
    // ================================================================
    abstract val walletTitle: String
    abstract val saveAddress: String
    abstract val walletLabel: String
    abstract val yourWallet: String
    abstract val currentHashrate: String
    abstract val avg24h: String
    abstract val workers: String
    abstract val recentBlocks: String
    abstract val recentPayments: String
    abstract val rewards: String
    abstract val topShares: String
    abstract val change: String
    abstract val cancel: String
    abstract val moneroAddress: String

    // ================================================================
    // POOL
    // ================================================================
    abstract val poolStats: String
    abstract val collectivePool: String
    abstract val soloPool: String
    abstract val hashrate: String
    abstract val totalBlocks: String
    abstract val orphans: String
    abstract val validShares: String
    abstract val effort: String
    abstract val blockValue: String
    abstract val poolConfig: String
    abstract val blockTemplate: String
    abstract val nextHeight: String
    abstract val nextDifficulty: String
    abstract val marketXmr: String
    abstract val priceUsd: String
    abstract val change24h: String
    abstract val marketCap: String
    abstract val volume24h: String
    abstract val poolPorts: String
    abstract val poolPayments: String
    abstract val minersPaid: String
    abstract val txsSent: String
    abstract val onlineNodes: String
    abstract val teamTopMiners: String
    abstract val teamCharts: String

    // ================================================================
    // SETTINGS
    // ================================================================
    abstract val settingsTitle: String
    abstract val secWallet: String
    abstract val secWalletDesc: String
    abstract val saveBtn: String
    abstract val secNotifications: String
    abstract val secNotifDesc: String
    abstract val notifBlock: String
    abstract val notifBlockDesc: String
    abstract val notifPayment: String
    abstract val notifPaymentDesc: String
    abstract val notifReward: String
    abstract val notifRewardDesc: String
    abstract val notifHashrateDrop: String
    abstract val notifHashrateDropDesc: String
    abstract val notifWorkerOffline: String
    abstract val notifWorkerOfflineDesc: String
    abstract val secCheckInterval: String
    abstract val everyMinutes: String
    abstract val minutes: String
    abstract val everyMinDesc: String
    abstract val seconds: String
    abstract val liveRefresh: String
    abstract val bgCheck: String
    abstract val secGuide: String
    abstract val secGuideDesc: String
    abstract val openGuide: String
    abstract val secAbout: String
    abstract val aboutName: String
    abstract val aboutDesc: String
    abstract val aboutData: String

    // ================================================================
    // GUIDE
    // ================================================================
    abstract val guideTitle: String
    abstract val guideHeader: String
    abstract val guideSubHeader: String
    abstract val guideFooter: String
    abstract val guideFooterText: String

    // ================================================================
    // GUIDE — FULL ENTRIES
    // ================================================================

    // ================================================================
    // GENERAL
    // ================================================================
    abstract val retry: String
    abstract val error: String
    abstract val loading: String
    abstract val collective: String
    abstract val solo: String
    abstract val mean: String
    abstract val median: String
    abstract val language: String
    abstract val turkish: String
    abstract val english: String

    // ================================================================
    // CHARTS
    // ================================================================
    abstract val chartsTitle: String
    abstract val periodHourly: String
    abstract val periodDaily: String
    abstract val periodWeekly: String
    abstract val periodMonthly: String
    abstract val poolHrMiners: String
    abstract val collectiveHr: String
    abstract val soloHr: String
    abstract val networkDifficulty: String
    abstract val currentDifficulty: String
    abstract val recentData: String
    abstract val recentDifficulty: String
}

// ====================================================================
// ENGLISH
// ====================================================================
class EnglishStrings : AppStrings(AppLanguage.EN) {
    override val navHome = "Dashboard"
    override val navWallet = "Wallet"
    override val navBlocks = "Blocks"
    override val navPayments = "Payments"
    override val navPool = "Pool"
    override val navTopMiners = "Top Miners"
    override val navCharts = "Charts"
    override val navGuide = "Guide"
    override val navSettings = "Settings"

    override val homeTitle = "HashVault"
    override val noWalletTitle = "No Wallet Configured"
    override val noWalletDesc = "Enter your Monero wallet address in Settings to track your mining stats."
    override val yourHashrate = "Your Hashrate"
    override val poolHashrate = "Pool Hashrate"
    override val miners = "Miners"
    override val totalPaid = "Total Paid"
    override val networkHeight = "Network Height"
    override val networkDiff = "Network Diff"
    override val latestBlocks = "Latest Blocks"
    override val latestPayments = "Latest Payments"
    override val pending = "Pending"

    override val walletTitle = "My Wallet"
    override val saveAddress = "Save Address"
    override val walletLabel = "Wallet"
    override val yourWallet = "Monero Wallet Address"
    override val currentHashrate = "Current Hashrate"
    override val avg24h = "24h Avg"
    override val workers = "Workers"
    override val recentBlocks = "Recent Blocks"
    override val recentPayments = "Recent Payments"
    override val rewards = "Rewards"
    override val topShares = "Top Shares"
    override val change = "Change"
    override val cancel = "Cancel"
    override val moneroAddress = "Monero Wallet Address"

    override val poolStats = "Pool Stats"
    override val collectivePool = "Collective Pool"
    override val soloPool = "Solo Pool"
    override val hashrate = "Hashrate"
    override val totalBlocks = "Total Blocks"
    override val orphans = "Orphans"
    override val validShares = "Valid Shares"
    override val effort = "Effort"
    override val blockValue = "Block Value"
    override val poolConfig = "Pool Configuration"
    override val blockTemplate = "Block Template"
    override val nextHeight = "Next Height"
    override val nextDifficulty = "Next Difficulty"
    override val marketXmr = "Market — XMR"
    override val priceUsd = "Price (USD)"
    override val change24h = "24h Change"
    override val marketCap = "Market Cap"
    override val volume24h = "24h Volume"
    override val poolPorts = "Pool Ports"
    override val poolPayments = "Pool Payments"
    override val minersPaid = "Miners Paid"
    override val txsSent = "Txs Sent"
    override val onlineNodes = "Online Nodes"
    override val teamTopMiners = "🏆 Top Miners"
    override val teamCharts = "📊 Charts"

    override val settingsTitle = "Settings"
    override val secWallet = "Monero Wallet"
    override val secWalletDesc = "Your mining wallet address"
    override val saveBtn = "Save"
    override val secNotifications = "Notifications"
    override val secNotifDesc = "Choose which events to get notified about"
    override val notifBlock = "🎉 Block Found"
    override val notifBlockDesc = "When your wallet finds a new block"
    override val notifPayment = "💰 Payment Received"
    override val notifPaymentDesc = "When you receive a payout from the pool"
    override val notifReward = "⛏️ New Reward"
    override val notifRewardDesc = "When a new mining reward is credited"
    override val notifHashrateDrop = "⚠️ Hashrate Drop"
    override val notifHashrateDropDesc = "When your hashrate drops more than 50%"
    override val notifWorkerOffline = "🔴 Worker Offline"
    override val notifWorkerOfflineDesc = "When a worker stops submitting shares for 10+ min"
    override val secCheckInterval = "Check Interval"
    override val everyMinutes = "Every"
    override val everyMinDesc = "Every"
    override val seconds = "s"
    override val liveRefresh = "Live Refresh (foreground)"
    override val bgCheck = "Background Check"
    override val minutes = "minutes"
    override val secGuide = "Learn"
    override val secGuideDesc = "Understand mining terms and how everything works"
    override val openGuide = "📖 Open Mining Guide"
    override val secAbout = "About"
    override val aboutName = "HashVault Monitor v1.0.0"
    override val aboutDesc = "Monero mining pool tracker for HashVault.pro"
    override val aboutData = "Data from api.hashvault.pro"

    override val guideTitle = "Mining Guide"
    override val guideHeader = "📖 HashVault Mining Guide"
    override val guideSubHeader = "Everything you need to understand your mining stats, terms, and how the pool works."
    override val guideFooter = "💡 Need more help?"
    override val guideFooterText = "Visit HashVault.pro or check the Monero documentation at getmonero.org"

    override val retry = "Retry"
    override val error = "Error"
    override val loading = "Loading"
    override val collective = "Collective"
    override val solo = "Solo"
    override val mean = "Mean HR"
    override val median = "Median HR"
    override val language = "Language"
    override val turkish = "Türkçe"
    override val english = "English"

    override val chartsTitle = "Charts"
    override val periodHourly = "Hourly"
    override val periodDaily = "Daily"
    override val periodWeekly = "Weekly"
    override val periodMonthly = "Monthly"
    override val poolHrMiners = "Pool Hashrate & Miners"
    override val collectiveHr = "Collective HR"
    override val soloHr = "Solo HR"
    override val networkDifficulty = "Network Difficulty"
    override val currentDifficulty = "Current Difficulty"
    override val recentData = "Recent data points (last"
    override val recentDifficulty = "Recent difficulty points (last"
}

// ====================================================================
// TURKISH
// ====================================================================
class TurkishStrings : AppStrings(AppLanguage.TR) {
    override val navHome = "Anasayfa"
    override val navWallet = "Cüzdan"
    override val navBlocks = "Bloklar"
    override val navPayments = "Ödemeler"
    override val navPool = "Havuz"
    override val navTopMiners = "Zirve Madenciler"
    override val navCharts = "Grafikler"
    override val navGuide = "Rehber"
    override val navSettings = "Ayarlar"

    override val homeTitle = "HashVault"
    override val noWalletTitle = "Cüzdan Ayarlanmamış"
    override val noWalletDesc = "Madencilik istatistiklerini görmek için Ayarlar'dan Monero cüzdan adresini gir."
    override val yourHashrate = "Senin Hashrate'in"
    override val poolHashrate = "Havuz Hashrate'i"
    override val miners = "Madenciler"
    override val totalPaid = "Toplam Ödenen"
    override val networkHeight = "Ağ Yüksekliği"
    override val networkDiff = "Ağ Zorluğu"
    override val latestBlocks = "Son Bloklar"
    override val latestPayments = "Son Ödemeler"
    override val pending = "Bekleyen"

    override val walletTitle = "Cüzdanım"
    override val saveAddress = "Adresi Kaydet"
    override val walletLabel = "Cüzdan"
    override val yourWallet = "Monero Cüzdan Adresi"
    override val currentHashrate = "Anlık Hashrate"
    override val avg24h = "24s Ort."
    override val workers = "İşçiler"
    override val recentBlocks = "Son Bloklar"
    override val recentPayments = "Son Ödemeler"
    override val rewards = "Ödüller"
    override val topShares = "En İyi Paylaşımlar"
    override val change = "Değiştir"
    override val cancel = "İptal"
    override val moneroAddress = "Monero Cüzdan Adresi"

    override val poolStats = "Havuz İstatistikleri"
    override val collectivePool = "Kolektif Havuz"
    override val soloPool = "Solo Havuz"
    override val hashrate = "Hashrate"
    override val totalBlocks = "Toplam Blok"
    override val orphans = "Yetimler"
    override val validShares = "Geçerli Pay"
    override val effort = "Efor"
    override val blockValue = "Blok Değeri"
    override val poolConfig = "Havuz Yapılandırması"
    override val blockTemplate = "Blok Şablonu"
    override val nextHeight = "Sonraki Yükseklik"
    override val nextDifficulty = "Sonraki Zorluk"
    override val marketXmr = "Piyasa — XMR"
    override val priceUsd = "Fiyat (USD)"
    override val change24h = "24s Değişim"
    override val marketCap = "Piyasa Değeri"
    override val volume24h = "24s Hacim"
    override val poolPorts = "Havuz Portları"
    override val poolPayments = "Havuz Ödemeleri"
    override val minersPaid = "Ödenen Madenci"
    override val txsSent = "Gönderilen İşlem"
    override val onlineNodes = "Çevrimiçi Düğümler"
    override val teamTopMiners = "🏆 Zirve Madenciler"
    override val teamCharts = "📊 Grafikler"

    override val settingsTitle = "Ayarlar"
    override val secWallet = "Monero Cüzdan"
    override val secWalletDesc = "Madencilik cüzdan adresin"
    override val saveBtn = "Kaydet"
    override val secNotifications = "Bildirimler"
    override val secNotifDesc = "Hangi olaylardan haberdar olmak istediğini seç"
    override val notifBlock = "🎉 Blok Bulundu"
    override val notifBlockDesc = "Cüzdanın yeni bir blok bulduğunda"
    override val notifPayment = "💰 Ödeme Alındı"
    override val notifPaymentDesc = "Havuzdan ödeme geldiğinde"
    override val notifReward = "⛏️ Yeni Ödül"
    override val notifRewardDesc = "Yeni madencilik ödülü kredilendiğinde"
    override val notifHashrateDrop = "⚠️ Hashrate Düşüşü"
    override val notifHashrateDropDesc = "Hashrate'in %50'den fazla düştüğünde"
    override val notifWorkerOffline = "🔴 İşçi Çevrimdışı"
    override val notifWorkerOfflineDesc = "Bir işçi 10+ dk pay göndermezse"
    override val everyMinDesc = "Her"
    override val seconds = "s"
    override val liveRefresh = "Anlık Güncelleme (ön planda)"
    override val bgCheck = "Arka Plan Kontrolü"
    override val secCheckInterval = "Kontrol Aralığı"
    override val everyMinutes = "Her"
    override val minutes = "dakikada bir"
    override val secGuide = "Öğren"
    override val secGuideDesc = "Madencilik terimlerini ve nasıl çalıştığını anla"
    override val openGuide = "📖 Madencilik Rehberi"
    override val secAbout = "Hakkında"
    override val aboutName = "HashVault Monitör v1.0.0"
    override val aboutDesc = "HashVault.pro için Monero madencilik havuzu takipçisi"
    override val aboutData = "Veri kaynağı: api.hashvault.pro"

    override val guideTitle = "Madencilik Rehberi"
    override val guideHeader = "📖 HashVault Madencilik Rehberi"
    override val guideSubHeader = "Madencilik istatistiklerini, terimleri ve havuzun nasıl çalıştığını anlamak için ihtiyacın olan her şey."
    override val guideFooter = "💡 Daha fazla yardım mı lazım?"
    override val guideFooterText = "HashVault.pro'yu ziyaret et veya getmonero.org'daki Monero dökümantasyonuna bak"

    override val retry = "Tekrar Dene"
    override val error = "Hata"
    override val loading = "Yükleniyor"
    override val collective = "Kolektif"
    override val solo = "Solo"
    override val mean = "Ort. HR"
    override val median = "Medyan HR"
    override val language = "Dil"
    override val turkish = "Türkçe"
    override val english = "English"

    override val chartsTitle = "Grafikler"
    override val periodHourly = "Saatlik"
    override val periodDaily = "Günlük"
    override val periodWeekly = "Haftalık"
    override val periodMonthly = "Aylık"
    override val poolHrMiners = "Havuz Hashrate & Madenciler"
    override val collectiveHr = "Kolektif HR"
    override val soloHr = "Solo HR"
    override val networkDifficulty = "Ağ Zorluğu"
    override val currentDifficulty = "Güncel Zorluk"
    override val recentData = "Son veri noktaları (son "
    override val recentDifficulty = "Son zorluk noktaları (son "
}
