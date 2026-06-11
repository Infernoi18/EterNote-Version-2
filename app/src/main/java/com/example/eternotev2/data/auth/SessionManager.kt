package com.example.eternotev2.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("eternote_auth", Context.MODE_PRIVATE)
    
    private val _userIdFlow = MutableStateFlow(getCurrentUserId())
    val userIdFlow: StateFlow<String> = _userIdFlow.asStateFlow()

    fun getCurrentUserId(): String {
        return prefs.getString("user_email", "guest") ?: "guest"
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun isGuest(): Boolean {
        return !isLoggedIn() || getCurrentUserId() == "guest"
    }

    fun setSession(email: String?, isLoggedIn: Boolean) {
        prefs.edit().apply {
            putString("user_email", email ?: "guest")
            putBoolean("is_logged_in", isLoggedIn)
            apply()
        }
        _userIdFlow.value = email ?: "guest"
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _userIdFlow.value = "guest"
    }
}
