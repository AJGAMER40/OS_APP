package com.zerovault.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerovault.app.data.security.MemorySanitizer
import com.zerovault.app.data.security.SecurePreferencesManager
import com.zerovault.app.threat.ThreatAssessment
import com.zerovault.app.threat.ThreatLevel
import com.zerovault.app.threat.ThreatMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Arrays

enum class AuthScreenMode {
    SETUP_ENTER,
    SETUP_CONFIRM,
    LOGIN_PIN,
    LOCKDOWN
}

data class AuthUiState(
    val mode: AuthScreenMode,
    val isPinConfigured: Boolean,
    val enteredPinLength: Int,
    val maxPinLength: Int = 6,
    val errorMessage: String? = null,
    val isBiometricsAvailable: Boolean = false,
    val remainingAttempts: Int = 3,
    val isLockedOut: Boolean = false,
    val lockoutSecondsRemaining: Int = 0,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val securePreferencesManager: SecurePreferencesManager,
    private val threatMonitor: ThreatMonitor,
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {

    private val _currentPinChars = MutableStateFlow(CharArray(0))
    private var tempSetupPin: CharArray? = null

    private val _mode = MutableStateFlow(
        if (securePreferencesManager.isPinConfigured()) AuthScreenMode.LOGIN_PIN else AuthScreenMode.SETUP_ENTER
    )
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isAuthenticated = MutableStateFlow(false)

    private data class InternalAuthState(
        val mode: AuthScreenMode,
        val pinLength: Int,
        val errorMessage: String?,
        val isAuthenticated: Boolean
    )

    private val internalState = combine(
        _mode,
        _currentPinChars,
        _errorMessage,
        _isAuthenticated
    ) { mode, pinChars, error, authenticated ->
        InternalAuthState(mode, pinChars.size, error, authenticated)
    }

    val uiState: StateFlow<AuthUiState> = combine(
        internalState,
        threatMonitor.isLockdownActive,
        threatMonitor.remainingSeconds,
        threatMonitor.failedAttempts
    ) { state, locked, seconds, failed ->
        val effectiveMode = if (locked) AuthScreenMode.LOCKDOWN else state.mode
        val remaining = (ThreatMonitor.MAX_FAILED_ATTEMPTS - failed).coerceAtLeast(0)

        AuthUiState(
            mode = effectiveMode,
            isPinConfigured = securePreferencesManager.isPinConfigured(),
            enteredPinLength = state.pinLength,
            maxPinLength = 6,
            errorMessage = state.errorMessage,
            isBiometricsAvailable = biometricAuthManager.checkBiometricAvailability() is BiometricAuthManager.BiometricAvailability.Available &&
                    securePreferencesManager.isBiometricsEnabled() &&
                    securePreferencesManager.isPinConfigured() &&
                    !locked,
            remainingAttempts = remaining,
            isLockedOut = locked,
            lockoutSecondsRemaining = seconds,
            isAuthenticated = state.isAuthenticated
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthUiState(
            mode = if (securePreferencesManager.isPinConfigured()) AuthScreenMode.LOGIN_PIN else AuthScreenMode.SETUP_ENTER,
            isPinConfigured = securePreferencesManager.isPinConfigured(),
            enteredPinLength = 0
        )
    )

    init {
        // Sync threat state on init
        threatMonitor.checkAndRestoreLockoutState()
    }

    fun onDigitEntered(digit: Char) {
        if (_isAuthenticated.value || threatMonitor.isLockdownActive.value) return

        val current = _currentPinChars.value
        if (current.size >= 6) return

        val newChars = current.copyOf(current.size + 1)
        newChars[newChars.size - 1] = digit
        _currentPinChars.value = newChars
        _errorMessage.value = null

        // Auto-submit when length reaches 6
        if (newChars.size == 6) {
            processPinSubmission(newChars)
        }
    }

    fun onDeleteDigit() {
        val current = _currentPinChars.value
        if (current.isNotEmpty()) {
            val newChars = current.copyOf(current.size - 1)
            MemorySanitizer.wipe(current)
            _currentPinChars.value = newChars
            _errorMessage.value = null
        }
    }

    fun onClearPin() {
        val current = _currentPinChars.value
        MemorySanitizer.wipe(current)
        _currentPinChars.value = CharArray(0)
        _errorMessage.value = null
    }

    private fun processPinSubmission(pin: CharArray) {
        when (_mode.value) {
            AuthScreenMode.SETUP_ENTER -> {
                tempSetupPin = pin.copyOf()
                clearPinInput()
                _mode.value = AuthScreenMode.SETUP_CONFIRM
                _errorMessage.value = null
            }
            AuthScreenMode.SETUP_CONFIRM -> {
                val temp = tempSetupPin
                if (temp != null && Arrays.equals(temp, pin)) {
                    // Set master PIN
                    securePreferencesManager.setMasterPin(pin)
                    MemorySanitizer.wipe(temp)
                    tempSetupPin = null
                    clearPinInput()
                    _isAuthenticated.value = true
                    threatMonitor.registerSuccess()
                } else {
                    _errorMessage.value = "PIN confirmation does not match. Try again."
                    tempSetupPin?.let { MemorySanitizer.wipe(it) }
                    tempSetupPin = null
                    clearPinInput()
                    _mode.value = AuthScreenMode.SETUP_ENTER
                }
            }
            AuthScreenMode.LOGIN_PIN -> {
                val isValid = securePreferencesManager.verifyPin(pin)
                clearPinInput()

                if (isValid) {
                    threatMonitor.registerSuccess()
                    _isAuthenticated.value = true
                    _errorMessage.value = null
                } else {
                    val assessment: ThreatAssessment = threatMonitor.registerFailedAttempt()
                    if (assessment.isLocked) {
                        _mode.value = AuthScreenMode.LOCKDOWN
                        _errorMessage.value = assessment.message
                    } else {
                        _errorMessage.value = assessment.message
                    }
                }
            }
            AuthScreenMode.LOCKDOWN -> {
                clearPinInput()
            }
        }
    }

    fun onBiometricSuccess() {
        if (!threatMonitor.isLockdownActive.value) {
            threatMonitor.registerSuccess()
            _isAuthenticated.value = true
            _errorMessage.value = null
        }
    }

    fun onBiometricFailed() {
        // Biometric sensor did not match face/fingerprint
        _errorMessage.value = "Biometric not recognized. Please retry or enter PIN."
    }

    fun lockVault() {
        MemorySanitizer.purgeAll()
        _isAuthenticated.value = false
        clearPinInput()
        _mode.value = if (securePreferencesManager.isPinConfigured()) {
            AuthScreenMode.LOGIN_PIN
        } else {
            AuthScreenMode.SETUP_ENTER
        }
        _errorMessage.value = null
    }

    private fun clearPinInput() {
        val current = _currentPinChars.value
        MemorySanitizer.wipe(current)
        _currentPinChars.value = CharArray(0)
    }

    override fun onCleared() {
        super.onCleared()
        clearPinInput()
        tempSetupPin?.let { MemorySanitizer.wipe(it) }
    }
}
