package com.example.eternotev2.ui.screens.capsule

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.data.model.CapsuleType
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.theme.*
import com.example.eternotev2.util.HapticUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CapsuleUnlockScreen(
    capsuleId: Long,
    onUnlockComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: CapsuleUnlockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    LaunchedEffect(uiState.currentPhase) {
        when (uiState.currentPhase) {
            UnlockPhase.UNSEALING -> HapticUtil.performLongPress(view)
            UnlockPhase.REVEALING -> HapticUtil.performConfirm(view)
            else -> {}
        }
    }

    LaunchedEffect(capsuleId) {
        viewModel.loadCapsule(capsuleId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val capsule = uiState.capsule
        val colors = if (capsule != null) moodColors(capsule.mood) else null
        
        AmbientBackground(
            primaryColor = colors?.primary ?: CosmicViolet,
            secondaryColor = colors?.secondary?.copy(alpha = 0.4f) ?: NebulaPink
        )

        if (capsule != null) {
            val moodColors = moodColors(capsule.mood)
            
            Crossfade(targetState = uiState.currentPhase, label = "UnlockPhase") { phase ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (phase) {
                        UnlockPhase.INTRO -> IntroPhaseView(capsule.title)
                        UnlockPhase.UNSEALING -> {
                            if (capsule.capsuleType == CapsuleType.NORMAL) {
                                NormalUnsealingView(moodColors.primary)
                            } else {
                                BirthdayUnsealingView(moodColors.primary)
                            }
                        }
                        UnlockPhase.REVEALING -> {
                            if (capsule.capsuleType == CapsuleType.NORMAL) {
                                NormalRevealingView(capsule.mood.emoji)
                            } else {
                                BirthdayRevealingView(capsule.mood.emoji)
                            }
                        }
                        UnlockPhase.READING -> ReadingPhaseView(
                            title = capsule.title,
                            message = capsule.message,
                            capsuleType = capsule.capsuleType,
                            accentColor = moodColors.primary,
                            onFinish = onUnlockComplete
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IntroPhaseView(title: String) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)) + expandVertically(tween(1000)),
        exit = fadeOut(tween(500))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "PREPARING TO UNSEAL",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

@Composable
fun NormalUnsealingView(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "NormalPulse")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )
    
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanLine"
    )

    Box(contentAlignment = Alignment.Center) {
        // Techy Ring
        Canvas(modifier = Modifier.size(280.dp).graphicsLayer { rotationZ = rotation }) {
            drawCircle(
                brush = Brush.sweepGradient(
                    0f to Color.Transparent,
                    0.5f to accentColor,
                    1f to Color.Transparent
                ),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            
            // Outer dashed ring
            drawCircle(
                color = accentColor.copy(alpha = 0.2f),
                style = Stroke(width = 1.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
            )
        }

        // Inner Pulsing Core
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Pulse"
        )
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            
            // Scanning effect
            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineY = size.height * scanLineProgress
                drawLine(
                    color = accentColor.copy(alpha = 0.8f),
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        
        Text(
            "SYNTHESIZING MEMORY...",
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.offset(y = 160.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
    }
}

@Composable
fun BirthdayUnsealingView(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "BirthdayPulse")
    
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bounce"
    )

    Box(contentAlignment = Alignment.Center) {
        // Festive Particles
        repeat(12) { i ->
            val angle = (i * 30f) * (PI / 180f).toFloat()
            val radiusAnim by infiniteTransition.animateFloat(
                initialValue = 60f,
                targetValue = 140f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, delayMillis = i * 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "Radius$i"
            )
            val alphaAnim by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, delayMillis = i * 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "Alpha$i"
            )
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(angle) * radiusAnim).dp,
                        y = (sin(angle) * radiusAnim).dp
                    )
                    .size(6.dp)
                    .alpha(alphaAnim)
                    .clip(CircleShape)
                    .background(if (i % 2 == 0) accentColor else NebulaPink)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = bounce.dp)
        ) {
            Icon(
                Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "UNWRAPPING",
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                letterSpacing = 6.sp
            )
        }
    }
}

@Composable
fun NormalRevealingView(emoji: String) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.2f, tween(1200, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, tween(800))
    }

    Box(contentAlignment = Alignment.Center) {
        // Geometric Expansion
        repeat(4) { i ->
            val s = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                delay(i * 150L)
                s.animateTo(2.5f, tween(1500, easing = LinearOutSlowInEasing))
            }
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(s.value)
                    .border(1.dp, Color.White.copy(alpha = (1f - s.value/2.5f).coerceIn(0f, 0.2f)), CircleShape)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                emoji,
                fontSize = 80.sp,
                modifier = Modifier.scale(scale.value).alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "DATA RETRIEVED",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

@Composable
fun BirthdayRevealingView(emoji: String) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    val confetti = remember { List(30) { ConfettiState() } }

    LaunchedEffect(Unit) {
        launch { scale.animateTo(1.8f, tween(1500, easing = OvershootInterpolator().toEasing())) }
        launch { alpha.animateTo(1f, tween(1000)) }
        launch { rotation.animateTo(360f, tween(2000, easing = FastOutSlowInEasing)) }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Confetti Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            confetti.forEach { state ->
                // Use a simple progress based on time for fallback if animation spec is complex
                val time = System.currentTimeMillis()
                val progress = ((time + state.delay) % 2000) / 2000f
                val y = size.height * (state.startY + progress * 1.5f)
                if (y in -50f..size.height + 50f) {
                    rotate(state.rotation + progress * 360, Offset(size.width * state.startX, y)) {
                        drawRect(
                            color = state.color,
                            topLeft = Offset(size.width * state.startX, y),
                            size = Size(10.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                emoji,
                fontSize = 100.sp,
                modifier = Modifier
                    .graphicsLayer { rotationZ = rotation.value }
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "HAPPY BIRTHDAY!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(alpha.value)
            )
            Text(
                "Time to unwrap your memory.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp).alpha(alpha.value)
            )
        }
    }
}

private class ConfettiState {
    val startX = Random.nextFloat()
    val startY = Random.nextFloat() * -0.5f
    val speed = Random.nextFloat() * 1.2f + 0.8f
    val color = listOf(AuroraCyan, NebulaPink, StarGold, Color.White).random()
    val rotation = Random.nextFloat() * 360f
    val delay = Random.nextInt(2000)
}

// Helper to use android Interpolator in Compose
fun android.view.animation.Interpolator.toEasing() = androidx.compose.animation.core.Easing { x ->
    getInterpolation(x)
}

class OvershootInterpolator : android.view.animation.Interpolator {
    override fun getInterpolation(input: Float): Float {
        val t = input - 1.0f
        return t * t * ((2.0f + 1.0f) * t + 2.0f) + 1.0f
    }
}

@Composable
fun ReadingPhaseView(
    title: String,
    message: String,
    capsuleType: CapsuleType,
    accentColor: Color,
    onFinish: () -> Unit
) {
    val view = LocalView.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (capsuleType != CapsuleType.NORMAL) {
            Icon(
                Icons.Default.Cake,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "HAPPY BIRTHDAY!",
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
        } else {
            Icon(
                Icons.Default.LockOpen,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            message,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 18.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(64.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(accentColor, accentColor.copy(alpha = 0.7f))),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    HapticUtil.performVirtualKey(view)
                    onFinish()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (capsuleType != CapsuleType.NORMAL) "Celebrate Memory" else "Keep Memory",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
