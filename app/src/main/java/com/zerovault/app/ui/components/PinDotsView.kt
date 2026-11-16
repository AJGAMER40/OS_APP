package com.zerovault.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCreamDark
import com.zerovault.app.ui.theme.SanctuaryCrimson
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryTheme

/**
 * PinDotsView displays filled vs. unfilled entry state for secret master PIN input.
 * Security & Accessibility compliance:
 * - NO ornamental animation on secret PIN digits (avoids visual timing leakage or delay)
 * - Renders instantaneous static indicators with high contrast
 * - Includes screen-reader contentDescription indicating progress without leaking digit values
 */
@Composable
fun PinDotsView(
    filledCount: Int,
    totalCount: Int = 6,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val filledColor = if (isWarm) SanctuaryGoldDark else SanctuaryGold
    val emptyBgColor = if (isWarm) SanctuaryCreamDark.copy(alpha = 0.5f) else SanctuaryPine
    val emptyBorderColor = if (isWarm) SanctuaryCreamDark else SanctuaryBorderSubtle

    val semanticLabel = if (isError) {
        "Incorrect PIN entered, please try again"
    } else {
        "$filledCount of $totalCount digits entered"
    }

    Row(
        modifier = modifier
            .semantics { contentDescription = semanticLabel },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalCount) {
            val isFilled = i < filledCount

            val dotBg = when {
                isError -> SanctuaryCrimson
                isFilled -> filledColor
                else -> emptyBgColor
            }

            val dotBorder = when {
                isError -> SanctuaryCrimson
                isFilled -> filledColor
                else -> emptyBorderColor
            }

            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(dotBg)
                        .border(
                            width = 1.5.dp,
                            color = dotBorder,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "PinDots - 3 of 6 (Dark)", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun PinDotsDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            PinDotsView(filledCount = 3, totalCount = 6)
        }
    }
}

@Preview(name = "PinDots - Error State", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun PinDotsErrorPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            PinDotsView(filledCount = 6, totalCount = 6, isError = true)
        }
    }
}

@Preview(name = "PinDots - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun PinDotsWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            PinDotsView(filledCount = 4, totalCount = 6)
        }
    }
}
