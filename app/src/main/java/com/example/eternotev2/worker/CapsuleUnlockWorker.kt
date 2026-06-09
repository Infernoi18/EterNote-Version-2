package com.example.eternotev2.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eternotev2.MainActivity
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CapsuleUnlockWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CapsuleRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val capsuleId = inputData.getLong(KEY_CAPSULE_ID, -1L)
        if (capsuleId == -1L) return Result.failure()

        val capsule = repository.getCapsuleByIdOnce(capsuleId) ?: return Result.failure()

        // Only notify if it hasn't been unlocked yet
        if (!capsule.isUnlocked) {
            showNotification(capsule.id, capsule.title)
        }

        return Result.success()
    }

    private fun showNotification(capsuleId: Long, capsuleTitle: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("unlock_capsule_id", capsuleId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            capsuleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "capsule_unlocks")
            .setSmallIcon(com.example.eternotev2.R.drawable.ic_notification)
            .setContentTitle("A Memory is Ready ✨")
            .setContentText("Your capsule '$capsuleTitle' is ready to be opened.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(capsuleId.toInt(), notification)
    }

    companion object {
        const val KEY_CAPSULE_ID = "capsule_id"
    }
}
