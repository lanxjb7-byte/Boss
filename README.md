# LANXX GAME BOOSTER

Project Android Native Java + WebView JavaScript.

## Fitur
- Intro LANXX animated.
- Launcher Free Fire dan Free Fire MAX (menggunakan package ID Android umum).
- Floating overlay bubble.
- Crosshair visual overlay: warna, ukuran, dan posisi drag.
- Android settings shortcut.
- GitHub Actions build APK otomatis.

## Build APK di GitHub
1. Buat repository GitHub baru.
2. Upload seluruh isi folder project ini ke root repository (termasuk `.github/workflows/build.yml`).
3. Buka tab **Actions**.
4. Pilih workflow **Build LANXX GAME BOOSTER APK** lalu tekan **Run workflow**, atau push commit.
5. Setelah selesai, buka hasil workflow dan unduh artifact `LANXX-GAME-BOOSTER-APK`.
6. Ekstrak ZIP artifact untuk mendapatkan `app-debug.apk`.

## Instalasi dan izin
- Minimum Android 10 (API 29); compile/target SDK 35 untuk kompatibilitas Android 10–16.
- Saat pertama kali memakai overlay, tekan ENABLE FLOATING WINDOW dan izinkan "Display over other apps".
- Overlay dapat dibatasi oleh beberapa sistem operasi/vendor saat game fullscreen atau ketika aplikasi masuk background.

## Catatan
- Package ID launcher: `com.dts.freefireth` dan `com.dts.freefiremax`. Jika publisher mengubah ID pada versi tertentu, launcher perlu disesuaikan.
- Crosshair adalah elemen visual yang dapat dipindah, bukan alat bidik otomatis.
- Tidak ada macro, auto-tap, injeksi input, atau bypass anti-cheat.
- "Membuka aplikasi lain di dalam aplikasi/game" tidak dapat dijamin oleh Android; gunakan Recent Apps / split-screen jika perangkat mendukung.
- Logo project ditempatkan sebagai launcher icon dan asset intro.
