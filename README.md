# PieCalculator

Aplikasi kalkulator Android (Kotlin + Jetpack Compose) dengan tema **Neumorphism** hitam-oranye. Terdiri dari 3 modul:

1. **Kalkulator** — evaluasi ekspresi matematika (+ - × ÷ % ^, sin/cos/tan/sqrt/log/ln)
2. **Kurs Mata Uang** — konversi realtime via [frankfurter.app](https://www.frankfurter.app/) (gratis, tanpa API key, basis data ECB)
3. **Aljabar** — solve persamaan linear (`ax+b=c`) & kuadrat (`ax^2+bx+c=0`), serta simplifikasi ekspresi (gabung suku sejenis)

## Struktur Proyek

```
app/src/main/java/com/dylphiiee/piecalculator/
├── MainActivity.kt              # Entry point + bottom navigation
├── data/                        # Retrofit API (Frankfurter) & model
├── logic/                       # ExpressionEvaluator & AlgebraEngine (pure Kotlin, unit-testable)
├── ui/
│   ├── theme/                   # Color, Theme, Modifier.neumorphic()
│   ├── components/              # NeumorphicButton, NeumorphicPanel (reusable)
│   └── screens/                 # CalculatorScreen, CurrencyScreen, AlgebraScreen
└── viewmodel/                   # CalculatorViewModel, CurrencyViewModel, AlgebraViewModel
```

## Cara Menjalankan (Android Studio)

1. Buka folder ini di Android Studio (Hedgehog+).
2. Sync Gradle otomatis akan mengunduh dependency.
3. Jalankan di emulator/device (minSdk 24 / Android 7.0+).

## Build via Termux (tanpa Android Studio)

```bash
pkg install openjdk-17 gradle
cd PieCalculator
gradle assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Build Otomatis via GitHub Actions

Setiap push ke branch `main` (atau trigger manual lewat tab **Actions → Run workflow**) akan:
1. Build APK debug
2. Build APK release (unsigned)
3. Upload keduanya sebagai artifact — unduh dari halaman run workflow di GitHub.

> Release APK belum ditandatangani (unsigned). Untuk rilis Play Store, tambahkan keystore signing config di `app/build.gradle.kts` dan simpan kredensial sebagai GitHub Secrets.

## Pengujian Manual

- **Kalkulator:** coba `12*(3+4)^2`, `sqrt(144)`, `sin(30)` → hasil harus 588, 12, 0.5
- **Kurs:** pilih USD → IDR, masukkan jumlah, tekan Konversi (butuh koneksi internet)
- **Aljabar (Solve):** `2x+3=7` → x = 2; `x^2-5x+6=0` → x₁=3, x₂=2
- **Aljabar (Simplify):** `2x+3x-5+x^2` → `x^2 + 5x - 5`

## Batasan Saat Ini

- Aljabar hanya mendukung 1 variabel (`x`), pangkat maksimal 2, tanpa tanda kurung bersarang.
- Kurs mata uang terbatas pada daftar yang didukung ECB/frankfurter.app (tidak termasuk cryptocurrency).
- Ikon aplikasi memakai vector drawable sederhana — ganti dengan aset final via Android Studio Image Asset Studio bila diperlukan untuk rilis produksi.

## Saran Pengembangan Selanjutnya

- Tambahkan riwayat perhitungan (Room database) — konsisten dengan pola PieBoard.
- Cache kurs terakhir secara lokal agar bisa dipakai saat offline.
- Tambahkan mode scientific calculator penuh (faktorial, kombinasi/permutasi).
- Tanda tangani APK release otomatis di CI menggunakan GitHub Secrets.
