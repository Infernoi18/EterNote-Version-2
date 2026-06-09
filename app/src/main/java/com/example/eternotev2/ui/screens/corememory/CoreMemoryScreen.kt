package com.example.eternotev2.ui.screens.corememory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.navigation.Routes
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.components.common.EternoteBottomBar
import com.example.eternotev2.ui.components.common.GlassCard
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.MoodGratefulPrimary
import com.example.eternotev2.ui.theme.StarGold
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary
import com.example.eternotev2.ui.theme.TextTertiary

@Composable
fun CoreMemoryScreen(
    onCapsuleClick: (Long) -> Unit,
    viewModel: CoreMemoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientBackground(
            modifier = Modifier.fillMaxSize(),
            primaryColor = StarGold,
            secondaryColor = CosmicViolet,
            tertiaryColor = MoodGratefulPrimary
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                Text(
                    text = "Core Memory",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The foundation of who you are.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            if (uiState.coreMemories.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⭐", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No core memories yet.",
                            color = TextTertiary,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.coreMemories) { capsule ->
                        CoreMemoryCard(
                            capsule = capsule,
                            onClick = { onCapsuleClick(capsule.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoreMemoryCard(
    capsule: Capsule,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        glowColor = StarGold,
        glowAlpha = 0.15f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(StarGold.copy(0.3f), StarGold.copy(0.05f))
                        )
                    )
            ) {
                Text(text = capsule.mood.emoji, fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = capsule.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
