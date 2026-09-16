package com.example.eternotev2.data.model

import com.example.eternotev2.data.local.entity.CapsuleEntity
import com.example.eternotev2.data.local.entity.VoiceNoteEntity
import com.example.eternotev2.data.local.entity.parsedMood
import com.example.eternotev2.data.local.entity.parsedTags
import com.example.eternotev2.data.local.entity.parsedWaveform
import com.example.eternotev2.ui.theme.Mood

// ── Entity → Domain ───────────────────────────────────────────────────────────
fun CapsuleEntity.toDomain(): Capsule = Capsule(
    id             = id,
    userId         = userId,
    title          = title,
    message        = message,
    mood           = parsedMood(),
    createdAt      = createdAt,
    unlockAt       = unlockAt,
    isUnlocked     = isUnlocked,
    isCoreMemory   = isCoreMemory,
    isFavorite     = isFavorite,
    hasVoiceNote   = hasVoiceNote,
    imageUri       = imageUri,
    unlockMessage  = unlockMessage,
    capsuleType    = runCatching { CapsuleType.valueOf(capsuleType) }.getOrDefault(CapsuleType.NORMAL),
    tags           = parsedTags(),
    workRequestId  = workRequestId
)

// ── Domain → Entity ───────────────────────────────────────────────────────────
fun Capsule.toEntity(): CapsuleEntity = CapsuleEntity(
    id            = id,
    userId        = userId,
    title         = title,
    message       = message,
    mood          = mood.name,
    createdAt     = createdAt,
    unlockAt      = unlockAt,
    isUnlocked    = isUnlocked,
    isCoreMemory  = isCoreMemory,
    isFavorite    = isFavorite,
    hasVoiceNote  = hasVoiceNote,
    imageUri      = imageUri,
    unlockMessage = unlockMessage,
    capsuleType   = capsuleType.name,
    tags          = tags.joinToString(","),
    workRequestId = workRequestId
)

// ── VoiceNote Entity → Domain ─────────────────────────────────────────────────
fun VoiceNoteEntity.toDomain(): VoiceNote = VoiceNote(
    id             = id,
    capsuleId      = capsuleId,
    filePath       = filePath,
    fileName       = fileName,
    durationMillis = durationMillis,
    waveformData   = parsedWaveform(),
    createdAt      = createdAt,
    transcript     = transcript
)

// ── VoiceNote Domain → Entity ─────────────────────────────────────────────────
fun VoiceNote.toEntity(): VoiceNoteEntity = VoiceNoteEntity(
    id             = id,
    capsuleId      = capsuleId,
    filePath       = filePath,
    fileName       = fileName,
    durationMillis = durationMillis,
    waveformData   = waveformData.joinToString(","),
    createdAt      = createdAt,
    transcript     = transcript
)
