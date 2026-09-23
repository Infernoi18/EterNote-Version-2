package com.example.eternotev2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EternoteApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var userPreferencesRepository:
        com.example.eternotev2.data.repository.UserPreferencesRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // ── Capsule Unlock Channel ─────────────────────────────────────
            val unlockChannel = NotificationChannel(
                CHANNEL_UNLOCK,
                "Capsule Unlocks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies you when a time capsule is ready to be opened"
                enableVibration(true)
                enableLights(true)
            }

            // ── Reminder Channel ───────────────────────────────────────────
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER,
                "Emotional Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle reminders to create new capsules"
            }

            manager.createNotificationChannels(listOf(unlockChannel, reminderChannel))
        }
    }

    companion object {
        const val CHANNEL_UNLOCK   = "channel_capsule_unlock"
        const val CHANNEL_REMINDER = "channel_reminder"
    }
}
