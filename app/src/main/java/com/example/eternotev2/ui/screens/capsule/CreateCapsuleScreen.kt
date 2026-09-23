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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.window.Dialog
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
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
                        StepIndicator(currentStep = step)

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
                                onDateSelected = { viewModel.onUnlockDateChanged(it) },
                                accentColor   = colors.primary
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

// ── Step indicator ────────────────────────────────────────────────────────────
@Composable
fun StepIndicator(currentStep: CreateStep) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CreateStep.entries.forEach { step ->
            val isActive    = step == currentStep
            val isCompleted = step.ordinal < currentStep.ordinal
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

// ── Step 1: Identity ──────────────────────────────────────────────────────────
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
                Text(
                    "Mark this as a significant life milestone",
                    color    = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
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

// ── Step 2: Mood ──────────────────────────────────────────────────────────────
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
                modifier   = Modifier
                    .padding(horizontal = 8.dp)
                    .offset(x = shakeOffset.value.dp)
            )
        }
    }
}

// ── Step 3: Message ───────────────────────────────────────────────────────────
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

// ── Step 4: Timing router ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingStep(
    selectedDate: Long,
    capsuleType: CapsuleType,
    onDateSelected: (Long) -> Unit,
    accentColor: Color
) {
    val viewModel: CreateCapsuleViewModel = hiltViewModel()

    if (capsuleType == CapsuleType.BIRTHDAY_SELF) {
        BirthdayTimingSection(
            selectedDate   = selectedDate,
            capsuleType    = capsuleType,
            onYearSelected = { viewModel.updateUnlockYear(it) },
            onTimeSelected = { h, m -> viewModel.updateUnlockTime(h, m) },
            accentColor    = accentColor,
            viewModel      = viewModel
        )
    } else {
        // NORMAL and BIRTHDAY_OTHER both use the full date+time picker UI.
        // BIRTHDAY_OTHER keeps its capsule type for the unlock experience.
        OtherBirthdayTimingSection(
            selectedDate   = selectedDate,
            onDateSelected = onDateSelected,
            accentColor    = accentColor,
            isBirthdayMode = capsuleType == CapsuleType.BIRTHDAY_OTHER
        )
    }
}

// ── Birthday timing ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayTimingSection(
    selectedDate: Long,
    capsuleType: CapsuleType,
    onYearSelected: (Int) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    accentColor: Color,
    viewModel: CreateCapsuleViewModel
) {
    val view     = LocalView.current
    val calendar = remember(selectedDate) {
        Calendar.getInstance().apply { timeInMillis = selectedDate }
    }

    // Compute whether this year's birthday has already passed.
    // Uses the user's birth month+day from the ViewModel's stored
    // date of birth to check against today.
    val birthdayPassedThisYear: Boolean = remember {
        val today    = Calendar.getInstance()
        val birthCal = viewModel.getUserBirthCalendar()
        if (birthCal == null) {
            false
        } else {
            val thisYearBirthday = Calendar.getInstance().apply {
                set(Calendar.YEAR,         today.get(Calendar.YEAR))
                set(Calendar.MONTH,        birthCal.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY,  0)
                set(Calendar.MINUTE,       0)
                set(Calendar.SECOND,       0)
                set(Calendar.MILLISECOND,  0)
            }
            today.after(thisYearBirthday)
        }
    }

    // The earliest year a user can select for their birthday capsule.
    val minimumSelectableYear: Int = remember(birthdayPassedThisYear) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (birthdayPassedThisYear) currentYear + 1 else currentYear
    }

    // If the currently selected unlock year is before the minimum
    // (e.g. user had 2025 selected but birthday already passed),
    // auto-advance it to the minimum year without user action.
    LaunchedEffect(minimumSelectableYear) {
        if (calendar.get(Calendar.YEAR) < minimumSelectableYear) {
            onYearSelected(minimumSelectableYear)
        }
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour   = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    // ── YearPickerDialog hoisted to top level of this composable ──────────────
    // Rendered OUTSIDE the scroll Column so it is never clipped by it
    if (showYearPicker) {
        YearPickerDialog(
            currentYear    = calendar.get(Calendar.YEAR),
            minimumYear    = minimumSelectableYear,
            onYearSelected = {
                onYearSelected(it)
                showYearPicker = false
            },
            onDismiss   = { showYearPicker = false },
            accentColor = accentColor
        )
    }

    if (showTimePicker) {
        EternoTimePickerDialog(
            state       = timePickerState,
            onConfirm   = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                showTimePicker = false
            },
            onDismiss   = { showTimePicker = false },
            accentColor = accentColor
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (capsuleType == CapsuleType.BIRTHDAY_SELF)
                    "Which birthday is this for?"
                else
                    "When should this gift unlock?",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (birthdayPassedThisYear)
                           "Your birthday has already passed this year — earliest unlock is $minimumSelectableYear."
                       else
                           "Select the year and exact time of unlock.",
                color    = if (birthdayPassedThisYear)
                               Color(0xFFFFAB40)   // warm amber — informational, not error
                           else
                               Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }

        // Year card
        val ageAtUnlock = viewModel.getAgeAtUnlock()
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape    = RoundedCornerShape(16.dp),
            border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier            = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Target Year",
                            color    = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                        Text(
                            "${calendar.get(Calendar.YEAR)}",
                            color      = Color.White,
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (capsuleType == CapsuleType.BIRTHDAY_SELF && ageAtUnlock != null) {
                        Surface(
                            color = accentColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Your ${ageAtUnlock}${getOrdinal(ageAtUnlock)} Birthday",
                                color      = accentColor,
                                modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // +N year quick buttons
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 5, 10).forEach { add ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable {
                                    HapticUtil.performVirtualKey(view)
                                    onYearSelected(minimumSelectableYear + (add - 1))
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val targetYear = minimumSelectableYear + (add - 1)
                            Text(
                                text = "$targetYear",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick  = { showYearPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Change Year", color = Color.White)
                }
            }
        }

        // Time card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true },
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape  = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier              = Modifier.padding(16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = accentColor)
                Column {
                    Text("Unlock Time", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text(
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(selectedDate)),
                        color      = Color.White,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint     = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        val fullDate = SimpleDateFormat(
            "MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()
        ).format(Date(selectedDate))
        Text(
            text      = "Capsule will reveal on: $fullDate",
            color     = accentColor.copy(alpha = 0.8f),
            fontSize  = 12.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

// ── Year picker dialog ────────────────────────────────────────────────────────
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
                                .background(
                                    if (year == currentYear)
                                        accentColor.copy(alpha = 0.2f)
                                    else
                                        Color.Transparent
                                )
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
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        },
        containerColor = Color(0xFF1A1A1A)
    )
}

// ── Drum Date Picker (Custom wheel picker matching requested style) ───────────
@Composable
fun DrumDatePickerDialog(
    initialDate: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
    accentColor: Color
) {
    val calendar = remember(initialDate) { Calendar.getInstance().apply { timeInMillis = initialDate } }
    
    var year by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var day by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val years = remember { (currentYear..currentYear + 50).map { it.toString() } }
    val months = remember { listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec") }
    
    val daysInMonth = remember(year, month) {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    
    LaunchedEffect(daysInMonth) {
        if (day > daysInMonth) day = daysInMonth
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1A1A1A),
            modifier = Modifier.width(320.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Set date",
                        color = Color(0xFF00B0FF),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF00B0FF)))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DrumPickerColumn(
                        items = (1..daysInMonth).map { it.toString().padStart(2, '0') },
                        selectedIndex = (day - 1).coerceIn(0, daysInMonth - 1),
                        onItemSelected = { day = it + 1 },
                        accentColor = Color(0xFF00B0FF)
                    )
                    DrumPickerColumn(
                        items = months,
                        selectedIndex = month,
                        onItemSelected = { month = it },
                        accentColor = Color(0xFF00B0FF)
                    )
                    DrumPickerColumn(
                        items = years,
                        selectedIndex = years.indexOf(year.toString()).coerceAtLeast(0),
                        onItemSelected = { year = years[it].toInt() },
                        accentColor = Color(0xFF00B0FF)
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                
                Row(
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    TextButton(
                        onClick = onDismiss, 
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 18.sp)
                    }
                    Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = 0.1f)))
                    TextButton(
                        onClick = {
                            val today = Calendar.getInstance()
                            year = today.get(Calendar.YEAR)
                            month = today.get(Calendar.MONTH)
                            day = today.get(Calendar.DAY_OF_MONTH)
                        }, 
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("Clear", color = Color.White, fontSize = 18.sp)
                    }
                    Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = 0.1f)))
                    TextButton(
                        onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = initialDate }
                            cal.set(Calendar.YEAR, year)
                            cal.set(Calendar.MONTH, month)
                            cal.set(Calendar.DAY_OF_MONTH, day)
                            onConfirm(cal.timeInMillis)
                        }, 
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("Set", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DrumPickerColumn(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    accentColor: Color
) {
    val pagerState = rememberPagerState(initialPage = selectedIndex, pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    // Sync pagerState changes to external state
    LaunchedEffect(pagerState.currentPage) {
        onItemSelected(pagerState.currentPage)
    }

    // Sync external selectedIndex changes to pagerState
    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex) {
            pagerState.scrollToPage(selectedIndex)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { 
                if (pagerState.currentPage > 0) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.ArrowDropUp, 
                contentDescription = null, 
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Box(
            modifier = Modifier.height(140.dp).width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Box(modifier = Modifier.width(65.dp).height(2.dp).background(accentColor))
                Spacer(modifier = Modifier.height(48.dp))
                Box(modifier = Modifier.width(65.dp).height(2.dp).background(accentColor))
            }
            
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 46.dp)
            ) { page ->
                val isSelected = pagerState.currentPage == page
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = items[page],
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                        fontSize = if (isSelected) 26.sp else 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        IconButton(
            onClick = { 
                if (pagerState.currentPage < items.size - 1) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.ArrowDropDown, 
                contentDescription = null, 
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ── Normal timing ─────────────────────────────────────────────────────────────
@Composable
fun InlineYearSelector(
    selectedYear: Int,
    minimumYear: Int,
    onYearSelected: (Int) -> Unit,
    accentColor: Color
) {
    val years = (minimumYear..minimumYear + 15).toList()
    val listState = rememberLazyListState()

    // Auto-scroll to selected year on first composition
    LaunchedEffect(selectedYear) {
        val index = years.indexOf(selectedYear).coerceAtLeast(0)
        listState.animateScrollToItem(index)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text      = "Select Year",
            color     = Color.White.copy(alpha = 0.5f),
            fontSize  = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        LazyRow(
            state                  = listState,
            horizontalArrangement  = Arrangement.spacedBy(8.dp),
            contentPadding         = PaddingValues(horizontal = 4.dp)
        ) {
            items(years) { year ->
                val isSelected = year == selectedYear
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) accentColor
                            else Color.White.copy(alpha = 0.08f)
                        )
                        .border(
                            width = if (isSelected) 0.dp else 1.dp,
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onYearSelected(year) }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text       = year.toString(),
                        color      = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                        fontSize   = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalTimingSection(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    accentColor: Color
) {
    val view = LocalView.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour   = Calendar.getInstance().apply { timeInMillis = selectedDate }
            .get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().apply { timeInMillis = selectedDate }
            .get(Calendar.MINUTE)
    )

    if (showDatePicker) {
        DrumDatePickerDialog(
            initialDate = selectedDate,
            onConfirm = { picked ->
                onDateSelected(picked)
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = { showDatePicker = false },
            accentColor = accentColor
        )
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
        val calendar = remember(selectedDate) {
            Calendar.getInstance().apply { timeInMillis = selectedDate }
        }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        Text(
            "When should this capsule reveal itself?",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Year selection card
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
                Column {
                    Text(
                        "Unlock Year",
                        color    = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text       = "${calendar.get(Calendar.YEAR)}",
                        color      = Color.White,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
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

        val presets = listOf(
            "1 Day"    to 1000L * 60 * 60 * 24,
            "1 Week"   to 1000L * 60 * 60 * 24 * 7,
            "1 Month"  to 1000L * 60 * 60 * 24 * 30,
            "6 Months" to 1000L * 60 * 60 * 24 * 180,
            "1 Year"   to 1000L * 60 * 60 * 24 * 365
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            presets.forEach { (label, offset) ->
                val timestamp  = System.currentTimeMillis() + offset
                val isSelected = Math.abs(selectedDate - timestamp) < 1000 * 60 * 60

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.3f)
                            else Color.White.copy(alpha = 0.05f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) accentColor else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
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

        val dateStr = SimpleDateFormat(
            "MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()
        ).format(Date(selectedDate))

        OutlinedButton(
            onClick  = {
                HapticUtil.performVirtualKey(view)
                showDatePicker = true
            },
            modifier = Modifier.fillMaxWidth(),
            border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text      = "Pick Date & Time",
                    color     = Color.White,
                    fontSize  = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text     = dateStr,
                    color    = accentColor,
                    fontSize = 12.sp
                )
            }
        }

        val fullDate = SimpleDateFormat(
            "MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()
        ).format(Date(selectedDate))
        Text(
            text      = "Capsule will reveal on: $fullDate",
            color     = accentColor.copy(alpha = 0.8f),
            fontSize  = 12.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherBirthdayTimingSection(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    accentColor: Color,
    isBirthdayMode: Boolean
) {
    val view = LocalView.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour   = Calendar.getInstance().apply { timeInMillis = selectedDate }
            .get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().apply { timeInMillis = selectedDate }
            .get(Calendar.MINUTE)
    )

    if (showDatePicker) {
        DrumDatePickerDialog(
            initialDate = selectedDate,
            onConfirm = { picked ->
                onDateSelected(picked)
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = { showDatePicker = false },
            accentColor = accentColor
        )
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
        val calendar = remember(selectedDate) {
            Calendar.getInstance().apply { timeInMillis = selectedDate }
        }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        Text(
            text = if (isBirthdayMode) "When is their birthday?" else "When should this capsule reveal itself?",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (isBirthdayMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Celebration,
                    contentDescription = null,
                    tint = accentColor
                )
                Text(
                    text = "This capsule will unlock on their birthday with a special celebration experience.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }

        // Year selection card
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
                Column {
                    Text(
                        "Unlock Year",
                        color    = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text       = "${calendar.get(Calendar.YEAR)}",
                        color      = Color.White,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
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

        val presets = listOf(
            "1 Day"    to 1000L * 60 * 60 * 24,
            "1 Week"   to 1000L * 60 * 60 * 24 * 7,
            "1 Month"  to 1000L * 60 * 60 * 24 * 30,
            "6 Months" to 1000L * 60 * 60 * 24 * 180,
            "1 Year"   to 1000L * 60 * 60 * 24 * 365
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            presets.forEach { (label, offset) ->
                val timestamp  = System.currentTimeMillis() + offset
                val isSelected = Math.abs(selectedDate - timestamp) < 1000 * 60 * 60

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.3f)
                            else Color.White.copy(alpha = 0.05f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) accentColor else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
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

        val dateStr = SimpleDateFormat(
            "MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()
        ).format(Date(selectedDate))

        OutlinedButton(
            onClick  = {
                HapticUtil.performVirtualKey(view)
                showDatePicker = true
            },
            modifier = Modifier.fillMaxWidth(),
            border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text      = "Pick Date & Time",
                    color     = Color.White,
                    fontSize  = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text     = dateStr,
                    color    = accentColor,
                    fontSize = 12.sp
                )
            }
        }

        val fullDate = SimpleDateFormat(
            "MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault()
        ).format(Date(selectedDate))
        Text(
            text      = "Capsule will reveal on: $fullDate",
            color     = accentColor.copy(alpha = 0.8f),
            fontSize  = 12.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

// ── Time picker dialog — renamed to avoid clash with M3's DatePickerDialog ────
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
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm", color = accentColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        },
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

// ── Helpers ───────────────────────────────────────────────────────────────────
fun getOrdinal(n: Int): String {
    if (n in 11..13) return "th"
    return when (n % 10) {
        1    -> "st"
        2    -> "nd"
        3    -> "rd"
        else -> "th"
    }
}

// ── Step 5: Review ────────────────────────────────────────────────────────────
@Composable
fun ReviewStep(
    uiState: CreateCapsuleUiState,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Review your Capsule",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )
        ReviewItem(label = "Title", value = uiState.title)
        ReviewItem(
            label = "Type",
            value = when (uiState.capsuleType) {
                CapsuleType.NORMAL         -> "Regular Memory"
                CapsuleType.BIRTHDAY_SELF  -> "My Birthday"
                CapsuleType.BIRTHDAY_OTHER -> "Someone's Birthday"
            },
            icon = when (uiState.capsuleType) {
                CapsuleType.NORMAL -> Icons.Default.Description
                else               -> Icons.Default.Cake
            }
        )
        ReviewItem(
            label = "Mood",
            value = uiState.mood?.let {
                it.name.lowercase().replaceFirstChar { c -> c.uppercase() }
            } ?: "Not selected",
            icon  = Icons.Default.Face
        )
        ReviewItem(
            label = "Message",
            value = if (uiState.message.isNotBlank()) "Written message included" else "No written message"
        )
        ReviewItem(
            label = "Voice Note",
            value = if (uiState.voiceFile != null) "Voice recording attached" else "No voice note"
        )
        ReviewItem(
            label = "Unlocks On",
            value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(Date(uiState.unlockAt)),
            icon  = Icons.Default.LockClock
        )
        if (uiState.isCoreMemory) {
            ReviewItem(label = "Type", value = "Core Memory", icon = Icons.Default.AutoAwesome)
        }
    }
}

// ── Capsule type selector ─────────────────────────────────────────────────────
@Composable
fun CapsuleTypeSelector(
    selectedType: CapsuleType,
    onTypeSelected: (CapsuleType) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Capsule Type", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CapsuleTypeOption(
                isSelected     = selectedType == CapsuleType.NORMAL,
                onClick        = { onTypeSelected(CapsuleType.NORMAL) },
                accentColor    = accentColor,
                icon           = Icons.Default.HistoryEdu,
                label          = "Normal"
            )
            CapsuleTypeOption(
                isSelected     = selectedType == CapsuleType.BIRTHDAY_SELF,
                onClick        = { onTypeSelected(CapsuleType.BIRTHDAY_SELF) },
                accentColor    = accentColor,
                icon           = Icons.Default.Cake,
                label          = "My Birthday"
            )
            CapsuleTypeOption(
                isSelected     = selectedType == CapsuleType.BIRTHDAY_OTHER,
                onClick        = { onTypeSelected(CapsuleType.BIRTHDAY_OTHER) },
                accentColor    = accentColor,
                icon           = Icons.Default.Celebration,
                label          = "Friend's B'day"
            )
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
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                if (isSelected) accentColor else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color      = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign  = TextAlign.Center
        )
    }
}

@Composable
fun ReviewItem(
    label: String,
    value: String,
    icon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint     = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column {
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Bottom nav bar ────────────────────────────────────────────────────────────
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
        color    = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
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
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(accentColor, accentColor.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            HapticUtil.performConfirm(view)
                            onSave()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Seal Memory", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick  = {
                        HapticUtil.performVirtualKey(view)
                        onNext()
                    },
                    enabled  = isNextEnabled,
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = accentColor,
                        disabledContainerColor = accentColor.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Continue",
                        color = if (isNextEnabled) Color.Black else Color.White.copy(alpha = 0.5f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier           = Modifier.padding(start = 8.dp).size(18.dp)
                    )
                }
            }
        }
    }
}

// ── Voice recording section ───────────────────────────────────────────────────
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
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onStartRecording()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.medium)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isRecording || hasRecording) {
            Box(
                modifier         = Modifier.fillMaxWidth().height(60.dp).padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                WaveformVisualizer(
                    waveform = waveform,
                    color    = if (isPaused) Color.Gray else accentColor
                )
            }

            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            Text(
                text  = "%d:%02d".format(minutes, seconds) + if (isPaused) " (Paused)" else "",
                color = if (isPaused) Color.Gray else Color.White,
                fontSize = 12.sp
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                if (isRecording) {
                    IconButton(onClick = { if (isPaused) onResumeRecording() else onPauseRecording() }) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint               = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onStopRecording) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.Red)
                    }
                } else {
                    IconButton(onClick = onDeleteRecording) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint               = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            Button(
                onClick         = { permissionsLauncher.launch(android.Manifest.permission.RECORD_AUDIO) },
                colors          = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                shape           = CircleShape,
                modifier        = Modifier.size(56.dp),
                contentPadding  = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Record", tint = accentColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add a voice note", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}

// ── Waveform canvas ───────────────────────────────────────────────────────────
@Composable
fun WaveformVisualizer(waveform: List<Float>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width    = size.width
        val height   = size.height
        val centerY  = height / 2
        val barWidth = 4.dp.toPx()
        val gap      = 2.dp.toPx()
        val maxBars  = (width / (barWidth + gap)).toInt()

        val displayWaveform = if (waveform.size > maxBars) waveform.takeLast(maxBars) else waveform

        displayWaveform.forEachIndexed { index, amplitude ->
            val x         = width - (displayWaveform.size - index) * (barWidth + gap)
            val barHeight = (amplitude * height).coerceAtLeast(4.dp.toPx())
            drawLine(
                color       = color,
                start       = Offset(x, centerY - barHeight / 2),
                end         = Offset(x, centerY + barHeight / 2),
                strokeWidth = barWidth,
                cap         = StrokeCap.Round
            )
        }
    }
}