package com.example.eternotev2.ui.screens.voice

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class VoiceNoteUiState(
    val isRecording: Boolean = false,
    val durationMillis: Long = 0L,
    val amplitudes: List<Float> = emptyList(),
    val isSaved: Boolean = false,
    val isLocked: Boolean = false,
    val hasExistingVoiceNote: Boolean = false
)

@HiltViewModel
class VoiceNoteViewModel @Inject constructor(
    private val repository: CapsuleRepository,
    private val voiceRecorder: VoiceRecorder,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val capsuleId: Long = savedStateHandle.get<Long>("capsuleId") ?: -1L
    private val _uiState = MutableStateFlow(VoiceNoteUiState())
    val uiState: StateFlow<VoiceNoteUiState> = _uiState.asStateFlow()
    
    private var recordingJob: Job? = null
    private var currentFile: File? = null
    private var currentVoiceNote: VoiceNote? = null

    init {
        if (capsuleId != -1L) {
            viewModelScope.launch {
                combine(
                    repository.getCapsuleById(capsuleId),
                    repository.getVoiceNotesForCapsule(capsuleId)
                ) { capsule, voiceNotes ->
                    val existing = voiceNotes.firstOrNull()
                    currentVoiceNote = existing
                    _uiState.update { it.copy(
                        isLocked = capsule?.isUnlocked == false,
                        hasExistingVoiceNote = existing != null,
                        amplitudes = if (existing != null && !it.isRecording && it.amplitudes.isEmpty()) existing.waveformData else it.amplitudes,
                        durationMillis = if (existing != null && !it.isRecording && it.durationMillis == 0L) existing.durationMillis else it.durationMillis
                    ) }
                }.collect()
            }
        }
    }

    fun toggleRecording() {
        if (_uiState.value.isLocked || _uiState.value.hasExistingVoiceNote) return
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun deleteExistingVoiceNote() {
        if (_uiState.value.isLocked) return
        viewModelScope.launch {
            currentVoiceNote?.let { voiceNote ->
                try {
                    File(voiceNote.filePath).delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                repository.deleteVoiceNote(voiceNote.id)
            }
            repository.setHasVoiceNote(capsuleId, false)
            _uiState.update { it.copy(
                hasExistingVoiceNote = false,
                amplitudes = emptyList(),
                durationMillis = 0L
            ) }
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
        if (_uiState.value.isLocked || _uiState.value.hasExistingVoiceNote) return
        viewModelScope.launch {
            val state = _uiState.value
            val sourceFile = currentFile
            
            if (state.durationMillis > 0 && sourceFile != null && sourceFile.exists()) {
                // Move from cache to permanent internal storage
                val storageDir = File(context.filesDir, "voice_notes").apply { mkdirs() }
                val permanentFile = File(storageDir, sourceFile.name)
                
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
