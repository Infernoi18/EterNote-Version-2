package com.example.eternotev2.data.model

import com.example.eternotev2.ui.theme.Mood

// ── Domain model used across UI, ViewModel, Repository ───────────────────────
data class Capsule(
    val id: Long,
    val userId: String,
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
        get() {
            val now = System.currentTimeMillis()
            val diff = unlockAt - now
            
            return when {
                isUnlocked -> "Unlocked"
                diff <= 0 -> "Ready to open"
                diff > 24 * 60 * 60 * 1000 -> {
                    val days = diff / (24 * 60 * 60 * 1000)
                    "${days}d left"
                }
                diff > 60 * 60 * 1000 -> {
                    val hours = diff / (60 * 60 * 1000)
                    val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
                    "${hours}h ${minutes}m left"
                }
                diff > 60 * 1000 -> {
                    val minutes = diff / (60 * 1000)
                    val seconds = (diff % (60 * 1000)) / 1000
                    "${minutes}m ${seconds}s left"
                }
                else -> {
                    val seconds = diff / 1000
                    "${seconds}s left"
                }
            }
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
