package com.zerovault.app

import android.app.Application
import android.util.Log
import com.zerovault.app.data.security.EncryptedFileManager
import com.zerovault.app.data.security.KeystoreManager
import com.zerovault.app.data.security.SecurePreferencesManager
import com.zerovault.app.threat.ThreatMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ZeroVaultApplication : Application() {

    companion object {
        private const val TAG = "ZeroVaultApp"
    }

    lateinit var keystoreManager: KeystoreManager
        private set
    lateinit var securePreferencesManager: SecurePreferencesManager
        private set
    lateinit var encryptedFileManager: EncryptedFileManager
        private set
    lateinit var threatMonitor: ThreatMonitor
        private set

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Initializing ZeroVault high-security enclave...")

        // 1. Initialize Keystore and Cryptographic subsystem
        keystoreManager = KeystoreManager(this)
        securePreferencesManager = SecurePreferencesManager(this, keystoreManager.masterKey)
        encryptedFileManager = EncryptedFileManager(this, keystoreManager.masterKey, securePreferencesManager)

        // 2. Initialize Threat Monitoring System
        threatMonitor = ThreatMonitor(this, securePreferencesManager)

        // 3. Purge any residual decrypted cache files from previous sessions
        CoroutineScope(Dispatchers.IO).launch {
            encryptedFileManager.clearTempFiles()
        }
    }
}
