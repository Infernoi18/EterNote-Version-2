package com.example.eternotev2.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A performance-optimized glassmorphic card.
 * Uses hardware-accelerated layers and efficient gradients instead of real-time blur.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    glowColor: Color = Color.White,
    glowAlpha: Float = 0f,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                this.shape = RoundedCornerShape(cornerRadius)
                this.clip = true
                if (glowAlpha > 0f) {
                    this.shadowElevation = 12.dp.toPx()
                }
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

/**
 * A glowing button with hardware-accelerated shadows.
 */
@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    gradientColors: List<Color>? = null,
    enabled: Boolean = true
) {
    val finalGradient = gradientColors ?: listOf(
        glowColor.copy(alpha = 0.8f),
        glowColor
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .graphicsLayer {
                this.shape = RoundedCornerShape(16.dp)
                this.clip = true
                if (enabled) {
                    this.shadowElevation = 8.dp.toPx()
                }
            }
            .background(
                if (enabled) {
                    Brush.linearGradient(colors = finalGradient)
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.3f),
                            Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
    }
}

/**
 * Minimalistic Glass Tag for categories or status.
 */
@Composable
fun GlassTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
