package com.example.eternotev2.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data class Ready(val isFirstLaunch: Boolean) : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Minimum splash display time for cinematic effect
            val splashJob = launch { delay(SPLASH_DURATION_MS) }

            // Read onboarding state in parallel
            val isOnboardingComplete = prefsRepository.isOnboardingComplete.first()

            // Wait for both splash duration AND data load
            splashJob.join()

            prefsRepository.updateLastOpenedAt()

            _uiState.value = SplashUiState.Ready(
                isFirstLaunch = !isOnboardingComplete
            )
        }
    }

    companion object {
        private const val SPLASH_DURATION_MS = 3000L
    }
}
