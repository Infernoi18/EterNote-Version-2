package com.example.eternotev2.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CapsuleWorkerScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleUnlockNotification(capsuleId: Long, unlockAtMillis: Long): String {
        val delayMillis = unlockAtMillis - System.currentTimeMillis()
        if (delayMillis <= 0) return ""

        val inputData = Data.Builder()
            .putLong(CapsuleUnlockWorker.KEY_CAPSULE_ID, capsuleId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<CapsuleUnlockWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("capsule_$capsuleId")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        return workRequest.id.toString()
    }

    fun cancelUnlockNotification(workRequestId: String) {
        if (workRequestId.isNotEmpty()) {
            val uuid = runCatching { java.util.UUID.fromString(workRequestId) }.getOrNull()
            if (uuid != null) {
                WorkManager.getInstance(context).cancelWorkById(uuid)
            }
        }
    }
}
