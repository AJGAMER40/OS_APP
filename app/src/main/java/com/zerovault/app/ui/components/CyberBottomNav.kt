package com.zerovault.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.BorderSubtle
import com.zerovault.app.ui.theme.TextMuted
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultSurfaceCard

enum class CyberNavTab {
    VAULT_ENCLAVE,
    CYBER_LAB,
    THREAT_HUD
}

@Composable
fun CyberBottomNav(
    selectedTab: CyberNavTab,
    onTabSelected: (CyberNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(VaultSurfaceCard.copy(alpha = 0.95f))
                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                title = "ENCLAVE",
                icon = Icons.Default.Security,
                isSelected = selectedTab == CyberNavTab.VAULT_ENCLAVE,
                onClick = { onTabSelected(CyberNavTab.VAULT_ENCLAVE) }
            )

            NavItem(
                title = "CYBER LAB",
                icon = Icons.Default.Biotech,
                isSelected = selectedTab == CyberNavTab.CYBER_LAB,
                onClick = { onTabSelected(CyberNavTab.CYBER_LAB) }
            )

            NavItem(
                title = "THREAT HUD",
                icon = Icons.Default.Radar,
                isSelected = selectedTab == CyberNavTab.THREAT_HUD,
                onClick = { onTabSelected(CyberNavTab.THREAT_HUD) }
            )
        }
    }
}

@Composable
private fun NavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val activeColor by animateColorAsState(
        targetValue = if (isSelected) VaultCyan else TextMuted,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_color"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) VaultCyan.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_bg"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = VaultCyan),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = activeColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = activeColor
            )
        }
    }
}
