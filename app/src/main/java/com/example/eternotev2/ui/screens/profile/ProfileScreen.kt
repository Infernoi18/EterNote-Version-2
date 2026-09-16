package com.example.eternotev2.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.ui.components.common.GlassCard
import com.example.eternotev2.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
    ) {
        StarField()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(CosmicViolet.copy(alpha = 0.2f))
                        .border(2.dp, CosmicViolet, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = CosmicViolet
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Username", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                                if (isEditing) {
                                    OutlinedTextField(
                                        value = editedUsername,
                                        onValueChange = { editedUsername = it },
                                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = CosmicViolet,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                            unfocusedContainerColor = Color.Transparent
                                        )
                                    )
                                } else {
                                    Text(uiState.username, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            
                            Row {
                                if (isEditing) {
                                    IconButton(onClick = {
                                        isEditing = false
                                    }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Cancel",
                                            tint = ErrorRed
                                        )
                                    }
                                    IconButton(onClick = {
                                        if (editedUsername.isNotBlank()) {
                                            viewModel.updateUsername(editedUsername)
                                            isEditing = false
                                        }
                                    }) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Save",
                                            tint = Color.Green
                                        )
                                    }
                                } else {
                                    IconButton(onClick = {
                                        editedUsername = uiState.username
                                        isEditing = true
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = CosmicViolet
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text("Email", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(uiState.email, fontSize = 16.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text("Birth Date", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val calendar = Calendar.getInstance()
                                        uiState.birthDate?.let { calendar.timeInMillis = it }
                                        android.app.DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                val selected = Calendar.getInstance().apply {
                                                    set(year, month, dayOfMonth)
                                                }
                                                viewModel.updateBirthDate(selected.timeInMillis)
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Cake, contentDescription = null, tint = CosmicViolet, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.birthDate != null) dateFormatter.format(Date(uiState.birthDate!!)) else "Not set",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section
                Text(
                    text = "Your Journey",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total",
                        value = uiState.totalCapsules.toString(),
                        icon = Icons.Default.HourglassEmpty,
                        color = CosmicViolet
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Unlocked",
                        value = uiState.unlockedCapsules.toString(),
                        icon = Icons.Default.LockOpen,
                        color = NebulaPink
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Core",
                        value = uiState.coreMemories.toString(),
                        icon = Icons.Default.Favorite,
                        color = AuroraCyan
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(ErrorRed.copy(alpha = 0.7f), ErrorRed)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.logout() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log Out",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
