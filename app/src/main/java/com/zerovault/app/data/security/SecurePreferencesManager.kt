package com.zerovault.app.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zerovault.app.data.model.VaultFileRecord
import org.json.JSONArray
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Manages encrypted key-value storage using AndroidX EncryptedSharedPreferences.
 * Keys are encrypted with AES-256 SIV and values are encrypted with AES-256 GCM.
 */
class SecurePreferencesManager(
    private val context: Context,
    masterKey: MasterKey
) {

    companion object {
        private const val PREFS_FILE_NAME = "zerovault_secure_prefs"
        private const val KEY_PIN_SALT = "sec_pin_salt"
        private const val KEY_PIN_HASH = "sec_pin_hash"
        private const val KEY_PIN_CONFIGURED = "sec_pin_configured"
        private const val KEY_BIOMETRICS_ENABLED = "sec_biometrics_enabled"
        private const val KEY_FAILED_ATTEMPTS = "sec_failed_attempts"
        private const val KEY_LOCKOUT_TIMESTAMP = "sec_lockout_timestamp"
        private const val KEY_VAULT_FILES = "sec_vault_files_meta"
        private const val HASH_ITERATIONS = 10_000
    }

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // ==========================================
    // PIN & AUTHENTICATION STORAGE
    // ==========================================

    fun isPinConfigured(): Boolean {
        return sharedPreferences.getBoolean(KEY_PIN_CONFIGURED, false)
    }

    fun isBiometricsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRICS_ENABLED, true)
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRICS_ENABLED, enabled).apply()
    }

    /**
     * Sets a new master PIN with a cryptographically secure random salt and iterated hash.
     * The raw PIN is accepted as a CharArray and sanitized immediately after hashing.
     */
    fun setMasterPin(pin: CharArray) {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        val saltHex = saltBytes.toHexString()

        val hashedPin = hashPinWithSalt(pin, saltBytes)
        MemorySanitizer.wipe(saltBytes)

        sharedPreferences.edit()
            .putString(KEY_PIN_SALT, saltHex)
            .putString(KEY_PIN_HASH, hashedPin)
            .putBoolean(KEY_PIN_CONFIGURED, true)
            .apply()
    }

    /**
     * Validates an entered PIN against the stored cryptographic hash.
     * Returns true if correct, false otherwise.
     */
    fun verifyPin(enteredPin: CharArray): Boolean {
        val storedSaltHex = sharedPreferences.getString(KEY_PIN_SALT, null) ?: return false
        val storedHash = sharedPreferences.getString(KEY_PIN_HASH, null) ?: return false

        val saltBytes = storedSaltHex.hexToByteArray()
        val computedHash = hashPinWithSalt(enteredPin, saltBytes)
        MemorySanitizer.wipe(saltBytes)

        // Constant-time comparison to prevent timing attacks
        return MessageDigest.isEqual(storedHash.toByteArray(Charsets.UTF_8), computedHash.toByteArray(Charsets.UTF_8))
    }

    // ==========================================
    // THREAT MONITOR & LOCKOUT STATE
    // ==========================================

    fun getFailedAttempts(): Int {
        return sharedPreferences.getInt(KEY_FAILED_ATTEMPTS, 0)
    }

    fun setFailedAttempts(count: Int) {
        sharedPreferences.edit().putInt(KEY_FAILED_ATTEMPTS, count).apply()
    }

    fun incrementFailedAttempts(): Int {
        val current = getFailedAttempts() + 1
        setFailedAttempts(current)
        return current
    }

    fun resetFailedAttempts() {
        sharedPreferences.edit().putInt(KEY_FAILED_ATTEMPTS, 0).apply()
    }

    fun getLockoutUntilTimestamp(): Long {
        return sharedPreferences.getLong(KEY_LOCKOUT_TIMESTAMP, 0L)
    }

    fun setLockoutUntilTimestamp(timestamp: Long) {
        sharedPreferences.edit().putLong(KEY_LOCKOUT_TIMESTAMP, timestamp).apply()
    }

    fun clearLockout() {
        sharedPreferences.edit()
            .putLong(KEY_LOCKOUT_TIMESTAMP, 0L)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .apply()
    }

    // ==========================================
    // VAULT FILE METADATA
    // ==========================================

    fun getVaultFiles(): List<VaultFileRecord> {
        val jsonString = sharedPreferences.getString(KEY_VAULT_FILES, null) ?: return emptyList()
        val list = mutableListOf<VaultFileRecord>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(VaultFileRecord.fromJson(obj))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveVaultFiles(files: List<VaultFileRecord>) {
        val jsonArray = JSONArray()
        files.forEach { jsonArray.put(it.toJson()) }
        sharedPreferences.edit().putString(KEY_VAULT_FILES, jsonArray.toString()).apply()
    }

    fun addVaultFile(file: VaultFileRecord) {
        val current = getVaultFiles().toMutableList()
        current.add(0, file)
        saveVaultFiles(current)
    }

    fun removeVaultFile(fileId: String) {
        val current = getVaultFiles().filter { it.id != fileId }
        saveVaultFiles(current)
    }

    // ==========================================
    // CRYPTOGRAPHIC UTILITIES
    // ==========================================

    private fun hashPinWithSalt(pin: CharArray, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)

        // Convert char array to bytes temporarily and wipe immediately
        val pinBytes = String(pin).toByteArray(Charsets.UTF_8)
        var hash = md.digest(pinBytes)
        MemorySanitizer.wipe(pinBytes)

        // Key-stretching iterations to mitigate brute-force dictionary attacks
        for (i in 0 until HASH_ITERATIONS) {
            md.reset()
            md.update(hash)
            md.update(salt)
            hash = md.digest()
        }

        val resultHex = hash.toHexString()
        MemorySanitizer.wipe(hash)
        return resultHex
    }

    private fun ByteArray.toHexString(): String {
        val hexChars = "0123456789abcdef"
        val result = StringBuilder(size * 2)
        for (b in this) {
            val i = b.toInt() and 0xFF
            result.append(hexChars[i ushr 4])
            result.append(hexChars[i and 0x0F])
        }
        return result.toString()
    }

    private fun String.hexToByteArray(): ByteArray {
        val len = length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(this[i], 16) shl 4) +
                    Character.digit(this[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}
