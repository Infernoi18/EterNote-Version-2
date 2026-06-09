package com.example.eternotev2.ui.screens.capsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.model.VoiceNote
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.util.VoiceRecorder
import com.example.eternotev2.ui.theme.Mood
import com.example.eternotev2.worker.CapsuleWorkerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val mood: Mood = Mood.HOPEFUL,
    val unlockAt: Long = System.currentTimeMillis() + (1000 * 60 * 60 * 24), // 1 day later
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0L,
    val waveform: List<Float> = emptyList(),
    val voiceFile: File? = null
)

@HiltViewModel
class CreateCapsuleViewModel @Inject constructor(
    private val capsuleRepository: CapsuleRepository,
    private val voiceRecorder: VoiceRecorder,
    private val workerScheduler: CapsuleWorkerScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private var recordingJob: Job? = null

    fun nextStep() {
        val current = _uiState.value.currentStep
        val next = when (current) {
            CreateStep.IDENTITY -> CreateStep.MOOD
            CreateStep.MOOD -> CreateStep.MESSAGE
            CreateStep.MESSAGE -> CreateStep.TIMING
            CreateStep.TIMING -> CreateStep.REVIEW
            CreateStep.REVIEW -> CreateStep.REVIEW
        }
        _uiState.update { it.copy(currentStep = next) }
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
    fun onMoodChanged(mood: Mood) = _uiState.update { it.copy(mood = mood) }
    fun onUnlockDateChanged(timestamp: Long) = _uiState.update { it.copy(unlockAt = timestamp) }

    fun startRecording() {
        val fileName = "voice_${System.currentTimeMillis()}"
        val file = voiceRecorder.startRecording(fileName)
        if (file != null) {
            _uiState.update { it.copy(isRecording = true, voiceFile = file, waveform = emptyList(), recordingDuration = 0) }
            startWaveformCollection()
        }
    }

    fun stopRecording() {
        voiceRecorder.stopRecording()
        recordingJob?.cancel()
        _uiState.update { it.copy(isRecording = false) }
    }

    private fun startWaveformCollection() {
        recordingJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (true) {
                delay(100)
                val amplitude = voiceRecorder.getAmplitude()
                // Normalize amplitude for visualization (0..1)
                val normalized = (amplitude / 32767f).coerceIn(0f, 1f)
                _uiState.update { 
                    it.copy(
                        waveform = it.waveform + normalized,
                        recordingDuration = System.currentTimeMillis() - startTime
                    )
                }
            }
        }
    }

    fun deleteRecording() {
        _uiState.value.voiceFile?.delete()
        _uiState.update { it.copy(voiceFile = null, waveform = emptyList(), recordingDuration = 0) }
    }

    fun saveCapsule() {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val newCapsule = Capsule(
                id = 0,
                title = currentState.title,
                message = currentState.message,
                mood = currentState.mood,
                createdAt = System.currentTimeMillis(),
                unlockAt = currentState.unlockAt,
                isUnlocked = false,
                isCoreMemory = currentState.isCoreMemory,
                isFavorite = false,
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
                val voiceNote = VoiceNote(
                    id = 0,
                    capsuleId = capsuleId,
                    filePath = file.absolutePath,
                    fileName = file.name,
                    durationMillis = currentState.recordingDuration,
                    waveformData = currentState.waveform,
                    createdAt = System.currentTimeMillis(),
                    transcript = null
                )
                capsuleRepository.saveVoiceNote(voiceNote)
            }

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}
