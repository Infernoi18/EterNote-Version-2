package com.example.eternotev2.ui.screens.capsule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import kotlinx.coroutines.launch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.BorderStroke
import com.example.eternotev2.ui.components.mood.MoodSelector
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.data.model.CapsuleType
import com.example.eternotev2.util.HapticUtil
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.Mood
import com.example.eternotev2.ui.theme.MoodColors
import com.example.eternotev2.ui.theme.moodColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
private val TomorrowOrLaterDates = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return utcTimeMillis > today.timeInMillis
    }

    override fun isSelectableYear(year: Int): Boolean =
        year >= Calendar.getInstance().get(Calendar.YEAR)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCapsuleScreen(
    onCapsuleCreated: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: CreateCapsuleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = uiState.mood?.let { moodColors(it) } ?: MoodColors(
        primary   = Color.White,
        secondary = Color.Gray,
        tertiary  = Color.LightGray,
        glow      = Color.White,
        surface   = Color.DarkGray,
        gradient  = listOf(Color.Black, Color.DarkGray)
    )
    val view = LocalView.current

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) onCapsuleCreated()
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                CreationNavigationBar(
                    currentStep   = uiState.currentStep,
                    onBack        = { viewModel.previousStep() },
                    onNext        = { viewModel.nextStep() },
                    onSave        = { viewModel.saveCapsule() },
                    isNextEnabled = when (uiState.currentStep) {
                        CreateStep.IDENTITY -> uiState.title.isNotBlank()
                        CreateStep.FRIEND_NAME -> uiState.friendName.isNotBlank()
                        CreateStep.MOOD     -> uiState.mood != null
                        CreateStep.MESSAGE  -> uiState.message.isNotBlank() || uiState.voiceFile != null
                        else                -> true
                    },
                    accentColor = uiState.mood?.let { moodColors(it).primary } ?: Color.White
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                AnimatedContent(
                    targetState  = uiState.currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { it } + fadeIn()) togetherWith
                                    (slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()) togetherWith
                                    (slideOutHorizontally { it } + fadeOut())
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
                        StepIndicator(currentStep = step, steps = uiState.steps)

                        when (step) {
                            CreateStep.IDENTITY -> IdentityStep(
                                title              = uiState.title,
                                tags               = uiState.tags,
                                isCoreMemory       = uiState.isCoreMemory,
                                capsuleType        = uiState.capsuleType,
                                onTitleChange      = { viewModel.onTitleChanged(it) },
                                onTagsChange       = { viewModel.onTagsChanged(it) },
                                onCoreMemoryChange = {
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.onCoreMemoryChanged(it)
                                },
                                onCapsuleTypeChange = { viewModel.onCapsuleTypeChanged(it) },
                                accentColor        = colors.primary
                            )
                            CreateStep.FRIEND_NAME -> FriendNameStep(
                                friendName         = uiState.friendName,
                                onFriendNameChange = { viewModel.onFriendNameChanged(it) },
                                accentColor        = colors.primary
                            )
                            CreateStep.MOOD -> MoodStep(
                                selectedMood   = uiState.mood,
                                showError      = uiState.showMoodError,
                                onMoodSelected = { viewModel.onMoodChanged(it) }
                            )
                            CreateStep.MESSAGE -> MessageStep(
                                message               = uiState.message,
                                unlockMessage         = uiState.unlockMessage,
                                isRecording           = uiState.isRecording,
                                isPaused              = uiState.isPaused,
                                waveform              = uiState.waveform,
                                duration              = uiState.recordingDuration,
                                hasRecording          = uiState.voiceFile != null,
                                onMessageChange       = { viewModel.onMessageChanged(it) },
                                onUnlockMessageChange = { viewModel.onUnlockMessageChanged(it) },
                                onStartRecording      = {
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.startRecording()
                                },
                                onPauseRecording  = {
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.pauseRecording()
                                },
                                onResumeRecording = {
                                    HapticUtil.performVirtualKey(view)
                                    viewModel.resumeRecording()
                                },
                                onStopRecording   = {
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
                                selectedDate  = uiState.unlockAt,
                                capsuleType   = uiState.capsuleType,
                                friendName    = uiState.friendName,
                                onDateSelected = { viewModel.onUnlockDateChanged(it) },
                                accentColor   = colors.primary,
                                viewModel     = viewModel
                            )
                            CreateStep.REVIEW -> ReviewStep(
                                uiState     = uiState,
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
fun StepIndicator(currentStep: CreateStep, steps: List<CreateStep>) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val currentIndex = steps.indexOf(currentStep)
        steps.forEachIndexed { index, step ->
            val isActive    = step == currentStep
            val isCompleted = index < currentIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 2.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive    -> Color.White
                            isCompleted -> Color.White.copy(alpha = 0.6f)
                            else        -> Color.White.copy(alpha = 0.2f)
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
    capsuleType: CapsuleType,
    onTitleChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    onCoreMemoryChange: (Boolean) -> Unit,
    onCapsuleTypeChange: (CapsuleType) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "Give your capsule a name and context.",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value         = title,
            onValueChange = onTitleChange,
            label         = { Text("Capsule Title") },
            modifier      = Modifier.fillMaxWidth(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor    = Color.White,
                unfocusedTextColor  = Color.White,
                focusedBorderColor  = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )

        CapsuleTypeSelector(
            selectedType  = capsuleType,
            onTypeSelected = onCapsuleTypeChange,
            accentColor   = accentColor
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isCoreMemory) accentColor.copy(alpha = 0.2f)
                    else Color.White.copy(alpha = 0.05f)
                )
                .clickable { onCoreMemoryChange(!isCoreMemory) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AutoAwesome,
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
                checked         = isCoreMemory,
                onCheckedChange = onCoreMemoryChange,
                colors          = SwitchDefaults.colors(checkedThumbColor = accentColor)
            )
        }
    }
}

@Composable
fun FriendNameStep(
    friendName: String,
    onFriendNameChange: (String) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "Whose birthday is it?",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value         = friendName,
            onValueChange = onFriendNameChange,
            label         = { Text("Friend's Name") },
            placeholder   = { Text("e.g. Sarah") },
            modifier      = Modifier.fillMaxWidth(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor    = Color.White,
                unfocusedTextColor  = Color.White,
                focusedBorderColor  = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Text(
            "We'll use this to personalize the countdown and the unlock experience.",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
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
                shakeOffset.animateTo(10f,  spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
                shakeOffset.animateTo(-10f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
            }
            shakeOffset.animateTo(0f)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            "What is the emotional tone of this memory?",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        MoodSelector(
            selectedMood   = selectedMood,
            onMoodSelected = onMoodSelected
        )

        if (showError) {
            Text(
                text       = "Please select a mood to continue",
                color      = Color.Red.copy(alpha = 0.8f),
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier   = Modifier.padding(horizontal = 8.dp).offset(x = shakeOffset.value.dp)
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
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value         = message,
            onValueChange = onMessageChange,
            label         = { Text("Write to your future self...") },
            modifier      = Modifier.fillMaxWidth().height(150.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor    = Color.White,
                unfocusedTextColor  = Color.White,
                focusedBorderColor  = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )

        VoiceRecordingSection(
            isRecording      = isRecording,
            isPaused         = isPaused,
            waveform         = waveform,
            duration         = duration,
            hasRecording     = hasRecording,
            onStartRecording  = onStartRecording,
            onPauseRecording  = onPauseRecording,
            onResumeRecording = onResumeRecording,
            onStopRecording   = onStopRecording,
            onDeleteRecording = onDeleteRecording,
            accentColor       = accentColor
        )

        OutlinedTextField(
            value         = unlockMessage,
            onValueChange = onUnlockMessageChange,
            label         = { Text("Short hint or unlock message (Optional)") },
            modifier      = Modifier.fillMaxWidth(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor    = Color.White,
                unfocusedTextColor  = Color.White,
                focusedBorderColor  = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingStep(
    selectedDate: Long,
    capsuleType: CapsuleType,
    friendName: String,
    onDateSelected: (Long) -> Unit,
    accentColor: Color,
    viewModel: CreateCapsuleViewModel
) {
    val view = LocalView.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = remember(selectedDate) {
        Calendar.getInstance().apply { timeInMillis = selectedDate }
    }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val timePickerState = rememberTimePickerState(
        initialHour   = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (selectedDate > System.currentTimeMillis()) selectedDate else null,
        selectableDates           = TomorrowOrLaterDates
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("OK", color = accentColor) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color.White.copy(alpha = 0.6f)) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color(0xFF1A1A1A))
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    selectedDayContainerColor = accentColor,
                    selectedDayContentColor = Color.Black,
                    todayContentColor = accentColor,
                    todayDateBorderColor = accentColor
                )
            )
        }
    }

    if (showTimePicker) {
        EternoTimePickerDialog(
            state       = timePickerState,
            onConfirm   = {
                val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                cal.set(Calendar.MINUTE,      timePickerState.minute)
                onDateSelected(cal.timeInMillis)
                showTimePicker = false
            },
            onDismiss   = { showTimePicker = false },
            accentColor = accentColor
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        val headerText = when (capsuleType) {
            CapsuleType.BIRTHDAY_SELF -> "When is your birthday?"
            CapsuleType.BIRTHDAY_OTHER -> {
                if (friendName.isNotBlank()) "When is $friendName's birthday?"
                else "When is their birthday?"
            }
            else -> "When should this memory reveal itself?"
        }

        Text(
            text = headerText,
            color      = Color.White,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold
        )

        if (capsuleType != CapsuleType.NORMAL) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Celebration, contentDescription = null, tint = accentColor)
                Text(
                    text = "This capsule will unlock with a special celebration experience.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

        // 1. Year selection card (Horizontal scroll)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape    = RoundedCornerShape(16.dp),
            border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Unlock Year", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        Text(
                            text       = "${calendar.get(Calendar.YEAR)}",
                            color      = Color.White,
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (capsuleType == CapsuleType.BIRTHDAY_SELF) {
                        viewModel.getAgeAtUnlock()?.let { age ->
                            Surface(
                                color = accentColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Your ${age}${getOrdinal(age)} Birthday",
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                InlineYearSelector(
                    selectedYear   = calendar.get(Calendar.YEAR),
                    minimumYear    = currentYear,
                    onYearSelected = { newYear ->
                        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                        cal.set(Calendar.YEAR, newYear)
                        onDateSelected(cal.timeInMillis)
                    },
                    accentColor    = accentColor
                )
            }
        }

        // 2. Calendar Opener (Pick Date & Time)
        val dateStr = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(selectedDate))

        OutlinedButton(
            onClick  = {
                HapticUtil.performVirtualKey(view)
                showDatePicker = true
            },
            modifier = Modifier.fillMaxWidth(),
            border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Pick Date & Time", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = dateStr, color = accentColor, fontSize = 12.sp)
            }
        }

        // 3. Presets (2x2 Grid)
        val presets = listOf(
            "1 Day"   to Calendar.DAY_OF_YEAR,
            "1 Week"  to Calendar.WEEK_OF_YEAR,
            "1 Month" to Calendar.MONTH,
            "1 Year"  to Calendar.YEAR
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            presets.chunked(2).forEach { rowPresets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowPresets.forEach { (label, field) ->
                        val presetCal = Calendar.getInstance().apply {
                            add(field, 1)
                        }
                        val timestamp = presetCal.timeInMillis
                        val isSelected = Math.abs(selectedDate - timestamp) < 1000 * 60 * 60

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) accentColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable {
                                    HapticUtil.performVirtualKey(view)
                                    onDateSelected(timestamp)
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        val fullDate = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(selectedDate))
        Text(
            text      = "Capsule will reveal on: $fullDate",
            color     = accentColor.copy(alpha = 0.8f),
            fontSize  = 12.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun YearPickerDialog(
    currentYear: Int,
    minimumYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    accentColor: Color
) {
    val years     = (minimumYear..minimumYear + 50).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Select Year", color = Color.White) },
        text = {
            Box(modifier = Modifier.height(300.dp)) {
                LazyColumn {
                    items(years) { year ->
                        Text(
                            text = year.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onYearSelected(year) }
                                .background(if (year == currentYear) accentColor.copy(alpha = 0.2f) else Color.Transparent)
                                .padding(16.dp),
                            color      = if (year == currentYear) accentColor else Color.White,
                            textAlign  = TextAlign.Center,
                            fontWeight = if (year == currentYear) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("Cancel", color = Color.White.copy(alpha = 0.6f)) }
        },
        containerColor = Color(0xFF1A1A1A)
    )
}

@Composable
fun InlineYearSelector(
    selectedYear: Int,
    minimumYear: Int,
    onYearSelected: (Int) -> Unit,
    accentColor: Color
) {
    val years = (minimumYear..minimumYear + 15).toList()
    val listState = rememberLazyListState()

    LaunchedEffect(selectedYear) {
        val index = years.indexOf(selectedYear).coerceAtLeast(0)
        listState.animateScrollToItem(index)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Select Year", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
        LazyRow(state = listState, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
            items(years) { year ->
                val isSelected = year == selectedYear
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (isSelected) accentColor else Color.White.copy(alpha = 0.08f)).border(width = if (isSelected) 0.dp else 1.dp, color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)).clickable { onYearSelected(year) }.padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(text = year.toString(), color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EternoTimePickerDialog(
    state: TimePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    accentColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirm", color = accentColor) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White.copy(alpha = 0.6f)) } },
        title = { Text("Select Time", color = Color.White) },
        text = {
            TimePicker(
                state  = state,
                colors = TimePickerDefaults.colors(
                    clockDialColor                    = Color.White.copy(alpha = 0.05f),
                    clockDialSelectedContentColor     = Color.White,
                    clockDialUnselectedContentColor   = Color.White.copy(alpha = 0.5f),
                    selectorColor                     = accentColor,
                    periodSelectorBorderColor         = Color.White.copy(alpha = 0.3f),
                    periodSelectorSelectedContainerColor   = accentColor.copy(alpha = 0.3f),
                    periodSelectorUnselectedContainerColor = Color.Transparent,
                    periodSelectorSelectedContentColor    = Color.White,
                    periodSelectorUnselectedContentColor  = Color.White.copy(alpha = 0.5f),
                    timeSelectorSelectedContainerColor    = accentColor.copy(alpha = 0.3f),
                    timeSelectorUnselectedContainerColor  = Color.White.copy(alpha = 0.05f),
                    timeSelectorSelectedContentColor      = Color.White,
                    timeSelectorUnselectedContentColor    = Color.White.copy(alpha = 0.5f)
                )
            )
        },
        containerColor = Color(0xFF1A1A1A)
    )
}

fun getOrdinal(n: Int): String {
    if (n in 11..13) return "th"
    return when (n % 10) {
        1    -> "st"
        2    -> "nd"
        3    -> "rd"
        else -> "th"
    }
}

@Composable
fun ReviewStep(
    uiState: CreateCapsuleUiState,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Review your Capsule", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        ReviewItem(label = "Title", value = uiState.title)
        ReviewItem(
            label = "Type",
            value = when (uiState.capsuleType) {
                CapsuleType.NORMAL         -> "Regular Memory"
                CapsuleType.BIRTHDAY_SELF  -> "My Birthday"
                CapsuleType.BIRTHDAY_OTHER -> if (uiState.friendName.isNotBlank()) "${uiState.friendName}'s Birthday" else "Friend's Birthday"
            },
            icon = when (uiState.capsuleType) {
                CapsuleType.NORMAL -> Icons.Default.Description
                else               -> Icons.Default.Cake
            }
        )
        ReviewItem(label = "Mood", value = uiState.mood?.let { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } } ?: "Not selected", icon = Icons.Default.Face)
        ReviewItem(label = "Message", value = if (uiState.message.isNotBlank()) "Written message included" else "No written message")
        ReviewItem(label = "Voice Note", value = if (uiState.voiceFile != null) "Voice recording attached" else "No voice note")
        ReviewItem(label = "Unlocks On", value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(uiState.unlockAt)), icon = Icons.Default.LockClock)
        if (uiState.isCoreMemory) { ReviewItem(label = "Type", value = "Core Memory", icon = Icons.Default.AutoAwesome) }
    }
}

@Composable
fun CapsuleTypeSelector(
    selectedType: CapsuleType,
    onTypeSelected: (CapsuleType) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Capsule Type", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CapsuleTypeOption(isSelected = selectedType == CapsuleType.NORMAL, onClick = { onTypeSelected(CapsuleType.NORMAL) }, accentColor = accentColor, icon = Icons.Default.HistoryEdu, label = "Normal")
            CapsuleTypeOption(isSelected = selectedType == CapsuleType.BIRTHDAY_SELF, onClick = { onTypeSelected(CapsuleType.BIRTHDAY_SELF) }, accentColor = accentColor, icon = Icons.Default.Cake, label = "My Birthday")
            CapsuleTypeOption(isSelected = selectedType == CapsuleType.BIRTHDAY_OTHER, onClick = { onTypeSelected(CapsuleType.BIRTHDAY_OTHER) }, accentColor = accentColor, icon = Icons.Default.Celebration, label = "Friend's B'day")
        }
    }
}

@Composable
fun RowScope.CapsuleTypeOption(
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    icon: ImageVector,
    label: String
) {
    Column(
        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (isSelected) accentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)).border(1.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun ReviewItem(label: String, value: String, icon: ImageVector? = null) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
    Surface(color = Color.Black.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(24.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (currentStep != CreateStep.IDENTITY) {
                TextButton(onClick = { HapticUtil.performVirtualKey(view); onBack() }) { Text("Back", color = Color.White.copy(alpha = 0.7f)) }
            } else { Spacer(modifier = Modifier.width(60.dp)) }

            if (currentStep == CreateStep.REVIEW) {
                Box(modifier = Modifier.width(180.dp).height(48.dp).background(brush = Brush.horizontalGradient(listOf(accentColor, accentColor.copy(alpha = 0.7f))), shape = RoundedCornerShape(12.dp)).clickable { HapticUtil.performConfirm(view); onSave() }, contentAlignment = Alignment.Center) {
                    Text("Seal Memory", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = { HapticUtil.performVirtualKey(view); onNext() }, enabled = isNextEnabled, colors = ButtonDefaults.buttonColors(containerColor = accentColor, disabledContainerColor = accentColor.copy(alpha = 0.3f)), shape = RoundedCornerShape(12.dp)) {
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
    val permissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> if (isGranted) onStartRecording() }
    Column(modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.medium).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isRecording || hasRecording) {
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                WaveformVisualizer(waveform = waveform, color = if (isPaused) Color.Gray else accentColor)
            }
            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            Text(text = "%d:%02d".format(minutes, seconds) + if (isPaused) " (Paused)" else "", color = if (isPaused) Color.Gray else Color.White, fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                if (isRecording) {
                    IconButton(onClick = { if (isPaused) onResumeRecording() else onPauseRecording() }) { Icon(if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = if (isPaused) "Resume" else "Pause", tint = Color.White) }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onStopRecording) { Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.Red) }
                } else {
                    IconButton(onClick = onDeleteRecording) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.6f)) }
                }
            }
        } else {
            Button(onClick = { permissionsLauncher.launch(android.Manifest.permission.RECORD_AUDIO) }, colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)), shape = CircleShape, modifier = Modifier.size(56.dp), contentPadding = PaddingValues(0.dp)) {
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
        val displayWaveform = if (waveform.size > maxBars) waveform.takeLast(maxBars) else waveform
        displayWaveform.forEachIndexed { index, amplitude ->
            val x = width - (displayWaveform.size - index) * (barWidth + gap)
            val barHeight = (amplitude * height).coerceAtLeast(4.dp.toPx())
            drawLine(color = color, start = Offset(x, centerY - barHeight / 2), end = Offset(x, centerY + barHeight / 2), strokeWidth = barWidth, cap = StrokeCap.Round)
        }
    }
}
