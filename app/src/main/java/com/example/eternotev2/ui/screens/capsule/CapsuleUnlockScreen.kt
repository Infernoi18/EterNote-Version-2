package com.example.eternotev2.ui.screens.capsule

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalView
import com.example.eternotev2.util.HapticUtil
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.ui.components.common.GlowButton
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.moodColors

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

    Box(modifier = Modifier.fillMaxSize().background(DeepVoid)) {
        StarField()

        if (uiState.capsule != null) {
            val capsule = uiState.capsule!!
            val colors = moodColors(capsule.mood)

            Crossfade(targetState = uiState.currentPhase, label = "UnlockPhase") { phase ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (phase) {
                        UnlockPhase.INTRO -> IntroPhaseView(capsule.title)
                        UnlockPhase.UNSEALING -> UnsealingPhaseView(colors.primary)
                        UnlockPhase.REVEALING -> RevealingPhaseView(capsule.mood.emoji)
                        UnlockPhase.READING -> ReadingPhaseView(
                            title = capsule.title,
                            message = capsule.message,
                            accentColor = colors.primary,
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
fun UnsealingPhaseView(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer Glow
        Canvas(modifier = Modifier.size(200.dp).scale(scale).alpha(alpha)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accentColor.copy(alpha = 0.5f), Color.Transparent)
                )
            )
        }
        
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        
        Text(
            "Breaking Temporal Seal...",
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.offset(y = 100.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun RevealingPhaseView(emoji: String) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.5f, tween(1500, easing = OvershootInterpolator().toEasing()))
        alpha.animateTo(1f, tween(1000))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            emoji,
            fontSize = 80.sp,
            modifier = Modifier.scale(scale.value).alpha(alpha.value)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Memory Restored",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 2.sp,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}

@Composable
fun ReadingPhaseView(
    title: String,
    message: String,
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
        Icon(
            Icons.Default.LockOpen,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(48.dp)
        )
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
        GlowButton(
            text = "Keep Memory",
            onClick = {
                HapticUtil.performVirtualKey(view)
                onFinish()
            },
            glowColor = accentColor
        )
    }
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
