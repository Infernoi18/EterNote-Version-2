package com.example.eternotev2.ui.screens.capsule

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import com.example.eternotev2.ui.components.mood.MoodSelector
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.ui.components.common.GlowButton
import androidx.compose.ui.platform.LocalView
import com.example.eternotev2.util.HapticUtil
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.Mood
import com.example.eternotev2.ui.theme.MoodColors
import com.example.eternotev2.ui.theme.moodColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCapsuleScreen(
    onCapsuleCreated: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: CreateCapsuleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = uiState.mood?.let { moodColors(it) } ?: MoodColors(
        primary = Color.White,
        secondary = Color.Gray,
        tertiary = Color.LightGray,
        glow = Color.White,
        surface = Color.DarkGray,
        gradient = listOf(Color.Black, Color.DarkGray)
    )
    val view = LocalView.current

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onCapsuleCreated()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepVoid)) {
        StarField()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Seal a Memory", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                CreationNavigationBar(
                    currentStep = uiState.currentStep,
                    onBack = { viewModel.previousStep() },
                    onNext = { viewModel.nextStep() },
                    onSave = { viewModel.saveCapsule() },
                    isNextEnabled = when (uiState.currentStep) {
                        CreateStep.IDENTITY -> uiState.title.isNotBlank()
                        CreateStep.MOOD -> uiState.mood != null
                        CreateStep.MESSAGE -> uiState.message.isNotBlank() || uiState.voiceFile != null
                        else -> true
                    },
                    accentColor = uiState.mood?.let { moodColors(it).primary } ?: Color.White
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                        }.using(SizeTransform(clip = false))
                    },
                    label = "CreationStep"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        StepIndicator(currentStep = step)

                        when (step) {
                            CreateStep.IDENTITY -> IdentityStep(
                                title = uiState.title,
                                tags = uiState.tags,
                                isCoreMemory = uiState.isCoreMemory,
                                onTitleChange = { viewModel.onTitleChanged(it) },
                                onTagsChange = { viewModel.onTagsChanged(it) },
                                onCoreMemoryChange = { 
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.onCoreMemoryChanged(it) 
                                },
                                accentColor = colors.primary
                            )
                            CreateStep.MOOD -> MoodStep(
                                selectedMood = uiState.mood,
                                showError = uiState.showMoodError,
                                onMoodSelected = { viewModel.onMoodChanged(it) }
                            )
                            CreateStep.MESSAGE -> MessageStep(
                                message = uiState.message,
                                unlockMessage = uiState.unlockMessage,
                                isRecording = uiState.isRecording,
                                isPaused = uiState.isPaused,
                                waveform = uiState.waveform,
                                duration = uiState.recordingDuration,
                                hasRecording = uiState.voiceFile != null,
                                onMessageChange = { viewModel.onMessageChanged(it) },
                                onUnlockMessageChange = { viewModel.onUnlockMessageChanged(it) },
                                onStartRecording = { 
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.startRecording() 
                                },
                                onPauseRecording = {
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.pauseRecording()
                                },
                                onResumeRecording = {
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.resumeRecording()
                                },
                                onStopRecording = { 
                                    HapticUtil.performConfirm(view)
                                    viewModel.stopRecording() 
                                },
                                onDeleteRecording = { 
                                    HapticUtil.performLongPress(view)
                                    viewModel.deleteRecording() 
                                },
                                accentColor = colors.primary
                            )
                            CreateStep.TIMING -> TimingStep(
                                selectedDate = uiState.unlockAt,
                                onDateSelected = { viewModel.onUnlockDateChanged(it) },
                                accentColor = colors.primary
                            )
                            CreateStep.REVIEW -> ReviewStep(
                                uiState = uiState,
                                accentColor = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: CreateStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CreateStep.values().forEach { step ->
            val isActive = step == currentStep
            val isCompleted = step.ordinal < currentStep.ordinal
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 2.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> Color.White
                            isCompleted -> Color.White.copy(alpha = 0.6f)
                            else -> Color.White.copy(alpha = 0.2f)
                        }
                    )
            )
        }
    }
}

@Composable
fun IdentityStep(
    title: String,
    tags: List<String>,
    isCoreMemory: Boolean,
    onTitleChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    onCoreMemoryChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "Give your capsule a name and context.",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Capsule Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )

        // Core Memory Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isCoreMemory) accentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                .clickable { onCoreMemoryChange(!isCoreMemory) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isCoreMemory) Icons.Default.AutoAwesome else Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (isCoreMemory) accentColor else Color.White.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Core Memory", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Mark this as a significant life milestone", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isCoreMemory,
                onCheckedChange = onCoreMemoryChange,
                colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
            )
        }
    }
}

@Composable
fun MoodStep(
    selectedMood: Mood?,
    showError: Boolean,
    onMoodSelected: (Mood) -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(showError) {
        if (showError) {
            repeat(3) {
                shakeOffset.animateTo(
                    targetValue = 10f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium)
                )
                shakeOffset.animateTo(
                    targetValue = -10f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium)
                )
            }
            shakeOffset.animateTo(0f)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "What is the emotional tone of this memory?",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        MoodSelector(
            selectedMood = selectedMood,
            onMoodSelected = onMoodSelected
        )

        if (showError) {
            Text(
                text = "Please select a mood to continue",
                color = Color.Red.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .offset(x = shakeOffset.value.dp)
            )
        }
    }
}

@Composable
fun MessageStep(
    message: String,
    unlockMessage: String,
    isRecording: Boolean,
    isPaused: Boolean,
    waveform: List<Float>,
    duration: Long,
    hasRecording: Boolean,
    onMessageChange: (String) -> Unit,
    onUnlockMessageChange: (String) -> Unit,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onDeleteRecording: () -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "Compose your message or record a note.",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            label = { Text("Write to your future self...") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )

        VoiceRecordingSection(
            isRecording = isRecording,
            isPaused = isPaused,
            waveform = waveform,
            duration = duration,
            hasRecording = hasRecording,
            onStartRecording = onStartRecording,
            onPauseRecording = onPauseRecording,
            onResumeRecording = onResumeRecording,
            onStopRecording = onStopRecording,
            onDeleteRecording = onDeleteRecording,
            accentColor = accentColor
        )

        OutlinedTextField(
            value = unlockMessage,
            onValueChange = onUnlockMessageChange,
            label = { Text("Short hint or unlock message (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingStep(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    accentColor: Color
) {
    val view = LocalView.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Return true only for today and future dates in UTC
                val today = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                return utcTimeMillis >= today
            }

            override fun isSelectableYear(year: Int): Boolean {
                val today = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                return year >= today.get(Calendar.YEAR)
            }
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().apply { timeInMillis = selectedDate }.get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().apply { timeInMillis = selectedDate }.get(Calendar.MINUTE)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it
                        // Keep current time
                        val oldCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                        calendar.set(Calendar.HOUR_OF_DAY, oldCal.get(Calendar.HOUR_OF_DAY))
                        calendar.set(Calendar.MINUTE, oldCal.get(Calendar.MINUTE))
                        onDateSelected(calendar.timeInMillis)
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = selectedDate
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    onDateSelected(calendar.timeInMillis)
                    showTimePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "When should this capsule reveal itself?",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        val presets = listOf(
            "1 Day" to 1000L * 60 * 60 * 24,
            "1 Week" to 1000L * 60 * 60 * 24 * 7,
            "1 Month" to 1000L * 60 * 60 * 24 * 30,
            "6 Months" to 1000L * 60 * 60 * 24 * 180,
            "1 Year" to 1000L * 60 * 60 * 24 * 365
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            presets.forEach { (label, offset) ->
                val timestamp = System.currentTimeMillis() + offset
                val isSelected = (selectedDate - timestamp).let { Math.abs(it) < 1000 * 60 * 60 } // Within an hour

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f))
                        .border(1.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { 
                            HapticUtil.performVirtualKey(view)
                            onDateSelected(timestamp) 
                        }
                        .padding(16.dp)
                ) {
                    Text(label, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }

        val dateStr = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(selectedDate))
        OutlinedButton(
            onClick = { 
                HapticUtil.performVirtualKey(view)
                showDatePicker = true 
            },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pick Custom Date & Time: $dateStr", color = Color.White)
        }
    }
}

@Composable
fun ReviewStep(
    uiState: CreateCapsuleUiState,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Review your Capsule",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        ReviewItem(label = "Title", value = uiState.title)
        ReviewItem(
            label = "Mood",
            value = uiState.mood?.let { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } } ?: "Not selected",
            icon = Icons.Default.Face
        )
        ReviewItem(label = "Message", value = if (uiState.message.isNotBlank()) "Written message included" else "No written message")
        ReviewItem(label = "Voice Note", value = if (uiState.voiceFile != null) "Voice recording attached" else "No voice note")
        ReviewItem(
            label = "Unlocks On", 
            value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(uiState.unlockAt)),
            icon = Icons.Default.LockClock
        )
        if (uiState.isCoreMemory) {
            ReviewItem(label = "Type", value = "Core Memory", icon = Icons.Default.AutoAwesome)
        }
    }
}

@Composable
fun ReviewItem(label: String, value: String, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column {
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CreationNavigationBar(
    currentStep: CreateStep,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
    isNextEnabled: Boolean,
    accentColor: Color
) {
    val view = LocalView.current
    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep != CreateStep.IDENTITY) {
                TextButton(onClick = {
                    HapticUtil.performVirtualKey(view)
                    onBack()
                }) {
                    Text("Back", color = Color.White.copy(alpha = 0.7f))
                }
            } else {
                Spacer(modifier = Modifier.width(60.dp))
            }

            if (currentStep == CreateStep.REVIEW) {
                GlowButton(
                    text = "Seal Memory",
                    onClick = {
                        HapticUtil.performConfirm(view)
                        onSave()
                    },
                    glowColor = accentColor,
                    modifier = Modifier.width(180.dp)
                )
            } else {
                Button(
                    onClick = {
                        HapticUtil.performVirtualKey(view)
                        onNext()
                    },
                    enabled = isNextEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        disabledContainerColor = accentColor.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue", color = if (isNextEnabled) Color.Black else Color.White.copy(alpha = 0.5f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp).size(18.dp))
                }
            }
        }
    }
}

@Composable
fun VoiceRecordingSection(
    isRecording: Boolean,
    isPaused: Boolean,
    waveform: List<Float>,
    duration: Long,
    hasRecording: Boolean,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onDeleteRecording: () -> Unit,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.medium)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isRecording || hasRecording) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                WaveformVisualizer(waveform = waveform, color = if (isPaused) Color.Gray else accentColor)
            }
            
            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            Text(
                text = "%d:%02d".format(minutes, seconds) + if (isPaused) " (Paused)" else "",
                color = if (isPaused) Color.Gray else Color.White,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRecording) {
                    IconButton(onClick = { if (isPaused) onResumeRecording() else onPauseRecording() }) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onStopRecording) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.Red)
                    }
                } else {
                    IconButton(onClick = onDeleteRecording) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.6f))
                    }
                }
            }
        } else {
            Button(
                onClick = onStartRecording,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                shape = CircleShape,
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Record", tint = accentColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add a voice note", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}

@Composable
fun WaveformVisualizer(waveform: List<Float>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val barWidth = 4.dp.toPx()
        val gap = 2.dp.toPx()
        val maxBars = (width / (barWidth + gap)).toInt()
        
        val displayWaveform = if (waveform.size > maxBars) {
            waveform.takeLast(maxBars)
        } else {
            waveform
        }

        displayWaveform.forEachIndexed { index, amplitude ->
            val x = width - (displayWaveform.size - index) * (barWidth + gap)
            val barHeight = (amplitude * height).coerceAtLeast(4.dp.toPx())
            
            drawLine(
                color = color,
                start = Offset(x, centerY - barHeight / 2),
                end = Offset(x, centerY + barHeight / 2),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
