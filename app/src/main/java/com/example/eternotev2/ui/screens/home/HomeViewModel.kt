package com.example.eternotev2.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.ui.theme.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentMood: Mood = Mood.HOPEFUL,
    val capsules: List<Capsule> = emptyList(),
    val isLoading: Boolean = false,
    val userName: String = "Traveler"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val capsuleRepository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCapsules()
    }

    private fun loadCapsules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            capsuleRepository.getAllCapsules()
                .onEach { capsules ->
                    _uiState.update { it.copy(capsules = capsules, isLoading = false) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onMoodChanged(newMood: Mood) {
        _uiState.update { it.copy(currentMood = newMood) }
    }
}
