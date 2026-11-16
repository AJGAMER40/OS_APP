package com.zerovault.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.VaultAmber
import com.zerovault.app.ui.theme.VaultAmberBg
import com.zerovault.app.ui.theme.VaultCrimson
import com.zerovault.app.ui.theme.VaultCrimsonBg
import com.zerovault.app.ui.theme.VaultEmerald
import com.zerovault.app.ui.theme.VaultEmeraldDim

@Composable
fun ThreatBadge(
    remainingAttempts: Int,
    isLockedOut: Boolean,
    modifier: Modifier = Modifier
) {
    val (statusColor, bgColor, text) = when {
        isLockedOut -> Triple(
            VaultCrimson,
            VaultCrimsonBg,
            "DEFENSE SHIELD: LOCKDOWN ACTIVE"
        )
        remainingAttempts == 1 -> Triple(
            VaultCrimson,
            VaultCrimsonBg,
            "CRITICAL ALERT: 1 ATTEMPT REMAINING"
        )
        remainingAttempts == 2 -> Triple(
            VaultAmber,
            VaultAmberBg,
            "ELEVATED RISK: 2 ATTEMPTS REMAINING"
        )
        else -> Triple(
            VaultEmerald,
            VaultEmeraldDim.copy(alpha = 0.2f),
            "ENCLAVE INTEGRITY: SECURE (3 ATTEMPTS)"
        )
    }

    val animatedColor by animateColorAsState(targetValue = statusColor, label = "badgeColor")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, animatedColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(animatedColor)
        )
        Text(
            text = text,
            color = animatedColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}
