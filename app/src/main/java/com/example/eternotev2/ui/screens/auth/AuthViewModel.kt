package com.example.eternotev2.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.local.entity.UserEntity
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.data.repository.UserRepository
import com.example.eternotev2.util.NetworkMonitor
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
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val isOnline: Boolean = true
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val capsuleRepository: CapsuleRepository,
    private val sessionManager: SessionManager,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (sessionManager.isLoggedIn()) {
            _uiState.update { it.copy(isAuthenticated = true) }
        }

        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isLogin = !it.isLogin, error = null) }
    }

    fun onUsernameChange(value: String) = _uiState.update { it.copy(username = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun performAuth() {
        val state = _uiState.value
        
        if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }

        if (!state.isLogin && state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }

        if (!state.isOnline) {
            _uiState.update { it.copy(error = "No internet connection. Please check your network.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Simulate network delay for cinematic feel
            kotlinx.coroutines.delay(800)

            if (state.isLogin) {
                val user = userRepository.getUserByEmail(state.email)
                if (user != null && user.password == state.password) {
                    sessionManager.setSession(user.email, user.username, true)
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = if (user == null) "Identity not found. Please register." else "Incorrect password."
                        ) 
                    }
                }
            } else {
                // Register
                val existingUser = userRepository.getUserByEmail(state.email)
                if (existingUser != null) {
                    _uiState.update { it.copy(isLoading = false, error = "This email is already registered.") }
                } else {
                    val newUser = UserEntity(
                        email = state.email,
                        username = state.username,
                        password = state.password
                    )
                    userRepository.registerUser(newUser)
                    
                    // Migrate any guest data to this new account
                    capsuleRepository.migrateGuestData(state.email)
                    
                    sessionManager.setSession(state.email, state.username, true)
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.update { it.copy(isAuthenticated = false) }
    }
}
