package com.example.eternotev2.di

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

// Central registry of all DataStore keys — no magic strings anywhere else
object PreferencesKeys {
    val ONBOARDING_COMPLETE  = booleanPreferencesKey("onboarding_complete")
    val LAST_OPENED_AT       = longPreferencesKey("last_opened_at")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val SELECTED_THEME       = stringPreferencesKey("selected_theme")
    val REMINDER_FREQUENCY   = stringPreferencesKey("reminder_frequency")
    val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
}
