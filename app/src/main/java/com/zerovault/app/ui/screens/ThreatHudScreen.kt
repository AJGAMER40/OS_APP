package com.zerovault.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.threat.ThreatEventType
import com.zerovault.app.threat.ThreatLogEvent
import com.zerovault.app.ui.theme.BorderSubtle
import com.zerovault.app.ui.theme.TextMuted
import com.zerovault.app.ui.theme.TextPrimary
import com.zerovault.app.ui.theme.TextSecondary
import com.zerovault.app.ui.theme.VaultBg
import com.zerovault.app.ui.theme.VaultCrimson
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultEmerald
import com.zerovault.app.ui.theme.VaultSurface
import com.zerovault.app.ui.theme.VaultSurfaceCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreatHudScreen(
    threatLogs: List<ThreatLogEvent>,
    onOpenSecurityAudit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_radar")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hudPulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VaultBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VaultSurfaceCard)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = VaultCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "THREAT MONITOR HUD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                            Text(
                                text = "LIVE INTRUSION TELEMETRY",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = VaultCyan,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Pulse indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(VaultSurfaceCard)
                            .border(1.dp, VaultEmerald.copy(alpha = pulseAlpha), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "ARMED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = VaultEmerald
                        )
                    }
                }
            }

            // Stat Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val failures = threatLogs.count { it.type == ThreatEventType.AUTH_FAILURE }
                    val lockouts = threatLogs.count { it.type == ThreatEventType.BRUTE_FORCE_TRIGGER || it.type == ThreatEventType.WORKMANAGER_ENGAGED }

                    HudStatCard(
                        modifier = Modifier.weight(1f),
                        label = "FAILED PROBES",
                        value = "$failures",
                        color = if (failures > 0) VaultCrimson else VaultEmerald
                    )
                    HudStatCard(
                        modifier = Modifier.weight(1f),
                        label = "LOCKOUTS",
                        value = "$lockouts",
                        color = if (lockouts > 0) VaultCrimson else VaultCyan
                    )
                    HudStatCard(
                        modifier = Modifier.weight(1f),
                        label = "DURESS SAFE",
                        value = "ARMED",
                        color = VaultCyan
                    )
                }
            }

            // Audit Action Button
            item {
                Button(
                    onClick = onOpenSecurityAudit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VaultSurfaceCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = VaultCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIEW HARDWARE KEYSTORE SPECIFICATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    )
                }
            }

            // Event Log Section Title
            item {
                Text(
                    text = "DEFENSE EVENT CHRONOLOGY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (threatLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VaultSurfaceCard)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No security events recorded in this session.\nVault integrity pristine.",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
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
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(VaultSurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = color
        )
    }
}

@Composable
private fun ThreatHudLogCard(event: ThreatLogEvent) {
    val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    val timeStr = formatter.format(Date(event.timestamp))

    val (badgeText, badgeColor) = when (event.type) {
        ThreatEventType.AUTH_FAILURE -> "INTRUSION PROBE" to VaultCrimson
        ThreatEventType.BRUTE_FORCE_TRIGGER -> "BRUTE FORCE TRIP" to VaultCrimson
        ThreatEventType.WORKMANAGER_ENGAGED -> "LOCKOUT ACTIVATED" to VaultCrimson
        ThreatEventType.AUTH_SUCCESS -> "AUTHORIZED ACCESS" to VaultEmerald
        ThreatEventType.COERCION_DURESS -> "DURESS COERCION" to Color(0xFFFFB300)
        ThreatEventType.RAM_SANITIZED -> "RAM WIPED" to VaultCyan
        ThreatEventType.LOCKOUT_EXPIRED -> "COOLDOWN ENDED" to VaultEmerald
        ThreatEventType.MANUAL_LOCK -> "MANUAL LOCK" to TextSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(VaultSurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
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
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = event.description,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = TextPrimary,
            lineHeight = 16.sp
        )
    }
}
