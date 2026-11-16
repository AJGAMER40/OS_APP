package com.zerovault.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.auth.AuthScreenMode
import com.zerovault.app.auth.AuthUiState
import com.zerovault.app.ui.components.PinDotsView
import com.zerovault.app.ui.components.QuantumShieldCanvas
import com.zerovault.app.ui.components.SecureKeypad
import com.zerovault.app.ui.components.ThreatBadge
import com.zerovault.app.ui.theme.TextMuted
import com.zerovault.app.ui.theme.TextPrimary
import com.zerovault.app.ui.theme.TextSecondary
import com.zerovault.app.ui.theme.VaultBg
import com.zerovault.app.ui.theme.VaultCrimson
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultEmerald
import com.zerovault.app.ui.theme.VaultSurface

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onDigitEntered: (Char) -> Unit,
    onDeleteDigit: () -> Unit,
    onClearPin: () -> Unit,
    onBiometricTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VaultBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Animated Quantum Shield
                QuantumShieldCanvas(
                    size = 80.dp,
                    shieldColor = VaultCyan
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ZEROVAULT",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 3.sp,
                    color = TextPrimary
                )

                val promptTitle = when (uiState.mode) {
                    AuthScreenMode.SETUP_ENTER -> "Configure Master PIN"
                    AuthScreenMode.SETUP_CONFIRM -> "Confirm Master PIN"
                    AuthScreenMode.LOGIN_PIN -> "Encrypted Enclave Locked"
                    AuthScreenMode.LOCKDOWN -> "Defense Lockdown Active"
                }

                val promptSubtitle = when (uiState.mode) {
                    AuthScreenMode.SETUP_ENTER -> "Establish a 6-digit master PIN protected by hardware Keystore"
                    AuthScreenMode.SETUP_CONFIRM -> "Re-enter the 6-digit PIN to finalize encryption parameters"
                    AuthScreenMode.LOGIN_PIN -> "Authenticate using Biometrics or Master PIN"
                    AuthScreenMode.LOCKDOWN -> "System under active brute-force quarantine"
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = promptTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VaultCyan
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = promptSubtitle,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Threat Status Pill
                if (uiState.isPinConfigured) {
                    ThreatBadge(
                        remainingAttempts = uiState.remainingAttempts,
                        isLockedOut = uiState.isLockedOut
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PIN Dots
                PinDotsView(
                    filledCount = uiState.enteredPinLength,
                    totalCount = uiState.maxPinLength,
                    isError = uiState.errorMessage != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Error Message
                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = VaultCrimson,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Biometric Option (Prioritized authentication)
            if (uiState.isBiometricsAvailable) {
                ElevatedButton(
                    onClick = onBiometricTrigger,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = VaultSurface,
                        contentColor = VaultEmerald
                    ),
                    shape = RoundedCornerShape(12.dp),
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
                            tint = VaultEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Authenticate with Biometrics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Keypad
            SecureKeypad(
                onDigitClick = onDigitEntered,
                onDeleteClick = onDeleteDigit,
                onClearClick = onClearPin,
                enabled = !uiState.isLockedOut
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer security notice
            Text(
                text = "Android Keystore AES-256-GCM Hardware-Backed Enclave",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
        }
    }
}
