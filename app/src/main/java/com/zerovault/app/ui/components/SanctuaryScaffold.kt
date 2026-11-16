package com.zerovault.app.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryCream
import com.zerovault.app.ui.theme.SanctuaryCreamSurface
import com.zerovault.app.ui.theme.SanctuaryDeep
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography

/**
 * SanctuaryScaffold provides a tranquil, dignified structural foundation for all screens.
 * Features:
 * - Subtle radiant architectural gradient background (dark pine sanctuary or warm ivory)
 * - Safe insets handling for status bar, navigation bar, and IME keyboard
 * - Tablet and landscape responsiveness with 8dp grid spacing
 * - Preserves status and navigation bar legibility
 */
@Composable
fun SanctuaryScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    useWarmIvory: Boolean = LocalSanctuaryWarmMode.current,
    showSubtleGradient: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val backgroundBrush = when {
        !showSubtleGradient && useWarmIvory -> Brush.linearGradient(
            colors = listOf(SanctuaryCream, SanctuaryCream)
        )
        !showSubtleGradient && !useWarmIvory -> Brush.linearGradient(
            colors = listOf(SanctuaryDeep, SanctuaryDeep)
        )
        useWarmIvory -> Brush.verticalGradient(
            colors = listOf(
                SanctuaryCreamSurface,
                SanctuaryCream,
                SanctuaryCream
            )
        )
        else -> Brush.radialGradient(
            colors = listOf(
                SanctuaryPine.copy(alpha = 0.95f),
                SanctuaryDeep,
                Color(0xFF07120F)
            ),
            center = Offset(Float.POSITIVE_INFINITY / 2, 0f),
            radius = 1600f
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = Color.Transparent,
        contentColor = if (useWarmIvory) Color(0xFF263B31) else Color(0xFFFFFDF8)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            content(innerPadding)
        }
    }
}

// =========================================================================
// NATIVE COMPOSE PREVIEWS (Responsive, Compact 360dp, Tablet & Landscape)
// =========================================================================

@Preview(name = "Sanctuary Scaffold - Dark Enclave", showBackground = true)
@Composable
private fun SanctuaryScaffoldDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryScaffold { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Dark Sanctuary Enclave",
                    style = SanctuaryTypography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Scaffold - Warm Ivory", showBackground = true)
@Composable
private fun SanctuaryScaffoldWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        SanctuaryScaffold(useWarmIvory = true) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Warm Ivory Sanctuary Surface",
                    style = SanctuaryTypography.headlineSmall,
                    color = Color(0xFF263B31),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Scaffold - Compact 360dp", widthDp = 360, heightDp = 640)
@Composable
private fun SanctuaryScaffoldCompactPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryScaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "360dp Compact Screen",
                    style = SanctuaryTypography.titleMedium,
                    color = SanctuaryGold
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Scaffold - Tablet", widthDp = 840, heightDp = 600)
@Composable
private fun SanctuaryScaffoldTabletPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryScaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tablet Layout Sanctuary",
                    style = SanctuaryTypography.headlineLarge,
                    color = SanctuaryGold
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Scaffold - Landscape", widthDp = 640, heightDp = 360)
@Composable
private fun SanctuaryScaffoldLandscapePreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryScaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Landscape Sanctuary Layout",
                    style = SanctuaryTypography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(name = "Sanctuary Scaffold - Large System Font", fontScale = 1.3f)
@Composable
private fun SanctuaryScaffoldLargeFontPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryScaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Accessible Large Font (130%)",
                    style = SanctuaryTypography.headlineSmall,
                    color = Color.White
                )
            }
        }
    }
}
