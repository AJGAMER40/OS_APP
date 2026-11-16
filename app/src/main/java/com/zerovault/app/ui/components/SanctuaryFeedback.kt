package com.zerovault.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCreamTextPrimary
import com.zerovault.app.ui.theme.SanctuaryCreamTextSecondary
import com.zerovault.app.ui.theme.SanctuaryCrimson
import com.zerovault.app.ui.theme.SanctuaryCrimsonBg
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * Accessible, tranquil empty state presenting a quiet sanctuary.
 */
@Composable
fun SanctuaryEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    icon: ImageVector = Icons.Default.Shield
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val titleColor = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite
    val descColor = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Serene Arch Illustration
        SanctuaryArchIllustration(
            size = 140.dp,
            animate = false,
            showSparks = false,
            showCenterSeal = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            style = SanctuaryTypography.headlineSmall,
            color = titleColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = SanctuaryTypography.bodyMedium,
            color = descColor,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        if (!actionButtonText.isNullOrBlank() && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            SanctuaryButton(
                text = actionButtonText,
                onClick = onActionClick,
                leadingIcon = Icons.Default.Add
            )
        }
    }
}

/**
 * Dignified loading indicator with semantic accessibility label.
 */
@Composable
fun SanctuaryLoadingIndicator(
    message: String = "Executing cryptographic operation...",
    modifier: Modifier = Modifier
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val goldColor = if (isWarm) SanctuaryGoldDark else SanctuaryGold
    val textColor = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = goldColor,
            strokeWidth = 3.dp,
            trackColor = if (isWarm) Color(0xFFE8DFD1) else SanctuaryPine
        )

        Text(
            text = message,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            textAlign = TextAlign.Center,
            letterSpacing = 0.4.sp
        )
    }
}

/**
 * Accessible error banner for clear, honest reporting of threats, cryptographic failures, or input errors.
 */
@Composable
fun SanctuaryErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Notice",
    onDismiss: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SanctuaryCrimsonBg)
            .border(1.dp, SanctuaryCrimson.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SanctuaryCrimson.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = SanctuaryCrimsonLight,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanctuaryWhite
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = message,
                    fontSize = 11.sp,
                    color = SanctuaryCrimsonLight,
                    lineHeight = 16.sp
                )
            }

            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = SanctuaryCrimsonLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Sanctuary Empty State - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryEmptyStateDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryEmptyState(
            title = "Cryptographic Enclave Empty",
            description = "No files encrypted yet. Tap below to import and protect documents, images, or notes in your hardware vault.",
            actionButtonText = "Add to Vault",
            onActionClick = {}
        )
    }
}

@Preview(name = "Sanctuary Loading - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryLoadingDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryLoadingIndicator()
    }
}

@Preview(name = "Sanctuary Error Banner - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryErrorBannerDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryErrorBanner(
            title = "Authentication Warning",
            message = "Incorrect PIN entered. 2 attempts remaining before automatic defense quarantine.",
            onDismiss = {}
        )
    }
}
