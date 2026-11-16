package com.zerovault.app.data.security

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.zerovault.app.data.model.VaultFileRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

/**
 * Handles hardware-backed file encryption and decryption using AndroidX EncryptedFile
 * and the Android Keystore system.
 *
 * Employs AES-256-GCM with HKDF-4KB chunking (FileEncryptionScheme.AES256_GCM_HKDF_4KB).
 * Files are isolated in scoped internal storage and sanitized against memory/disk remanence.
 */
class EncryptedFileManager(
    private val context: Context,
    private val masterKey: MasterKey,
    private val securePreferencesManager: SecurePreferencesManager
) {

    companion object {
        private const val TAG = "EncryptedFileManager"
        private const val VAULT_DIR_NAME = "vault_storage"
        private const val TEMP_DIR_NAME = "vault_temp"
        private const val BUFFER_SIZE = 8192
    }

    private val vaultDirectory: File by lazy {
        File(context.filesDir, VAULT_DIR_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val tempDirectory: File by lazy {
        File(context.cacheDir, TEMP_DIR_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Encrypts a user-selected file from a ContentResolver Uri and persists it to local app-internal storage.
     */
    suspend fun encryptFile(
        sourceUri: Uri,
        originalFileName: String,
        mimeType: String
    ): Result<VaultFileRecord> = withContext(Dispatchers.IO) {
        try {
            val fileId = UUID.randomUUID().toString()
            val encryptedFileName = "enc_${fileId}.zvd"
            val targetFile = File(vaultDirectory, encryptedFileName)

            if (targetFile.exists()) {
                targetFile.delete()
            }

            // Create AndroidX EncryptedFile instance backed by Keystore MasterKey
            val encryptedFile = EncryptedFile.Builder(
                context,
                targetFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(sourceUri)
                ?: return@withContext Result.failure(IllegalStateException("Unable to open source file stream"))

            val messageDigest = MessageDigest.getInstance("SHA-256")
            var totalBytesRead = 0L

            encryptedFile.openFileOutput().use { encryptedOutputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    encryptedOutputStream.write(buffer, 0, bytesRead)
                    messageDigest.update(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                }
                encryptedOutputStream.flush()
                MemorySanitizer.wipe(buffer)
            }
            inputStream.close()

            val checksum = messageDigest.digest().toHexString()

            val record = VaultFileRecord(
                id = fileId,
                originalFileName = originalFileName,
                encryptedFileName = encryptedFileName,
                mimeType = mimeType,
                sizeBytes = totalBytesRead,
                encryptedAt = System.currentTimeMillis(),
                sha256Checksum = checksum
            )

            // Save record in EncryptedSharedPreferences
            securePreferencesManager.addVaultFile(record)

            Log.i(TAG, "Successfully encrypted $originalFileName ($totalBytesRead bytes) as $encryptedFileName")
            Result.success(record)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt file", e)
            Result.failure(e)
        }
    }

    /**
     * Decrypts an encrypted vault file into memory for previewing.
     * The allocated byte buffer is registered in MemorySanitizer so it can be wiped
     * on demand or during emergency threat lockdown.
     */
    suspend fun decryptToMemory(record: VaultFileRecord): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val file = File(vaultDirectory, record.encryptedFileName)
            if (!file.exists()) {
                return@withContext Result.failure(NoSuchFileException(file, reason = "Encrypted file does not exist on disk"))
            }

            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val outputStream = ByteArrayOutputStream()
            encryptedFile.openFileInput().use { encryptedInputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (encryptedInputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                MemorySanitizer.wipe(buffer)
            }

            val decryptedBytes = outputStream.toByteArray()
            // Register in sanitizer for threat tracking
            MemorySanitizer.register(decryptedBytes)

            Log.i(TAG, "Successfully decrypted ${record.originalFileName} to memory buffer (${decryptedBytes.size} bytes)")
            Result.success(decryptedBytes)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed for ${record.originalFileName}", e)
            Result.failure(e)
        }
    }

    /**
     * Decrypts the file directly to an external destination Uri selected by user via Storage Access Framework.
     */
    suspend fun decryptAndExport(
        record: VaultFileRecord,
        destinationUri: Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(vaultDirectory, record.encryptedFileName)
            if (!file.exists()) {
                return@withContext Result.failure(NoSuchFileException(file))
            }

            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val outputStream = context.contentResolver.openOutputStream(destinationUri)
                ?: return@withContext Result.failure(IllegalStateException("Cannot open destination stream for export"))

            encryptedFile.openFileInput().use { encryptedInputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (encryptedInputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
                MemorySanitizer.wipe(buffer)
            }
            outputStream.close()

            Log.i(TAG, "Exported decrypted copy of ${record.originalFileName} to $destinationUri")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed exporting decrypted file", e)
            Result.failure(e)
        }
    }

    /**
     * Cryptographic Shred & Delete: Overwrites the physical disk blocks with random noise
     * and zeroes before unlinking to prevent flash remanence forensics.
     */
    suspend fun shredAndDeleteFile(record: VaultFileRecord): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val file = File(vaultDirectory, record.encryptedFileName)
            if (file.exists()) {
                val length = file.length()
                val secureRandom = SecureRandom()

                // Pass 1: Overwrite with cryptographic pseudo-random noise
                FileOutputStream(file).use { fos ->
                    val noise = ByteArray(BUFFER_SIZE)
                    var written = 0L
                    while (written < length) {
                        secureRandom.nextBytes(noise)
                        val toWrite = minOf(noise.size.toLong(), length - written).toInt()
                        fos.write(noise, 0, toWrite)
                        written += toWrite
                    }
                    fos.flush()
                    MemorySanitizer.wipe(noise)
                }

                // Pass 2: Overwrite with zeroes
                FileOutputStream(file).use { fos ->
                    val zeroes = ByteArray(BUFFER_SIZE)
                    var written = 0L
                    while (written < length) {
                        val toWrite = minOf(zeroes.size.toLong(), length - written).toInt()
                        fos.write(zeroes, 0, toWrite)
                        written += toWrite
                    }
                    fos.flush()
                }

                // Unlink file
                file.delete()
            }

            // Remove from metadata list
            securePreferencesManager.removeVaultFile(record.id)
            Log.i(TAG, "Shredded and deleted file ${record.originalFileName} (${record.encryptedFileName})")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to shred file", e)
            Result.failure(e)
        }
    }

    /**
     * Purges any temporary preview files in cache directory.
     */
    fun clearTempFiles() {
        try {
            tempDirectory.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.w(TAG, "Error cleaning temp files", e)
        }
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
}
