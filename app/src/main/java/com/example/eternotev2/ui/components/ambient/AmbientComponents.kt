package com.example.eternotev2.ui.components.ambient

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.eternotev2.ui.theme.MoodColors
import kotlin.random.Random

/**
 * A beautiful particle field that reacts to the current mood colors.
 * Particles drift slowly upwards and fade in/out.
 */
@Composable
fun ParticleField(
    modifier: Modifier = Modifier,
    colors: MoodColors,
    particleCount: Int = 30
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ParticleField")
    
    val particles = remember {
        List(particleCount) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 10f + 2f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                alpha = Random.nextFloat()
            )
        }
    }

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = (particle.y - progress * particle.speed) % 1f
            val adjustedY = if (currentY < 0) currentY + 1f else currentY
            
            val alpha = (0.5f + 0.5f * kotlin.math.sin(progress * 2 * Math.PI.toFloat() + particle.alpha * 10f)).coerceIn(0f, 1f)
            
            drawCircle(
                color = colors.glow.copy(alpha = alpha * 0.4f),
                radius = particle.size,
                center = Offset(particle.x * size.width, adjustedY * size.height)
            )
        }
    }
}

/**
 * A static yet depth-rich star field for a cosmic atmosphere.
 */
@Composable
fun StarField(
    modifier: Modifier = Modifier,
    starCount: Int = 100
) {
    val stars = remember {
        List(starCount) {
            Offset(Random.nextFloat(), Random.nextFloat()) to (Random.nextFloat() * 2f + 1f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { (pos, size) ->
            drawCircle(
                color = Color.White.copy(alpha = Random.nextFloat() * 0.7f + 0.3f),
                radius = size,
                center = Offset(pos.x * this.size.width, pos.y * this.size.height)
            )
        }
    }
}

/**
 * A glowing orb that pulses and follows a subtle path.
 */
@Composable
fun GlowOrb(
    modifier: Modifier = Modifier,
    color: Color,
    sizePx: Float = 400f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlowOrb")
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbPulse"
    )

    val translationX by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbX"
    )

    Canvas(modifier = modifier) {
        val brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
            center = center,
            radius = sizePx * pulse
        )
        
        withTransform({
            translate(left = translationX)
        }) {
            drawCircle(
                brush = brush,
                radius = sizePx * pulse,
                center = center
            )
        }
    }
}

private data class ParticleData(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)
