package com.example.eternotev2.data.model

import com.example.eternotev2.ui.theme.Mood

// ── Domain model used across UI, ViewModel, Repository ───────────────────────
data class Capsule(
    val id: Long,
    val title: String,
    val message: String,
    val mood: Mood,
    val createdAt: Long,
    val unlockAt: Long,
    val isUnlocked: Boolean,
    val isCoreMemory: Boolean,
    val isFavorite: Boolean,
    val hasVoiceNote: Boolean,
    val imageUri: String?,
    val unlockMessage: String?,
    val tags: List<String>,
    val workRequestId: String?
) {
    // ── Computed properties ───────────────────────────────────────────────────
    val isUnlockable: Boolean
        get() = !isUnlocked && System.currentTimeMillis() >= unlockAt

    val daysUntilUnlock: Long
        get() {
            val diff = unlockAt - System.currentTimeMillis()
            return if (diff <= 0) 0L else diff / (1000 * 60 * 60 * 24)
        }

    val hoursUntilUnlock: Long
        get() {
            val diff = unlockAt - System.currentTimeMillis()
            return if (diff <= 0) 0L else diff / (1000 * 60 * 60)
        }

    val countdownLabel: String
        get() = when {
            isUnlocked         -> "Opened"
            isUnlockable       -> "Ready to open"
            daysUntilUnlock > 365 -> "${daysUntilUnlock / 365}y ${(daysUntilUnlock % 365) / 30}mo"
            daysUntilUnlock > 30  -> "${daysUntilUnlock / 30}mo ${daysUntilUnlock % 30}d"
            daysUntilUnlock > 0   -> "${daysUntilUnlock}d ${hoursUntilUnlock % 24}h"
            else                  -> "${hoursUntilUnlock}h left"
        }
}

// ── Domain model for VoiceNote ────────────────────────────────────────────────
data class VoiceNote(
    val id: Long,
    val capsuleId: Long,
    val filePath: String,
    val fileName: String,
    val durationMillis: Long,
    val waveformData: List<Float>,
    val createdAt: Long,
    val transcript: String?
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
