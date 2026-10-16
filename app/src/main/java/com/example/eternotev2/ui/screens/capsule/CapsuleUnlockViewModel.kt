package com.example.eternotev2.ui.screens.capsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UnlockPhase {
    INTRO, UNSEALING, REVEALING, READING
}

data class CapsuleUnlockUiState(
    val capsule: Capsule? = null,
    val currentPhase: UnlockPhase = UnlockPhase.INTRO,
    val isLoading: Boolean = true
)

@HiltViewModel
class CapsuleUnlockViewModel @Inject constructor(
    private val repository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CapsuleUnlockUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCapsule(id: Long) {
        viewModelScope.launch {
            val capsule = repository.getCapsuleByIdOnce(id)
            _uiState.update { it.copy(capsule = capsule, isLoading = false) }
            
            if (capsule != null) {
                startUnlockSequence()
            }
        }
    }

    private fun startUnlockSequence() {
        viewModelScope.launch {
            // INTRO Phase
            delay(1500)
            _uiState.update { it.copy(currentPhase = UnlockPhase.UNSEALING) }
            
            // Actually mark as unlocked in DB when we start unsealing
            _uiState.value.capsule?.let {
                repository.unlockCapsule(it.id)
            }
        }
    }

    fun onAnimationFinished() {
        _uiState.update { it.copy(currentPhase = UnlockPhase.READING) }
    }
}
