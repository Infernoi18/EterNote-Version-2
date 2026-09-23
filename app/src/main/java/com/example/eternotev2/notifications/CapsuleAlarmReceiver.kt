package com.example.eternotev2.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.eternotev2.EternoteApplication
import com.example.eternotev2.MainActivity
import com.example.eternotev2.R
import com.example.eternotev2.data.repository.CapsuleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CapsuleAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: CapsuleRepository

    override fun onReceive(context: Context, intent: Intent) {
        val capsuleId = intent.getLongExtra("capsule_id", -1L)
        if (capsuleId == -1L) return

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val capsule = repository.getCapsuleByIdOnce(capsuleId)
            if (capsule != null && !capsule.isUnlocked) {
                showNotification(context, capsule.id, capsule.title)
            }
        }
    }

    private fun showNotification(context: Context, capsuleId: Long, capsuleTitle: String) {
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

        val notification = NotificationCompat.Builder(context, EternoteApplication.CHANNEL_UNLOCK)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🔓 Capsule Unlocked!")
            .setContentText("Your capsule '$capsuleTitle' is ready to be opened.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setColor(0xFF7B5CF0.toInt())
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 600, 200, 400, 150, 300, 150, 250))
            .setLights(0xFF33D6FF.toInt(), 500, 2000)
            .build()

        notificationManager.notify(capsuleId.toInt(), notification)
    }
}
