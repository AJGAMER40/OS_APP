package com.zerovault.app.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCreamDark
import com.zerovault.app.ui.theme.SanctuaryCreamSurface
import com.zerovault.app.ui.theme.SanctuaryCreamTextPrimary
import com.zerovault.app.ui.theme.SanctuaryCreamTextSecondary
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * SecureKeypad renders a substantial, accessible, and responsive numeric entry keypad.
 * Features:
 * - Minimum 64dp diameter touch targets ensuring comfortable tap accuracy
 * - Tightened 10-12dp vertical spacing to prevent vertical clipping on short screens
 * - Full accessibility semantics for screen readers (TalkBack)
 * - Distinct, non-theatrical disabled posture when locked out
 * - Zero logging of entered digits
 */
@Composable
fun SecureKeypad(
    onDigitClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonSize: Dp = 68.dp
) {
    val keypadLayout = listOf(
        listOf("1" to "", "2" to "ABC", "3" to "DEF"),
        listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
        listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
        listOf("CLEAR" to "", "0" to "+", "DELETE" to "")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 380.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keypadLayout.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { (key, subtext) ->
                    when (key) {
                        "CLEAR" -> {
                            KeypadSpecialButton(
                                text = "CLR",
                                accessibilityLabel = "Clear all digits",
                                onClick = onClearClick,
                                enabled = enabled,
                                size = buttonSize
                            )
                        }
                        "DELETE" -> {
                            KeypadIconButton(
                                accessibilityLabel = "Delete last digit",
                                onClick = onDeleteClick,
                                enabled = enabled,
                                size = buttonSize
                            )
                        }
                        else -> {
                            KeypadNumberButton(
                                digit = key[0],
                                subtext = subtext,
                                onClick = { onDigitClick(key[0]) },
                                enabled = enabled,
                                size = buttonSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadNumberButton(
    digit: Char,
    subtext: String,
    onClick: () -> Unit,
    enabled: Boolean,
    size: Dp
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val interactionSource = remember { MutableInteractionSource() }

    val bg = if (isWarm) SanctuaryCreamSurface else SanctuaryPineSurface
    val border = if (isWarm) SanctuaryCreamDark else SanctuaryBorderSubtle
    val digitColor = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite
    val subtextColor = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted
    val rippleColor = if (isWarm) SanctuaryGoldDark else SanctuaryGold

    val accessibilityDesc = if (subtext.isNotEmpty()) "Digit $digit, letters $subtext" else "Digit $digit"

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, border, CircleShape)
            .alpha(if (enabled) 1f else 0.35f)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = rippleColor, bounded = true),
                enabled = enabled,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = accessibilityDesc
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = digitColor
            )
            if (subtext.isNotEmpty()) {
                Text(
                    text = subtext,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = subtextColor
                )
            }
        }
    }
}

@Composable
private fun KeypadIconButton(
    accessibilityLabel: String,
    onClick: () -> Unit,
    enabled: Boolean,
    size: Dp
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val interactionSource = remember { MutableInteractionSource() }
    val tintColor = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite
    val rippleColor = if (isWarm) SanctuaryGoldDark else SanctuaryGold

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .alpha(if (enabled) 1f else 0.35f)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = rippleColor, bounded = true),
                enabled = enabled,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = accessibilityLabel
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun KeypadSpecialButton(
    text: String,
    accessibilityLabel: String,
    onClick: () -> Unit,
    enabled: Boolean,
    size: Dp
) {
    val isWarm = LocalSanctuaryWarmMode.current
    val interactionSource = remember { MutableInteractionSource() }
    val textColor = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted
    val rippleColor = if (isWarm) SanctuaryGoldDark else SanctuaryGold

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .alpha(if (enabled) 1f else 0.35f)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = rippleColor, bounded = true),
                enabled = enabled,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = accessibilityLabel
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "SecureKeypad - Dark Active", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SecureKeypadDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            SecureKeypad(
                onDigitClick = {},
                onDeleteClick = {},
                onClearClick = {},
                enabled = true
            )
        }
    }
}

@Preview(name = "SecureKeypad - Disabled Lockdown", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SecureKeypadDisabledPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            SecureKeypad(
                onDigitClick = {},
                onDeleteClick = {},
                onClearClick = {},
                enabled = false
            )
        }
    }
}

@Preview(name = "SecureKeypad - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun SecureKeypadWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            SecureKeypad(
                onDigitClick = {},
                onDeleteClick = {},
                onClearClick = {},
                enabled = true
            )
        }
    }
}
