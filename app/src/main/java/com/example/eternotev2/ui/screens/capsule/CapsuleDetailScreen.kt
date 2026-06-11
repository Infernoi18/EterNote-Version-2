package com.example.eternotev2.ui.screens.capsule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.ui.components.common.GlassCard
import com.example.eternotev2.ui.components.common.GlowButton
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.moodColors
import androidx.compose.ui.platform.LocalView
import com.example.eternotev2.util.HapticUtil
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleDetailScreen(
    capsuleId: Long,
    onUnlockClick: () -> Unit,
    onVoiceNote: () -> Unit,
    onBack: () -> Unit,
    viewModel: CapsuleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Real-time countdown for locked capsules
    var countdownText by remember(uiState.capsule) { 
        mutableStateOf(uiState.capsule?.countdownLabel ?: "") 
    }

    LaunchedEffect(uiState.capsule?.id, uiState.capsule?.unlockAt, uiState.capsule?.isUnlocked) {
        val capsule = uiState.capsule
        if (capsule != null && !capsule.isUnlocked) {
            while (true) {
                countdownText = capsule.countdownLabel
                if (System.currentTimeMillis() >= capsule.unlockAt) break
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    LaunchedEffect(capsuleId) {
        viewModel.loadCapsule(capsuleId)
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepVoid)) {
        StarField()
        
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else if (uiState.capsule != null) {
            val capsule = uiState.capsule!!
            val colors = moodColors(capsule.mood)

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Capsule?") },
                    text = { Text("Are you sure you want to permanently delete this memory? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteCapsule()
                                showDeleteDialog = false
                                onBack()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel", color = Color.White)
                        }
                    },
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White,
                    textContentColor = Color.White.copy(alpha = 0.7f)
                )
            }

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("Capsule Details", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = {
                                HapticUtil.performVirtualKey(view)
                                onBack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                HapticUtil.performConfirm(view)
                                viewModel.toggleFavorite()
                            }) {
                                Icon(
                                    if (capsule.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (capsule.isFavorite) Color.Red else Color.White
                                )
                            }
                            IconButton(onClick = {
                                HapticUtil.performLongPress(view)
                                showDeleteDialog = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.6f))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = capsule.mood.emoji,
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = capsule.title,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Created on ${formatDate(capsule.createdAt)}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Status Section
                    InfoRow(
                        icon = if (capsule.isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        label = "Status",
                        value = if (capsule.isUnlocked) {
                            "Unlocked: ${formatDate(capsule.unlockAt)}"
                        } else if (System.currentTimeMillis() >= capsule.unlockAt) {
                            "Ready to unlock"
                        } else {
                            "Locked • $countdownText"
                        },
                        accentColor = colors.primary
                    )

                    InfoRow(
                        icon = Icons.Default.AutoAwesome,
                        label = "Type",
                        value = if (capsule.isCoreMemory) "Core Memory" else "None",
                        accentColor = colors.primary
                    )

                    // Content Preview
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Memory Content", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            if (capsule.isUnlocked) {
                                Text(capsule.message, color = Color.White, lineHeight = 24.sp)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = colors.primary, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "This memory is sealed.",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    capsule.unlockMessage?.let {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            it,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 14.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Voice Note Section
                    if (capsule.hasVoiceNote) {
                        VoiceNotePreview(
                            isUnlocked = capsule.isUnlocked,
                            isPlaying = uiState.isPlaying,
                            onPlayClick = {
                                HapticUtil.performVirtualKey(view)
                                if (capsule.isUnlocked) {
                                    viewModel.playVoiceNote()
                                } else {
                                    onVoiceNote()
                                }
                            },
                            accentColor = colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))

                    if (!capsule.isUnlocked) {
                        val isUnlockable = System.currentTimeMillis() >= capsule.unlockAt
                        GlowButton(
                            text = if (isUnlockable) "Unlock Now" else "Unlocking in $countdownText",
                            onClick = {
                                HapticUtil.performConfirm(view)
                                onUnlockClick()
                            },
                            enabled = isUnlockable,
                            glowColor = colors.primary
                        )
                    } else {
                        OutlinedButton(
                            onClick = {
                                HapticUtil.performVirtualKey(view)
                                onBack()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Text("Back to Timeline", color = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun VoiceNotePreview(
    isUnlocked: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(accentColor.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
                )
            )
            .clickable(onClick = onPlayClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when {
                !isUnlocked -> Icons.Default.Mic
                isPlaying -> Icons.Default.Stop
                else -> Icons.Default.PlayArrow
            },
            contentDescription = null,
            tint = accentColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Voice Note", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = when {
                    !isUnlocked -> "Sealed voice memory"
                    isPlaying -> "Playing recording..."
                    else -> "Click to play recording"
                },
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
}
