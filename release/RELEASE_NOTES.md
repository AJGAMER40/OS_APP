# ZeroVault v1.0.0 — Sanctuary Native Release

**ZeroVault** is an offline, hardware-anchored private data enclave for Android. Version 1.0.0 introduces the **Sanctuary** native Jetpack Compose visual system, bringing serene Romanesque architectural geometry, warm golden illumination, and deep evergreen surfaces while preserving zero-compromise hardware cryptography.

---

## 🔒 Security & Cryptographic Core
- **Hardware-Anchored Keymaster**: Hardware-backed AES-256-GCM MasterKey anchored in Android Keystore (StrongBox Keymaster where available, falling back to SoC TEE).
- **Stream Encryption**: Files encrypted via `EncryptedFile` using AES-256-GCM with chunked 4 KB HKDF key derivation.
- **Volatile Memory Sanitization**: Cryptographic keys, PIN characters, and decrypted RAM buffers are held exclusively in mutable primitive byte arrays and overwritten with `0x00` upon dialog dismissal, lock, or backgrounding (`onStop()`).
- **DoS & Brute-Force Rate Limiting**: 3 consecutive failed authentication attempts engage a 60-second hardware quarantine enforced across app restarts via `WorkManager`.
- **Physical Coercion / Decoy Vault**: Duress PIN `999999` routes strictly to an isolated safe decoy vault with complete data separation.
- **Network Isolation & Screen Protection**: Zero network permissions, `cleartextTrafficPermitted="false"`, `allowBackup="false"`, and `FLAG_SECURE` window protection against unauthorized task screenshots.

---

## 🏛️ Sanctuary Design System
- **Pure Native Jetpack Compose**: Built with Material 3 and custom Canvas vectors. Zero WebViews, zero cloud fonts, and zero remote assets.
- **Warm & Serene Palette**: Deep evergreen vault surfaces (`SanctuaryDeep`, `SanctuaryPine`), warm ivory introductory modes, and luminous golden arch geometry.
- **Accessible & Responsive**: 68dp touch targets on numeric keypads, full TalkBack semantics, high-contrast labels, and compatibility with compact 360dp phone viewports and system font scaling.

---

## 📦 Release Assets & Checksums

| File | Size | SHA-256 Checksum | Description |
| :--- | :--- | :--- | :--- |
| **`ZeroVault-v1.0.0.apk`** | 2.33 MB | `25F683DF37A76D2E7BAA9691D8873B490A46C82C33627C761A152978C59884A0` | **Optimized Production Release** (R8 minified & resource shrunk) |
| **`ZeroVault-v1.0.0-debug.apk`** | 19.4 MB | `CDB49FB3EDC6C2B6174B9A20ECC9E950E1E75BF1E0855DB69AAA4FB762856E75` | Debug build with inspection tools & debug symbols |

---

## 📲 Installation Instructions

1. Download **`ZeroVault-v1.0.0.apk`** to your Android device (Android 8.0 Oreo / API 26 or higher).
2. Open the downloaded `.apk` file from your device's file manager or notification shade.
3. If prompted by Android, grant temporary permission to **"Install unknown apps"** for your file manager.
4. Tap **Install** and launch **ZeroVault**.
5. Establish your 6-digit Master PIN to initialize your hardware enclave.
