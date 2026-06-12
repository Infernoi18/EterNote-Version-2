package com.example.eternotev2.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val email = sessionManager.getCurrentUserId()
        viewModelScope.launch {
            userRepository.getUserByEmailFlow(email).collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(
                        username = user.username,
                        email = user.email
                    ) }
                }
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            val email = _uiState.value.email
            val user = userRepository.getUserByEmail(email)
            if (user != null) {
                val updatedUser = user.copy(username = newUsername)
                userRepository.updateUser(updatedUser)
                
                // Update session to reflect change immediately in UI components using SharedPreferences
                sessionManager.setSession(
                    email = email,
                    username = newUsername,
                    isLoggedIn = true
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.update { it.copy(isLoggedOut = true) }
    }
}
