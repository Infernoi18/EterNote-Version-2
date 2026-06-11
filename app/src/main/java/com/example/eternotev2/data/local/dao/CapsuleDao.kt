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
    @Query("SELECT * FROM capsules WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllCapsules(userId: String): Flow<List<CapsuleEntity>>

    @Query("SELECT * FROM capsules WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getAllCapsulesOnce(userId: String): List<CapsuleEntity>

    @Query("SELECT * FROM capsules WHERE user_id = :userId ORDER BY unlock_at ASC")
    fun getAllCapsulesSortedByUnlock(userId: String): Flow<List<CapsuleEntity>>

    // ── Locked / Unlocked ─────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE user_id = :userId AND is_unlocked = 0 ORDER BY unlock_at ASC")
    fun getLockedCapsules(userId: String): Flow<List<CapsuleEntity>>

    @Query("SELECT * FROM capsules WHERE user_id = :userId AND is_unlocked = 1 ORDER BY unlock_at DESC")
    fun getUnlockedCapsules(userId: String): Flow<List<CapsuleEntity>>

    // ── Ready to unlock ───────────────────────────────────────────────────────
    @Query("""
        SELECT * FROM capsules 
        WHERE user_id = :userId AND is_unlocked = 0 AND unlock_at <= :currentTime 
        ORDER BY unlock_at ASC
    """)
    fun getUnlockableCapsules(userId: String, currentTime: Long): Flow<List<CapsuleEntity>>

    @Query("""
        SELECT * FROM capsules 
        WHERE user_id = :userId AND is_unlocked = 0 AND unlock_at <= :currentTime 
        ORDER BY unlock_at ASC
    """)
    suspend fun getUnlockableCapsulesOnce(userId: String, currentTime: Long): List<CapsuleEntity>

    // ── Next upcoming capsule ─────────────────────────────────────────────────
    @Query("""
        SELECT * FROM capsules 
        WHERE user_id = :userId AND is_unlocked = 0 AND unlock_at > :currentTime 
        ORDER BY unlock_at ASC LIMIT 1
    """)
    fun getNextUpcomingCapsule(userId: String, currentTime: Long): Flow<CapsuleEntity?>

    // ── Core Memory ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE user_id = :userId AND is_core_memory = 1 ORDER BY created_at DESC")
    fun getCoreMemoryCapsules(userId: String): Flow<List<CapsuleEntity>>

    // ── Favorites ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE user_id = :userId AND is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteCapsules(userId: String): Flow<List<CapsuleEntity>>

    // ── Mood filter ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM capsules WHERE user_id = :userId AND mood = :mood ORDER BY created_at DESC")
    fun getCapsulesByMood(userId: String, mood: String): Flow<List<CapsuleEntity>>

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

    // ── User Data Management ──────────────────────────────────────────────────
    @Query("UPDATE capsules SET user_id = :newUserId WHERE user_id = 'guest'")
    suspend fun migrateGuestData(newUserId: String)

    // ── Stats queries ─────────────────────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM capsules WHERE user_id = :userId")
    fun getTotalCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM capsules WHERE user_id = :userId AND is_unlocked = 1")
    fun getUnlockedCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM capsules WHERE user_id = :userId AND is_core_memory = 1")
    fun getCoreMemoryCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM capsules WHERE user_id = :userId AND mood = :mood")
    suspend fun getCountByMood(userId: String, mood: String): Int

    @Query("SELECT COUNT(*) FROM capsules WHERE user_id = :userId AND mood = :mood")
    suspend fun getCountByMoodOnce(userId: String, mood: String): Int

    @Query("SELECT DISTINCT mood FROM capsules WHERE user_id = :userId")
    suspend fun getUsedMoods(userId: String): List<String>

    @Query("SELECT DISTINCT mood FROM capsules WHERE user_id = :userId")
    fun getUsedMoodsFlow(userId: String): Flow<List<String>>

    // ── Timeline (grouped by month, we do grouping in repo) ───────────────────
    @Query("SELECT * FROM capsules WHERE user_id = :userId ORDER BY created_at ASC")
    fun getCapsulesChronological(userId: String): Flow<List<CapsuleEntity>>
}
