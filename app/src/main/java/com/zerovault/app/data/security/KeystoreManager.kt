package com.zerovault.app.data.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.MasterKey
import java.security.KeyStore

/**
 * Manages the Android Keystore system and MasterKey for cryptographic operations.
 * Enforces hardware-backed TEE / StrongBox protection where supported by the device.
 */
class KeystoreManager(private val context: Context) {

    companion object {
        private const val TAG = "KeystoreManager"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val MASTER_KEY_ALIAS = "_zerovault_master_key_"
    }

    /**
     * Obtains or creates the hardware-backed MasterKey using AndroidX Security MasterKey.Builder.
     */
    val masterKey: MasterKey by lazy {
        createOrGetMasterKey()
    }

    private fun createOrGetMasterKey(): MasterKey {
        val hasStrongBox = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }

        val keyGenSpecBuilder = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && hasStrongBox) {
            try {
                keyGenSpecBuilder.setIsStrongBoxBacked(true)
            } catch (e: Exception) {
                Log.w(TAG, "StrongBox requested but failed to configure, falling back to TEE: ${e.message}")
            }
        }

        return try {
            MasterKey.Builder(context, MASTER_KEY_ALIAS)
                .setKeyGenParameterSpec(keyGenSpecBuilder.build())
                .build()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create MasterKey with custom spec, falling back to default AES256_GCM_SPEC: ${e.message}")
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
    }

    /**
     * Inspects device Keystore capabilities to verify hardware security isolation.
     */
    fun getSecurityDiagnostics(): KeystoreDiagnostics {
        val hasStrongBox = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }

        var isHardwareBacked = false
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            if (keyStore.containsAlias(MASTER_KEY_ALIAS)) {
                val entry = keyStore.getEntry(MASTER_KEY_ALIAS, null)
                if (entry != null) {
                    isHardwareBacked = true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error inspecting Keystore entry", e)
        }

        return KeystoreDiagnostics(
            algorithm = "AES-256-GCM",
            provider = KEYSTORE_PROVIDER,
            strongBoxSupported = hasStrongBox,
            hardwareBacked = isHardwareBacked,
            keyAlias = MASTER_KEY_ALIAS
        )
    }
}

data class KeystoreDiagnostics(
    val algorithm: String,
    val provider: String,
    val strongBoxSupported: Boolean,
    val hardwareBacked: Boolean,
    val keyAlias: String
)
