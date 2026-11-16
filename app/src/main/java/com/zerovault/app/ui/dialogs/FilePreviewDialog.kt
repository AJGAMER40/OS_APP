package com.zerovault.app.ui.dialogs

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerovault.app.ui.components.SanctuaryButton
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineBorder
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryStone
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite
import com.zerovault.app.vault.ActivePreview

/**
 * FilePreviewDialog presents decrypted file content ephemerally in RAM.
 *
 * Security & Lifecycle Invariants:
 * - Content is never written to public caches, shared storage, or temporary disk files
 * - Volatile RAM buffer is overwritten with zeroes by ActivePreview.wipe() upon dismissal
 * - Window is protected by FLAG_SECURE preventing screenshot capture and task switcher leakage
 */
@Composable
fun FilePreviewDialog(
    activePreview: ActivePreview,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val record = activePreview.record

    val bitmap = remember(activePreview) {
        if (record.mimeType.startsWith("image/") ||
            record.originalFileName.endsWith(".png") ||
            record.originalFileName.endsWith(".jpg") ||
            record.originalFileName.endsWith(".jpeg")
        ) {
            try {
                BitmapFactory.decodeByteArray(activePreview.data, 0, activePreview.data.size)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SanctuaryPineSurface)
                .border(1.dp, SanctuaryPineBorder, RoundedCornerShape(16.dp))
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SanctuaryGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "EPHEMERAL DECRYPTION",
                            style = SanctuaryEyebrowStyle,
                            color = SanctuaryGoldLight,
                            fontSize = 9.sp
                        )
                        Text(
                            text = record.originalFileName,
                            style = SanctuaryTypography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SanctuaryWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close & Wipe RAM",
                        tint = SanctuaryMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ephemeral RAM Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SanctuaryPine)
                    .border(1.dp, SanctuaryEmeraldLight.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "VOLATILE RAM BUFFER: Decrypted payload exists solely in application heap memory. It will be overwritten with zeroes immediately upon closing.",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SanctuaryEmeraldLight,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Decrypted Preview Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SanctuaryPine)
                    .border(1.dp, SanctuaryBorderSubtle, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Decrypted Image Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                } else if (activePreview.textContent != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = activePreview.textContent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SanctuaryWhite,
                            lineHeight = 16.sp
                        )
                    }
                } else {
                    // Binary Payload Overview
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Binary Payload Decrypted in RAM",
                            style = SanctuaryTypography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SanctuaryWhite
                        )

                        Text(
                            text = "MIME Type: ${record.mimeType}\nSize: ${record.sizeBytes} bytes\nSHA-256: ${record.sha256Checksum}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SanctuaryMuted,
                            lineHeight = 16.sp
                        )

                        Text(
                            text = "Use 'Export Decrypted Copy' from the vault list if you need to write this payload to external storage.",
                            style = SanctuaryTypography.bodySmall,
                            color = SanctuaryStone,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Close & Zero-Sanitize RAM Button
            SanctuaryButton(
                text = "Close & Zero-Sanitize RAM",
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            )
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "FilePreviewDialog - Text Preview", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun FilePreviewDialogTextPreview() {
    val sampleRecord = com.zerovault.app.data.model.VaultFileRecord(
        id = "1",
        originalFileName = "Confidential_Recovery_Phrases.txt",
        encryptedFileName = "enc_3b8a1c99.vault",
        mimeType = "text/plain",
        sizeBytes = 256L,
        encryptedAt = System.currentTimeMillis(),
        sha256Checksum = "e5d4c3b2a1"
    )
    val samplePreview = ActivePreview(
        record = sampleRecord,
        data = "sample payload".toByteArray(),
        isText = true,
        textContent = "1. alpha\n2. bravo\n3. charlie\n4. delta\n5. echo\n6. foxtrot"
    )

    com.zerovault.app.ui.theme.SanctuaryTheme(darkTheme = true) {
        FilePreviewDialog(
            activePreview = samplePreview,
            onDismiss = {}
        )
    }
}