package com.zerovault.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.SanctuaryEmerald
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryTheme

/**
 * Restyled Sanctuary Enclave Shield Canvas.
 * Replaces aggressive sci-fi reticles with a tranquil, geometric architectural emblem.
 * Compliance:
 * - 100% locally drawn in Compose Canvas (zero network, zero bitmaps)
 * - Animation is purely decorative breathing and can be disabled via `animate = false` for reduced-motion settings
 * - Avoids deceptive animated 'scanning' or fake telemetry
 */
@Composable
fun QuantumShieldCanvas(
    size: Dp = 100.dp,
    shieldColor: Color = SanctuaryGold,
    modifier: Modifier = Modifier,
    animate: Boolean = false // Disabled by default for reduced motion
) {
    val auraAlpha: Float
    val subtlePulseScale: Float

    if (animate) {
        val infiniteTransition = rememberInfiniteTransition(label = "sanctuary_shield")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.45f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "aura_pulse"
        )
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale_pulse"
        )
        auraAlpha = alpha
        subtlePulseScale = scale
    } else {
        auraAlpha = 0.3f
        subtlePulseScale = 1.0f
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val minDim = this.size.minDimension
            val outerRadius = (minDim / 2f) * 0.88f
            val innerRadius = (minDim / 2f) * 0.72f

            // 1. Soft Ambient Luminous Radial Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        shieldColor.copy(alpha = auraAlpha),
                        shieldColor.copy(alpha = auraAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius * 1.15f
                ),
                radius = outerRadius * 1.15f,
                center = center
            )

            // 2. Concentric Luminous Halo Arcs
            drawCircle(
                color = shieldColor.copy(alpha = 0.2f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            drawCircle(
                color = shieldColor.copy(alpha = 0.35f),
                radius = innerRadius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Four cardinal alignment tick marks
            val tickLength = 5.dp.toPx()
            drawLine(
                color = shieldColor.copy(alpha = 0.6f),
                start = Offset(center.x, center.y - outerRadius - tickLength),
                end = Offset(center.x, center.y - outerRadius + tickLength),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = shieldColor.copy(alpha = 0.6f),
                start = Offset(center.x, center.y + outerRadius - tickLength),
                end = Offset(center.x, center.y + outerRadius + tickLength),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = shieldColor.copy(alpha = 0.6f),
                start = Offset(center.x - outerRadius - tickLength, center.y),
                end = Offset(center.x - outerRadius + tickLength, center.y),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = shieldColor.copy(alpha = 0.6f),
                start = Offset(center.x + outerRadius - tickLength, center.y),
                end = Offset(center.x + outerRadius + tickLength, center.y),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 3. Central Geometric Enclave Shield Path
            val s = (minDim * 0.22f) * subtlePulseScale
            val shieldPath = Path().apply {
                moveTo(center.x, center.y - s)
                lineTo(center.x + s, center.y - (s * 0.55f))
                lineTo(center.x + s, center.y + (s * 0.25f))
                cubicTo(
                    center.x + s, center.y + (s * 0.8f),
                    center.x, center.y + (s * 1.15f),
                    center.x, center.y + (s * 1.25f)
                )
                cubicTo(
                    center.x, center.y + (s * 1.15f),
                    center.x - s, center.y + (s * 0.8f),
                    center.x - s, center.y + (s * 0.25f)
                )
                lineTo(center.x - s, center.y - (s * 0.55f))
                close()
            }

            drawPath(
                path = shieldPath,
                color = shieldColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Central Sacred Checkmark / Anchor
            val checkPath = Path().apply {
                moveTo(center.x - (s * 0.42f), center.y + (s * 0.05f))
                lineTo(center.x - (s * 0.1f), center.y + (s * 0.42f))
                lineTo(center.x + (s * 0.48f), center.y - (s * 0.32f))
            }

            drawPath(
                path = checkPath,
                color = SanctuaryGoldLight,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "QuantumShieldCanvas - Sanctuary Restyle", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun QuantumShieldCanvasPreview() {
    SanctuaryTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            QuantumShieldCanvas(size = 90.dp)
        }
    }
}
