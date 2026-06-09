package com.example.eternotev2.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.eternotev2.di.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // ── Onboarding ────────────────────────────────────────────────────────────
    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[PreferencesKeys.ONBOARDING_COMPLETE] ?: false }

    suspend fun setOnboardingComplete() {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.ONBOARDING_COMPLETE] = true
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    val areNotificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // ── Last Opened ───────────────────────────────────────────────────────────
    val lastOpenedAt: Flow<Long> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[PreferencesKeys.LAST_OPENED_AT] ?: 0L }

    suspend fun updateLastOpenedAt() {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.LAST_OPENED_AT] = System.currentTimeMillis()
        }
    }

    // ── Reminder Frequency ────────────────────────────────────────────────────
    val reminderFrequency: Flow<String> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[PreferencesKeys.REMINDER_FREQUENCY] ?: ReminderFrequency.WEEKLY.name }

    suspend fun setReminderFrequency(frequency: ReminderFrequency) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.REMINDER_FREQUENCY] = frequency.name
        }
    }
}

// ── Reminder Frequency Options ────────────────────────────────────────────────
enum class ReminderFrequency(val label: String, val intervalDays: Long) {
    DAILY("Every day", 1),
    WEEKLY("Every week", 7),
    BIWEEKLY("Every 2 weeks", 14),
    MONTHLY("Every month", 30),
    NEVER("Never", -1)
}
