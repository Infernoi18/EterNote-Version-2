package com.example.eternotev2.ui.screens.capsule

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.data.model.CapsuleType
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.components.animation.ConstellationBurstAnimation
import com.example.eternotev2.ui.components.animation.TemporalRiftAnimation
import com.example.eternotev2.ui.theme.*
import com.example.eternotev2.util.HapticUtil

@Composable
fun CapsuleUnlockScreen(
    capsuleId: Long,
    onUnlockComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: CapsuleUnlockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                                TemporalRiftAnimation(
                                    onAnimationEnd = { viewModel.onAnimationFinished() }
                                )
                            } else {
                                ConstellationBurstAnimation(
                                    onAnimationEnd = { viewModel.onAnimationFinished() }
                                )
                            }
                        }
                        UnlockPhase.REVEALING -> {
                            // This phase is now largely handled within the cinematic animations, 
                            // but kept for transition compatibility if needed.
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
