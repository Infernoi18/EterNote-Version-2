package com.example.eternotev2.ui.components.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.eternotev2.ui.theme.StarGold
import kotlinx.coroutines.isActive
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun ConstellationBurstAnimation(
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
        var burstFired = false
        
        while (isActive) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed / 3500f).coerceIn(0f, 1f)
            
            // Phase 3: BURST (around 20% into the animation)
            if (progress > 0.2f && !burstFired) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                burstFired = true
                
                repeat(50) {
                    val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
                    val speed = Random.nextFloat() * 15f + 5f
                    particles.add(
                        Particle(
                            x = 0f,
                            y = 0f,
                            vx = Math.cos(angle.toDouble()).toFloat() * speed,
                            vy = Math.sin(angle.toDouble()).toFloat() * speed,
                            size = Random.nextFloat() * 6f + 2f,
                            alpha = 1f,
                            rotation = Random.nextFloat() * 360f,
                            color = if (Random.nextFloat() > 0.3f) StarGold else Color.White,
                            shape = if (Random.nextBoolean()) ParticleShape.STAR else ParticleShape.CIRCLE,
                            friction = 0.97f
                        )
                    )
                }
            }

            particles.forEach { it.update() }
            
            withFrameNanos { }
            if (progress >= 1f) break
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = center
        val progress = animationProgress.value
        
        // 1. Screen Pulse
        val pulseAlpha = if (progress < 0.2f) progress * 5f else (1f - progress) * 0.5f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(StarGold.copy(alpha = 0.2f * pulseAlpha), Color.Transparent),
                center = center,
                radius = size.width
            ),
            radius = size.width
        )

        // 2. Central Orb
        if (progress < 0.4f) {
            val orbScale = if (progress < 0.2f) 1f + progress else 1.2f * (1f - (progress - 0.2f) * 5f)
            val orbAlpha = (1f - progress * 2.5f).coerceIn(0f, 1f)
            drawCircle(
                color = StarGold.copy(alpha = orbAlpha),
                radius = 40.dp.toPx() * orbScale.coerceAtLeast(0f),
                center = center
            )
        }

        // 3 & 5. Particles & Constellation Lines
        val constellationAlpha = if (progress > 0.4f) ((progress - 0.4f) / 0.4f).coerceIn(0f, 1f) * (1f - progress) else 0f
        
        if (constellationAlpha > 0f) {
            // Draw lines between nearby particles
            for (i in particles.indices) {
                for (j in i + 1 until particles.indices.last.coerceAtMost(i + 5)) {
                    val p1 = particles[i]
                    val p2 = particles[j]
                    val dist = sqrt(((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)).toDouble()).toFloat()
                    
                    if (dist < 150f) {
                        drawLine(
                            color = StarGold.copy(alpha = (1f - dist / 150f) * 0.3f * constellationAlpha),
                            start = Offset(p1.x + center.x, p1.y + center.y),
                            end = Offset(p2.x + center.x, p2.y + center.y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }
        }

        particles.forEach { p ->
            val drawP = p.copy(x = p.x + center.x, y = p.y + center.y, alpha = p.alpha * (1f - progress))
            drawParticle(drawP)
        }
    }
}
