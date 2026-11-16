package com.zerovault.app.data.model

import org.json.JSONObject

/**
 * Metadata record for a file securely stored inside ZeroVault.
 */
data class VaultFileRecord(
    val id: String,
    val originalFileName: String,
    val encryptedFileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val encryptedAt: Long,
    val sha256Checksum: String
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("originalFileName", originalFileName)
            put("encryptedFileName", encryptedFileName)
            put("mimeType", mimeType)
            put("sizeBytes", sizeBytes)
            put("encryptedAt", encryptedAt)
            put("sha256Checksum", sha256Checksum)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): VaultFileRecord {
            return VaultFileRecord(
                id = json.getString("id"),
                originalFileName = json.getString("originalFileName"),
                encryptedFileName = json.getString("encryptedFileName"),
                mimeType = json.optString("mimeType", "application/octet-stream"),
                sizeBytes = json.getLong("sizeBytes"),
                encryptedAt = json.getLong("encryptedAt"),
                sha256Checksum = json.optString("sha256Checksum", "")
            )
        }
    }
}
