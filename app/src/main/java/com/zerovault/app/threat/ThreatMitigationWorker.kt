package com.zerovault.app.threat

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.zerovault.app.data.security.KeystoreManager
import com.zerovault.app.data.security.MemorySanitizer
import com.zerovault.app.data.security.SecurePreferencesManager
import kotlinx.coroutines.delay

/**
 * Background WorkManager worker simulating active DoS/brute-force defense mitigation.
 * Runs a 60-second defensive lock cycle in the background, independent of UI lifecycle.
 */
class ThreatMitigationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "zerovault_threat_mitigation_work"
        const val KEY_REMAINING_SECONDS = "key_remaining_seconds"
        const val KEY_LOCKOUT_COMPLETED = "key_lockout_completed"
        const val LOCKOUT_DURATION_SECONDS = 60
        private const val TAG = "ThreatMitigationWorker"
    }

    override suspend fun doWork(): Result {
        Log.w(TAG, "ENGAGING SYSTEM THREAT MITIGATION: Starting 60s quarantine...")

        // Sanitize any remaining RAM buffers upon worker launch
        MemorySanitizer.purgeAll()

        val keystoreManager = KeystoreManager(applicationContext)
        val securePreferencesManager = SecurePreferencesManager(applicationContext, keystoreManager.masterKey)

        val lockoutUntil = securePreferencesManager.getLockoutUntilTimestamp()
        var remainingSeconds = if (lockoutUntil > System.currentTimeMillis()) {
            ((lockoutUntil - System.currentTimeMillis()) / 1000).toInt().coerceAtLeast(1)
        } else {
            LOCKOUT_DURATION_SECONDS
        }

        while (remainingSeconds > 0) {
            setProgress(
                workDataOf(
                    KEY_REMAINING_SECONDS to remainingSeconds,
                    KEY_LOCKOUT_COMPLETED to false
                )
            )
            Log.d(TAG, "Mitigation in progress. Threat Cooldown: ${remainingSeconds}s remaining.")
            delay(1000L)
            remainingSeconds--
        }

        // Lockout expired: reset defense posture
        securePreferencesManager.clearLockout()
        setProgress(
            workDataOf(
                KEY_REMAINING_SECONDS to 0,
                KEY_LOCKOUT_COMPLETED to true
            )
        )

        Log.i(TAG, "THREAT MITIGATION COMPLETE: Lockout cooldown concluded. Vault defense reset.")
        return Result.success(workDataOf(KEY_LOCKOUT_COMPLETED to true))
    }
}
