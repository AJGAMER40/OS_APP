package com.zerovault.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zerovault.app.auth.AuthScreenMode
import com.zerovault.app.auth.AuthViewModel
import com.zerovault.app.auth.BiometricAuthManager
import com.zerovault.app.data.security.MemorySanitizer
import com.zerovault.app.threat.CyberLabViewModel
import com.zerovault.app.ui.components.CyberBottomNav
import com.zerovault.app.ui.components.CyberNavTab
import com.zerovault.app.ui.dialogs.FilePreviewDialog
import com.zerovault.app.ui.dialogs.SecurityAuditDialog
import com.zerovault.app.ui.screens.AuthScreen
import com.zerovault.app.ui.screens.CyberLabScreen
import com.zerovault.app.ui.screens.DecoyVaultScreen
import com.zerovault.app.ui.screens.ThreatHudScreen
import com.zerovault.app.ui.screens.ThreatLockoutScreen
import com.zerovault.app.ui.screens.VaultDashboardScreen
import com.zerovault.app.ui.theme.VaultBg
import com.zerovault.app.ui.theme.ZeroVaultTheme
import com.zerovault.app.vault.VaultViewModel

class MainActivity : FragmentActivity() {

    private lateinit var biometricAuthManager: BiometricAuthManager

    private val authViewModel: AuthViewModel by viewModels {
        val app = application as ZeroVaultApplication
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    securePreferencesManager = app.securePreferencesManager,
                    threatMonitor = app.threatMonitor,
                    biometricAuthManager = biometricAuthManager
                ) as T
            }
        }
    }

    private val vaultViewModel: VaultViewModel by viewModels {
        val app = application as ZeroVaultApplication
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VaultViewModel(
                    encryptedFileManager = app.encryptedFileManager,
                    securePreferencesManager = app.securePreferencesManager,
                    doDShredder = app.doDShredder
                ) as T
            }
        }
    }

    private val cyberLabViewModel: CyberLabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OS Security Best Practice: FLAG_SECURE
        // Prevents screenshots, video captures, and leaks in recent apps thumbnail overview
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        biometricAuthManager = BiometricAuthManager(this)

        setContent {
            ZeroVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VaultBg
                ) {
                    ZeroVaultAppRoot()
                }
            }
        }
    }

    @Composable
    private fun ZeroVaultAppRoot() {
        val app = application as ZeroVaultApplication
        val authUiState by authViewModel.uiState.collectAsState()
        val vaultUiState by vaultViewModel.uiState.collectAsState()
        val threatLogs by app.threatMonitor.threatLogs.collectAsState()

        var currentTab by remember { mutableStateOf(CyberNavTab.VAULT_ENCLAVE) }
        var showSecurityAudit by remember { mutableStateOf(false) }

        // Automatically trigger Biometrics if configured and not locked out
        LaunchedEffect(authUiState.mode, authUiState.isBiometricsAvailable) {
            if (authUiState.mode == AuthScreenMode.LOGIN_PIN &&
                authUiState.isBiometricsAvailable &&
                !authUiState.isLockedOut &&
                !authUiState.isAuthenticated
            ) {
                triggerBiometricPrompt()
            }
        }

        when {
            // Priority 1: Threat Monitor Lockdown Active (DoS / Brute-force mitigation)
            authUiState.isLockedOut -> {
                ThreatLockoutScreen(
                    remainingSeconds = authUiState.lockoutSecondsRemaining,
                    threatLogs = threatLogs
                )
            }

            // Priority 2: Authentication Required (Setup PIN / Enter PIN / Biometric)
            !authUiState.isAuthenticated -> {
                AuthScreen(
                    uiState = authUiState,
                    onDigitEntered = { authViewModel.onDigitEntered(it) },
                    onDeleteDigit = { authViewModel.onDeleteDigit() },
                    onClearPin = { authViewModel.onClearPin() },
                    onBiometricTrigger = { triggerBiometricPrompt() }
                )
            }

            // Priority 3: Decoy Duress Safe (Plausible Deniability triggered by 999999)
            authUiState.isDecoyMode -> {
                DecoyVaultScreen(
                    onLockVault = {
                        authViewModel.lockVault()
                        currentTab = CyberNavTab.VAULT_ENCLAVE
                    }
                )
            }

            // Priority 4: Authenticated Master Vault Enclave
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentTab) {
                        CyberNavTab.VAULT_ENCLAVE -> {
                            VaultDashboardScreen(
                                uiState = vaultUiState,
                                onSearchChanged = { vaultViewModel.onSearchQueryChanged(it) },
                                onImportFile = { uri, name, mime, shred ->
                                    vaultViewModel.encryptAndImport(uri, name, mime, shred)
                                },
                                onPreviewFile = { record -> vaultViewModel.previewFile(record) },
                                onExportFile = { record, destUri -> vaultViewModel.exportDecrypted(record, destUri) },
                                onShredFile = { record -> vaultViewModel.shredAndDelete(record) },
                                onLockEnclave = {
                                    vaultViewModel.closePreview()
                                    authViewModel.lockVault()
                                    currentTab = CyberNavTab.VAULT_ENCLAVE
                                },
                                onOpenSecurityAudit = { showSecurityAudit = true },
                                onClearStatusMessage = { vaultViewModel.clearStatusMessage() }
                            )
                        }
                        CyberNavTab.CYBER_LAB -> {
                            CyberLabScreen(viewModel = cyberLabViewModel)
                        }
                        CyberNavTab.THREAT_HUD -> {
                            ThreatHudScreen(
                                threatLogs = threatLogs,
                                onOpenSecurityAudit = { showSecurityAudit = true }
                            )
                        }
                    }

                    // Floating Cyber Bottom Navigation Bar
                    CyberBottomNav(
                        selectedTab = currentTab,
                        onTabSelected = { currentTab = it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    // Ephemeral File Preview Dialog
                    vaultUiState.activePreview?.let { preview ->
                        FilePreviewDialog(
                            activePreview = preview,
                            onDismiss = { vaultViewModel.closePreview() }
                        )
                    }

                    // Security Diagnostics Inspection Dialog
                    if (showSecurityAudit) {
                        val diagnostics = remember { app.keystoreManager.getSecurityDiagnostics() }
                        SecurityAuditDialog(
                            diagnostics = diagnostics,
                            onDismiss = { showSecurityAudit = false }
                        )
                    }
                }
            }
        }
    }

    private fun triggerBiometricPrompt() {
        biometricAuthManager.authenticate(
            activity = this,
            title = getString(R.string.biometric_title),
            subtitle = getString(R.string.biometric_subtitle),
            negativeButtonText = getString(R.string.biometric_negative),
            onSuccess = {
                authViewModel.onBiometricSuccess()
            },
            onFallbackToPin = {
                // User explicitly requested PIN fallback
            },
            onFailed = {
                authViewModel.onBiometricFailed()
            },
            onError = { _, _ ->
                // Handled gracefully in UI state
            }
        )
    }

    override fun onStop() {
        super.onStop()
        // Memory Sanitization: purge active in-memory decrypted previews when app enters background
        vaultViewModel.closePreview()
        MemorySanitizer.purgeAll()
    }
}
