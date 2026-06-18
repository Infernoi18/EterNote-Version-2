package com.example.eternotev2.ui.screens.capsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.model.VoiceNote
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.util.VoiceRecorder
import com.example.eternotev2.ui.theme.Mood
import com.example.eternotev2.worker.CapsuleWorkerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

enum class CreateStep {
    IDENTITY, MOOD, MESSAGE, TIMING, REVIEW
}

data class CreateCapsuleUiState(
    val currentStep: CreateStep = CreateStep.IDENTITY,
    val title: String = "",
    val tags: List<String> = emptyList(),
    val isCoreMemory: Boolean = false,
    val message: String = "",
    val unlockMessage: String = "",
    val mood: Mood? = null,
    val showMoodError: Boolean = false,
    val unlockAt: Long = System.currentTimeMillis() + (1000 * 60 * 60 * 24), // 1 day later
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val recordingDuration: Long = 0L,
    val waveform: List<Float> = emptyList(),
    val voiceFile: File? = null
)

@HiltViewModel
class CreateCapsuleViewModel @Inject constructor(
    private val capsuleRepository: CapsuleRepository,
    private val voiceRecorder: VoiceRecorder,
    private val workerScheduler: CapsuleWorkerScheduler,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private var recordingJob: Job? = null

    fun nextStep() {
        if (_uiState.value.isRecording) {
            stopRecording()
        }

        val current = _uiState.value.currentStep
        
        if (current == CreateStep.MOOD && _uiState.value.mood == null) {
            _uiState.update { it.copy(showMoodError = true) }
            return
        }

        val next = when (current) {
            CreateStep.IDENTITY -> CreateStep.MOOD
            CreateStep.MOOD -> CreateStep.MESSAGE
            CreateStep.MESSAGE -> CreateStep.TIMING
            CreateStep.TIMING -> CreateStep.REVIEW
            CreateStep.REVIEW -> CreateStep.REVIEW
        }
        _uiState.update { it.copy(currentStep = next, showMoodError = false) }
    }

    fun previousStep() {
        val current = _uiState.value.currentStep
        val prev = when (current) {
            CreateStep.IDENTITY -> CreateStep.IDENTITY
            CreateStep.MOOD -> CreateStep.IDENTITY
            CreateStep.MESSAGE -> CreateStep.MOOD
            CreateStep.TIMING -> CreateStep.MESSAGE
            CreateStep.REVIEW -> CreateStep.TIMING
        }
        _uiState.update { it.copy(currentStep = prev) }
    }

    fun onTitleChanged(title: String) = _uiState.update { it.copy(title = title) }
    fun onTagsChanged(tags: List<String>) = _uiState.update { it.copy(tags = tags) }
    fun onCoreMemoryChanged(isCore: Boolean) = _uiState.update { it.copy(isCoreMemory = isCore) }
    fun onMessageChanged(message: String) = _uiState.update { it.copy(message = message) }
    fun onUnlockMessageChanged(msg: String) = _uiState.update { it.copy(unlockMessage = msg) }
    fun onMoodChanged(mood: Mood) = _uiState.update { it.copy(mood = mood, showMoodError = false) }
    fun onUnlockDateChanged(timestamp: Long) = _uiState.update { it.copy(unlockAt = timestamp) }

    fun startRecording() {
        val fileName = "voice_${System.currentTimeMillis()}"
        val file = voiceRecorder.startRecording(fileName)
        if (file != null) {
            _uiState.update { it.copy(isRecording = true, isPaused = false, voiceFile = file, waveform = emptyList(), recordingDuration = 0) }
            startWaveformCollection()
        }
    }

    fun pauseRecording() {
        voiceRecorder.pauseRecording()
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeRecording() {
        voiceRecorder.resumeRecording()
        _uiState.update { it.copy(isPaused = false) }
    }

    fun stopRecording() {
        voiceRecorder.stopRecording()
        recordingJob?.cancel()
        _uiState.update { it.copy(isRecording = false, isPaused = false) }
    }

    private fun startWaveformCollection() {
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (true) {
                delay(100)
                if (!_uiState.value.isPaused) {
                    val amplitude = voiceRecorder.getAmplitude()
                    // Normalize amplitude for visualization (0..1)
                    val normalized = (amplitude / 32767f).coerceIn(0f, 1f)
                    _uiState.update { 
                        it.copy(
                            waveform = it.waveform + normalized,
                            recordingDuration = it.recordingDuration + 100
                        )
                    }
                }
            }
        }
    }

    fun deleteRecording() {
        _uiState.value.voiceFile?.delete()
        _uiState.update { it.copy(voiceFile = null, waveform = emptyList(), recordingDuration = 0) }
    }

    fun saveCapsule() {
        if (_uiState.value.isRecording) {
            stopRecording()
        }
        val currentState = _uiState.value
        val mood = currentState.mood ?: return
        if (currentState.title.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val newCapsule = Capsule(
                id = 0,
                userId = sessionManager.getCurrentUserId(),
                title = currentState.title,
                message = currentState.message,
                mood = mood,
                createdAt = System.currentTimeMillis(),
                unlockAt = currentState.unlockAt,
                isUnlocked = false,
                isCoreMemory = currentState.isCoreMemory,
                isFavorite = currentState.isCoreMemory,
                hasVoiceNote = currentState.voiceFile != null,
                imageUri = null,
                unlockMessage = currentState.unlockMessage.takeIf { it.isNotBlank() },
                tags = currentState.tags,
                workRequestId = null
            )
            
            val capsuleId = capsuleRepository.createCapsule(newCapsule)

            // Schedule notification
            val workRequestId = workerScheduler.scheduleUnlockNotification(
                capsuleId = capsuleId,
                unlockAtMillis = currentState.unlockAt
            )
            
            // Update capsule with work request ID
            if (workRequestId.isNotEmpty()) {
                capsuleRepository.setWorkRequestId(capsuleId, workRequestId)
            }

            // Save voice note if exists
            currentState.voiceFile?.let { file ->
                val permanentFile = File(context.filesDir, "voice_notes/${file.name}")
                permanentFile.parentFile?.mkdirs()
                
                try {
                    file.inputStream().use { input ->
                        permanentFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    file.delete()

                    val voiceNote = VoiceNote(
                        id = 0,
                        capsuleId = capsuleId,
                        filePath = permanentFile.absolutePath,
                        fileName = permanentFile.name,
                        durationMillis = currentState.recordingDuration,
                        waveformData = currentState.waveform.map { it * 100f },
                        createdAt = System.currentTimeMillis(),
                        transcript = null
                    )
                    capsuleRepository.saveVoiceNote(voiceNote)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}
