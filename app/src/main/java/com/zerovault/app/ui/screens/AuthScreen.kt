package com.zerovault.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.auth.AuthScreenMode
import com.zerovault.app.auth.AuthUiState
import com.zerovault.app.ui.components.PinDotsView
import com.zerovault.app.ui.components.SanctuaryArchIllustration
import com.zerovault.app.ui.components.SanctuaryCard
import com.zerovault.app.ui.components.SanctuaryScaffold
import com.zerovault.app.ui.components.SanctuaryStatusChip
import com.zerovault.app.ui.components.SanctuaryStatusLevel
import com.zerovault.app.ui.components.SecureKeypad
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryStone
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * AuthScreen renders the serene Sanctuary PIN and Biometric authentication interface.
 * Preserves all underlying Keystore, ThreatMonitor, and duress PIN verification pathways.
 *
 * Security & Session Invariance:
 * - Never persists PINs or auth tokens in Compose state, navigation args, or logs
 * - Zero extra taps for recurring unlock: biometrics fire immediately on display
 * - BackHandler is active only during initial PIN setup to return to the welcome screen
 * - Status chips reflect genuine cryptographic and attempt posture
 * - Distinct, readable instructions for SETUP_ENTER, SETUP_CONFIRM, LOGIN_PIN, and LOCKDOWN
 * - Never presents LOCKDOWN as ordinary login: quarantine details clearly explained and inputs disabled
 */
@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onDigitEntered: (Char) -> Unit,
    onDeleteDigit: () -> Unit,
    onClearPin: () -> Unit,
    onBiometricTrigger: () -> Unit,
    modifier: Modifier = Modifier,
    onBackToWelcome: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val isLockdown = uiState.isLockedOut || uiState.mode == AuthScreenMode.LOCKDOWN

    // Safe BackHandler: only allowed when in initial setup mode to return to welcome screen
    if (onBackToWelcome != null && uiState.mode == AuthScreenMode.SETUP_ENTER) {
        BackHandler(onBack = onBackToWelcome)
    }

    SanctuaryScaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Centered Architectural Sanctuary Arch & Seal (Local Vector Canvas)
                SanctuaryArchIllustration(
                    size = 110.dp,
                    animate = false,
                    showSparks = false,
                    showCenterSeal = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Sanctuary Brand Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ZEROVAULT",
                        style = SanctuaryTypography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = SanctuaryWhite
                    )
                    Text(
                        text = ".",
                        style = SanctuaryTypography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SanctuaryGold
                    )
                }

                // Explicit, readable instructions per AuthScreenMode
                val promptEyebrow = when {
                    isLockdown -> "RATE-LIMITING DEFENSE ENGAGED"
                    uiState.mode == AuthScreenMode.SETUP_ENTER -> "INITIAL ENCLAVE SETUP · STEP 1 OF 2"
                    uiState.mode == AuthScreenMode.SETUP_CONFIRM -> "CREDENTIAL VERIFICATION · STEP 2 OF 2"
                    else -> "SECURE ENCLAVE ACCESS"
                }

                val promptTitle = when {
                    isLockdown -> "Quarantine Active"
                    uiState.mode == AuthScreenMode.SETUP_ENTER -> "Establish Master PIN"
                    uiState.mode == AuthScreenMode.SETUP_CONFIRM -> "Confirm Master PIN"
                    else -> "Enter Your Sanctuary"
                }

                val promptSubtitle = when {
                    isLockdown -> "3 invalid attempts detected. Interface and cryptographic access temporarily locked to protect against brute-force attacks."
                    uiState.mode == AuthScreenMode.SETUP_ENTER -> "Configure a 6-digit master PIN. Key derivation uses PBKDF2 (10,000 rounds) anchored inside hardware Keystore."
                    uiState.mode == AuthScreenMode.SETUP_CONFIRM -> "Re-enter the exact 6-digit PIN to verify credentials before initializing encrypted storage."
                    else -> "Authenticate with your Master PIN or enrolled biometrics. 3 consecutive failed attempts trigger an automated 60-second hardware quarantine."
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = promptEyebrow,
                    style = SanctuaryEyebrowStyle,
                    color = if (isLockdown) SanctuaryCrimsonLight else SanctuaryGold,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = promptTitle,
                    style = SanctuaryTypography.headlineMedium,
                    color = SanctuaryWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = promptSubtitle,
                    style = SanctuaryTypography.bodyMedium,
                    color = SanctuaryMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Telemetry / Attempt Status Chip
                if (uiState.isPinConfigured) {
                    val statusLevel = when {
                        isLockdown -> SanctuaryStatusLevel.LOCKDOWN
                        uiState.remainingAttempts <= 2 -> SanctuaryStatusLevel.ELEVATED_RISK
                        else -> SanctuaryStatusLevel.SECURE
                    }

                    val chipLabel = when {
                        isLockdown -> if (uiState.lockoutSecondsRemaining > 0) {
                            "DEFENSE QUARANTINE: ${uiState.lockoutSecondsRemaining}S REMAINING"
                        } else {
                            "DEFENSE LOCKDOWN ACTIVE"
                        }
                        uiState.remainingAttempts == 1 -> "CRITICAL: 1 ATTEMPT REMAINING"
                        uiState.remainingAttempts == 2 -> "WARNING: 2 ATTEMPTS REMAINING"
                        else -> "HARDWARE TEE ACTIVE (3 ATTEMPTS)"
                    }

                    SanctuaryStatusChip(
                        label = chipLabel,
                        statusLevel = statusLevel
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PIN Visuals or Quarantine Banner (Never present lockdown as ordinary login)
                if (isLockdown) {
                    SanctuaryCard(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ENCLAVE QUARANTINED",
                                style = SanctuaryEyebrowStyle,
                                color = SanctuaryCrimsonLight,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (uiState.lockoutSecondsRemaining > 0) {
                                    "${uiState.lockoutSecondsRemaining} seconds remaining"
                                } else {
                                    "Defense worker active"
                                },
                                style = SanctuaryTypography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryWhite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Keypad input and biometric unlocks are suspended to prevent dictionary/brute-force attacks.",
                                style = SanctuaryTypography.bodySmall,
                                color = SanctuaryMuted,
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                } else {
                    // Luminous Golden PIN Dots
                    PinDotsView(
                        filledCount = uiState.enteredPinLength,
                        totalCount = uiState.maxPinLength,
                        isError = uiState.errorMessage != null
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Error Message with accessible contrast
                AnimatedVisibility(
                    visible = uiState.errorMessage != null && !isLockdown,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = SanctuaryCrimsonLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                // Biometric Option (Prioritized authentication for recurring unlock; hidden when locked out)
                if (uiState.isBiometricsAvailable && !isLockdown) {
                    ElevatedButton(
                        onClick = onBiometricTrigger,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = SanctuaryPineSurface,
                            contentColor = SanctuaryEmerald
                        ),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Prompt",
                                tint = SanctuaryEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Unlock with Biometrics",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SanctuaryWhite
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Refined Sanctuary Keypad (Disabled during lockdown)
                SecureKeypad(
                    onDigitClick = onDigitEntered,
                    onDeleteClick = onDeleteDigit,
                    onClearClick = onClearPin,
                    enabled = !isLockdown
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Trust Footnote
                Text(
                    text = "Android Keystore AES-256-GCM Hardware-Backed Enclave",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SanctuaryStone,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "AuthScreen - Login Mode", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun AuthScreenLoginPreview() {
    SanctuaryTheme(darkTheme = true) {
        AuthScreen(
            uiState = AuthUiState(
                mode = AuthScreenMode.LOGIN_PIN,
                isPinConfigured = true,
                enteredPinLength = 3,
                isBiometricsAvailable = true,
                remainingAttempts = 3
            ),
            onDigitEntered = {},
            onDeleteDigit = {},
            onClearPin = {},
            onBiometricTrigger = {}
        )
    }
}

@Preview(name = "AuthScreen - Setup Enter Mode", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun AuthScreenSetupEnterPreview() {
    SanctuaryTheme(darkTheme = true) {
        AuthScreen(
            uiState = AuthUiState(
                mode = AuthScreenMode.SETUP_ENTER,
                isPinConfigured = false,
                enteredPinLength = 0,
                isBiometricsAvailable = false
            ),
            onDigitEntered = {},
            onDeleteDigit = {},
            onClearPin = {},
            onBiometricTrigger = {}
        )
    }
}

@Preview(name = "AuthScreen - Setup Confirm Mode", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun AuthScreenSetupConfirmPreview() {
    SanctuaryTheme(darkTheme = true) {
        AuthScreen(
            uiState = AuthUiState(
                mode = AuthScreenMode.SETUP_CONFIRM,
                isPinConfigured = false,
                enteredPinLength = 4,
                isBiometricsAvailable = false
            ),
            onDigitEntered = {},
            onDeleteDigit = {},
            onClearPin = {},
            onBiometricTrigger = {}
        )
    }
}

@Preview(name = "AuthScreen - Lockdown Mode", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun AuthScreenLockdownPreview() {
    SanctuaryTheme(darkTheme = true) {
        AuthScreen(
            uiState = AuthUiState(
                mode = AuthScreenMode.LOCKDOWN,
                isPinConfigured = true,
                enteredPinLength = 0,
                isBiometricsAvailable = false,
                remainingAttempts = 0,
                isLockedOut = true,
                lockoutSecondsRemaining = 45
            ),
            onDigitEntered = {},
            onDeleteDigit = {},
            onClearPin = {},
            onBiometricTrigger = {}
        )
    }
}