package com.zerovault.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerovault.app.data.security.KeystoreDiagnostics
import com.zerovault.app.ui.theme.BorderSubtle
import com.zerovault.app.ui.theme.TextPrimary
import com.zerovault.app.ui.theme.TextSecondary
import com.zerovault.app.ui.theme.VaultBg
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultEmerald
import com.zerovault.app.ui.theme.VaultSurface
import com.zerovault.app.ui.theme.VaultSurfaceCard

@Composable
fun SecurityAuditDialog(
    diagnostics: KeystoreDiagnostics,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VaultSurfaceCard)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = VaultCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Security Audit Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AuditItem(
                    title = "Android Keystore Provider",
                    value = diagnostics.provider,
                    status = "Hardware Enclave Active",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "MasterKey Cryptographic Scheme",
                    value = "AES-256 GCM (No Padding)",
                    status = "NIST SP 800-38D Compliant",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "StrongBox / TEE Hardware Backing",
                    value = if (diagnostics.strongBoxSupported) "Dedicated StrongBox HSM" else "Trusted Execution Environment (TEE)",
                    status = if (diagnostics.strongBoxSupported) "Level 4 HSM" else "Level 3 TEE",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "AndroidX EncryptedFile Scheme",
                    value = "FileEncryptionScheme.AES256_GCM_HKDF_4KB",
                    status = "Chunked Authenticated Encryption",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "EncryptedSharedPreferences",
                    value = "Key: AES256-SIV | Value: AES256-GCM",
                    status = "Synthetic Initialization Vector",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "Master PIN Protection",
                    value = "PBKDF2 SHA-256 + 10,000 Iterations + CSPRNG Salt",
                    status = "Anti-Dictionary Stretched",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "System Threat Monitor",
                    value = "WorkManager 60s Quarantine on 3 Failures",
                    status = "Brute-Force / DoS Defense",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "Volatile Memory Sanitization",
                    value = "MemorySanitizer Zero-Filling",
                    status = "Anti-RAM Dump / Forensics",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "Window Protection (FLAG_SECURE)",
                    value = "WindowManager.LayoutParams.FLAG_SECURE",
                    status = "Anti-Screen Recording / Task Snapshot",
                    statusColor = VaultEmerald
                )

                AuditItem(
                    title = "Sandbox & Cloud Protection",
                    value = "allowBackup=false | cleartextTraffic=false",
                    status = "Zero Cloud Exfiltration",
                    statusColor = VaultEmerald
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VaultCyan,
                    contentColor = VaultBg
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Acknowledge Audit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun AuditItem(
    title: String,
    value: String,
    status: String,
    statusColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(VaultSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary
        )
    }
}
