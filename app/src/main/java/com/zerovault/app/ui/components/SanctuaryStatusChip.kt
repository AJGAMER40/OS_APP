package com.zerovault.app.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryAmber
import com.zerovault.app.ui.theme.SanctuaryAmberBg
import com.zerovault.app.ui.theme.SanctuaryAmberLight
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCrimson
import com.zerovault.app.ui.theme.SanctuaryCrimsonBg
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryEmeraldContainer
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryWhite

enum class SanctuaryStatusLevel {
    SECURE,        // Hardware TEE / KeyStore verified
    ELEVATED_RISK, // Failed attempts detected
    LOCKDOWN,      // Active brute-force cooldown
    NEUTRAL        // Decoy / informative state
}

/**
 * SanctuaryStatusChip renders verified, honest security telemetry.
 * Avoids deceptive 'all clear' badges or fake animated 'security scans'.
 * Reflects genuine hardware Keystore and ThreatMonitor states with peaceful dignity.
 */
@Composable
fun SanctuaryStatusChip(
    label: String,
    statusLevel: SanctuaryStatusLevel,
    modifier: Modifier = Modifier,
    enablePulse: Boolean = false // Decorative only; false by default for reduced motion
) {
    val isWarm = LocalSanctuaryWarmMode.current

    val (dotColor, textColor, bgColor, borderColor) = when (statusLevel) {
        SanctuaryStatusLevel.SECURE -> {
            val dot = if (isWarm) SanctuaryEmerald else SanctuaryEmeraldLight
            val text = if (isWarm) Color(0xFF1E4835) else SanctuaryEmeraldLight
            val bg = if (isWarm) Color(0xFFE3EDE4) else SanctuaryEmeraldContainer.copy(alpha = 0.45f)
            val border = if (isWarm) SanctuaryEmerald.copy(alpha = 0.4f) else SanctuaryEmerald.copy(alpha = 0.35f)
            listOf(dot, text, bg, border)
        }
        SanctuaryStatusLevel.ELEVATED_RISK -> {
            val dot = SanctuaryAmber
            val text = if (isWarm) Color(0xFF7A4A0A) else SanctuaryAmberLight
            val bg = if (isWarm) Color(0xFFFBEEDB) else SanctuaryAmberBg
            val border = SanctuaryAmber.copy(alpha = 0.5f)
            listOf(dot, text, bg, border)
        }
        SanctuaryStatusLevel.LOCKDOWN -> {
            val dot = SanctuaryCrimson
            val text = if (isWarm) Color(0xFF7A1D1D) else SanctuaryCrimsonLight
            val bg = if (isWarm) Color(0xFFFCEAEA) else SanctuaryCrimsonBg
            val border = SanctuaryCrimson.copy(alpha = 0.6f)
            listOf(dot, text, bg, border)
        }
        SanctuaryStatusLevel.NEUTRAL -> {
            val dot = if (isWarm) SanctuaryGoldDark else SanctuaryGold
            val text = if (isWarm) Color(0xFF263B31) else SanctuaryWhite
            val bg = if (isWarm) Color(0xFFEFE8DB) else SanctuaryPineSurface
            val border = SanctuaryBorderSubtle
            listOf(dot, text, bg, border)
        }
    }

    // Optional decorative pulse strictly respecting reduced motion when disabled
    val dotAlpha = if (enablePulse) {
        val infiniteTransition = rememberInfiniteTransition(label = "chip_pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
        alpha
    } else {
        1.0f
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
                .alpha(dotAlpha)
        )

        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Sanctuary Status Chips - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryStatusChipsDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SanctuaryStatusChip(
                label = "TEE / STRONGBOX ACTIVE",
                statusLevel = SanctuaryStatusLevel.SECURE
            )

            SanctuaryStatusChip(
                label = "2 ATTEMPTS REMAINING",
                statusLevel = SanctuaryStatusLevel.ELEVATED_RISK
            )

            SanctuaryStatusChip(
                label = "DEFENSE LOCKDOWN ACTIVE",
                statusLevel = SanctuaryStatusLevel.LOCKDOWN
            )

            SanctuaryStatusChip(
                label = "DURESS SAFE ACTIVE",
                statusLevel = SanctuaryStatusLevel.NEUTRAL
            )
        }
    }
}

@Preview(name = "Sanctuary Status Chips - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun SanctuaryStatusChipsWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SanctuaryStatusChip(
                label = "VAULT SESSION ACTIVE",
                statusLevel = SanctuaryStatusLevel.SECURE
            )

            SanctuaryStatusChip(
                label = "COERCION SAFE ARMED",
                statusLevel = SanctuaryStatusLevel.NEUTRAL
            )
        }
    }
}
