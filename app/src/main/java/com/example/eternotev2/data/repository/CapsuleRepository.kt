package com.example.eternotev2.data.repository

import com.example.eternotev2.data.local.dao.CapsuleDao
import com.example.eternotev2.data.local.dao.VoiceNoteDao
import com.example.eternotev2.data.local.entity.CapsuleEntity
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.model.VoiceNote
import com.example.eternotev2.data.model.toDomain
import com.example.eternotev2.data.model.toEntity
import com.example.eternotev2.ui.theme.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CapsuleRepository @Inject constructor(
    private val capsuleDao: CapsuleDao,
    private val voiceNoteDao: VoiceNoteDao
) {

    // ── Capsule CRUD ──────────────────────────────────────────────────────────
    suspend fun createCapsule(capsule: Capsule): Long =
        capsuleDao.insertCapsule(capsule.toEntity())

    suspend fun updateCapsule(capsule: Capsule) =
        capsuleDao.updateCapsule(capsule.toEntity())

    suspend fun deleteCapsule(capsule: Capsule) =
        capsuleDao.deleteCapsule(capsule.toEntity())

    suspend fun deleteCapsuleById(id: Long) =
        capsuleDao.deleteCapsuleById(id)

    // ── Capsule Reads ─────────────────────────────────────────────────────────
    fun getCapsuleById(id: Long): Flow<Capsule?> =
        capsuleDao.getCapsuleById(id).map { it?.toDomain() }

    suspend fun getCapsuleByIdOnce(id: Long): Capsule? =
        capsuleDao.getCapsuleByIdOnce(id)?.toDomain()

    fun getAllCapsules(): Flow<List<Capsule>> =
        capsuleDao.getAllCapsules().map { list -> list.map { it.toDomain() } }

    suspend fun getAllCapsulesOnce(): List<Capsule> =
        capsuleDao.getAllCapsulesOnce().map { it.toDomain() }

    fun getLockedCapsules(): Flow<List<Capsule>> =
        capsuleDao.getLockedCapsules().map { list -> list.map { it.toDomain() } }

    fun getUnlockedCapsules(): Flow<List<Capsule>> =
        capsuleDao.getUnlockedCapsules().map { list -> list.map { it.toDomain() } }

    fun getUnlockableCapsules(): Flow<List<Capsule>> =
        capsuleDao.getUnlockableCapsules(System.currentTimeMillis())
            .map { list -> list.map { it.toDomain() } }

    fun getNextUpcomingCapsule(): Flow<Capsule?> =
        capsuleDao.getNextUpcomingCapsule(System.currentTimeMillis())
            .map { it?.toDomain() }

    fun getCoreMemoryCapsules(): Flow<List<Capsule>> =
        capsuleDao.getCoreMemoryCapsules().map { list -> list.map { it.toDomain() } }

    fun getFavoriteCapsules(): Flow<List<Capsule>> =
        capsuleDao.getFavoriteCapsules().map { list -> list.map { it.toDomain() } }

    fun getCapsulesByMood(mood: Mood): Flow<List<Capsule>> =
        capsuleDao.getCapsulesByMood(mood.name).map { list -> list.map { it.toDomain() } }

    fun getCapsulesChronological(): Flow<List<Capsule>> =
        capsuleDao.getCapsulesChronological().map { list -> list.map { it.toDomain() } }

    // ── Capsule State Changes ─────────────────────────────────────────────────
    suspend fun unlockCapsule(id: Long) =
        capsuleDao.markAsUnlocked(id)

    suspend fun setCoreMemory(id: Long, isCoreMemory: Boolean) =
        capsuleDao.setCoreMemory(id, isCoreMemory)

    suspend fun setFavorite(id: Long, isFavorite: Boolean) =
        capsuleDao.setFavorite(id, isFavorite)

    suspend fun setHasVoiceNote(id: Long, hasVoiceNote: Boolean) =
        capsuleDao.setHasVoiceNote(id, hasVoiceNote)

    suspend fun setWorkRequestId(id: Long, workId: String?) =
        capsuleDao.setWorkRequestId(id, workId)

    // ── Stats ─────────────────────────────────────────────────────────────────
    fun getTotalCount(): Flow<Int> = capsuleDao.getTotalCount()
    fun getUnlockedCount(): Flow<Int> = capsuleDao.getUnlockedCount()
    fun getCoreMemoryCount(): Flow<Int> = capsuleDao.getCoreMemoryCount()

    suspend fun getMoodDistribution(): Map<Mood, Int> {
        val usedMoods = capsuleDao.getUsedMoods()
        return usedMoods.associate { moodName ->
            val mood = runCatching { Mood.valueOf(moodName) }.getOrDefault(Mood.NOSTALGIC)
            mood to capsuleDao.getCountByMood(moodName)
        }
    }

    // ── Voice Notes ───────────────────────────────────────────────────────────
    fun getVoiceNotesForCapsule(capsuleId: Long): Flow<List<VoiceNote>> =
        voiceNoteDao.getVoiceNotesForCapsule(capsuleId)
            .map { list -> list.map { it.toDomain() } }

    suspend fun saveVoiceNote(voiceNote: VoiceNote): Long =
        voiceNoteDao.insertVoiceNote(voiceNote.toEntity())

    suspend fun deleteVoiceNote(id: Long) =
        voiceNoteDao.deleteById(id)

    suspend fun deleteVoiceNotesForCapsule(capsuleId: Long) =
        voiceNoteDao.deleteVoiceNotesForCapsule(capsuleId)

    suspend fun updateWaveformAndDuration(id: Long, waveform: List<Float>, duration: Long) =
        voiceNoteDao.updateWaveformAndDuration(
            id       = id,
            waveform = waveform.joinToString(","),
            duration = duration
        )
}
