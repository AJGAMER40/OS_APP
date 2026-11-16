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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.threat.ThreatEventType
import com.zerovault.app.threat.ThreatLogEvent
import com.zerovault.app.threat.ThreatSeverity
import com.zerovault.app.ui.components.SanctuaryCard
import com.zerovault.app.ui.components.SanctuaryOutlinedButton
import com.zerovault.app.ui.components.SanctuaryScaffold
import com.zerovault.app.ui.components.SanctuaryStatusChip
import com.zerovault.app.ui.components.SanctuaryStatusLevel
import com.zerovault.app.ui.theme.SanctuaryAmberLight
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldLight
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
 * ThreatHudScreen provides a dense, inspectable telemetry dashboard for runtime security events.
 *
 * Design & Security Principles:
 * - High density of telemetry without theatrical flashing or deceptive certainty
 * - Honest statistics derived from real session event logs
 * - Quick inspection of verified Keystore hardware specifications
 * - Chronological event log with clean monospace timestamps and semantic status badges
 */
@Composable
fun ThreatHudScreen(
    threatLogs: List<ThreatLogEvent>,
    onOpenSecurityAudit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val failures = threatLogs.count { it.type == ThreatEventType.AUTH_FAILURE }
    val lockouts = threatLogs.count {
        it.type == ThreatEventType.BRUTE_FORCE_TRIGGER || it.type == ThreatEventType.WORKMANAGER_ENGAGED
    }
    val duressTrips = threatLogs.count { it.type == ThreatEventType.COERCION_DURESS }

    SanctuaryScaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SECURITY TELEMETRY",
                            style = SanctuaryEyebrowStyle,
                            color = SanctuaryGoldLight,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Threat Posture & Audit",
                            style = SanctuaryTypography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SanctuaryWhite
                        )
                    }

                    SanctuaryStatusChip(
                        label = if (lockouts > 0) "LOCKOUT LOGGED" else "POSTURE SECURE",
                        statusLevel = if (lockouts > 0) SanctuaryStatusLevel.LOCKDOWN else SanctuaryStatusLevel.SECURE
                    )
                }
            }

            // Stat Cards Row (Denser information layout)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HudStatCard(
                        modifier = Modifier.weight(1f),
                        label = "FAILED PROBES",
                        value = "$failures",
                        color = if (failures > 0) SanctuaryCrimsonLight else SanctuaryEmeraldLight
                    )
                    HudStatCard(
                        modifier = Modifier.weight(1f),
                        label = "QUARANTINES",
                        value = "$lockouts",
                        color = if (lockouts > 0) SanctuaryCrimsonLight else SanctuaryGold
                    )
                    HudStatCard(
                        modifier = Modifier.weight(1f),
                        label = "DURESS ALERTS",
                        value = "$duressTrips",
                        color = if (duressTrips > 0) SanctuaryAmberLight else SanctuaryGoldLight
                    )
                }
            }

            // Hardware Keystore Diagnostics Trigger
            item {
                SanctuaryOutlinedButton(
                    text = "Inspect Keystore Specifications",
                    leadingIcon = Icons.Default.Shield,
                    onClick = onOpenSecurityAudit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )
            }

            // Event Chronology Section Header
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EVENT AUDIT CHRONOLOGY (${threatLogs.size})",
                        style = SanctuaryEyebrowStyle,
                        color = SanctuaryGold,
                        fontSize = 11.sp
                    )
                }
            }

            // Empty or Populated Log List
            if (threatLogs.isEmpty()) {
                item {
                    SanctuaryCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No security events recorded in this session.",
                                style = SanctuaryTypography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SanctuaryWhite,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Authentication probes, quarantine lockouts, and memory sanitizations will appear here chronologically.",
                                style = SanctuaryTypography.bodySmall,
                                color = SanctuaryMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                items(threatLogs.asReversed()) { logEvent ->
                    ThreatHudLogCard(event = logEvent)
                }
            }

            // Bottom space for bottom navigation bar
            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@Composable
private fun HudStatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    SanctuaryCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = SanctuaryMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = color
            )
        }
    }
}

@Composable
private fun ThreatHudLogCard(event: ThreatLogEvent) {
    val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    val timeStr = formatter.format(Date(event.timestamp))

    val (badgeText, badgeColor) = when (event.type) {
        ThreatEventType.AUTH_FAILURE -> "INTRUSION PROBE" to SanctuaryCrimsonLight
        ThreatEventType.BRUTE_FORCE_TRIGGER -> "BRUTE FORCE TRIP" to SanctuaryCrimsonLight
        ThreatEventType.WORKMANAGER_ENGAGED -> "QUARANTINE ACTIVE" to SanctuaryCrimsonLight
        ThreatEventType.AUTH_SUCCESS -> "AUTHORIZED ACCESS" to SanctuaryEmeraldLight
        ThreatEventType.COERCION_DURESS -> "DURESS COERCION" to SanctuaryAmberLight
        ThreatEventType.RAM_SANITIZED -> "RAM WIPED" to SanctuaryEmeraldLight
        ThreatEventType.LOCKOUT_EXPIRED -> "COOLDOWN ENDED" to SanctuaryEmeraldLight
        ThreatEventType.MANUAL_LOCK -> "MANUAL LOCK" to SanctuaryMuted
    }

    SanctuaryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = badgeColor
                    )
                }

                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SanctuaryStone
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.description,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = SanctuaryWhite,
                lineHeight = 15.sp
            )
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "ThreatHud - Populated", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatHudPopulatedPreview() {
    val sampleLogs = listOf(
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 60000,
            type = ThreatEventType.AUTH_FAILURE,
            description = "Failed authentication attempt (Attempt 1 of 3)",
            severity = ThreatSeverity.WARNING
        ),
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 30000,
            type = ThreatEventType.AUTH_SUCCESS,
            description = "Primary enclave master PIN verified successfully",
            severity = ThreatSeverity.INFO
        ),
        ThreatLogEvent(
            timestamp = System.currentTimeMillis() - 5000,
            type = ThreatEventType.RAM_SANITIZED,
            description = "Volatile memory purged: ephemeral secret buffers zeroed",
            severity = ThreatSeverity.INFO
        )
    )

    SanctuaryTheme(darkTheme = true) {
        ThreatHudScreen(
            threatLogs = sampleLogs,
            onOpenSecurityAudit = {}
        )
    }
}

@Preview(name = "ThreatHud - Empty Pristine", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatHudEmptyPreview() {
    SanctuaryTheme(darkTheme = true) {
        ThreatHudScreen(
            threatLogs = emptyList(),
            onOpenSecurityAudit = {}
        )
    }
}