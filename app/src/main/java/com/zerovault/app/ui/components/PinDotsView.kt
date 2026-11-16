package com.zerovault.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultCrimson
import com.zerovault.app.ui.theme.BorderSubtle
import com.zerovault.app.ui.theme.VaultSurfaceVariant

@Composable
fun PinDotsView(
    filledCount: Int,
    totalCount: Int = 6,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalCount) {
            val isFilled = i < filledCount

            val dotColor by animateColorAsState(
                targetValue = when {
                    isError -> VaultCrimson
                    isFilled -> VaultCyan
                    else -> VaultSurfaceVariant
                },
                animationSpec = tween(durationMillis = 200),
                label = "dotColor"
            )

            val dotSize by animateDpAsState(
                targetValue = if (isFilled) 18.dp else 14.dp,
                animationSpec = tween(durationMillis = 150),
                label = "dotSize"
            )

            Box(
                modifier = Modifier
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(dotColor)
                        .border(
                            width = 1.5.dp,
                            color = if (isFilled) dotColor else BorderSubtle,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
