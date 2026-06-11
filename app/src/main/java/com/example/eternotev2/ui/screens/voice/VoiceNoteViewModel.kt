package com.example.eternotev2.ui.screens.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.eternotev2.data.model.VoiceNote
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.util.VoiceRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class VoiceNoteUiState(
    val isRecording: Boolean = false,
    val durationMillis: Long = 0L,
    val amplitudes: List<Float> = emptyList(),
    val isSaved: Boolean = false
)

@HiltViewModel
class VoiceNoteViewModel @Inject constructor(
    private val repository: CapsuleRepository,
    private val voiceRecorder: VoiceRecorder,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceNoteUiState())
    val uiState: StateFlow<VoiceNoteUiState> = _uiState.asStateFlow()
    
    private var recordingJob: Job? = null
    private var currentFile: File? = null

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        val fileName = "voice_note_${System.currentTimeMillis()}"
        currentFile = voiceRecorder.startRecording(fileName)
        
        if (currentFile != null) {
            _uiState.update { it.copy(isRecording = true, durationMillis = 0L, amplitudes = emptyList()) }
            
            recordingJob = viewModelScope.launch {
                while (true) {
                    delay(100)
                    val amp = voiceRecorder.getAmplitude()
                    // Normalize amplitude for visualization (0-100 range)
                    val normalizedAmp = (amp / 32767f) * 100f
                    
                    _uiState.update { state ->
                        val updatedAmplitudes = (state.amplitudes + normalizedAmp).takeLast(50)
                        state.copy(
                            durationMillis = state.durationMillis + 100L,
                            amplitudes = updatedAmplitudes
                        )
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        voiceRecorder.stopRecording()
        recordingJob?.cancel()
        _uiState.update { it.copy(isRecording = false) }
    }

    fun saveVoiceNote(capsuleId: Long) {
        viewModelScope.launch {
            val state = _uiState.value
            val sourceFile = currentFile
            
            if (state.durationMillis > 0 && sourceFile != null && sourceFile.exists()) {
                // Move from cache to permanent internal storage
                val permanentFile = File(context.filesDir, "voice_notes/${sourceFile.name}")
                permanentFile.parentFile?.mkdirs()
                
                try {
                    sourceFile.inputStream().use { input ->
                        permanentFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    // Delete temp file
                    sourceFile.delete()

                    val voiceNote = VoiceNote(
                        id = 0,
                        capsuleId = capsuleId,
                        filePath = permanentFile.absolutePath,
                        fileName = permanentFile.name,
                        durationMillis = state.durationMillis,
                        waveformData = state.amplitudes,
                        createdAt = System.currentTimeMillis(),
                        transcript = null
                    )
                    repository.saveVoiceNote(voiceNote)
                    repository.setHasVoiceNote(capsuleId, true)
                    _uiState.update { it.copy(isSaved = true) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
