package com.example.eternotev2.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int   = 0,
    val totalPages: Int    = 3,
    val isCompleting: Boolean = false
) {
    val isLastPage: Boolean get() = currentPage == totalPages - 1
    val progress: Float get() = (currentPage + 1f) / totalPages
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextPage() {
        _uiState.update { state ->
            if (state.currentPage < state.totalPages - 1)
                state.copy(currentPage = state.currentPage + 1)
            else state
        }
    }

    fun previousPage() {
        _uiState.update { state ->
            if (state.currentPage > 0)
                state.copy(currentPage = state.currentPage - 1)
            else state
        }
    }

    fun goToPage(page: Int) {
        _uiState.update { it.copy(currentPage = page.coerceIn(0, it.totalPages - 1)) }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCompleting = true) }
            prefsRepository.setOnboardingComplete()
            onComplete()
        }
    }
}
