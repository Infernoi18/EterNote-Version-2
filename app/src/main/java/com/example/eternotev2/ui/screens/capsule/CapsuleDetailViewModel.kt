package com.example.eternotev2.ui.screens.capsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.model.VoiceNote
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CapsuleDetailUiState(
    val capsule: Capsule? = null,
    val voiceNotes: List<VoiceNote> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CapsuleDetailViewModel @Inject constructor(
    private val repository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CapsuleDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCapsule(id: Long) {
        viewModelScope.launch {
            combine(
                repository.getCapsuleById(id),
                repository.getVoiceNotesForCapsule(id)
            ) { capsule, voiceNotes ->
                if (capsule != null) {
                    _uiState.update { it.copy(
                        capsule = capsule,
                        voiceNotes = voiceNotes,
                        isLoading = false
                    ) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Capsule not found") }
                }
            }.collect()
        }
    }

    fun toggleFavorite() {
        val capsule = _uiState.value.capsule ?: return
        val newFavorite = !capsule.isFavorite
        viewModelScope.launch {
            repository.setFavorite(capsule.id, newFavorite)
            // If favorited, also mark as core memory as requested
            if (newFavorite) {
                repository.setCoreMemory(capsule.id, true)
            }
        }
    }

    fun toggleCoreMemory() {
        val capsule = _uiState.value.capsule ?: return
        viewModelScope.launch {
            repository.setCoreMemory(capsule.id, !capsule.isCoreMemory)
        }
    }

    fun deleteCapsule() {
        val capsule = _uiState.value.capsule ?: return
        viewModelScope.launch {
            repository.deleteCapsuleById(capsule.id)
            repository.deleteVoiceNotesForCapsule(capsule.id)
        }
    }
}
