package com.example.eternotev2.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eternotev2.data.local.dao.CapsuleDao
import com.example.eternotev2.data.local.dao.UserDao
import com.example.eternotev2.data.local.dao.VoiceNoteDao
import com.example.eternotev2.data.local.entity.CapsuleEntity
import com.example.eternotev2.data.local.entity.UserEntity
import com.example.eternotev2.data.local.entity.VoiceNoteEntity

@Database(
    entities  = [
        CapsuleEntity::class,
        VoiceNoteEntity::class,
        UserEntity::class
    ],
    version   = 3,
    exportSchema = true      // enables schema export for migration tracking
)
abstract class EternoteDatabase : RoomDatabase() {
    abstract fun capsuleDao(): CapsuleDao
    abstract fun userDao(): UserDao
    abstract fun voiceNoteDao(): VoiceNoteDao

    companion object {
        const val DATABASE_NAME = "eternote_database"
    }
}
