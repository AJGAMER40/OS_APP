package com.zerovault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.threat.ThreatEventType
import com.zerovault.app.threat.ThreatLogEvent
import com.zerovault.app.threat.ThreatSeverity
import com.zerovault.app.ui.components.SanctuaryCard
import com.zerovault.app.ui.components.SanctuaryScaffold
import com.zerovault.app.ui.components.SanctuaryStatusChip
import com.zerovault.app.ui.components.SanctuaryStatusLevel
import com.zerovault.app.ui.theme.SanctuaryAmber
import com.zerovault.app.ui.theme.SanctuaryAmberBg
import com.zerovault.app.ui.theme.SanctuaryAmberLight
import com.zerovault.app.ui.theme.SanctuaryCrimson
import com.zerovault.app.ui.theme.SanctuaryCrimsonBg
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineBorder
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryStone
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ThreatLockoutScreen renders the calm, authoritative defense quarantine interface.
 * Engaged when 3 consecutive invalid authentication attempts are detected.
 *
 * Security & Threat Model:
 * - Visibly distinct from ordinary login: all keypads and biometric triggers disabled
 * - Honest, informative presentation of active rate-limiting defense (no theatrical panic/sirens)
 * - Hardware and memory defenses verified: RAM purged, WorkManager persistent quarantine timer
 * - Chronological security event audit log displayed with verified timestamps
 */
@Composable
fun ThreatLockoutScreen(
    remainingSeconds: Int,
    threatLogs: List<ThreatLogEvent>,
    modifier: Modifier = Modifier
) {
    val totalSeconds = 60
    val progress = (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    val scrollState = rememberScrollState()

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
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Rate-Limiting Quarantine Status Chip
                SanctuaryStatusChip(
                    label = "RATE-LIMITING DEFENSE ENGAGED",
                    statusLevel = SanctuaryStatusLevel.LOCKDOWN
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Calm, Dignified Shield Badge (No flashing sirens or theatrical panic)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SanctuaryCrimsonBg)
                        .border(1.5.dp, SanctuaryCrimson.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Defense Quarantine Active",
                        tint = SanctuaryCrimsonLight,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "RATE-LIMITING ACTIVE",
                    style = SanctuaryEyebrowStyle,
                    color = SanctuaryCrimsonLight,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Quarantine Active",
                    style = SanctuaryTypography.headlineMedium,
                    color = SanctuaryWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "3 invalid attempts detected. Interface and cryptographic operations quarantined for 60 seconds to protect master credentials against automated dictionary and brute-force attacks.",
                    style = SanctuaryTypography.bodyMedium,
                    color = SanctuaryMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Dignified Circular Countdown Indicator
                Box(
                    modifier = Modifier.size(144.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = SanctuaryCrimsonLight,
                        trackColor = SanctuaryPineSurface,
                        strokeWidth = 6.dp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format(Locale.US, "%02d", remainingSeconds),
                            style = SanctuaryTypography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = SanctuaryWhite
                        )
                        Text(
                            text = "SECONDS REMAINING",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SanctuaryCrimsonLight,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Active Defenses Triggered Checklist
                SanctuaryCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "AUTOMATED HARDWARE DEFENSES ENGAGED",
                            style = SanctuaryEyebrowStyle,
                            color = SanctuaryAmberLight,
                            fontSize = 10.sp
                        )

                        DefenseCheckItem(
                            title = "RAM Purged & Sanitized",
                            desc = "Ephemeral key material, decrypted buffers, and PIN digits overwritten with zeroes in memory."
                        )
                        DefenseCheckItem(
                            title = "WorkManager Quarantine Task",
                            desc = "Background defense worker actively tracking rate-limiting window across process restarts."
                        )
                        DefenseCheckItem(
                            title = "Interface Hard-Locked",
                            desc = "Numeric keypad, biometric prompts, and credential verification intents disabled."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Threat Event Log Terminal
                SanctuaryCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SECURITY AUDIT LOG",
                                style = SanctuaryEyebrowStyle,
                                color = SanctuaryCrimsonLight,
                                fontSize = 10.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SanctuaryCrimsonLight)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (threatLogs.isEmpty()) {
                            Text(
                                text = "Awaiting security event telemetry...",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SanctuaryStone,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(threatLogs) { event ->
                                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(event.timestamp))
                                    val tagColor = when (event.type) {
                                        ThreatEventType.AUTH_FAILURE -> SanctuaryCrimsonLight
                                        ThreatEventType.BRUTE_FORCE_TRIGGER -> SanctuaryCrimsonLight
                                        ThreatEventType.RAM_SANITIZED -> SanctuaryEmeraldLight
                                        ThreatEventType.WORKMANAGER_ENGAGED -> SanctuaryAmberLight
                                        ThreatEventType.LOCKOUT_EXPIRED -> SanctuaryEmeraldLight
                                        ThreatEventType.AUTH_SUCCESS -> SanctuaryEmeraldLight
                                        ThreatEventType.MANUAL_LOCK -> SanctuaryMuted
                                        ThreatEventType.COERCION_DURESS -> SanctuaryCrimsonLight
                                    }

                                    Text(
                                        text = "[$timeStr] ${event.description}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = tagColor,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DefenseCheckItem(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SanctuaryEmerald,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = SanctuaryTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = SanctuaryWhite,
                fontSize = 12.sp
            )
            Text(
                text = desc,
                style = SanctuaryTypography.bodySmall,
                color = SanctuaryMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "ThreatLockoutScreen - Active Countdown", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatLockoutScreenActivePreview() {
    val sampleLogs = listOf(
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 24000,
            type = ThreatEventType.AUTH_FAILURE,
            description = "Failed PIN authentication attempt (Attempt 1 of 3)",
            severity = ThreatSeverity.WARNING
        ),
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 14000,
            type = ThreatEventType.AUTH_FAILURE,
            description = "Failed PIN authentication attempt (Attempt 2 of 3)",
            severity = ThreatSeverity.WARNING
        ),
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 4000,
            type = ThreatEventType.BRUTE_FORCE_TRIGGER,
            description = "3 consecutive failures. Rate-limiting engaged for 60s",
            severity = ThreatSeverity.CRITICAL
        ),
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 3000,
            type = ThreatEventType.RAM_SANITIZED,
            description = "RAM purged: Decrypted buffers and keys overwritten with zeroes",
            severity = ThreatSeverity.INFO
        )
    )

    SanctuaryTheme(darkTheme = true) {
        ThreatLockoutScreen(
            remainingSeconds = 42,
            threatLogs = sampleLogs
        )
    }
}

@Preview(name = "ThreatLockoutScreen - Initial State", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatLockoutScreenInitialPreview() {
    SanctuaryTheme(darkTheme = true) {
        ThreatLockoutScreen(
            remainingSeconds = 60,
            threatLogs = emptyList()
        )
    }
}