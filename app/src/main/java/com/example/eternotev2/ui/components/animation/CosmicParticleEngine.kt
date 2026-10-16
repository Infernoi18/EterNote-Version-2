package com.example.eternotev2.ui.components.animation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance shared particle engine for cinematic animations in Eternote V2.
 */

enum class ParticleShape {
    CIRCLE, STAR, SHARD
}

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var alpha: Float,
    var rotation: Float,
    val color: Color,
    val shape: ParticleShape,
    val friction: Float = 0.98f
) {
    fun update() {
        x += vx
        y += vy
        vx *= friction
        vy *= friction
        rotation += 2f
    }
}

fun DrawScope.drawParticle(particle: Particle) {
    val alphaColor = particle.color.copy(alpha = particle.alpha)
    when (particle.shape) {
        ParticleShape.CIRCLE -> {
            drawCircle(
                color = alphaColor,
                radius = particle.size,
                center = Offset(particle.x, particle.y)
            )
        }
        ParticleShape.STAR -> {
            rotate(particle.rotation, Offset(particle.x, particle.y)) {
                val path = Path().apply {
                    val outerRadius = particle.size
                    val innerRadius = particle.size / 2.5f
                    for (i in 0 until 5) {
                        val angle = (i * 72f - 90f) * (PI / 180f).toFloat()
                        val px = (particle.x + cos(angle.toDouble()) * outerRadius).toFloat()
                        val py = (particle.y + sin(angle.toDouble()) * outerRadius).toFloat()
                        if (i == 0) moveTo(px, py) else lineTo(px, py)

                        val nextAngle = (i * 72f + 36f - 90f) * (PI / 180f).toFloat()
                        lineTo(
                            (particle.x + cos(nextAngle.toDouble()) * innerRadius).toFloat(),
                            (particle.y + sin(nextAngle.toDouble()) * innerRadius).toFloat()
                        )
                    }
                    close()
                }
                drawPath(path, alphaColor)
            }
        }
        ParticleShape.SHARD -> {
            rotate(particle.rotation, Offset(particle.x, particle.y)) {
                val path = Path().apply {
                    moveTo(particle.x - particle.size, particle.y)
                    lineTo(particle.x, particle.y - particle.size * 1.5f)
                    lineTo(particle.x + particle.size, particle.y + particle.size * 0.5f)
                    close()
                }
                drawPath(path, alphaColor)
            }
        }
    }
}
