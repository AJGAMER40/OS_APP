package com.zerovault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerovault.app.data.security.DecoyFile
import com.zerovault.app.data.security.DecoyVaultManager
import com.zerovault.app.ui.components.SanctuaryArchIllustration
import com.zerovault.app.ui.components.SanctuaryButton
import com.zerovault.app.ui.components.SanctuaryCard
import com.zerovault.app.ui.components.SanctuaryOutlinedButton
import com.zerovault.app.ui.components.SanctuaryScaffold
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineBorder
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * DecoyVaultScreen renders a plausible, authentic-looking safe environment.
 * Engaged when the secondary duress PIN (999999) is entered under coercion.
 *
 * Security & Separation Invariants:
 * - 100% strict separation from real vault: zero encrypted file IDs, records, or counts are accessed
 * - Never references true vault statistics, Keystore master keys, or actual storage directories
 * - Appears visually consistent with the Sanctuary design language to prevent duress detection
 * - Visible Lock action immediately locks the session
 */
@Composable
fun DecoyVaultScreen(
    onLockVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDecoy by remember { mutableStateOf<DecoyFile?>(null) }

    SanctuaryScaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SanctuaryArchIllustration(
                        size = 36.dp,
                        animate = false,
                        showSparks = false,
                        showCenterSeal = true
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PERSONAL VAULT",
                                style = SanctuaryTypography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = SanctuaryWhite
                            )
                            Text(
                                text = ".",
                                style = SanctuaryTypography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryGold
                            )
                        }
                        Text(
                            text = "SECURE ENCLAVE STORAGE",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SanctuaryGoldLight,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Visible Lock Action Button
                SanctuaryOutlinedButton(
                    text = "Lock",
                    leadingIcon = Icons.Default.Lock,
                    onClick = onLockVault,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Realistic Decoy Files Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STORED RECORDS (${DecoyVaultManager.decoyFiles.size})",
                    style = SanctuaryEyebrowStyle,
                    color = SanctuaryGold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Realistic Decoy Files List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DecoyVaultManager.decoyFiles) { file ->
                    SanctuaryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDecoy = file }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SanctuaryPine)
                                    .border(1.dp, SanctuaryBorderSubtle, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                                    contentDescription = null,
                                    tint = SanctuaryGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.name,
                                    style = SanctuaryTypography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SanctuaryWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${file.category} • ${file.size} • ${file.date}",
                                    style = SanctuaryTypography.bodySmall,
                                    color = SanctuaryMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Decoy File Viewer Modal
        selectedDecoy?.let { file ->
            val scrollState = rememberScrollState()
            Dialog(onDismissRequest = { selectedDecoy = null }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SanctuaryPineSurface)
                        .border(1.dp, SanctuaryPineBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.name,
                                style = SanctuaryTypography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${file.category} • ${file.date}",
                                style = SanctuaryTypography.bodySmall,
                                color = SanctuaryMuted,
                                fontSize = 11.sp
                            )
                        }
                        IconButton(
                            onClick = { selectedDecoy = null },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SanctuaryMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SanctuaryPine)
                            .border(1.dp, SanctuaryBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = file.content,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SanctuaryWhite,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SanctuaryButton(
                        text = "Close Document",
                        onClick = { selectedDecoy = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "DecoyVault - Plausible Deniability Safe", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun DecoyVaultScreenPreview() {
    com.zerovault.app.ui.theme.SanctuaryTheme(darkTheme = true) {
        DecoyVaultScreen(
            onLockVault = {}
        )
    }
}