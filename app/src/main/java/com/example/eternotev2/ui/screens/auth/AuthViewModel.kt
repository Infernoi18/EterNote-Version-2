package com.example.eternotev2.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLogin: Boolean = true,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val capsuleRepository: CapsuleRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (sessionManager.isLoggedIn()) {
            _uiState.update { it.copy(isAuthenticated = true) }
        }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isLogin = !it.isLogin, error = null) }
    }

    fun onUsernameChange(value: String) = _uiState.update { it.copy(username = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun performAuth() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Simulate network delay
            kotlinx.coroutines.delay(1000)

            if (state.isLogin) {
                // In a real app, we'd verify with a backend. 
                // For now, let's just log them in if they provide any email.
                if (state.email.contains("@")) {
                    sessionManager.setSession(state.email, true)
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Invalid email") }
                }
            } else {
                // Register
                if (state.email.isNotBlank() && state.password.length >= 6) {
                    capsuleRepository.migrateGuestData(state.email)
                    sessionManager.setSession(state.email, true)
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Invalid input or password too short") }
                }
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.update { it.copy(isAuthenticated = false) }
    }
}
