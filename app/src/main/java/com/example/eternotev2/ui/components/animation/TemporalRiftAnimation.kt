package com.example.eternotev2.ui.components.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.eternotev2.ui.theme.AuroraCyan
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.DeepVoid
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun TemporalRiftAnimation(
    onAnimationEnd: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val animationProgress = remember { Animatable(0f) }
    val particles = remember { mutableStateListOf<Particle>() }
    
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
        )
        onAnimationEnd()
    }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        var hapticFired = false
        var particlesSpawned = false
        
        while (isActive) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed / 3500f).coerceIn(0f, 1f)
            
            if (progress > 0.4f && !particlesSpawned) {
                repeat(16) {
                    val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
                    val speed = Random.nextFloat() * 15f + 10f
                    particles.add(
                        Particle(
                            x = 0f,
                            y = 0f,
                            vx = cos(angle.toDouble()).toFloat() * speed,
                            vy = sin(angle.toDouble()).toFloat() * speed,
                            size = Random.nextFloat() * 12f + 6f,
                            alpha = 1f,
                            rotation = Random.nextFloat() * 360f,
                            color = if (Random.nextBoolean()) AuroraCyan else Color.White,
                            shape = if (Random.nextBoolean()) ParticleShape.SHARD else ParticleShape.CIRCLE,
                            friction = 0.96f
                        )
                    )
                }
                particlesSpawned = true
            }

            if (progress > 0.7f && !hapticFired) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                hapticFired = true
            }

            particles.forEach { 
                it.update() 
                it.alpha *= 0.97f
            }
            particles.removeAll { it.alpha < 0.05f }
            
            withFrameNanos { }
            if (progress >= 1f) break
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = center
        val progress = animationProgress.value

        // 1. Dark Overlay
        val overlayAlpha = if (progress < 0.2f) progress * 5f else if (progress > 0.8f) (1f - progress) * 5f else 1f
        drawRect(color = DeepVoid.copy(alpha = overlayAlpha.coerceIn(0f, 0.95f)))

        // 2, 3, 6. Seam / Tear / Collapse
        if (progress in 0.1f..0.9f) {
            val tearWidthFactor = when {
                progress < 0.4f -> (progress - 0.1f) / 0.3f
                progress < 0.7f -> 1f
                else -> 1f - (progress - 0.7f) / 0.2f
            }.coerceIn(0f, 1f)
            
            val tearWidth = tearWidthFactor * 120f
            val tearHeight = size.height * 0.7f
            
            val path = Path().apply {
                moveTo(center.x, center.y - tearHeight / 2)
                for (i in 1..12) {
                    val y = center.y - tearHeight / 2 + (tearHeight / 12) * i
                    val xOffset = if (i % 2 == 0) -tearWidth / 2 else tearWidth / 2
                    lineTo(center.x + xOffset, y)
                }
                for (i in 11 downTo 0) {
                    val y = center.y - tearHeight / 2 + (tearHeight / 12) * i
                    val xOffset = if (i % 2 == 0) tearWidth / 2 else -tearWidth / 2
                    lineTo(center.x + xOffset, y)
                }
                close()
            }

            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(CosmicViolet.copy(alpha = 0.8f * tearWidthFactor), Color.Transparent),
                    center = center,
                    radius = tearWidth * 2f
                )
            )
            
            // 4. Bloom
            if (progress > 0.4f) {
                val bloomAlpha = when {
                    progress < 0.7f -> (progress - 0.4f) / 0.3f
                    else -> 1f - (progress - 0.7f) / 0.2f
                }.coerceIn(0f, 1f)
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.7f * bloomAlpha), AuroraCyan.copy(alpha = 0.4f * bloomAlpha), Color.Transparent),
                        center = center,
                        radius = size.width * bloomAlpha
                    ),
                    radius = size.width * bloomAlpha
                )
            }
            
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.9f * tearWidthFactor),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        particles.forEach { p ->
            val drawP = p.copy(x = p.x + center.x, y = p.y + center.y)
            drawParticle(drawP)
        }
    }
}
