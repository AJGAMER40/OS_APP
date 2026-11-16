package com.zerovault.app.threat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerovault.app.data.security.MemorySanitizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HeapBufferState(
    val addressTag: String = "0x7FFF94E2",
    val cleartextPreview: String,
    val hexDump: String,
    val isSanitized: Boolean
)

class CyberLabViewModel(
    private val cryptoBenchmark: CryptoBenchmark = CryptoBenchmark(),
    private val kernelTamperAuditor: KernelTamperAuditor = KernelTamperAuditor()
) : ViewModel() {

    private var activeTestBuffer: ByteArray? = null

    private val _heapState = MutableStateFlow(createInitialHeapBuffer())
    val heapState: StateFlow<HeapBufferState> = _heapState.asStateFlow()

    private val _benchmarkResult = MutableStateFlow<BenchmarkResult?>(null)
    val benchmarkResult: StateFlow<BenchmarkResult?> = _benchmarkResult.asStateFlow()
    private val _isBenchmarking = MutableStateFlow(false)
    val isBenchmarking: StateFlow<Boolean> = _isBenchmarking.asStateFlow()

    private val _timingResult = MutableStateFlow<TimingTestResult?>(null)
    val timingResult: StateFlow<TimingTestResult?> = _timingResult.asStateFlow()

    private val _tamperResult = MutableStateFlow<TamperScanResult?>(null)
    val tamperResult: StateFlow<TamperScanResult?> = _tamperResult.asStateFlow()
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        runKernelScan()
    }

    private fun createInitialHeapBuffer(): HeapBufferState {
        val secret = "ENCLAVE_MASTER_SECRET_KEY_9A"
        val bytes = secret.toByteArray(Charsets.UTF_8)
        activeTestBuffer = bytes
        MemorySanitizer.register(bytes)
        return HeapBufferState(
            addressTag = "0x7F" + Integer.toHexString(bytes.hashCode()).uppercase(),
            cleartextPreview = secret,
            hexDump = bytes.toHexFormatted(),
            isSanitized = false
        )
    }

    fun allocateSensitiveSecret(customSecret: String = "CONFIDENTIAL_PAYLOAD_89F") {
        activeTestBuffer?.let { MemorySanitizer.wipe(it) }
        val bytes = customSecret.toByteArray(Charsets.UTF_8)
        activeTestBuffer = bytes
        MemorySanitizer.register(bytes)
        _heapState.value = HeapBufferState(
            addressTag = "0x7F" + Integer.toHexString(bytes.hashCode()).uppercase(),
            cleartextPreview = customSecret,
            hexDump = bytes.toHexFormatted(),
            isSanitized = false
        )
    }

    fun sanitizeHeapMemory() {
        val buffer = activeTestBuffer
        if (buffer != null) {
            MemorySanitizer.wipe(buffer)
            _heapState.value = HeapBufferState(
                addressTag = _heapState.value.addressTag,
                cleartextPreview = "[ZERO-WIPED / EPHEMERAL PURGE COMPLETED]",
                hexDump = buffer.toHexFormatted(),
                isSanitized = true
            )
        }
    }

    fun runBenchmark() {
        viewModelScope.launch {
            _isBenchmarking.value = true
            try {
                val result = cryptoBenchmark.runHardwareThroughputBenchmark()
                _benchmarkResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isBenchmarking.value = false
            }
        }
    }

    fun runTimingAnalysis() {
        viewModelScope.launch {
            try {
                val result = cryptoBenchmark.runTimingLeakAnalysis()
                _timingResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun runKernelScan() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                val result = kernelTamperAuditor.performDeepAudit()
                _tamperResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isScanning.value = false
            }
        }
    }

    private fun ByteArray.toHexFormatted(): String {
        val sb = StringBuilder()
        for (i in indices) {
            sb.append(String.format("%02X ", this[i]))
            if ((i + 1) % 8 == 0 && i != lastIndex) {
                sb.append(" ")
            }
        }
        return sb.toString().trim()
    }

    override fun onCleared() {
        super.onCleared()
        activeTestBuffer?.let { MemorySanitizer.wipe(it) }
    }
}
