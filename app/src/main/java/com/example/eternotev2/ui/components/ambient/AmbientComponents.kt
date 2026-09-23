package com.example.eternotev2.ui.components.ambient

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import com.example.eternotev2.ui.theme.LocalAmbientAlpha
import com.example.eternotev2.ui.theme.MoodColors
import kotlin.random.Random

@Composable
fun ParticleField(
    modifier: Modifier = Modifier,
    colors: MoodColors,
    particleCount: Int = 20
) {
    val ambientAlpha = LocalAmbientAlpha.current
    val infiniteTransition = rememberInfiniteTransition(label = "ParticleField")
    
    val particles = remember {
        List(particleCount) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 5f + 2f,
                speed = Random.nextFloat() * 0.008f + 0.004f,
                baseAlpha = Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }

    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleProgress"
    )

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val color = colors.glow
                val p = progress.value
                particles.forEach { particle ->
                    val currentY = (particle.y - p * particle.speed) % 1f
                    val adjustedY = if (currentY < 0) currentY + 1f else currentY
                    
                    val alphaFade = (0.5f + 0.5f * kotlin.math.sin(p * 2 * Math.PI.toFloat() + particle.baseAlpha * 10f)).coerceIn(0f, 1f)
                    
                    val alpha = alphaFade * particle.baseAlpha
                    
                    // Core dot
                    drawCircle(
                        color = color.copy(alpha = alpha * ambientAlpha),
                        radius = particle.size,
                        center = Offset(particle.x * size.width, adjustedY * size.height)
                    )

                    // Glow halo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = alpha * 0.4f * ambientAlpha),
                                Color.Transparent
                            ),
                            center = Offset(particle.x * size.width, adjustedY * size.height),
                            radius = particle.size * 3f
                        ),
                        radius = particle.size * 3f,
                        center = Offset(particle.x * size.width, adjustedY * size.height)
                    )
                }
            }
    )
}

@Composable
fun StarField(
    modifier: Modifier = Modifier,
    starCount: Int = 50
) {
    val ambientAlpha = LocalAmbientAlpha.current
    val stars = remember {
        List(starCount) {
            Triple(
                Offset(Random.nextFloat(), Random.nextFloat()), 
                Random.nextFloat() * 1f + 0.5f,
                Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                stars.forEach { (pos, size, baseAlpha) ->
                    drawCircle(
                        color = Color.White.copy(alpha = baseAlpha * ambientAlpha),
                        radius = size,
                        center = Offset(pos.x * this.size.width, pos.y * this.size.height)
                    )
                }
            }
    )
}

@Composable
fun GlowOrb(
    modifier: Modifier = Modifier,
    color: Color,
    sizePx: Float = 300f
) {
    val ambientAlpha = LocalAmbientAlpha.current
    val infiniteTransition = rememberInfiniteTransition(label = "GlowOrb")
    
    val pulse = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbPulse"
    )

    val translationXAnim = infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbX"
    )

    Spacer(
        modifier = modifier
            .graphicsLayer {
                translationX = translationXAnim.value
                scaleX = pulse.value
                scaleY = pulse.value
                alpha = 0.7f * ambientAlpha
            }
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.15f * ambientAlpha), Color.Transparent),
                        center = center,
                        radius = sizePx
                    ),
                    radius = sizePx,
                    center = center
                )
            }
    )
}

private data class ParticleData(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val baseAlpha: Float
)
