package com.example.eternotev2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eternotev2.ui.theme.Mood

@Entity(tableName = "capsules")
data class CapsuleEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String,                  // associated user email or "guest"
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "mood")
    val mood: String,                    // stored as Mood.name (e.g. "HAPPY")

    // ── Timing ────────────────────────────────────────────────────────────────
    @ColumnInfo(name = "created_at")
    val createdAt: Long,                 // epoch millis

    @ColumnInfo(name = "unlock_at")
    val unlockAt: Long,                  // epoch millis — when capsule unlocks

    // ── State ─────────────────────────────────────────────────────────────────
    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false,

    @ColumnInfo(name = "is_core_memory")
    val isCoreMemory: Boolean = false,   // pinned to Core Memory section

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    // ── Media ─────────────────────────────────────────────────────────────────
    @ColumnInfo(name = "has_voice_note")
    val hasVoiceNote: Boolean = false,

    @ColumnInfo(name = "image_uri")
    val imageUri: String? = null,        // optional attached image

    // ── Unlock experience ─────────────────────────────────────────────────────
    @ColumnInfo(name = "unlock_message")
    val unlockMessage: String? = null,   // optional message shown at unlock

    @ColumnInfo(name = "tags")
    val tags: String = "",               // comma-separated tag list e.g. "love,2024,trip"

    // ── WorkManager ───────────────────────────────────────────────────────────
    @ColumnInfo(name = "work_request_id")
    val workRequestId: String? = null    // UUID of scheduled WorkManager job
)

// ── Extension: Mood from stored string ────────────────────────────────────────
fun CapsuleEntity.parsedMood(): Mood =
    runCatching { Mood.valueOf(mood) }.getOrDefault(Mood.NOSTALGIC)

// ── Extension: Tags as List ───────────────────────────────────────────────────
fun CapsuleEntity.parsedTags(): List<String> =
    if (tags.isBlank()) emptyList()
    else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

// ── Extension: Is unlockable right now ────────────────────────────────────────
fun CapsuleEntity.isUnlockable(): Boolean =
    !isUnlocked && System.currentTimeMillis() >= unlockAt
