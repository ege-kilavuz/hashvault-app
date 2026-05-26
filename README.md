# HashVault Monitor

Android uygulaması ile [HashVault.pro](https://hashvault.pro) Monero mining pool'unu takip edin.

## Özellikler

- **Dashboard** — canlı hashrate, bakiye, pool istatistikleri
- **Wallet** — kazanç, ödemeler, worker listesi
- **Blocks** — collective/solo blok listesi
- **Payments** — ödeme geçmişi
- **Pool Stats** — pool, network ve port detayları
- **Notifications** — yeni blok bulunca bildirim

## Kurulum

APK'yı [Releases](https://github.com/egeuysalli/hashvault-app/releases) sayfasından indirin.

## Build

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/
```

## API

Data from [api.hashvault.pro/v3/monero](https://api.hashvault.pro/monero/api)
