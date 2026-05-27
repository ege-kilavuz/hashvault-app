@file:OptIn(ExperimentalMaterial3Api::class)

package com.hashvault.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashvault.app.AppLanguage
import com.hashvault.app.LocalStrings

private data class GuideEntry(
    val title: String,
    val emoji: String,
    val shortDesc: String,
    val detail: String
)

private val enEntries = listOf(
    GuideEntry("Hashrate", "⚡", "Your mining speed — how many hashes per second your rig computes.",
        "• H/s = Hashes per second (lowest unit)\n• kH/s = 1,000 H/s\n• MH/s = 1,000,000 H/s\n• GH/s = 1,000,000,000 H/s\n\nHigher = faster = more chance to find a block.\nPool hashrate = combined power of ALL miners."),
    GuideEntry("Shares", "🎯", "Proof of work your miner submits to the pool.",
        "• Valid Share = accepted → earns reward\n• Invalid Share = rejected (bad config)\n• Stale Share = too late (block already found)\n\nShares = how pool divides rewards fairly.\nMore valid shares = bigger reward cut."),
    GuideEntry("Effort", "🎲", "How lucky you've been finding blocks vs. expectation.",
        "• 100% = exactly as expected (average luck)\n• 50% = twice as lucky\n• 200% = took twice as long\n\nEffort = (actual hashes / expected) × 100%\nOverall effort → 100% over long periods."),
    GuideEntry("PPLNS vs SOLO", "⛏️", "Two mining modes — how rewards are shared.",
        "PPLNS: Everyone shares rewards by recent shares, steady income.\nSOLO: You keep 100% of blocks YOU find, less frequent payouts."),
    GuideEntry("Block Reward", "💰", "The XMR you earn when a block is found.",
        "Current Monero block reward: ~0.6 XMR.\nIncludes block subsidy + tx fees.\nPool takes a small fee. Matures after 30 confirms (~1h)."),
    GuideEntry("Network Difficulty", "🌐", "How hard it is to find a block on the network.",
        "Adjusts every block based on total hashrate.\nHigher = harder. Goes up with more miners, down with less."),
    GuideEntry("Pool vs Wallet Hashrate", "📊", "Pool = everyone. Wallet = just your rigs.",
        "Pool Hashrate = all miners combined.\nWallet Hashrate = YOUR mining power.\nSudden drop? Check your miner!"),
    GuideEntry("Workers", "🖥️", "Individual mining rigs under your wallet.",
        "Each miner = different worker name.\nTrack each rig's performance.\nLast share >10 min ago = may be offline."),
    GuideEntry("Orphans", "👻", "Blocks found but not accepted by the network.",
        "Two miners find block at same time → one orphaned.\nOrphan rate <1%. Higher = latency issues."),
    GuideEntry("Payout Threshold", "💳", "Minimum balance before pool sends XMR.",
        "Default min: 0.001 XMR.\nWallet default: 0.1 XMR.\nAuto-paid when threshold reached. Pool processes hourly."),
    GuideEntry("Maturity Depth", "⏳", "Blocks before a reward becomes spendable.",
        "Monero needs 30 confirms (~1h).\nDuring this → \"maturing\" / pending.\nThen moves to confirmed balance."),
    GuideEntry("PPLNS Window", "🪟", "How far back pool looks for your reward.",
        "Current window: ~2,500 sec (~41 min).\nBased on last 41 min of shares.\nNew miners need ~41 min to build share."),
    GuideEntry("Round Hashes", "🔄", "Total hashes since the last block found.",
        "Round starts at block found, ends at next.\nCompared to expected = effort.\nShorter rounds = lucky!"),
    GuideEntry("Share Rate", "📈", "Shares your miner submits per second.",
        "Measured in shares/second (sh/s).\nHigher = more frequent.\nDrops to 0 = miner may have issues."),
    GuideEntry("Market Data", "📉", "Current XMR price and market stats.",
        "Price in USD/EUR/RUB + % change (24h/7d/14d/30d).\nMarket cap = total XMR value.\n24h volume = trading activity."),
    GuideEntry("Block Template", "🧱", "The next block the pool is working on.",
        "Height = next block number.\nDifficulty = how hard to solve.\nUpdates as new transactions arrive.")
)

private val trEntries = listOf(
    GuideEntry("Hashrate (İşlem Gücü)", "⚡", "Madencilik hızın — rig'in saniyede kaç hash hesapladığı.",
        "• H/s = Saniyede hash (en küçük birim)\n• kH/s = 1.000 H/s\n• MH/s = 1.000.000 H/s\n• GH/s = 1.000.000.000 H/s\n\nYüksek = hızlı = blok bulma şansı artar.\nHavuz hashrate'i = TÜM madencilerin toplam gücü."),
    GuideEntry("Paylaşımlar (Shares)", "🎯", "Madencinin havuza gönderdiği iş kanıtı.",
        "• Geçerli Pay = kabul edildi → ödül kazandırır\n• Geçersiz Pay = reddedildi (yanlış yapılandırma)\n• Bayat Pay = çok geç gönderildi (blok çözüldü)\n\nPaylaşımlar = havuzun ödülü adil bölüşme yöntemi."),
    GuideEntry("Efor (Effort)", "🎲", "Blok bulma şansının istatistiksel beklentiye oranı.",
        "• %100 = tam beklendiği gibi (ortalama şans)\n• %50 = iki kat şanslı\n• %200 = iki kat uzun sürdü\n\nEfor = (gerçek hash) / (beklenen hash) × %100\nUzun vadede %100'e yaklaşır."),
    GuideEntry("PPLNS vs SOLO", "⛏️", "İki madencilik modu — ödüller nasıl paylaşılır.",
        "PPLNS: Herkes son paylaşımlara göre ödül alır, düzenli gelir.\nSOLO: SADECE senin bulduğun blokların ödülü sana, seyrek ama büyük."),
    GuideEntry("Blok Ödülü", "💰", "Blok bulunca kazandığın XMR.",
        "Güncel Monero blok ödülü: ~0.6 XMR.\nBlok sübvansiyonu + işlem ücretlerini içerir.\n30 onay (~1 saat) sonra harcanabilir."),
    GuideEntry("Ağ Zorluğu (Difficulty)", "🌐", "Blok bulmanın ne kadar zor olduğu.",
        "Her blokta toplam ağ hashrate'ine göre ayarlanır.\nYüksek = zor. Madenci artışıyla yükselir, azalışıyla düşer."),
    GuideEntry("Havuz vs Cüzdan Hashrate'i", "📊", "Havuz = herkes. Cüzdan = sadece senin rig'lerin.",
        "Havuz Hashrate'i = tüm madencilerin toplamı.\nCüzdan Hashrate'i = SENİN gücün.\nAni düşüş? Madencini kontrol et!"),
    GuideEntry("İşçiler (Workers)", "🖥️", "Cüzdanına bağlı her bir madencilik makinesi.",
        "Her makinene farklı isim verebilirsin.\nHer rig'in performansını ayrı takip et.\nSon pay >10 dk önceyse çevrimdışı olabilir."),
    GuideEntry("Yetim Bloklar (Orphans)", "👻", "Bulunan ama ağ tarafından kabul edilmeyen bloklar.",
        "İki madenci aynı anda blok bulursa biri yetim kalır.\nYetim oranı <%1. Yüksekse = gecikme sorunu."),
    GuideEntry("Ödeme Eşiği (Payout Threshold)", "💳", "Havuzun XMR göndermesi için gereken minimum bakiye.",
        "Varsayılan min: 0.001 XMR.\nCüzdan varsayılan: 0.1 XMR.\nEşiğe ulaşınca otomatik ödenir. Havuz saat başı işler."),
    GuideEntry("Olgunlaşma Süresi (Maturity)", "⏳", "Ödülün harcanabilir olması için gereken blok sayısı.",
        "Monero 30 onay ister (~1 saat).\nBu sürede ödül \"olgunlaşıyor\" (pending).\nSonra onaylanmış bakiyeye geçer."),
    GuideEntry("PPLNS Penceresi", "🪟", "Havuzun ödül hesaplamasında geriye ne kadar baktığı.",
        "Güncel pencere: ~2.500 saniye (~41 dk).\nSon 41 dk'daki paylaşımlarına göre ödül.\nYeni madenci ~41 dk'da tam pay kazanır."),
    GuideEntry("Tur Hash'leri", "🔄", "Son bloktan beri hesaplanan toplam hash.",
        "Tur blok bulunca başlar, yenisi bulunca biter.\nBeklenenle karşılaştırılması = efor.\nKısa turlar = şanslısın!"),
    GuideEntry("Paylaşım Hızı (Share Rate)", "📈", "Madencinin saniyede gönderdiği paylaşım sayısı.",
        "Shares/second (sh/s) ile ölçülür.\nYüksek = daha sık gönderim.\nSıfıra düşerse madencinde sorun var."),
    GuideEntry("Piyasa Verileri", "📉", "Güncel XMR fiyatı ve piyasa istatistikleri.",
        "USD/EUR/RUB cinsinden fiyat + % değişim (24s/7g/14g/30g).\nPiyasa değeri = toplam XMR değeri.\n24s hacim = ne kadar XMR işlem görmüş."),
    GuideEntry("Blok Şablonu (Template)", "🧱", "Havuzun üzerinde çalıştığı bir sonraki blok.",
        "Yükseklik = sıradaki blok numarası.\nZorluk = çözülmesi ne kadar zor.\nYeni işlemler geldikçe güncellenir.")
)

@Composable
fun GuideScreen() {
    val s = LocalStrings.current
    val entries = if (s.lang == AppLanguage.TR) trEntries else enEntries

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.guideTitle) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = s.guideHeader, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = s.guideSubHeader, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            items(entries) { entry -> GuideCard(entry) }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = s.guideFooter, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = s.guideFooterText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun GuideCard(entry: GuideEntry) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (expanded) 0.4f else 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${entry.emoji}  ${entry.title}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = entry.shortDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = if (expanded) "▲" else "▼", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = entry.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
            }
        }
    }
}
