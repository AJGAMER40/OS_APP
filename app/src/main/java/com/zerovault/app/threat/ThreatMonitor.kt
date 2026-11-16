package com.zerovault.app.threat

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.zerovault.app.data.security.MemorySanitizer
import com.zerovault.app.data.security.SecurePreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * System Threat Monitor: Actively tracks brute-force patterns and unauthorized access attempts.
 * When 3 consecutive failed login attempts occur, it triggers an emergency security protocol:
 * 1. Clears and overwrites all sensitive data in RAM via MemorySanitizer.
 * 2. Launches a background WorkManager defense worker to enforce a 60-second lockout.
 * 3. Locks down the user interface until the threat mitigation window concludes.
 */
class ThreatMonitor(
    private val context: Context,
    private val securePreferencesManager: SecurePreferencesManager,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {

    companion object {
        const val MAX_FAILED_ATTEMPTS = 3
        const val LOCKOUT_DURATION_MS = 60_000L
        private const val TAG = "ThreatMonitor"
    }

    private val workManager = WorkManager.getInstance(context)

    private val _failedAttempts = MutableStateFlow(securePreferencesManager.getFailedAttempts())
    val failedAttempts: StateFlow<Int> = _failedAttempts.asStateFlow()

    private val _isLockdownActive = MutableStateFlow(false)
    val isLockdownActive: StateFlow<Boolean> = _isLockdownActive.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _threatLogs = MutableStateFlow<List<ThreatLogEvent>>(emptyList())
    val threatLogs: StateFlow<List<ThreatLogEvent>> = _threatLogs.asStateFlow()

    private var countdownJob: Job? = null

    init {
        checkAndRestoreLockoutState()
    }

    /**
     * Checks if a previous lockout is still active across app restarts.
     */
    fun checkAndRestoreLockoutState() {
        val lockoutUntil = securePreferencesManager.getLockoutUntilTimestamp()
        val now = System.currentTimeMillis()

        if (lockoutUntil > now) {
            val secondsLeft = ((lockoutUntil - now) / 1000).toInt().coerceAtLeast(1)
            _isLockdownActive.value = true
            _remainingSeconds.value = secondsLeft
            addLog(
                ThreatEventType.WORKMANAGER_ENGAGED,
                "Restored active quarantine: ${secondsLeft}s remaining.",
                ThreatSeverity.CRITICAL
            )
            startLocalCountdown(secondsLeft)
            ensureWorkManagerEnqueued()
        } else if (_failedAttempts.value >= MAX_FAILED_ATTEMPTS) {
            // Lockout period has passed while app was closed
            liftLockdown("Quarantine window elapsed. Defense posture reset.")
        } else {
            _isLockdownActive.value = false
            _remainingSeconds.value = 0
        }
    }

    /**
     * Registers a failed authentication attempt (wrong PIN or biometric rejection).
     */
    fun registerFailedAttempt(): ThreatAssessment {
        val currentFailed = securePreferencesManager.incrementFailedAttempts()
        _failedAttempts.value = currentFailed

        Log.w(TAG, "Failed authentication attempt registered: $currentFailed of $MAX_FAILED_ATTEMPTS")

        if (currentFailed >= MAX_FAILED_ATTEMPTS) {
            // Trigger emergency DoS/brute-force defense
            triggerEmergencyLockdown()
            return ThreatAssessment(
                threatLevel = ThreatLevel.LOCKDOWN,
                remainingAttempts = 0,
                isLocked = true,
                message = "CRITICAL: 3 failed attempts! System interface locked for 60 seconds. RAM sanitized."
            )
        } else {
            val remaining = MAX_FAILED_ATTEMPTS - currentFailed
            addLog(
                ThreatEventType.AUTH_FAILURE,
                "Failed authentication attempt ($currentFailed/$MAX_FAILED_ATTEMPTS). Warning: $remaining attempts remain.",
                ThreatSeverity.WARNING
            )
            return ThreatAssessment(
                threatLevel = ThreatLevel.ELEVATED_RISK,
                remainingAttempts = remaining,
                isLocked = false,
                message = "Incorrect PIN. $remaining attempt${if (remaining == 1) "" else "s"} remaining before security lockout."
            )
        }
    }

    /**
     * Resets failed attempt counters on successful authentication.
     */
    fun registerSuccess() {
        if (_failedAttempts.value > 0) {
            securePreferencesManager.resetFailedAttempts()
            _failedAttempts.value = 0
        }
        addLog(
            ThreatEventType.AUTH_SUCCESS,
            "Authentication successful. Cryptographic vault unlocked.",
            ThreatSeverity.INFO
        )
    }

    /**
     * Registers a Duress / Canary PIN trigger.
     * Silently sanitizes sensitive RAM and logs forensic alert.
     */
    fun registerDuress() {
        MemorySanitizer.purgeAll()
        addLog(
            ThreatEventType.BRUTE_FORCE_TRIGGER,
            "COVERT ALERT: Duress Canary PIN entered. Real enclave quarantined & memory sanitized.",
            ThreatSeverity.CRITICAL
        )
    }

    /**
     * Executes the emergency mitigation protocol.
     */
    private fun triggerEmergencyLockdown() {
        _isLockdownActive.value = true
        _remainingSeconds.value = 60

        // 1. Clear all sensitive in-memory buffers
        val wipedBuffers = MemorySanitizer.purgeAll()

        // 2. Persist lockout timestamp to withstand app force-stop/restart
        val lockoutUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS
        securePreferencesManager.setLockoutUntilTimestamp(lockoutUntil)

        // 3. Log threat events
        addLog(
            ThreatEventType.BRUTE_FORCE_TRIGGER,
            "CRITICAL THREAT: Brute-Force / DoS anomaly threshold reached (3 failed attempts). Initiating lockdown.",
            ThreatSeverity.CRITICAL
        )
        addLog(
            ThreatEventType.RAM_SANITIZED,
            "DEFENSE ACTION: Cleared and overwritten $wipedBuffers sensitive RAM allocations with zeroes.",
            ThreatSeverity.CRITICAL
        )

        // 4. Enqueue WorkManager background defense task
        ensureWorkManagerEnqueued()

        addLog(
            ThreatEventType.WORKMANAGER_ENGAGED,
            "DEFENSE ACTION: WorkManager defense worker dispatched for 60-second isolation.",
            ThreatSeverity.CRITICAL
        )

        // 5. Start smooth countdown ticker
        startLocalCountdown(60)
    }

    private fun ensureWorkManagerEnqueued() {
        val workRequest = OneTimeWorkRequestBuilder<ThreatMitigationWorker>()
            .addTag(ThreatMitigationWorker.WORK_NAME)
            .build()

        workManager.enqueueUniqueWork(
            ThreatMitigationWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun startLocalCountdown(seconds: Int) {
        countdownJob?.cancel()
        countdownJob = externalScope.launch {
            var currentSec = seconds
            while (currentSec > 0) {
                _remainingSeconds.value = currentSec
                delay(1000L)
                currentSec--
            }
            _remainingSeconds.value = 0
            liftLockdown("Threat mitigation cooldown completed. Interface unlocked.")
        }
    }

    private fun liftLockdown(reason: String) {
        securePreferencesManager.clearLockout()
        _failedAttempts.value = 0
        _isLockdownActive.value = false
        _remainingSeconds.value = 0
        addLog(ThreatEventType.LOCKOUT_EXPIRED, reason, ThreatSeverity.INFO)
    }

    private fun addLog(type: ThreatEventType, description: String, severity: ThreatSeverity) {
        val event = ThreatLogEvent(
            timestamp = System.currentTimeMillis(),
            type = type,
            description = description,
            severity = severity
        )
        val list = _threatLogs.value.toMutableList()
        list.add(0, event)
        if (list.size > 20) {
            list.removeAt(list.size - 1)
        }
        _threatLogs.value = list
    }
}

enum class ThreatLevel {
    SECURE,
    ELEVATED_RISK,
    LOCKDOWN
}

data class ThreatAssessment(
    val threatLevel: ThreatLevel,
    val remainingAttempts: Int,
    val isLocked: Boolean,
    val message: String
)
