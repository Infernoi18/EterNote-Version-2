package com.example.eternotev2.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.data.repository.ReminderFrequency
import com.example.eternotev2.ui.components.ambient.AmbientBackground
import com.example.eternotev2.ui.components.common.GlassCard
import com.example.eternotev2.ui.theme.AuroraCyan
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.StarGold
import com.example.eternotev2.ui.theme.SurfaceGlass
import com.example.eternotev2.ui.theme.SurfaceMid
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary
import com.example.eternotev2.ui.theme.TextTertiary
import com.example.eternotev2.ui.theme.UserThemePreference
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceGlass)
                        .clickable(onClick = onBack)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                
                // Appearance Card
                Text(
                    text = "APPEARANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextTertiary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ThemeOptionRow(
                            title = "System Default",
                            subtitle = "Follows your device setting",
                            selected = uiState.themePreference == UserThemePreference.SYSTEM,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.setThemePreference(UserThemePreference.SYSTEM)
                                }
                            }
                        )
                        ThemeOptionRow(
                            title = "Dark Mode",
                            subtitle = "Deep space — always dark",
                            selected = uiState.themePreference == UserThemePreference.DARK,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.setThemePreference(UserThemePreference.DARK)
                                }
                            }
                        )
                        ThemeOptionRow(
                            title = "Light Mode",
                            subtitle = "Midnight navy — lighter feel",
                            selected = uiState.themePreference == UserThemePreference.LIGHT,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.setThemePreference(UserThemePreference.LIGHT)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Notifications Card
                Text(
                    text = "NOTIFICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextTertiary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AuroraCyan.copy(alpha = 0.15f))
                                ) {
                                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = AuroraCyan, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Capsule Unlocks", fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text("Alerts when a memory is ready", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Switch(
                                checked = uiState.notificationsEnabled,
                                onCheckedChange = { viewModel.toggleNotifications(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AuroraCyan,
                                    uncheckedThumbColor = TextTertiary,
                                    uncheckedTrackColor = SurfaceMid
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Reminder Frequency Card
                Text(
                    text = "GENTLE REMINDERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextTertiary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StarGold.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Filled.Schedule, contentDescription = null, tint = StarGold, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Writing Prompts", fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Reminders to capture a moment", fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        // Frequency Options
                        ReminderFrequency.entries.forEach { frequency ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (uiState.reminderFrequency == frequency) CosmicViolet.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { viewModel.setReminderFrequency(frequency) }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = frequency.label,
                                    fontSize = 14.sp,
                                    color = if (uiState.reminderFrequency == frequency) CosmicViolet else TextPrimary,
                                    fontWeight = if (uiState.reminderFrequency == frequency) FontWeight.Bold else FontWeight.Normal
                                )
                                if (uiState.reminderFrequency == frequency) {
                                    Text(text = "✓", color = CosmicViolet, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) CosmicViolet.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio indicator
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) CosmicViolet else TextSecondary,
                    shape = CircleShape
                )
                .padding(4.dp)
                .background(
                    color = if (selected) CosmicViolet else Color.Transparent,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
