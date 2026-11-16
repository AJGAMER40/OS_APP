package com.zerovault.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zerovault.app.ui.components.SanctuaryArchIllustration
import com.zerovault.app.ui.components.SanctuaryButton
import com.zerovault.app.ui.components.SanctuaryCard
import com.zerovault.app.ui.components.SanctuaryOutlinedButton
import com.zerovault.app.ui.components.SanctuaryScaffold
import com.zerovault.app.ui.components.SanctuarySectionTitle
import com.zerovault.app.ui.components.SanctuaryStatusChip
import com.zerovault.app.ui.components.SanctuaryStatusLevel
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCream
import com.zerovault.app.ui.theme.SanctuaryCreamSurface
import com.zerovault.app.ui.theme.SanctuaryCreamTextPrimary
import com.zerovault.app.ui.theme.SanctuaryCreamTextSecondary
import com.zerovault.app.ui.theme.SanctuaryDeep
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryStone
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite
import kotlinx.coroutines.launch

/**
 * SanctuaryWelcomeScreen introduces users to ZeroVault's serene, privacy-first sanctuary.
 * Inspired by the architectural mood reference:
 * - Centered luminous abstract golden arch with fine geometric shield mark
 * - Headline: "Some things deserve to remain sacred."
 * - Subtitle: "A private sanctuary for what matters most."
 * - Clear "Enter your sanctuary" primary CTA
 * - Restrained, honest trust language backed by real Android Keystore cryptography
 *
 * Security & Architectural Invariance:
 * - Does NOT bypass, defer, or cover active lockout (governed by decision hierarchy in ZeroVaultAppRoot)
 * - Does NOT display or touch any decrypted user content
 * - Does NOT persist PINs, auth tokens, or session secrets in Compose state or navigation
 * - Responsive across compact 360dp screens, tablets (840dp), and landscape layouts
 */
@Composable
fun SanctuaryWelcomeScreen(
    onEnterSanctuary: () -> Unit,
    modifier: Modifier = Modifier,
    useWarmIvory: Boolean = false
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var showPromiseModal by remember { mutableStateOf(false) }

    SanctuaryScaffold(
        modifier = modifier.fillMaxSize(),
        useWarmIvory = useWarmIvory
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =========================================================================
            // HERO SECTION: Centered Luminous Arch & Sacred Entrance
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Brand Mark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ZeroVault",
                        style = SanctuaryTypography.headlineMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (useWarmIvory) SanctuaryCreamTextPrimary else SanctuaryWhite
                    )
                    Text(
                        text = ".",
                        style = SanctuaryTypography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SanctuaryGold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Centered Golden Romanesque Arch Illustration (100% Vector Canvas)
                SanctuaryArchIllustration(
                    size = 240.dp,
                    animate = true,
                    showSparks = true,
                    showCenterSeal = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Eyebrow
                Text(
                    text = "A SANCTUARY FOR WHAT MATTERS",
                    style = SanctuaryEyebrowStyle,
                    color = if (useWarmIvory) SanctuaryGoldDark else SanctuaryGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Editorial Serif Headline
                Text(
                    text = "Some things deserve\nto remain sacred.",
                    style = SanctuaryTypography.displayMedium,
                    color = if (useWarmIvory) SanctuaryCreamTextPrimary else SanctuaryWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Serene Subtitle
                Text(
                    text = "Your memories. Your documents. Your private world.\nA quieter place to keep what matters most.",
                    style = SanctuaryTypography.bodyLarge,
                    color = if (useWarmIvory) SanctuaryCreamTextSecondary else SanctuaryMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Primary CTA: Enter your sanctuary
                SanctuaryButton(
                    text = "Enter your sanctuary",
                    onClick = onEnterSanctuary,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary CTA: Discover our promise
                SanctuaryOutlinedButton(
                    text = "Discover our promise",
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(48.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Restrained Trust Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SanctuaryEmeraldLight)
                    )
                    Text(
                        text = "DESIGNED FOR PRIVACY. BUILT FOR PEACE OF MIND.",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = if (useWarmIvory) SanctuaryCreamTextSecondary else SanctuaryStone
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // =========================================================================
            // PHILOSOPHY SECTION: Thoughtful by Design (The 3 Enclave Pillars)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (useWarmIvory) SanctuaryCreamSurface else SanctuaryPineSurface.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SanctuarySectionTitle(
                        eyebrow = "Thoughtful by design",
                        title = "A place for everything you hold close.",
                        subtitle = "Security should never feel intimidating. ZeroVault pairs purposeful hardware protection with an atmosphere of calm—so your private world feels cared for, not complicated.",
                        centered = true
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Feature Pillar 1: Memories
                    SanctuaryFeatureCard(
                        icon = Icons.Default.Image,
                        title = "For your memories",
                        description = "Keep personal photographs and cherished moments together in a private, encrypted space isolated from external apps."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feature Pillar 2: Life's Work
                    SanctuaryFeatureCard(
                        icon = Icons.Default.Description,
                        title = "For your life's work",
                        description = "Make room for important contracts, financial records, and sensitive identity documents safeguarded by Android Keystore."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feature Pillar 3: Confidential files
                    SanctuaryFeatureCard(
                        icon = Icons.Default.Folder,
                        title = "For what's yours",
                        description = "Organize confidential files with intention and care. Permanent DoD 5220.22-M shredding ensures zero forensic recoverability."
                    )
                }
            }

            // =========================================================================
            // THE INSIDE EXPERIENCE: Genuine Cryptographic Assurance
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SanctuarySectionTitle(
                    eyebrow = "The inside experience",
                    title = "Quiet on the outside.\nConsidered within.",
                    subtitle = "A calm home for your vault, clear actions, and privacy-first interactions. Your security controls remain real, while the interface becomes gentler to live with.",
                    centered = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Architectural Trust Highlights Card
                SanctuaryCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TrustItem(
                            icon = Icons.Default.Shield,
                            title = "Hardware Keystore Enclave",
                            detail = "AES-256 GCM authenticated encryption backed by device TEE or StrongBox hardware security module."
                        )

                        TrustItem(
                            icon = Icons.Default.VerifiedUser,
                            title = "Zero Cloud Exfiltration",
                            detail = "No remote telemetry, no external accounts, no cloud sync. All data exists strictly within your device's protected internal storage."
                        )

                        TrustItem(
                            icon = Icons.Default.Lock,
                            title = "Plausible Deniability Safe",
                            detail = "Built-in secondary duress code silently opens an isolated decoy safe with realistic non-sensitive notes under physical coercion."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Call to Action
                SanctuaryButton(
                    text = "Enter your sanctuary",
                    onClick = onEnterSanctuary,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Editorial Quote
                Text(
                    text = "“Peace of mind is knowing there is a place for what matters.”",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = SanctuaryGoldLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Note
                Text(
                    text = "ZeroVault · A Private Sanctuary for What Matters Most",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SanctuaryStone,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SanctuaryFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val isWarm = LocalSanctuaryWarmMode.current

    SanctuaryCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isWarm) SanctuaryGold.copy(alpha = 0.2f) else SanctuaryPine)
                    .border(1.dp, SanctuaryBorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isWarm) SanctuaryGoldDark else SanctuaryGold,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = SanctuaryTypography.titleMedium,
                    color = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = SanctuaryTypography.bodyMedium,
                    color = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun TrustItem(
    icon: ImageVector,
    title: String,
    detail: String
) {
    val isWarm = LocalSanctuaryWarmMode.current

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SanctuaryEmerald,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isWarm) SanctuaryCreamTextPrimary else SanctuaryWhite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = detail,
                fontSize = 12.sp,
                color = if (isWarm) SanctuaryCreamTextSecondary else SanctuaryMuted,
                lineHeight = 17.sp
            )
        }
    }
}

// =========================================================================
// NATIVE COMPOSE PREVIEWS (Responsive, Compact 360dp, Tablet & Landscape)
// =========================================================================

@Preview(name = "Sanctuary Welcome - Dark Phone (360dp)", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun SanctuaryWelcomeDarkPhonePreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryWelcomeScreen(onEnterSanctuary = {})
    }
}

@Preview(name = "Sanctuary Welcome - Warm Ivory Phone", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun SanctuaryWelcomeWarmIvoryPhonePreview() {
    SanctuaryTheme(useWarmIvory = true) {
        SanctuaryWelcomeScreen(
            onEnterSanctuary = {},
            useWarmIvory = true
        )
    }
}

@Preview(name = "Sanctuary Welcome - Tablet (840dp)", widthDp = 840, heightDp = 600, showBackground = true)
@Composable
private fun SanctuaryWelcomeTabletPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryWelcomeScreen(onEnterSanctuary = {})
    }
}

@Preview(name = "Sanctuary Welcome - Landscape (640dp)", widthDp = 640, heightDp = 360, showBackground = true)
@Composable
private fun SanctuaryWelcomeLandscapePreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryWelcomeScreen(onEnterSanctuary = {})
    }
}

@Preview(name = "Sanctuary Welcome - Large System Font (130%)", fontScale = 1.3f, showBackground = true)
@Composable
private fun SanctuaryWelcomeLargeFontPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryWelcomeScreen(onEnterSanctuary = {})
    }
}
