package com.example.eternotev2.ui.screens.timeline

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.navigation.Routes
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.components.common.EternoteBottomBar
import com.example.eternotev2.ui.components.common.GlassCard
import com.example.eternotev2.ui.theme.AuroraCyan
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.SurfaceGlass
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary
import com.example.eternotev2.ui.theme.TextTertiary
import com.example.eternotev2.ui.theme.moodColors
import androidx.compose.ui.platform.LocalView
import com.example.eternotev2.util.HapticUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineScreen(
    onCapsuleClick: (Long) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientBackground(
            modifier = Modifier.fillMaxSize(),
            primaryColor = AuroraCyan,
            secondaryColor = CosmicViolet
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 120.dp, top = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Timeline",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Your journey, chronologically mapped.",
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
                Spacer(modifier = Modifier.height(32.dp))
            }

            uiState.groupedCapsules.forEach { (monthYear, capsules) ->
                val isCollapsed = uiState.collapsedMonths.contains(monthYear)
                item(key = "header_$monthYear") {
                    TimelineMonthHeader(
                        monthYear = monthYear,
                        isCollapsed = isCollapsed,
                        onToggle = { viewModel.toggleMonth(monthYear) }
                    )
                }
                if (!isCollapsed) {
                    items(
                        items = capsules,
                        key = { it.id }
                    ) { capsule ->
                        TimelineCapsuleNode(
                            capsule = capsule,
                            onClick = {
                                HapticUtil.performLongPress(view)
                                onCapsuleClick(capsule.id)
                            }
                        )
                    }
                }
            }

            if (uiState.isEmpty) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No history recorded yet.",
                            color = TextTertiary,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineMonthHeader(
    monthYear: String,
    isCollapsed: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AuroraCyan)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = monthYear.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = AuroraCyan
            )
        }
        
        Icon(
            imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            contentDescription = if (isCollapsed) "Expand" else "Collapse",
            tint = AuroraCyan,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TimelineCapsuleNode(
    capsule: Capsule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moodClrs = moodColors(capsule.mood)
    val formatter = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }
    val dateStr = remember(capsule.createdAt) { formatter.format(Date(capsule.createdAt)) }

    // Optimization: Draw animations only when needed or use Draw phase
    // Note: rememberInfiniteTransition still runs, but we minimize recompositions
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Timeline line + dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(moodClrs.primary.copy(alpha = glowAlpha))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White, moodClrs.primary),
                                center = androidx.compose.ui.geometry.Offset(0f, 6f)
                            )
                        )
                )
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(moodClrs.primary.copy(0.4f), SurfaceGlass)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content Card
        GlassCard(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = capsule.mood.emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = capsule.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dateStr • ${if (capsule.isUnlocked) "Opened" else "Sealed"}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
