package com.example.eternotev2.ui.screens.voice

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.components.common.GlowButton
import com.example.eternotev2.ui.components.common.glowEffect
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.NebulaPink
import com.example.eternotev2.ui.theme.SurfaceGlass
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import com.example.eternotev2.ui.theme.*

@Composable
fun VoiceNoteScreen(
    capsuleId: Long,
    onBack: () -> Unit,
    viewModel: VoiceNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // For now using a default mood color or we could pass the mood from capsule
    val baseColor = MoodLovelyPrimary
    val gradientPartner = MoodLovelySecondary

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepVoid)) {
        AmbientBackground(
            modifier = Modifier.fillMaxSize(),
            primaryColor = baseColor,
            secondaryColor = gradientPartner
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(baseColor.copy(alpha = 0.15f))
                        .border(1.dp, baseColor.copy(alpha = 0.4f), CircleShape)
                        .clickable(onClick = onBack)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Waveform display
            VoiceWaveform(amplitudes = uiState.amplitudes, color = baseColor)

            Spacer(modifier = Modifier.height(32.dp))

            // Timer
            val seconds = uiState.durationMillis / 1000
            Text(
                text = String.format("%02d:%02d", (seconds / 60).toInt(), (seconds % 60).toInt()),
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            // Record Button
            RecordButton(
                isRecording = uiState.isRecording,
                baseColor = baseColor,
                onClick = viewModel::toggleRecording
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button (Primary Gradient Button)
            if (uiState.durationMillis > 0 && !uiState.isRecording) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(56.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(baseColor, gradientPartner)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.saveVoiceNote(capsuleId) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Seal Voice Recording", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun VoiceWaveform(amplitudes: List<Float>, color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 24.dp)
    ) {
        val barWidth = 4.dp.toPx()
        val spacing = 4.dp.toPx()
        val totalBars = (size.width / (barWidth + spacing)).toInt()
        
        val displayAmplitudes = if (amplitudes.size > totalBars) {
            amplitudes.takeLast(totalBars)
        } else {
            amplitudes
        }

        val startX = (size.width - (displayAmplitudes.size * (barWidth + spacing))) / 2f

        displayAmplitudes.forEachIndexed { index, amp ->
            val barHeight = (amp / 100f) * size.height
            drawRoundRect(
                color = color.copy(alpha = 0.8f),
                topLeft = Offset(startX + index * (barWidth + spacing), (size.height - barHeight) / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

@Composable
private fun RecordButton(isRecording: Boolean, baseColor: Color, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    val bgColor by animateColorAsState(targetValue = if (isRecording) Color.Red.copy(0.8f) else baseColor, label = "bgColor")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(bgColor)
            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = "Record",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}
