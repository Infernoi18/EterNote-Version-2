package com.example.eternotev2.ui.screens.corememory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoreMemoryUiState(
    val isLoading: Boolean = true,
    val coreMemories: List<Capsule> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class CoreMemoryViewModel @Inject constructor(
    private val repository: CapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoreMemoryUiState())
    val uiState: StateFlow<CoreMemoryUiState> = _uiState.asStateFlow()

    init {
        loadCoreMemories()
    }

    private fun loadCoreMemories() {
        viewModelScope.launch {
            repository.getCoreMemoryCapsules()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { capsules ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            coreMemories = capsules
                        )
                    }
                }
        }
    }
}
