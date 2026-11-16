package com.zerovault.app.threat

import android.os.Build
import android.os.Debug
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.InetSocketAddress
import java.net.Socket

data class TamperScanResult(
    val timestamp: Long = System.currentTimeMillis(),
    val isDebuggerAttached: Boolean,
    val tracerPid: Int,
    val isPTraceDetected: Boolean,
    val isFridaHookingDetected: Boolean,
    val isRootBinaryDetected: Boolean,
    val isTestKeysBuild: Boolean,
    val suspiciousModules: List<String>,
    val threatScore: Int // 0 to 100
) {
    val isCompromised: Boolean
        get() = threatScore >= 30
}

/**
 * KernelTamperAuditor actively inspects Linux kernel parameters and Android OS runtime
 * to detect reverse-engineering tools, dynamic instrumentation (Frida), interactive debuggers,
 * and root privilege escalation.
 */
class KernelTamperAuditor {

    companion object {
        private const val TAG = "KernelTamperAuditor"

        private val KNOWN_ROOT_PATHS = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/app/Superuser.apk"
        )

        private val FRIDA_SIGNATURES = listOf(
            "frida",
            "gadget",
            "xposed",
            "substrate",
            "gum-js"
        )
    }

    suspend fun performDeepAudit(): TamperScanResult = withContext(Dispatchers.IO) {
        val tracerPid = readTracerPid()
        val isDebugger = Debug.isDebuggerConnected() || Debug.waitingForDebugger()
        val isPTrace = tracerPid > 0 || isDebugger
        val suspiciousModules = scanMemoryMapsForHooking()
        val isFrida = suspiciousModules.isNotEmpty() || probeFridaPort()
        val isRoot = probeRootBinaries()
        val isTestKeys = Build.TAGS?.contains("test-keys") == true

        var score = 0
        if (isDebugger) score += 40
        if (isPTrace) score += 35
        if (isFrida) score += 50
        if (isRoot) score += 30
        if (isTestKeys) score += 15

        TamperScanResult(
            isDebuggerAttached = isDebugger,
            tracerPid = tracerPid,
            isPTraceDetected = isPTrace,
            isFridaHookingDetected = isFrida,
            isRootBinaryDetected = isRoot,
            isTestKeysBuild = isTestKeys,
            suspiciousModules = suspiciousModules,
            threatScore = score.coerceIn(0, 100)
        )
    }

    /**
     * Reads /proc/self/status looking for TracerPid.
     * In Linux, when a process is attached by ptrace/gdb/lldb, TracerPid holds the debugger PID.
     */
    private fun readTracerPid(): Int {
        return try {
            val statusFile = File("/proc/self/status")
            if (statusFile.exists()) {
                BufferedReader(FileReader(statusFile)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line!!.startsWith("TracerPid:")) {
                            val parts = line!!.split("\\s+".toRegex())
                            if (parts.size >= 2) {
                                return parts[1].toIntOrNull() ?: 0
                            }
                        }
                    }
                }
            }
            0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Inspects /proc/self/maps for dynamically injected libraries (e.g. frida-agent.so, xposed.so).
     */
    private fun scanMemoryMapsForHooking(): List<String> {
        val detected = mutableListOf<String>()
        try {
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                BufferedReader(FileReader(mapsFile)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val lower = line!!.lowercase()
                        for (sig in FRIDA_SIGNATURES) {
                            if (lower.contains(sig) && !detected.contains(sig)) {
                                detected.add(sig)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error inspecting /proc/self/maps", e)
        }
        return detected
    }

    /**
     * Tests local socket on Frida's default listening port 27042.
     */
    private fun probeFridaPort(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("127.0.0.1", 27042), 50)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks for su execution binaries on typical system partitions.
     */
    private fun probeRootBinaries(): Boolean {
        for (path in KNOWN_ROOT_PATHS) {
            try {
                if (File(path).exists()) return true
            } catch (e: Exception) {
                // Access restricted or sandboxed
            }
        }
        return false
    }
}
