package com.example.eternotev2.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.eternotev2.notifications.CapsuleAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CapsuleWorkerScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleUnlockNotification(capsuleId: Long, unlockAtMillis: Long): String {
        // Use AlarmManager for high precision "burst" notification
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CapsuleAlarmReceiver::class.java).apply {
            putExtra("capsule_id", capsuleId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            capsuleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            unlockAtMillis,
            pendingIntent
        )

        // We primarily use AlarmManager for the "burst" notification.
        return "alarm_$capsuleId"
    }

    fun cancelUnlockNotification(capsuleId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CapsuleAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            capsuleId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
        
        // Also cancel any WorkManager job with the tag
        WorkManager.getInstance(context).cancelAllWorkByTag("capsule_$capsuleId")
    }
}

