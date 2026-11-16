package com.zerovault.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryBorderMedium
import com.zerovault.app.ui.theme.SanctuaryCreamTextPrimary
import com.zerovault.app.ui.theme.SanctuaryCrimson
import com.zerovault.app.ui.theme.SanctuaryCrimsonBg
import com.zerovault.app.ui.theme.SanctuaryDeep
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * Primary action button in the Sanctuary visual system.
 * Warm golden pill container with high-contrast text and accessible 48dp minimum touch target.
 */
@Composable
fun SanctuaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val containerColor = if (isWarm) SanctuaryPineSurface else SanctuaryGold
    val contentColor = if (isWarm) SanctuaryGoldLight else SanctuaryDeep

    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.35f),
            disabledContentColor = contentColor.copy(alpha = 0.45f)
        ),
        contentPadding = contentPadding
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )

        if (!isLoading && trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Secondary outlined action button with subtle golden or pine outline.
 */
@Composable
fun SanctuaryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 13.dp)
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val strokeColor = if (isWarm) SanctuaryGoldDark.copy(alpha = 0.5f) else SanctuaryBorderMedium
    val textColor = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(1.dp, strokeColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor,
            disabledContentColor = textColor.copy(alpha = 0.4f)
        ),
        contentPadding = contentPadding
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (isWarm) SanctuaryGoldDark else SanctuaryGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp
        )
    }
}

/**
 * Destructive action button (for forensic shredding, RAM wipe, or permanent deletion).
 */
@Composable
fun SanctuaryDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = Icons.Default.DeleteForever
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SanctuaryCrimson,
            contentColor = SanctuaryWhite,
            disabledContainerColor = SanctuaryCrimsonBg,
            disabledContentColor = SanctuaryWhite.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = SanctuaryWhite,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
    }
}

/**
 * Minimal text/ghost action.
 */
@Composable
fun SanctuaryTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = SanctuaryGold
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 44.dp),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = color,
            disabledContentColor = color.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp
        )
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Sanctuary Buttons - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryButtonsDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SanctuaryButton(
                text = "Enter Your Sanctuary",
                onClick = {},
                leadingIcon = Icons.Default.Shield,
                modifier = Modifier.fillMaxWidth()
            )

            SanctuaryOutlinedButton(
                text = "Discover Our Promise",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            SanctuaryDestructiveButton(
                text = "DoD 5220.22-M Shred File",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            SanctuaryTextButton(
                text = "Close & Wipe RAM",
                onClick = {}
            )
        }
    }
}

@Preview(name = "Sanctuary Buttons - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun SanctuaryButtonsWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SanctuaryButton(
                text = "Explore the Sanctuary",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            SanctuaryOutlinedButton(
                text = "Learn More",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Sanctuary Buttons - States (Loading & Disabled)", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryButtonsStatesPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SanctuaryButton(
                text = "Encrypting File...",
                onClick = {},
                isLoading = true,
                modifier = Modifier.fillMaxWidth()
            )

            SanctuaryButton(
                text = "Disabled Action",
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            SanctuaryOutlinedButton(
                text = "Disabled Outlined",
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Sanctuary Buttons - Large Font Scale", fontScale = 1.3f, showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryButtonsLargeFontPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SanctuaryButton(
                text = "Accessible Touch Target",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
