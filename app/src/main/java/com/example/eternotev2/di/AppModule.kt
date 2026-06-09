package com.example.eternotev2.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.eternotev2.data.local.dao.CapsuleDao
import com.example.eternotev2.data.local.dao.VoiceNoteDao
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.data.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = "eternote_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideCapsuleRepository(
        capsuleDao: CapsuleDao,
        voiceNoteDao: VoiceNoteDao
    ): CapsuleRepository = CapsuleRepository(capsuleDao, voiceNoteDao)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        dataStore: DataStore<Preferences>
    ): UserPreferencesRepository = UserPreferencesRepository(dataStore)
}
