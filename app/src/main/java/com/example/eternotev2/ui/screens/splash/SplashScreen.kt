package com.example.eternotev2.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.theme.AuroraCyan
import com.example.eternotev2.ui.theme.AuroraCyanGlow
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.CosmicVioletGlow
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.NebulaPink
import com.example.eternotev2.ui.theme.NebulaPinkGlow
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashComplete: (isFirstLaunch: Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate when ready
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Ready) {
            onSplashComplete((uiState as SplashUiState.Ready).isFirstLaunch)
        }
    }

    // ── Entry animations ──────────────────────────────────────────────────────
    val logoAlpha   = remember { Animatable(0f) }
    val logoScale   = remember { Animatable(0.6f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo appears first
        launch {
            logoAlpha.animateTo(
                1f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoScale.animateTo(
                1f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }
        // Tagline fades in after logo
        delay(600)
        taglineAlpha.animateTo(
            1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
    }

    // ── Infinite ambient animations ───────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "splashAmbient")

    val outerRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRing"
    )

    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue  = 0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerRing"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid),
        contentAlignment = Alignment.Center
    ) {

        // ── Ambient star field background ─────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawStarField(particleProgress)
        }

        // ── Radial background glow ────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRadialGlow(
                center = center,
                glowAlpha = glowPulse * 0.3f
            )
        }

        // ── Logo + text column ────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(logoAlpha.value)
                .scale(logoScale.value)
        ) {

            // ── Orbital logo mark ─────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawOrbitalRings(
                        outerRotation = outerRingRotation,
                        innerRotation = innerRingRotation,
                        glowPulse     = glowPulse
                    )
                }

                // Center glyph
                Canvas(modifier = Modifier.size(60.dp)) {
                    val r = size.minDimension / 2f
                    // Outer glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CosmicViolet.copy(alpha = glowPulse * 0.8f),
                                Color.Transparent
                            )
                        ),
                        radius = r * 1.4f
                    )
                    // Core circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AuroraCyan, CosmicViolet)
                        ),
                        radius = r * 0.7f
                    )
                    // Inner bright dot
                    drawCircle(
                        color  = Color.White.copy(alpha = 0.9f),
                        radius = r * 0.25f
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── App name ──────────────────────────────────────────────────────
            Text(
                text       = "ETERNOTE",
                fontSize   = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 8.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Text(
                text     = "Messages to your future self",
                fontSize = 14.sp,
                color    = TextSecondary.copy(alpha = taglineAlpha.value),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}

// ── Canvas Draw Helpers ───────────────────────────────────────────────────────

private fun DrawScope.drawOrbitalRings(
    outerRotation: Float,
    innerRotation: Float,
    glowPulse: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Outer dashed ring
    rotate(outerRotation, Offset(cx, cy)) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    CosmicViolet.copy(alpha = 0.8f),
                    AuroraCyan.copy(alpha = 0.6f),
                    Color.Transparent
                )
            ),
            radius = size.minDimension / 2f * 0.9f,
            style  = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Outer ring dots
        for (i in 0..7) {
            val angle  = (i * 45f) * (Math.PI / 180f).toFloat()
            val radius = size.minDimension / 2f * 0.9f
            drawCircle(
                color  = AuroraCyan.copy(alpha = glowPulse),
                radius = 3.dp.toPx(),
                center = Offset(
                    cx + cos(angle) * radius,
                    cy + sin(angle) * radius
                )
            )
        }
    }

    // Inner ring (counter-rotating)
    rotate(innerRotation, Offset(cx, cy)) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    NebulaPink.copy(alpha = 0.7f),
                    CosmicViolet.copy(alpha = 0.5f),
                    Color.Transparent
                )
            ),
            radius = size.minDimension / 2f * 0.65f,
            style  = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Inner ring dots
        for (i in 0..3) {
            val angle  = (i * 90f) * (Math.PI / 180f).toFloat()
            val radius = size.minDimension / 2f * 0.65f
            drawCircle(
                color  = NebulaPink.copy(alpha = glowPulse * 0.9f),
                radius = 4.dp.toPx(),
                center = Offset(
                    cx + cos(angle) * radius,
                    cy + sin(angle) * radius
                )
            )
        }
    }
}

private fun DrawScope.drawRadialGlow(center: Offset, glowAlpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                CosmicVioletGlow.copy(alpha = glowAlpha),
                AuroraCyanGlow.copy(alpha = glowAlpha * 0.5f),
                Color.Transparent
            ),
            center = center,
            radius = size.minDimension * 0.8f
        ),
        radius = size.minDimension * 0.8f,
        center = center
    )
}

private fun DrawScope.drawStarField(progress: Float) {
    val starPositions = List(80) { i ->
        val x = ((i * 137.508f) % size.width)
        val y = ((i * 97.333f)  % size.height)
        val r = (i % 3 + 1) * 0.8f
        Triple(x, y, r)
    }

    starPositions.forEachIndexed { i, (x, y, r) ->
        val twinkle = sin((progress * Math.PI * 2 + i * 0.7).toFloat()).toFloat()
            .coerceIn(-1f, 1f)
        val alpha = ((twinkle + 1f) / 2f) * 0.7f + 0.1f
        drawCircle(
            color  = Color.White.copy(alpha = alpha),
            radius = r.dp.toPx(),
            center = Offset(x, y)
        )
    }
}
