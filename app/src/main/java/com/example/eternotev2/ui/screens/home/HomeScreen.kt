package com.example.eternotev2.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.ui.components.ambient.*
import com.example.eternotev2.ui.components.common.*
import com.example.eternotev2.ui.components.mood.*
import com.example.eternotev2.ui.theme.*
import androidx.compose.ui.platform.LocalView
import com.example.eternotev2.util.HapticUtil
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateCapsule: () -> Unit,
    onCapsuleClick: (Long) -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToCoreMemory: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val animatedColors = animatedMoodColors(uiState.currentMood)
    val moodColors = moodColors(uiState.currentMood)
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)) // Deep space background
    ) {
        // Ambient Background
        StarField()
        GlowOrb(
            color = animatedColors.glow,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 100.dp, y = (-100).dp)
        )
        ParticleField(colors = moodColors)

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        HapticUtil.performLongPress(view)
                        onCreateCapsule()
                    },
                    containerColor = animatedColors.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 80.dp) // Adjust for bottom nav
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Capsule")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Section
                item {
                    HomeHeroSection(
                        userName = uiState.userName,
                        mood = uiState.currentMood,
                        colors = animatedColors
                    )
                }

                // Mood Selector
                item {
                    MoodSelector(
                        selectedMood = uiState.currentMood,
                        onMoodSelected = { viewModel.onMoodChanged(it) }
                    )
                }

                // Capsule List Header
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Time Capsules",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            
                            var showSortMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { showSortMenu = true }) {
                                    Icon(Icons.Default.Sort, contentDescription = "Sort", tint = Color.White)
                                }
                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false },
                                    modifier = Modifier.background(SurfaceMid)
                                ) {
                                    SortOrder.values().forEach { order ->
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    text = when(order) {
                                                        SortOrder.DATE_ASC -> "Date (Oldest First)"
                                                        SortOrder.DATE_DESC -> "Date (Newest First)"
                                                        SortOrder.NAME_ASC -> "Name (A-Z)"
                                                        SortOrder.NAME_DESC -> "Name (Z-A)"
                                                    },
                                                    color = Color.White
                                                )
                                            },
                                            onClick = {
                                                viewModel.updateSortOrder(order)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.filters.status == FilterStatus.ALL,
                                onClick = { viewModel.updateFilters(uiState.filters.copy(status = FilterStatus.ALL)) },
                                label = { Text("All") },
                                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color.Black, selectedContainerColor = moodColors.primary)
                            )
                            FilterChip(
                                selected = uiState.filters.status == FilterStatus.UNOPENED,
                                onClick = { viewModel.updateFilters(uiState.filters.copy(status = FilterStatus.UNOPENED)) },
                                label = { Text("Sealed") },
                                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color.Black, selectedContainerColor = moodColors.primary)
                            )
                            FilterChip(
                                selected = uiState.filters.status == FilterStatus.OPENED,
                                onClick = { viewModel.updateFilters(uiState.filters.copy(status = FilterStatus.OPENED)) },
                                label = { Text("Opened") },
                                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color.Black, selectedContainerColor = moodColors.primary)
                            )
                            FilterChip(
                                selected = uiState.filters.hasVoiceNote == true,
                                onClick = { 
                                    val newVal = if (uiState.filters.hasVoiceNote == true) null else true
                                    viewModel.updateFilters(uiState.filters.copy(hasVoiceNote = newVal)) 
                                },
                                label = { Text("Voice") },
                                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color.Black, selectedContainerColor = moodColors.primary)
                            )
                            FilterChip(
                                selected = uiState.filters.isCoreMemory == true,
                                onClick = { 
                                    val newVal = if (uiState.filters.isCoreMemory == true) null else true
                                    viewModel.updateFilters(uiState.filters.copy(isCoreMemory = newVal)) 
                                },
                                label = { Text("Core") },
                                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color.Black, selectedContainerColor = moodColors.primary)
                            )
                        }
                    }
                }

                // Capsules
                if (uiState.filteredCapsules.isEmpty()) {
                    item(key = "empty_view") {
                        EmptyCapsulesView()
                    }
                } else {
                    items(
                        items = uiState.filteredCapsules,
                        key = { it.id },
                        contentType = { "capsule_card" }
                    ) { capsule ->
                        CapsuleCard(
                            capsule = capsule,
                            onClick = { onCapsuleClick(capsule.id) }
                        )
                    }
                }
                
                // Extra space for bottom nav
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun HomeHeroSection(
    userName: String,
    mood: Mood,
    colors: AnimatedMoodColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "Welcome back,",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp
        )
        Text(
            text = userName,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = randomMoodQuote(mood),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )
        }
    }
}


@Composable
fun CapsuleCard(
    capsule: Capsule,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable {
                HapticUtil.performLongPress(view)
                onClick()
            }
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon or Symbol
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(moodColors(capsule.mood).primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = capsule.mood.emoji, fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = capsule.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                // Real-time countdown
                var countdown by remember { mutableStateOf(capsule.countdownLabel) }
                LaunchedEffect(capsule.unlockAt, capsule.isUnlocked) {
                    if (!capsule.isUnlocked) {
                        while (true) {
                            countdown = capsule.countdownLabel
                            kotlinx.coroutines.delay(1000)
                        }
                    }
                }

                Text(
                    text = if (capsule.isUnlocked) "Opened on ${dateFormat.format(Date(capsule.unlockAt))}" else countdown,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            GlassTag(
                text = capsule.mood.label,
                color = moodColors(capsule.mood).primary
            )
        }
    }
}

@Composable
fun EmptyCapsulesView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No capsules yet",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 16.sp
        )
        Text(
            text = "Tap + to seal your first memory",
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 14.sp
        )
    }
}
