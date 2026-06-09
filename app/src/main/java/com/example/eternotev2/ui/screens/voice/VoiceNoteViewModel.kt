package com.example.eternotev2.ui.screens.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.VoiceNote
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class VoiceNoteUiState(
    val isRecording: Boolean = false,
    val durationMillis: Long = 0L,
    val amplitudes: List<Float> = emptyList(),
    val isSaved: Boolean = false
)

@HiltViewModel
class VoiceNoteViewModel @Inject constructor(
    private val repository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceNoteUiState())
    val uiState: StateFlow<VoiceNoteUiState> = _uiState.asStateFlow()
    
    private var recordingJob: Job? = null

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        _uiState.update { it.copy(isRecording = true, durationMillis = 0L, amplitudes = emptyList()) }
        
        recordingJob = viewModelScope.launch {
            while (true) {
                delay(100) // Update every 100ms
                _uiState.update { state ->
                    // Mocking audio amplitude between 10f and 100f
                    val newAmplitude = Random.nextFloat() * 90f + 10f
                    val updatedAmplitudes = (state.amplitudes + newAmplitude).takeLast(50)
                    state.copy(
                        durationMillis = state.durationMillis + 100L,
                        amplitudes = updatedAmplitudes
                    )
                }
            }
        }
    }

    private fun stopRecording() {
        recordingJob?.cancel()
        _uiState.update { it.copy(isRecording = false) }
    }

    fun saveVoiceNote(capsuleId: Long) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.durationMillis > 0) {
                val voiceNote = VoiceNote(
                    id = 0,
                    capsuleId = capsuleId,
                    filePath = "stub_path_${System.currentTimeMillis()}.m4a",
                    fileName = "Voice Note",
                    durationMillis = state.durationMillis,
                    waveformData = state.amplitudes,
                    createdAt = System.currentTimeMillis(),
                    transcript = null
                )
                repository.saveVoiceNote(voiceNote)
                repository.setHasVoiceNote(capsuleId, true)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }
}
