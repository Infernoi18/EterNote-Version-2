package com.example.eternotev2.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.worker.CapsuleWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: CapsuleRepository

    @Inject
    lateinit var scheduler: CapsuleWorkerScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val capsules = repository.getAllCapsulesOnce()
                capsules.forEach { capsule ->
                    if (!capsule.isUnlocked && capsule.unlockAt > System.currentTimeMillis()) {
                        scheduler.scheduleUnlockNotification(capsule.id, capsule.unlockAt)
                    }
                }
            }
        }
    }
}
