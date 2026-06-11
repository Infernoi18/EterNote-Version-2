package com.example.eternotev2.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.ui.theme.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InsightsUiState(
    val isLoading: Boolean = true,
    val moodDistribution: Map<Mood, Int> = emptyMap(),
    val totalCapsules: Int = 0,
    val totalUnlocked: Int = 0,
    val totalCoreMemories: Int = 0
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        repository.getTotalCount()
            .onEach { total -> _uiState.update { it.copy(totalCapsules = total) } }
            .launchIn(viewModelScope)

        repository.getUnlockedCount()
            .onEach { unlocked -> _uiState.update { it.copy(totalUnlocked = unlocked) } }
            .launchIn(viewModelScope)

        repository.getCoreMemoryCount()
            .onEach { core -> _uiState.update { it.copy(totalCoreMemories = core) } }
            .launchIn(viewModelScope)

        repository.getMoodDistribution()
            .onEach { distribution ->
                _uiState.update { it.copy(moodDistribution = distribution, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }
}
