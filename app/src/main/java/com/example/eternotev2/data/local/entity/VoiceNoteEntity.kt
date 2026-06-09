package com.example.eternotev2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "voice_notes",
    foreignKeys = [
        ForeignKey(
            entity        = CapsuleEntity::class,
            parentColumns = ["id"],
            childColumns  = ["capsule_id"],
            onDelete      = ForeignKey.CASCADE   // delete voice note if capsule deleted
        )
    ],
    indices = [Index("capsule_id")]
)
data class VoiceNoteEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    // ── Relationship ──────────────────────────────────────────────────────────
    @ColumnInfo(name = "capsule_id")
    val capsuleId: Long,

    // ── File ──────────────────────────────────────────────────────────────────
    @ColumnInfo(name = "file_path")
    val filePath: String,                // absolute path to .m4a file

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name = "duration_millis")
    val durationMillis: Long = 0L,       // total recording duration

    // ── Waveform ──────────────────────────────────────────────────────────────
    @ColumnInfo(name = "waveform_data")
    val waveformData: String = "",       // comma-separated amplitude values e.g. "12,45,78,23"

    // ── Metadata ──────────────────────────────────────────────────────────────
    @ColumnInfo(name = "created_at")
    val createdAt: Long,                 // epoch millis

    @ColumnInfo(name = "transcript")
    val transcript: String? = null       // optional future speech-to-text
)

// ── Extension: Waveform amplitudes as Float list ──────────────────────────────
fun VoiceNoteEntity.parsedWaveform(): List<Float> =
    if (waveformData.isBlank()) emptyList()
    else waveformData.split(",")
        .mapNotNull { it.trim().toFloatOrNull() }
        .map { it.coerceIn(0f, 100f) }

// ── Extension: Duration as readable string ────────────────────────────────────
fun VoiceNoteEntity.formattedDuration(): String {
    val totalSeconds = durationMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
