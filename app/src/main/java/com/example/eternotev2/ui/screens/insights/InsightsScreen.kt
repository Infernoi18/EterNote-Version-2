package com.example.eternotev2.ui.screens.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.navigation.Routes
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.components.common.EternoteBottomBar
import com.example.eternotev2.ui.components.common.GlassCard
import com.example.eternotev2.ui.theme.AuroraCyan
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.NebulaPink
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary
import com.example.eternotev2.ui.theme.TextTertiary
import com.example.eternotev2.ui.theme.moodColors

@Composable
fun InsightsScreen(
    onProfileClick: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientBackground(
            modifier = Modifier.fillMaxSize(),
            primaryColor = CosmicViolet,
            secondaryColor = AuroraCyan
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Insights",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your emotional fingerprint over time.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCardInsights("Total\nCapsules", uiState.totalCapsules, CosmicViolet, Modifier.weight(1f))
                StatCardInsights("Opened\nCapsules", uiState.totalUnlocked, AuroraCyan, Modifier.weight(1f))
                StatCardInsights("Core\nMemories", uiState.totalCoreMemories, NebulaPink, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mood Distribution Chart
            Text(
                text = "Mood Distribution",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (uiState.moodDistribution.isEmpty()) {
                        Text("No mood data available.", color = TextTertiary, fontSize = 14.sp)
                    } else {
                        val maxCount = uiState.moodDistribution.values.maxOrNull()?.toFloat() ?: 1f
                        uiState.moodDistribution.forEach { (mood, count) ->
                            val moodClrs = moodColors(mood)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(text = mood.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f).height(12.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val barWidth = (count / maxCount) * size.width
                                        drawRoundRect(
                                            color = moodClrs.primary,
                                            size = Size(barWidth, size.height),
                                            cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = count.toString(), color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCardInsights(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}
