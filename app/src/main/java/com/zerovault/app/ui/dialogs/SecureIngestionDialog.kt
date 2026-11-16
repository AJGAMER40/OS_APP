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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerovault.app.ui.components.SanctuaryButton
import com.zerovault.app.ui.components.SanctuaryOutlinedButton
import com.zerovault.app.ui.theme.SanctuaryAmberLight
import com.zerovault.app.ui.theme.SanctuaryBorderMedium
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryDeep
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryEmeraldContainer
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineBorder
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryStone
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * SecureIngestionDialog prompts the user to select how an imported file is ingested.
 *
 * Security & Technical Accuracy:
 * - Text clarity wins over decorative hype for destructive options
 * - Truthful explanation of flash storage characteristics: explains that 3-pass overwrite (0x55, 0xAA, noise)
 *   overwrites addressable sectors, but wear-leveling / FTL means physical block remnants cannot be
 *   100% guaranteed without hardware-level secure erase.
 * - Explains that AES-256-GCM hardware enclave encryption is the primary protection.
 */
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
                .clip(RoundedCornerShape(16.dp))
                .background(SanctuaryPineSurface)
                .border(1.dp, SanctuaryPineBorder, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SanctuaryPine)
                    .border(1.dp, SanctuaryGoldLight.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = SanctuaryGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ENCLAVE INGESTION",
                style = SanctuaryEyebrowStyle,
                color = SanctuaryGoldLight,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Import & Encrypt File",
                style = SanctuaryTypography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SanctuaryWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = fileName,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = SanctuaryGoldLight,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option 1: 3-Pass Software Overwrite & Unlink (Recommended)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (shredSelected) SanctuaryPine else SanctuaryPineSurface)
                    .border(
                        width = if (shredSelected) 1.5.dp else 1.dp,
                        color = if (shredSelected) SanctuaryGold else SanctuaryPineBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { shredSelected = true }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = if (shredSelected) SanctuaryGold else SanctuaryMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "3-Pass Overwrite & Unlink",
                                style = SanctuaryTypography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryWhite,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SanctuaryEmeraldContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "RECOMMENDED",
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SanctuaryEmeraldLight
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Encrypts into hardware enclave, then overwrites source file in 3 passes (0x55, 0xAA, random noise) and syncs before unlinking. Note: Due to flash wear-leveling (FTL), physical block copies cannot be guaranteed eradicated; enclave encryption is your primary security barrier.",
                            style = SanctuaryTypography.bodySmall,
                            color = SanctuaryMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Copy Only (Keep Original)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!shredSelected) SanctuaryPine else SanctuaryPineSurface)
                    .border(
                        width = if (!shredSelected) 1.5.dp else 1.dp,
                        color = if (!shredSelected) SanctuaryGold else SanctuaryPineBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { shredSelected = false }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (!shredSelected) SanctuaryGold else SanctuaryMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Standard Encrypt (Keep Original)",
                            style = SanctuaryTypography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SanctuaryWhite,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Creates an encrypted enclave duplicate. Leaves the unencrypted original intact in its current public storage location.",
                            style = SanctuaryTypography.bodySmall,
                            color = SanctuaryMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SanctuaryOutlinedButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )

                SanctuaryButton(
                    text = if (shredSelected) "Shred & Vault" else "Encrypt Copy",
                    onClick = { onConfirmIngestion(shredSelected) },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                )
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "SecureIngestionDialog - Ingestion Prompt", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SecureIngestionDialogPreview() {
    com.zerovault.app.ui.theme.SanctuaryTheme(darkTheme = true) {
        SecureIngestionDialog(
            fileName = "Offshore_Trust_Declaration_2026.pdf",
            onConfirmIngestion = {},
            onDismiss = {}
        )
    }
}