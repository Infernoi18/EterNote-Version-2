package com.example.eternotev2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eternotev2.data.local.entity.VoiceNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceNoteDao {

    // ── Insert / Update / Delete ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceNote(voiceNote: VoiceNoteEntity): Long

    @Update
    suspend fun updateVoiceNote(voiceNote: VoiceNoteEntity)

    @Delete
    suspend fun deleteVoiceNote(voiceNote: VoiceNoteEntity)

    @Query("DELETE FROM voice_notes WHERE capsule_id = :capsuleId")
    suspend fun deleteVoiceNotesForCapsule(capsuleId: Long)

    @Query("DELETE FROM voice_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    // ── Queries ───────────────────────────────────────────────────────────────
    @Query("SELECT * FROM voice_notes WHERE capsule_id = :capsuleId")
    fun getVoiceNotesForCapsule(capsuleId: Long): Flow<List<VoiceNoteEntity>>

    @Query("SELECT * FROM voice_notes WHERE capsule_id = :capsuleId")
    suspend fun getVoiceNotesForCapsuleOnce(capsuleId: Long): List<VoiceNoteEntity>

    @Query("SELECT * FROM voice_notes WHERE id = :id")
    suspend fun getVoiceNoteById(id: Long): VoiceNoteEntity?

    @Query("SELECT COUNT(*) FROM voice_notes WHERE capsule_id = :capsuleId")
    suspend fun getVoiceNoteCount(capsuleId: Long): Int

    // ── Update waveform after recording ──────────────────────────────────────
    @Query("UPDATE voice_notes SET waveform_data = :waveform, duration_millis = :duration WHERE id = :id")
    suspend fun updateWaveformAndDuration(id: Long, waveform: String, duration: Long)
}
