package com.zerovault.app.vault

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerovault.app.data.model.VaultFileRecord
import com.zerovault.app.data.security.DoDShredder
import com.zerovault.app.data.security.EncryptedFileManager
import com.zerovault.app.data.security.MemorySanitizer
import com.zerovault.app.data.security.SecurePreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActivePreview(
    val record: VaultFileRecord,
    val data: ByteArray,
    val isText: Boolean,
    val textContent: String? = null
) {
    fun wipe() {
        MemorySanitizer.wipe(data)
    }
}

data class VaultUiState(
    val files: List<VaultFileRecord> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val activePreview: ActivePreview? = null,
    val totalSizeBytes: Long = 0L
)

class VaultViewModel(
    private val encryptedFileManager: EncryptedFileManager,
    private val securePreferencesManager: SecurePreferencesManager,
    private val doDShredder: DoDShredder
) : ViewModel() {

    private val _rawFiles = MutableStateFlow<List<VaultFileRecord>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _statusMessage = MutableStateFlow<String?>(null)
    private val _activePreview = MutableStateFlow<ActivePreview?>(null)

    val uiState: StateFlow<VaultUiState> = combine(
        _rawFiles,
        _searchQuery,
        _isLoading,
        _statusMessage,
        _activePreview
    ) { files, query, loading, message, preview ->
        val filtered = if (query.isBlank()) {
            files
        } else {
            files.filter { it.originalFileName.contains(query, ignoreCase = true) }
        }
        val totalSize = files.sumOf { it.sizeBytes }

        VaultUiState(
            files = filtered,
            searchQuery = query,
            isLoading = loading,
            statusMessage = message,
            activePreview = preview,
            totalSizeBytes = totalSize
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VaultUiState()
    )

    init {
        loadFiles()
    }

    fun loadFiles() {
        _rawFiles.value = securePreferencesManager.getVaultFiles()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun encryptAndImport(uri: Uri, fileName: String, mimeType: String, shredSource: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Hardware encrypting $fileName via AES-256 GCM..."
            val result = encryptedFileManager.encryptFile(uri, fileName, mimeType)
            if (result.isSuccess) {
                if (shredSource) {
                    _statusMessage.value = "Executing DoD 5220.22-M 3-pass shred on unencrypted source..."
                    doDShredder.shredUri(uri).collect { progress ->
                        _statusMessage.value = progress.passDescription
                    }
                    _statusMessage.value = "Encrypted $fileName into enclave & eradicated source from public storage."
                } else {
                    _statusMessage.value = "Encrypted & secured $fileName successfully."
                }
                loadFiles()
            } else {
                _statusMessage.value = "Encryption failed: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun previewFile(record: VaultFileRecord) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Decrypting ${record.originalFileName} to isolated RAM buffer..."
            val result = encryptedFileManager.decryptToMemory(record)
            _isLoading.value = false

            if (result.isSuccess) {
                val bytes = result.getOrThrow()
                val isText = record.mimeType.startsWith("text/") ||
                        record.originalFileName.endsWith(".txt") ||
                        record.originalFileName.endsWith(".json") ||
                        record.originalFileName.endsWith(".xml") ||
                        record.originalFileName.endsWith(".csv") ||
                        record.originalFileName.endsWith(".md")

                val textContent = if (isText) {
                    try {
                        String(bytes, Charsets.UTF_8)
                    } catch (e: Exception) {
                        null
                    }
                } else null

                _activePreview.value = ActivePreview(
                    record = record,
                    data = bytes,
                    isText = isText,
                    textContent = textContent
                )
            } else {
                _statusMessage.value = "Decryption failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun closePreview() {
        _activePreview.value?.wipe()
        _activePreview.value = null
    }

    fun exportDecrypted(record: VaultFileRecord, destinationUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Exporting decrypted copy to storage..."
            val result = encryptedFileManager.decryptAndExport(record, destinationUri)
            _isLoading.value = false
            if (result.isSuccess) {
                _statusMessage.value = "Exported ${record.originalFileName} successfully."
            } else {
                _statusMessage.value = "Export failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun shredAndDelete(record: VaultFileRecord) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Cryptographically overwriting blocks for ${record.originalFileName}..."
            val result = encryptedFileManager.shredAndDeleteFile(record)
            _isLoading.value = false
            if (result.isSuccess) {
                _statusMessage.value = "File permanently shredded and wiped."
                loadFiles()
            } else {
                _statusMessage.value = "Failed to shred: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        closePreview()
    }
}
