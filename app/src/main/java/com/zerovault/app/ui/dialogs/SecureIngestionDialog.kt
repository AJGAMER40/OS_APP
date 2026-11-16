package com.zerovault.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerovault.app.ui.theme.BorderSubtle
import com.zerovault.app.ui.theme.TextPrimary
import com.zerovault.app.ui.theme.TextSecondary
import com.zerovault.app.ui.theme.VaultBg
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultEmerald
import com.zerovault.app.ui.theme.VaultSurface
import com.zerovault.app.ui.theme.VaultSurfaceCard

@Composable
fun SecureIngestionDialog(
    fileName: String,
    onConfirmIngestion: (shredOriginal: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var shredSelected by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(VaultSurfaceCard)
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(VaultCyan.copy(alpha = 0.15f))
                    .border(1.5.dp, VaultCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = VaultCyan,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Secure Enclave Ingestion",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Target: $fileName",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = VaultCyan,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 1: DoD 5220.22-M Move & Eradicate (Recommended)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (shredSelected) VaultEmerald.copy(alpha = 0.15f) else VaultSurface)
                    .border(
                        width = 1.5.dp,
                        color = if (shredSelected) VaultEmerald else BorderSubtle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { shredSelected = true }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = VaultEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "DoD 5220.22-M Move & Shred",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Encrypts into hardware enclave, then overwrites original flash storage in 3 passes (0x55, 0xAA, Noise) to prevent forensic file carving.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Copy Only
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!shredSelected) VaultCyan.copy(alpha = 0.15f) else VaultSurface)
                    .border(
                        width = 1.5.dp,
                        color = if (!shredSelected) VaultCyan else BorderSubtle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { shredSelected = false }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = VaultCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Standard Encrypt (Keep Original)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Creates an encrypted enclave duplicate. Leaves unencrypted source intact in your Downloads/Gallery.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VaultSurface,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", fontSize = 12.sp)
                }

                Button(
                    onClick = { onConfirmIngestion(shredSelected) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (shredSelected) VaultEmerald else VaultCyan,
                        contentColor = VaultBg
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text(
                        text = if (shredSelected) "Shred & Vault" else "Encrypt Copy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
