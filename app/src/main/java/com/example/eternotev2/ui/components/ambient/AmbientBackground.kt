package com.example.eternotev2.ui.components.ambient

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.eternotev2.ui.theme.*

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = CosmicViolet,
    secondaryColor: Color = NebulaPink,
    showParticles: Boolean = true
) {
    val ambientAlpha = LocalAmbientAlpha.current
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundFlow")
    
    val phase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        bgColor,
                        surfaceColor.copy(alpha = 0.8f),
                        bgColor
                    )
                )
            )
    ) {
        StarField()
        
        // Animated Primary Orb
        GlowOrb(
            color = primaryColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    val angle = phase.value * 2 * Math.PI
                    translationX = (Math.cos(angle) * 50).dp.toPx()
                    translationY = (Math.sin(angle) * 50).dp.toPx()
                }
                .offset(x = 100.dp, y = (-50).dp),
            sizePx = 500f
        )
        
        // Animated Secondary Orb
        GlowOrb(
            color = secondaryColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .graphicsLayer {
                    val angle = (phase.value + 0.5f) * 2 * Math.PI
                    translationX = (Math.sin(angle) * 80).dp.toPx()
                    translationY = (Math.cos(angle) * 80).dp.toPx()
                }
                .offset(x = (-100).dp, y = 100.dp),
            sizePx = 600f
        )

        if (showParticles) {
            ParticleField(
                colors = MoodColors(
                    primary = primaryColor,
                    secondary = secondaryColor,
                    tertiary = Color.White,
                    glow = primaryColor.copy(alpha = 0.5f),
                    surface = Color.Transparent,
                    gradient = listOf(primaryColor, secondaryColor)
                )
            )
        }

        // Glossy Mesh/Sweep Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepProgress = (phase.value * 2) % 1f
            drawRect(
                brush = Brush.linearGradient(
                    0f to Color.Transparent,
                    0.45f to Color.Transparent,
                    0.5f to Color.White.copy(alpha = 0.03f * ambientAlpha),
                    0.55f to Color.Transparent,
                    1f to Color.Transparent,
                    start = Offset(0f, size.height * sweepProgress),
                    end = Offset(size.width, size.height * (sweepProgress - 0.5f))
                ),
                blendMode = BlendMode.Plus
            )

            // Noise/Grain Overlay
            drawRect(
                color = Color.White.copy(alpha = 0.02f),
                blendMode = BlendMode.Overlay
            )
        }
    }
}
