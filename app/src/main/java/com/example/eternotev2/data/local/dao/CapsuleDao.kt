package com.example.eternotev2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eternotev2.data.local.entity.CapsuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CapsuleDao {

    // ── Insert / Update / Delete ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: CapsuleEntity): Long

    @Update
    suspend fun updateCapsule(capsule: CapsuleEntity)

    @Delete
    suspend fun deleteCapsule(capsule: CapsuleEntity)

    @Query("DELETE FROM capsules WHERE id = :id")
    suspend fun deleteCapsuleById(id: Long)

    // ── Single Capsule ────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE id = :id")
    fun getCapsuleById(id: Long): Flow<CapsuleEntity?>

    @Query("SELECT * FROM capsules WHERE id = :id")
    suspend fun getCapsuleByIdOnce(id: Long): CapsuleEntity?

    // ── All Capsules ──────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules ORDER BY created_at DESC")
    fun getAllCapsules(): Flow<List<CapsuleEntity>>

    @Query("SELECT * FROM capsules ORDER BY created_at DESC")
    suspend fun getAllCapsulesOnce(): List<CapsuleEntity>

    @Query("SELECT * FROM capsules ORDER BY unlock_at ASC")
    fun getAllCapsulesSortedByUnlock(): Flow<List<CapsuleEntity>>

    // ── Locked / Unlocked ─────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE is_unlocked = 0 ORDER BY unlock_at ASC")
    fun getLockedCapsules(): Flow<List<CapsuleEntity>>

    @Query("SELECT * FROM capsules WHERE is_unlocked = 1 ORDER BY unlock_at DESC")
    fun getUnlockedCapsules(): Flow<List<CapsuleEntity>>

    // ── Ready to unlock ───────────────────────────────────────────────────────
    @Query("""
        SELECT * FROM capsules 
        WHERE is_unlocked = 0 AND unlock_at <= :currentTime 
        ORDER BY unlock_at ASC
    """)
    fun getUnlockableCapsules(currentTime: Long): Flow<List<CapsuleEntity>>

    @Query("""
        SELECT * FROM capsules 
        WHERE is_unlocked = 0 AND unlock_at <= :currentTime 
        ORDER BY unlock_at ASC
    """)
    suspend fun getUnlockableCapsulesOnce(currentTime: Long): List<CapsuleEntity>

    // ── Next upcoming capsule ─────────────────────────────────────────────────
    @Query("""
        SELECT * FROM capsules 
        WHERE is_unlocked = 0 AND unlock_at > :currentTime 
        ORDER BY unlock_at ASC LIMIT 1
    """)
    fun getNextUpcomingCapsule(currentTime: Long): Flow<CapsuleEntity?>

    // ── Core Memory ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE is_core_memory = 1 ORDER BY created_at DESC")
    fun getCoreMemoryCapsules(): Flow<List<CapsuleEntity>>

    // ── Favorites ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteCapsules(): Flow<List<CapsuleEntity>>

    // ── Mood filter ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE mood = :mood ORDER BY created_at DESC")
    fun getCapsulesByMood(mood: String): Flow<List<CapsuleEntity>>

    // ── Unlock a capsule ──────────────────────────────────────────────────────
    @Query("UPDATE capsules SET is_unlocked = 1 WHERE id = :id")
    suspend fun markAsUnlocked(id: Long)

    // ── Toggle flags ──────────────────────────────────────────────────────────
    @Query("UPDATE capsules SET is_core_memory = :isCoreMemory WHERE id = :id")
    suspend fun setCoreMemory(id: Long, isCoreMemory: Boolean)

    @Query("UPDATE capsules SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE capsules SET has_voice_note = :hasVoiceNote WHERE id = :id")
    suspend fun setHasVoiceNote(id: Long, hasVoiceNote: Boolean)

    @Query("UPDATE capsules SET work_request_id = :workId WHERE id = :id")
    suspend fun setWorkRequestId(id: Long, workId: String?)

    // ── Stats queries ─────────────────────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM capsules")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM capsules WHERE is_unlocked = 1")
    fun getUnlockedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM capsules WHERE is_core_memory = 1")
    fun getCoreMemoryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM capsules WHERE mood = :mood")
    suspend fun getCountByMood(mood: String): Int

    @Query("SELECT DISTINCT mood FROM capsules")
    suspend fun getUsedMoods(): List<String>

    // ── Timeline (grouped by month, we do grouping in repo) ───────────────────
    @Query("SELECT * FROM capsules ORDER BY created_at ASC")
    fun getCapsulesChronological(): Flow<List<CapsuleEntity>>
}
