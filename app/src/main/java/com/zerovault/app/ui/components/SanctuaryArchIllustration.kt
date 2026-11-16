package com.zerovault.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.LocalSanctuaryWarmMode
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldDark
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryTheme

/**
 * Locally drawn Romanesque sanctuary arch with golden luminous contours.
 * Implemented 100% in native Compose Canvas:
 * - Zero network requests, zero webviews, zero cloud fonts, zero bitmap assets
 * - Gracefully scales across phone (360dp), tablet, and landscape layouts
 * - Subtle spark drift animation is decorative only and can be disabled for reduced-motion settings
 */
@Composable
fun SanctuaryArchIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    animate: Boolean = true,
    showSparks: Boolean = true,
    showCenterSeal: Boolean = true
) {
    val isWarm = LocalSanctuaryWarmMode.current

    // Subtle decorative drift animation (respects reduced-motion when animate is false)
    val sparkOffset1: Float
    val sparkOffset2: Float
    val glowAlpha: Float

    if (animate) {
        val infiniteTransition = rememberInfiniteTransition(label = "sanctuary_arch_anim")

        val offset1 by infiniteTransition.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "spark1"
        )
        val offset2 by infiniteTransition.animateFloat(
            initialValue = 6f,
            targetValue = -6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "spark2"
        )
        val glow by infiniteTransition.animateFloat(
            initialValue = 0.22f,
            targetValue = 0.38f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "arch_glow"
        )
        sparkOffset1 = offset1
        sparkOffset2 = offset2
        glowAlpha = glow
    } else {
        sparkOffset1 = 0f
        sparkOffset2 = 0f
        glowAlpha = 0.25f
    }

    val primaryGold = if (isWarm) SanctuaryGoldDark else SanctuaryGold
    val secondaryGold = if (isWarm) SanctuaryGold else SanctuaryGoldLight

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = this.size.width
            val height = this.size.height

            // 1. Soft Ambient Luminous Radial Glow Behind the Arch
            val centerPoint = Offset(width / 2f, height * 0.45f)
            val glowRadius = width * 0.55f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryGold.copy(alpha = glowAlpha),
                        primaryGold.copy(alpha = glowAlpha * 0.4f),
                        Color.Transparent
                    ),
                    center = centerPoint,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = centerPoint
            )

            // 2. Outer Sanctuary Arch Path
            val archWidth = width * 0.72f
            val archHeight = height * 0.82f
            val archLeft = (width - archWidth) / 2f
            val archTop = height * 0.10f
            val archRight = archLeft + archWidth
            val archBottom = archTop + archHeight
            val archRadius = archWidth / 2f

            val outerArchPath = Path().apply {
                // Top semicircular arch
                arcTo(
                    rect = Rect(
                        left = archLeft,
                        top = archTop,
                        right = archRight,
                        bottom = archTop + (archRadius * 2)
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                // Right vertical pillar
                lineTo(archRight, archBottom)
                // Base
                lineTo(archLeft, archBottom)
                // Left vertical pillar back to arc start
                close()
            }

            // Outer Arch Soft Fill
            val fillBrush = if (isWarm) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1AD7B77D),
                        Color(0x05D7B77D)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        SanctuaryPine.copy(alpha = 0.45f),
                        SanctuaryPine.copy(alpha = 0.10f)
                    )
                )
            }
            drawPath(path = outerArchPath, brush = fillBrush, style = Fill)

            // Outer Arch Stroke
            drawPath(
                path = outerArchPath,
                color = primaryGold.copy(alpha = 0.45f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Outer Ring Halo (Echoing the sanctuary mood reference)
            drawRoundRect(
                color = primaryGold.copy(alpha = 0.15f),
                topLeft = Offset(archLeft - 10.dp.toPx(), archTop - 10.dp.toPx()),
                size = Size(archWidth + 20.dp.toPx(), archHeight + 10.dp.toPx()),
                cornerRadius = CornerRadius(archRadius + 10.dp.toPx(), archRadius + 10.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )

            // 3. Inner Concentric Sanctuary Arch Path
            val innerInset = 16.dp.toPx()
            val innerLeft = archLeft + innerInset
            val innerTop = archTop + innerInset
            val innerRight = archRight - innerInset
            val innerBottom = archBottom - (innerInset * 0.4f)
            val innerRadius = (innerRight - innerLeft) / 2f

            val innerArchPath = Path().apply {
                arcTo(
                    rect = Rect(
                        left = innerLeft,
                        top = innerTop,
                        right = innerRight,
                        bottom = innerTop + (innerRadius * 2)
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(innerRight, innerBottom)
                lineTo(innerLeft, innerBottom)
                close()
            }

            drawPath(
                path = innerArchPath,
                color = secondaryGold.copy(alpha = 0.28f),
                style = Stroke(
                    width = 1.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Keystone Architectural Lines
            val crownX = width / 2f
            val crownY = archTop
            drawLine(
                color = secondaryGold.copy(alpha = 0.7f),
                start = Offset(crownX, crownY - 6.dp.toPx()),
                end = Offset(crownX, innerTop),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Spring line (where arch curves meet vertical pillars)
            val springY = archTop + archRadius
            drawLine(
                color = primaryGold.copy(alpha = 0.25f),
                start = Offset(archLeft, springY),
                end = Offset(archLeft + 14.dp.toPx(), springY),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = primaryGold.copy(alpha = 0.25f),
                start = Offset(archRight - 14.dp.toPx(), springY),
                end = Offset(archRight, springY),
                strokeWidth = 1.5.dp.toPx()
            )

            // 5. Central Sanctuary Seal (Shield + Sacred Checkmark Monogram)
            if (showCenterSeal) {
                val sealCenter = Offset(crownX, springY + (innerRadius * 0.35f))
                val sealRadius = width * 0.16f

                // Luminous Seal Ring
                drawCircle(
                    color = primaryGold.copy(alpha = 0.55f),
                    radius = sealRadius,
                    center = sealCenter,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = primaryGold.copy(alpha = 0.12f),
                    radius = sealRadius + 5.dp.toPx(),
                    center = sealCenter,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = primaryGold.copy(alpha = 0.08f),
                    radius = sealRadius,
                    center = sealCenter,
                    style = Fill
                )

                // Enclave Shield Glyph inside seal
                val glyphScale = sealRadius * 0.52f
                val shieldPath = Path().apply {
                    moveTo(sealCenter.x, sealCenter.y - glyphScale)
                    lineTo(sealCenter.x + glyphScale, sealCenter.y - (glyphScale * 0.6f))
                    lineTo(sealCenter.x + glyphScale, sealCenter.y + (glyphScale * 0.2f))
                    cubicTo(
                        sealCenter.x + glyphScale, sealCenter.y + (glyphScale * 0.8f),
                        sealCenter.x, sealCenter.y + (glyphScale * 1.15f),
                        sealCenter.x, sealCenter.y + (glyphScale * 1.25f)
                    )
                    cubicTo(
                        sealCenter.x, sealCenter.y + (glyphScale * 1.15f),
                        sealCenter.x - glyphScale, sealCenter.y + (glyphScale * 0.8f),
                        sealCenter.x - glyphScale, sealCenter.y + (glyphScale * 0.2f)
                    )
                    lineTo(sealCenter.x - glyphScale, sealCenter.y - (glyphScale * 0.6f))
                    close()
                }

                drawPath(
                    path = shieldPath,
                    color = secondaryGold,
                    style = Stroke(width = 1.75.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Delicate internal seal checkmark
                val checkPath = Path().apply {
                    moveTo(sealCenter.x - (glyphScale * 0.4f), sealCenter.y + (glyphScale * 0.05f))
                    lineTo(sealCenter.x - (glyphScale * 0.08f), sealCenter.y + (glyphScale * 0.42f))
                    lineTo(sealCenter.x + (glyphScale * 0.48f), sealCenter.y - (glyphScale * 0.35f))
                }
                drawPath(
                    path = checkPath,
                    color = secondaryGold,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // 6. Floating Luminous Sparks (Subtle architectural embers)
            if (showSparks) {
                // Spark 1 (Top Left)
                drawCircle(
                    color = secondaryGold,
                    radius = 2.dp.toPx(),
                    center = Offset(width * 0.24f, (height * 0.26f) + sparkOffset1)
                )
                // Spark 2 (Mid Right)
                drawCircle(
                    color = secondaryGold.copy(alpha = 0.85f),
                    radius = 1.75.dp.toPx(),
                    center = Offset(width * 0.78f, (height * 0.48f) + sparkOffset2)
                )
                // Spark 3 (Lower Left Pillar)
                drawCircle(
                    color = secondaryGold.copy(alpha = 0.65f),
                    radius = 1.5.dp.toPx(),
                    center = Offset(width * 0.22f, (height * 0.65f) - (sparkOffset1 * 0.5f))
                )
            }
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Sanctuary Arch - Dark", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryArchDarkPreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryArchIllustration(
            size = 280.dp,
            animate = false
        )
    }
}

@Preview(name = "Sanctuary Arch - Warm Ivory", showBackground = true, backgroundColor = 0xFFF4EEE4)
@Composable
private fun SanctuaryArchWarmIvoryPreview() {
    SanctuaryTheme(useWarmIvory = true) {
        SanctuaryArchIllustration(
            size = 280.dp,
            animate = false
        )
    }
}

@Preview(name = "Sanctuary Arch - Miniature Seal (120dp)", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun SanctuaryArchMiniaturePreview() {
    SanctuaryTheme(darkTheme = true) {
        SanctuaryArchIllustration(
            size = 120.dp,
            animate = false,
            showSparks = false
        )
    }
}
