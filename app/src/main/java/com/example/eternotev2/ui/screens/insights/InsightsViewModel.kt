package com.example.eternotev2.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.ui.theme.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        viewModelScope.launch {
            repository.getTotalCount().collect { total ->
                _uiState.update { it.copy(totalCapsules = total) }
            }
        }
        viewModelScope.launch {
            repository.getUnlockedCount().collect { unlocked ->
                _uiState.update { it.copy(totalUnlocked = unlocked) }
            }
        }
        viewModelScope.launch {
            repository.getCoreMemoryCount().collect { core ->
                _uiState.update { it.copy(totalCoreMemories = core) }
            }
        }
        viewModelScope.launch {
            val distribution = repository.getMoodDistribution()
            _uiState.update { it.copy(moodDistribution = distribution, isLoading = false) }
        }
    }
}
