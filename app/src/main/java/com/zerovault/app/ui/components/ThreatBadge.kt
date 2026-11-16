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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.SanctuaryAmber
import com.zerovault.app.ui.theme.SanctuaryAmberBg
import com.zerovault.app.ui.theme.SanctuaryAmberLight
import com.zerovault.app.ui.theme.SanctuaryCrimson
import com.zerovault.app.ui.theme.SanctuaryCrimsonBg
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryEmeraldContainer
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryTheme

/**
 * ThreatBadge displays the verified rate-limiting attempt count and defense state.
 * Restyled with dignified, professional posture:
 * - Warns clearly without theatrical panic or fake alarms
 * - Instantaneous, accessible status transitions
 */
@Composable
fun ThreatBadge(
    remainingAttempts: Int,
    isLockedOut: Boolean,
    modifier: Modifier = Modifier
) {
    val (dotColor, textColor, bgColor, borderColor, text) = when {
        isLockedOut -> Quintuple(
            SanctuaryCrimson,
            SanctuaryCrimsonLight,
            SanctuaryCrimsonBg,
            SanctuaryCrimson.copy(alpha = 0.5f),
            "Quarantine Active · Rate-Limiting Engaged"
        )
        remainingAttempts == 1 -> Quintuple(
            SanctuaryCrimson,
            SanctuaryCrimsonLight,
            SanctuaryCrimsonBg,
            SanctuaryCrimson.copy(alpha = 0.4f),
            "Critical Notice · 1 Attempt Remaining"
        )
        remainingAttempts == 2 -> Quintuple(
            SanctuaryAmber,
            SanctuaryAmberLight,
            SanctuaryAmberBg,
            SanctuaryAmber.copy(alpha = 0.4f),
            "Security Notice · 2 Attempts Remaining"
        )
        else -> Quintuple(
            SanctuaryEmeraldLight,
            SanctuaryEmeraldLight,
            SanctuaryEmeraldContainer.copy(alpha = 0.45f),
            SanctuaryEmerald.copy(alpha = 0.35f),
            "Hardware Enclave Active · 3 Attempts"
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "ThreatBadge - Secure", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatBadgeSecurePreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThreatBadge(remainingAttempts = 3, isLockedOut = false)
        }
    }
}

@Preview(name = "ThreatBadge - Warning 2 Attempts", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatBadgeWarningPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThreatBadge(remainingAttempts = 2, isLockedOut = false)
        }
    }
}

@Preview(name = "ThreatBadge - Critical 1 Attempt", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatBadgeCriticalPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThreatBadge(remainingAttempts = 1, isLockedOut = false)
        }
    }
}

@Preview(name = "ThreatBadge - Lockdown Active", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun ThreatBadgeLockdownPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThreatBadge(remainingAttempts = 0, isLockedOut = true)
        }
    }
}
