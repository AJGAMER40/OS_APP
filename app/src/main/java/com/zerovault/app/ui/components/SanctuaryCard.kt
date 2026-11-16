package com.zerovault.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCreamDark
import com.zerovault.app.ui.theme.SanctuaryCreamSurface
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldSubtle
import com.zerovault.app.ui.theme.SanctuaryPineElevated
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * SanctuaryCard provides a peaceful, elevated surface for grouping data, files, and controls.
 * Features:
 * - Subtle 1dp architectural hairline border (golden/stone)
 * - Deep pine surface with high contrast against the background
 * - Seamless adaptation for warm-ivory introductory mode
 * - 8dp spacing grid compatibility
 */
@Composable
fun SanctuaryCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val effectiveBg = backgroundColor ?: if (isWarm) SanctuaryCreamSurface else SanctuaryPineSurface
    val effectiveBorder = borderColor ?: if (isWarm) SanctuaryCreamDark else SanctuaryBorderSubtle

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(borderWidth, effectiveBorder, shape),
        shape = shape,
        color = effectiveBg,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Clickable Sanctuary card with smooth ripple feedback and accessibility semantics.
 */
@Composable
fun SanctuaryClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClickLabel: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val effectiveBg = backgroundColor ?: if (isWarm) SanctuaryCreamSurface else SanctuaryPineSurface
    val effectiveBorder = borderColor ?: if (isWarm) SanctuaryCreamDark else SanctuaryBorderSubtle
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(borderWidth, effectiveBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = SanctuaryGold),
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick
            ),
        shape = shape,
        color = effectiveBg,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Elevated accent card for cryptographic highlights, hardware status, and benchmark summaries.
 */
@Composable
fun SanctuaryHighlightCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val bg = if (isWarm) SanctuaryCreamSurface else SanctuaryPineElevated
    val border = if (isWarm) SanctuaryGold.copy(alpha = 0.4f) else SanctuaryBorderSubtle

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, border, shape),
        shape = shape,
        color = bg,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Sanctuary Card - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryCardDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SanctuaryCard {
                Text(
                    text = "Personal Sanctuary",
                    style = SanctuaryTypography.titleMedium,
                    color = SanctuaryWhite
                )
                Text(
                    text = "A tranquil space for your confidential documents.",
                    style = SanctuaryTypography.bodyMedium,
                    color = com.zerovault.app.ui.theme.SanctuaryMuted
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Card - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun SanctuaryCardWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SanctuaryCard {
                Text(
                    text = "Thoughtful by Design",
                    style = SanctuaryTypography.titleMedium,
                    color = com.zerovault.app.ui.theme.SanctuaryCreamTextPrimary
                )
                Text(
                    text = "Security should never feel intimidating.",
                    style = SanctuaryTypography.bodyMedium,
                    color = com.zerovault.app.ui.theme.SanctuaryCreamTextSecondary
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Highlight Card & Clickable - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryCardClickablePreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            SanctuaryHighlightCard {
                Text(
                    text = "Hardware Keystore AES-256",
                    style = SanctuaryTypography.titleSmall,
                    color = SanctuaryGold
                )
                Text(
                    text = "StrongBox HSM Level 4 Backed",
                    style = SanctuaryTypography.bodySmall,
                    color = com.zerovault.app.ui.theme.SanctuaryMuted
                )
            }

            SanctuaryClickableCard(onClick = {}) {
                Text(
                    text = "Interactive Vault Record",
                    style = SanctuaryTypography.titleSmall,
                    color = SanctuaryWhite
                )
                Text(
                    text = "Tap to decrypt and preview in volatile memory",
                    style = SanctuaryTypography.bodySmall,
                    color = com.zerovault.app.ui.theme.SanctuaryStone
                )
            }
        }
    }
}
