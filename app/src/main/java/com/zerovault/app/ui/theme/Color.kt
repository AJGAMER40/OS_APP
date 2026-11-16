package com.zerovault.app.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// SANCTUARY DESIGN SYSTEM — COLOR PALETTE
// Inspired by quiet sanctuaries, serene pine forests, and luminous gold.
// =========================================================================

// --- Dark Sanctuary Surfaces (Primary Vault Enclave) ---
val SanctuaryDeep = Color(0xFF0C1B17)           // Deep velvety forest-black background
val SanctuaryPine = Color(0xFF142B24)           // Subdued deep pine container
val SanctuaryPineSurface = Color(0xFF1A352C)    // Card and elevated surface tone
val SanctuaryPineElevated = Color(0xFF224237)   // Active, highlighted, or pop-up surface
val SanctuaryPineBorder = Color(0xFF2D5245)     // Subtle pine divider / structural border

// --- Warm Ivory Surfaces (Introductory & Philosophy Surfaces) ---
val SanctuaryCream = Color(0xFFF4EEE4)          // Serene warm ivory background
val SanctuaryCreamSurface = Color(0xFFFFFAF2)   // Soft cream card surface
val SanctuaryCreamDark = Color(0xFFE8DFD1)      // Warm stone border / divider
val SanctuaryCreamTextPrimary = Color(0xFF263B31) // Deep forest green text for ivory mode
val SanctuaryCreamTextSecondary = Color(0xFF627267) // Subdued olive sage text for ivory mode

// --- Luminous Gold (Sacred Enclave Accent & Key Actions) ---
val SanctuaryGold = Color(0xFFD7B77D)           // Primary golden accent
val SanctuaryGoldLight = Color(0xFFE5CD9D)      // Highlight / hover golden glow
val SanctuaryGoldDark = Color(0xFF9E814E)       // Antique burnished gold (secondary)
val SanctuaryGoldGlow = Color(0x33D7B77D)       // Diffuse radiant golden aura (20% alpha)
val SanctuaryGoldSubtle = Color(0x1AD7B77D)     // Delicate golden surface wash (10% alpha)

// --- Security Semantic Posture (Honest Telemetry) ---
val SanctuaryEmerald = Color(0xFF7CA88C)        // Verified enclave / tamper-free sage
val SanctuaryEmeraldLight = Color(0xFFB8D69A)   // Active status indicator pulse
val SanctuaryEmeraldContainer = Color(0xFF193B2E) // Emerald container background

val SanctuaryCrimson = Color(0xFFEF4444)        // Defense lockdown / threat alert
val SanctuaryCrimsonBg = Color(0xFF421616)      // Deep crimson background
val SanctuaryCrimsonLight = Color(0xFFF87171)   // Light crimson label text

val SanctuaryAmber = Color(0xFFF59E0B)          // Elevated risk / warning
val SanctuaryAmberBg = Color(0xFF451A03)        // Deep amber warning background
val SanctuaryAmberLight = Color(0xFFFBBF24)     // Light amber warning text

// --- Typographic Neutrals (High Contrast & Legibility) ---
val SanctuaryWhite = Color(0xFFFFFDF8)          // Primary text on dark surfaces
val SanctuaryMuted = Color(0xFFC8D3CA)          // Secondary informative text
val SanctuaryStone = Color(0xFF8D9E92)          // Monospace / metadata / tertiary text

// --- Structural Borders ---
val SanctuaryBorderSubtle = Color(0x38D7B77D)   // Subtle golden hairline border (22% alpha)
val SanctuaryBorderMedium = Color(0x4DD7B77D)   // Medium outline border (30% alpha)
val SanctuaryBorderStrong = Color(0x66D7B77D)   // Focused / active border (40% alpha)

// =========================================================================
// BACKWARD COMPATIBILITY ALIASES
// Ensures existing screens, viewmodels, and dialogs continue to compile
// and seamlessly inherit the Sanctuary visual hierarchy.
// =========================================================================
val VaultBg = SanctuaryDeep
val VaultSurface = SanctuaryPine
val VaultSurfaceVariant = SanctuaryPineElevated
val VaultSurfaceCard = SanctuaryPineSurface

val VaultCyan = SanctuaryGold
val VaultCyanDim = SanctuaryGoldDark
val VaultEmerald = SanctuaryEmerald
val VaultEmeraldDim = SanctuaryEmeraldContainer

val VaultCrimson = SanctuaryCrimson
val VaultCrimsonBg = SanctuaryCrimsonBg
val VaultAmber = SanctuaryAmber
val VaultAmberBg = SanctuaryAmberBg

val TextPrimary = SanctuaryWhite
val TextSecondary = SanctuaryMuted
val TextMuted = SanctuaryStone
val BorderSubtle = SanctuaryBorderSubtle
