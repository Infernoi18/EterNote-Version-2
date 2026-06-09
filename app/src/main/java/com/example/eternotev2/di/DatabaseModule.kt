package com.example.eternotev2.di

import android.content.Context
import androidx.room.Room
import com.example.eternotev2.data.local.dao.CapsuleDao
import com.example.eternotev2.data.local.dao.VoiceNoteDao
import com.example.eternotev2.data.local.database.EternoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): EternoteDatabase = Room.databaseBuilder(
        context,
        EternoteDatabase::class.java,
        EternoteDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()   // dev only — replace with proper migrations in prod
        .build()

    @Provides
    @Singleton
    fun provideCapsuleDao(database: EternoteDatabase): CapsuleDao =
        database.capsuleDao()

    @Provides
    @Singleton
    fun provideVoiceNoteDao(database: EternoteDatabase): VoiceNoteDao =
        database.voiceNoteDao()
}
