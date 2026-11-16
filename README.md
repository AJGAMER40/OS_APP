# ZeroVault: Secure Android File Vault

**ZeroVault** is an enterprise-grade Android security vault application written in **Kotlin** and **Jetpack Compose**, implementing Android OS security best practices, hardware-backed cryptography via the **Android Keystore**, authenticated chunked encryption with **AndroidX `EncryptedFile`**, multi-layered biometric authentication, and an automated **System Threat Monitor** mitigating brute-force and Denial-of-Service (DoS) attacks via **WorkManager**.

---

## Key Security Architecture

```
                                  +-------------------------------------------------------+
                                  |                     ZeroVault App                     |
                                  +-------------------------------------------------------+
                                                              |
                     +----------------------------------------+----------------------------------------+
                     |                                        |                                        |
                     v                                        v                                        v
+-----------------------------------------+  +--------------------------------+  +-----------------------------------------+
|        Android Keystore System          |  |   Multi-Layer Authentication   |  |          System Threat Monitor          |
|-----------------------------------------|  |--------------------------------|  |-----------------------------------------|
| • Hardware-backed MasterKey             |  | 1. BiometricPrompt (Primary)   |  | • Failed Attempt Counter (Max: 3)       |
| • AES-256 GCM (No Padding)              |  |    (Fingerprint / Face ID)     |  | • RAM Sanitizer (Zero-filling buffers)  |
| • StrongBox Keymaster / TEE             |  | 2. Master PIN Fallback         |  | • WorkManager 60s Quarantine Task       |
| • EncryptedFile (HKDF 4KB chunks)       |  |    (Salted + SHA-256 Stretched)|  | • Hard UI Lockdown & Forensic Log       |
| • EncryptedSharedPreferences (SIV+GCM)  |  | • Instant CharArray wiping     |  | • Tamper-resistant Persisted State      |
+-----------------------------------------+  +--------------------------------+  +-----------------------------------------+
```

---

## 1. Cryptographic System & Storage Security

### A. Android Keystore System (`KeystoreManager.kt`)
* Master Key is generated inside the hardware-isolated **Android Keystore (`AndroidKeyStore`)** with AES-256 GCM (`KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT`).
* Supports **StrongBox Keymaster** hardware security modules (HSM) on devices with dedicated hardware security chips (Pixel Titan M/M2, Knox Vault), automatically falling back to **Trusted Execution Environment (TEE)**.
* Cryptographic keys never leave the secure hardware boundary in cleartext.

### B. EncryptedFile API (`EncryptedFileManager.kt`)
* Implements `androidx.security.crypto.EncryptedFile` with `FileEncryptionScheme.AES256_GCM_HKDF_4KB`.
* Files selected by the user via Storage Access Framework (`OpenDocument`) are streamed directly from `ContentResolver` into the `EncryptedFile` output stream.
* Files are stored within internal scoped application storage (`context.filesDir/vault_storage`), inaccessible to third-party applications and unaffected by external storage permission changes.
* **Cryptographic Shred & Wipe**: File deletion performs a multi-pass overwrite (Pass 1: CSPRNG cryptographic random noise; Pass 2: Zeroes) before unlinking the inode from flash storage to prevent file system forensics.

### C. EncryptedSharedPreferences (`SecurePreferencesManager.kt`)
* Keys are encrypted using **AES-256 SIV** (Synthetic Initialization Vector) for deterministic key lookups without metadata leakage.
* Values are encrypted using **AES-256 GCM**.
* Stores salted PIN hashes, biometric flags, and vault metadata.

---

## 2. Multi-Layered Authentication Flow

```
+------------------------------------+
|            App Launch              |
+------------------------------------+
                  |
         [ Is PIN Configured? ]
         /                  \
       No                    Yes
       /                      \
+---------------+      [ Biometric Available? ]
| Setup 6-digit |       /                    \
|   Master PIN  |     Yes                     No
+---------------+     /                        \
                    v                            v
          +--------------------+        +--------------------+
          |   BiometricPrompt  |        |    Master PIN Pad  |
          | (Fingerprint/Face) |        |      (Fallback)    |
          +--------------------+        +--------------------+
                    |                            |
       [ Cancel / Negative Button ]              |
                    +----------------------------+
```

1. **Biometric Priority**: On devices with enrolled biometrics, `BiometricAuthManager` immediately initiates `BiometricPrompt` (`BIOMETRIC_STRONG or BIOMETRIC_WEAK`).
2. **Graceful PIN Fallback**: If biometrics are rejected, cancelled, or if the user clicks **"Use Master PIN"**, the app seamlessly falls back to the secure PIN pad.
3. **Anti-Dictionary PIN Protection**: The master PIN is salted with 16 bytes of secure randomness (`SecureRandom`) and hashed with 10,000 iterations of SHA-256 key stretching.
4. **Volatile Memory Wiping**: Entered PINs are stored as temporary `CharArray` buffers and immediately zeroed out (`\u0000`) following validation using `MemorySanitizer`.

---

## 3. System Threat Monitor (Brute-Force & DoS Mitigation)

### Threat Detection Logic (`ThreatMonitor.kt`)
* Tracks failed login attempts in real-time.
* Status Levels:
  * **SECURE**: 3 attempts remaining (Green).
  * **ELEVATED RISK**: 1-2 failed attempts (Amber/Crimson warnings displayed on UI).
  * **ATTACK DETECTED / LOCKDOWN**: 3 failed attempts (Red).

### Mitigation Protocol (On 3rd Failure)
1. **Emergency RAM Sanitization (`MemorySanitizer.purgeAll()`)**:
   * Instantly zero-fills all registered sensitive byte arrays (decrypted file buffers, intermediate cryptographic blocks, char buffers).
   * Calls `System.gc()` to purge references.
2. **WorkManager Background Defense (`ThreatMitigationWorker.kt`)**:
   * Enqueues a persistent `OneTimeWorkRequest` with `ExistingWorkPolicy.REPLACE`.
   * Enforces a 60-second isolation countdown.
   * WorkManager progress data is streamed back to the system.
3. **Anti-Tamper State Persistence**:
   * Stores `lockout_until_timestamp` in `EncryptedSharedPreferences`.
   * **Force-closing, killing, or rebooting the device does not bypass the quarantine**: Upon cold start, `ThreatMonitor.checkAndRestoreLockoutState()` recalculates remaining time and resumes the lockout screen.
4. **Interface Lockdown (`ThreatLockoutScreen.kt`)**:
   * All navigation intents, keypad inputs, and file operations are completely blocked.
   * Live circular countdown (60s) with real-time audit event terminal displaying forensic logs.

---

## 4. Android OS Security Hardening Checklist

| Security Control | Implementation | Purpose |
| :--- | :--- | :--- |
| **`FLAG_SECURE`** | `window.setFlags(FLAG_SECURE, FLAG_SECURE)` in `MainActivity` | Blocks screen recording, screenshots, and task switcher thumbnail leaks. |
| **No Cloud Backup** | `android:allowBackup="false"` & `data_extraction_rules.xml` | Prevents ADB backups and Google Drive cloud extraction of encrypted vaults. |
| **Cleartext Traffic Block** | `network_security_config.xml` (`cleartextTrafficPermitted="false"`) | Prevents unencrypted network socket vulnerabilities. |
| **Hardware Key Protection** | `AndroidKeyStore` + `KeyGenParameterSpec` (AES-256 GCM) | Keys generated and stored inside hardware TEE/StrongBox. |
| **Ephemeral File Preview** | `ActivePreview.wipe()` on dismiss and `onStop()` | Prevents memory dump forensic recovery of viewed files. |
| **Secure Shredding** | Two-pass overwrite (CSPRNG Noise + Zeroes) | Eliminates flash memory file recovery remnants. |
| **Scoped File Provider** | `androidx.core.content.FileProvider` (`cache-path: vault_temp/`) | Safe temporary content URI generation for file export. |

---

## Project Structure

```
ZeroVault/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/zerovault/app/
│       │   ├── MainActivity.kt                      # Main entrypoint with FLAG_SECURE
│       │   ├── ZeroVaultApplication.kt              # App lifecycle and security enclave initialization
│       │   ├── auth/
│       │   │   ├── AuthViewModel.kt                 # Multi-layered auth state machine
│       │   │   └── BiometricAuthManager.kt          # BiometricPrompt integration with fallback
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   └── VaultFileRecord.kt           # Encrypted file metadata model
│       │   │   └── security/
│       │   │       ├── EncryptedFileManager.kt      # AndroidX EncryptedFile AES-256-GCM HKDF 4KB
│       │   │       ├── KeystoreManager.kt           # Android Keystore StrongBox/TEE MasterKey
│       │   │       ├── MemorySanitizer.kt           # Zero-filling RAM buffer sanitizer
│       │   │       └── SecurePreferencesManager.kt  # EncryptedSharedPreferences (SIV + GCM)
│       │   ├── threat/
│       │   │   ├── ThreatLogEvent.kt                # Security audit log model
│       │   │   ├── ThreatMitigationWorker.kt        # WorkManager 60-second isolation worker
│       │   │   └── ThreatMonitor.kt                 # Brute-force & DoS attack detection
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── PinDotsView.kt               # Animated secure PIN indicators
│       │   │   │   ├── SecureKeypad.kt              # Tactile cybersecurity numeric keypad
│       │   │   │   └── ThreatBadge.kt               # Real-time threat posture pill badge
│       │   │   ├── dialogs/
│       │   │   │   ├── FilePreviewDialog.kt         # In-memory ephemeral viewer with zero-wipe
│       │   │   │   └── SecurityAuditDialog.kt       # System security audit & specs inspection
│       │   │   ├── screens/
│       │   │   │   ├── AuthScreen.kt                # Biometric & Master PIN authentication UI
│       │   │   │   ├── ThreatLockoutScreen.kt       # 60-second brute-force quarantine UI
│       │   │   │   └── VaultDashboardScreen.kt      # Encrypted files list, import, shred & stats
│       │   │   └── theme/
│       │   │       ├── Color.kt                     # Obsidian, Cyan, Emerald & Crimson palette
│       │   │       ├── Theme.kt                     # Jetpack Compose Material 3 theme
│       │   │       └── Type.kt                      # Monospace & Sans typography
│       │   └── vault/
│       │       └── VaultViewModel.kt                # Vault operations (encrypt, decrypt, shred, export)
│       └── res/
│           ├── drawable/ic_vault_shield.xml         # Vector icon for ZeroVault
│           ├── values/{colors.xml, strings.xml, themes.xml}
│           └── xml/{data_extraction_rules.xml, file_paths.xml, network_security_config.xml}
├── gradle/
│   ├── libs.versions.toml                           # Version catalog
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Build & Run Instructions

1. Open **Android Studio** (Koala, Ladybug, or 2024.1+).
2. Select **File > Open** and browse to `ZeroVault`.
3. Allow Gradle to sync dependencies (`androidx.security.crypto`, `androidx.biometric`, `androidx.work`, Jetpack Compose Material 3).
4. Run on an Android Device or Emulator running **Android 8.0 (API 26) through Android 15 (API 35)**.
   * *Tip for Emulator*: In emulator settings, configure fingerprint/biometric simulation under **Extended Controls > Fingerprint** to test multi-layer biometric authentication.
