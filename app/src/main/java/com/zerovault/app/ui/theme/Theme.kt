package com.zerovault.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VaultCyan,
    onPrimary = VaultBg,
    primaryContainer = VaultSurfaceVariant,
    onPrimaryContainer = VaultCyan,
    secondary = VaultEmerald,
    onSecondary = VaultBg,
    secondaryContainer = VaultEmeraldDim,
    onSecondaryContainer = TextPrimary,
    tertiary = VaultAmber,
    background = VaultBg,
    onBackground = TextPrimary,
    surface = VaultSurface,
    onSurface = TextPrimary,
    surfaceVariant = VaultSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = VaultCrimson,
    onError = TextPrimary,
    errorContainer = VaultCrimsonBg,
    onErrorContainer = VaultCrimson,
    outline = BorderSubtle
)

@Composable
fun ZeroVaultTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = VaultBg.toArgb()
                window.navigationBarColor = VaultBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VaultTypography,
        content = content
    )
}
