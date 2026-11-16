package com.zerovault.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryCreamTextPrimary
import com.zerovault.app.ui.theme.SanctuaryCreamTextSecondary
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * Editorial section header pairing an uppercase golden eyebrow with a dignified serif title.
 * Conveys serenity, calm, and purpose rather than intimidation.
 */
@Composable
fun SanctuarySectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    centered: Boolean = false
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val alignment = if (centered) Alignment.CenterHorizontally else Alignment.Start
    val textAlign = if (centered) TextAlign.Center else TextAlign.Start

    val eyebrowColor = if (isWarm) SanctuaryGoldDark else SanctuaryGold
    val titleColor = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite
    val subtitleColor = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!eyebrow.isNullOrBlank()) {
            Text(
                text = eyebrow.uppercase(),
                style = SanctuaryEyebrowStyle,
                color = eyebrowColor,
                textAlign = textAlign
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = title,
            style = SanctuaryTypography.headlineMedium,
            color = titleColor,
            textAlign = textAlign
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = SanctuaryTypography.bodyMedium,
                color = subtitleColor,
                textAlign = textAlign,
                lineHeight = 20.sp
            )
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Sanctuary Section Title - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuarySectionTitleDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuarySectionTitle(
            eyebrow = "A sanctuary for what matters",
            title = "Some things deserve to remain sacred.",
            subtitle = "Your memories. Your documents. Your private world in a quieter place.",
            centered = true
        )
    }
}

@Preview(name = "Sanctuary Section Title - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun SanctuarySectionTitleWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        SanctuarySectionTitle(
            eyebrow = "Thoughtful by design",
            title = "A place for everything you hold close.",
            subtitle = "Security should never feel intimidating. ZeroVault pairs purposeful protection with an atmosphere of calm.",
            centered = false
        )
    }
}
