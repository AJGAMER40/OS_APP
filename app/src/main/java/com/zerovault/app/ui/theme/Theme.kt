package com.zerovault.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// CompositionLocal to allow deep descendants to know whether they are rendering
// within a warm-ivory introductory sanctuary surface or dark vault enclave.
val LocalSanctuaryWarmMode = staticCompositionLocalOf { false }

// Primary Dark Enclave Color Scheme
val SanctuaryColorSchemeDark = darkColorScheme(
    primary = SanctuaryGold,
    onPrimary = SanctuaryDeep,
    primaryContainer = SanctuaryPineElevated,
    onPrimaryContainer = SanctuaryGoldLight,
    secondary = SanctuaryEmerald,
    onSecondary = SanctuaryDeep,
    secondaryContainer = SanctuaryEmeraldContainer,
    onSecondaryContainer = SanctuaryWhite,
    tertiary = SanctuaryAmber,
    onTertiary = SanctuaryDeep,
    tertiaryContainer = SanctuaryAmberBg,
    onTertiaryContainer = SanctuaryAmberLight,
    background = SanctuaryDeep,
    onBackground = SanctuaryWhite,
    surface = SanctuaryPine,
    onSurface = SanctuaryWhite,
    surfaceVariant = SanctuaryPineSurface,
    onSurfaceVariant = SanctuaryMuted,
    outline = SanctuaryBorderSubtle,
    outlineVariant = SanctuaryPineBorder,
    error = SanctuaryCrimson,
    onError = SanctuaryWhite,
    errorContainer = SanctuaryCrimsonBg,
    onErrorContainer = SanctuaryCrimsonLight
)

// Optional Warm Ivory Introductory Color Scheme
val SanctuaryColorSchemeLight = lightColorScheme(
    primary = SanctuaryCreamTextPrimary,
    onPrimary = SanctuaryCreamSurface,
    primaryContainer = SanctuaryGoldLight,
    onPrimaryContainer = SanctuaryCreamTextPrimary,
    secondary = SanctuaryGoldDark,
    onSecondary = SanctuaryCreamSurface,
    secondaryContainer = SanctuaryGoldSubtle,
    onSecondaryContainer = SanctuaryCreamTextPrimary,
    tertiary = SanctuaryEmerald,
    onTertiary = SanctuaryCreamSurface,
    background = SanctuaryCream,
    onBackground = SanctuaryCreamTextPrimary,
    surface = SanctuaryCreamSurface,
    onSurface = SanctuaryCreamTextPrimary,
    surfaceVariant = SanctuaryCreamDark,
    onSurfaceVariant = SanctuaryCreamTextSecondary,
    outline = SanctuaryCreamDark,
    outlineVariant = SanctuaryBorderSubtle,
    error = SanctuaryCrimson,
    onError = SanctuaryWhite,
    errorContainer = SanctuaryCrimsonBg,
    onErrorContainer = SanctuaryCrimsonLight
)

@Composable
fun SanctuaryTheme(
    darkTheme: Boolean = true,
    useWarmIvory: Boolean = false,
    content: @Composable () -> Unit
) {
    val isWarm = useWarmIvory || !darkTheme
    val colorScheme = if (isWarm) SanctuaryColorSchemeLight else SanctuaryColorSchemeDark
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val statusBarColor = if (isWarm) SanctuaryCream else SanctuaryDeep
                val navBarColor = if (isWarm) SanctuaryCream else SanctuaryDeep

                window.statusBarColor = statusBarColor.toArgb()
                window.navigationBarColor = navBarColor.toArgb()

                val insetsController = WindowCompat.getInsetsController(window, view)
                // When light/warm, status and nav bar icons must be dark for contrast;
                // when dark sanctuary, status and nav bar icons must be light.
                insetsController.isAppearanceLightStatusBars = isWarm
                insetsController.isAppearanceLightNavigationBars = isWarm
            }
        }
    }

    CompositionLocalProvider(
        LocalSanctuaryWarmMode provides isWarm
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SanctuaryTypography,
            content = content
        )
    }
}

// Backward-compatible alias for existing root callers
@Composable
fun ZeroVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    SanctuaryTheme(
        darkTheme = true, // Default to secure dark vault enclave
        useWarmIvory = false,
        content = content
    )
}
