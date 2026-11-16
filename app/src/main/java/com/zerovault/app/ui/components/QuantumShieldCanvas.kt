package com.zerovault.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultEmerald
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-tech animated Canvas component rendering a futuristic Quantum Enclave Shield
 * with rotating cybernetic reticle rings and orbiting particle pulses.
 */
@Composable
fun QuantumShieldCanvas(
    size: Dp = 100.dp,
    shieldColor: Color = VaultCyan,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "quantum_shield")

    // Rotation angles
    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_outer"
    )

    val rotationInner by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_inner"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val outerRadius = (this.size.minDimension / 2) * 0.92f
            val innerRadius = (this.size.minDimension / 2) * 0.72f

            // Outer Dashed Arc 1
            drawArc(
                color = shieldColor.copy(alpha = 0.5f),
                startAngle = rotationOuter,
                sweepAngle = 100f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Outer Dashed Arc 2
            drawArc(
                color = shieldColor.copy(alpha = 0.5f),
                startAngle = rotationOuter + 180f,
                sweepAngle = 100f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Inner Segment Ring
            drawArc(
                color = VaultEmerald.copy(alpha = 0.7f),
                startAngle = rotationInner,
                sweepAngle = 60f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = VaultEmerald.copy(alpha = 0.7f),
                startAngle = rotationInner + 120f,
                sweepAngle = 60f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = VaultEmerald.copy(alpha = 0.7f),
                startAngle = rotationInner + 240f,
                sweepAngle = 60f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Orbiting Quantum Particles
            val particleCount = 4
            for (i in 0 until particleCount) {
                val angleRad = Math.toRadians((rotationOuter + (i * 90)).toDouble())
                val px = center.x + (outerRadius * cos(angleRad)).toFloat()
                val py = center.y + (outerRadius * sin(angleRad)).toFloat()

                drawCircle(
                    color = shieldColor,
                    radius = 3.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        // Central Shield Emblem
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Enclave Shield",
            tint = shieldColor,
            modifier = Modifier.size(size * 0.42f * pulseScale)
        )
    }
}
