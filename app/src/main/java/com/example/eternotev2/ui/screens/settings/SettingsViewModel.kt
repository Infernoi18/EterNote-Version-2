package com.example.eternotev2.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eternotev2.data.repository.ReminderFrequency
import com.example.eternotev2.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val reminderFrequency: ReminderFrequency = ReminderFrequency.WEEKLY
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        prefsRepository.areNotificationsEnabled,
        prefsRepository.reminderFrequency
    ) { notifications, frequencyStr ->
        val frequency = runCatching { 
            ReminderFrequency.valueOf(frequencyStr) 
        }.getOrDefault(ReminderFrequency.WEEKLY)
        
        SettingsUiState(
            notificationsEnabled = notifications,
            reminderFrequency = frequency
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setReminderFrequency(frequency: ReminderFrequency) {
        viewModelScope.launch {
            prefsRepository.setReminderFrequency(frequency)
        }
    }
}
