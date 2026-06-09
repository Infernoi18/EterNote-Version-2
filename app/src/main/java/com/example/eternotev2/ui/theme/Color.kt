package com.example.eternotev2.ui.theme

import androidx.compose.ui.graphics.Color

// ── Base Neutrals ─────────────────────────────────────────────────────────────
val DeepVoid        = Color(0xFF03020A)   // deepest background
val VoidBlack       = Color(0xFF07050F)
val SurfaceDeep     = Color(0xFF0D0B1A)
val SurfaceMid      = Color(0xFF13101F)
val SurfaceElevated = Color(0xFF1A1628)
val SurfaceGlass    = Color(0x1AFFFFFF)   // glassmorphism base
val SurfaceGlassHigh= Color(0x26FFFFFF)

// ── Primary Accent — Cosmic Violet ───────────────────────────────────────────
val CosmicViolet        = Color(0xFF7B5CF0)
val CosmicVioletLight   = Color(0xFF9D7FF5)
val CosmicVioletDim     = Color(0xFF4A3490)
val CosmicVioletGlow    = Color(0x557B5CF0)
val CosmicVioletGlowSoft= Color(0x2A7B5CF0)

// ── Secondary Accent — Nebula Pink ───────────────────────────────────────────
val NebulaPink      = Color(0xFFE040FB)
val NebulaPinkLight = Color(0xFFEA7FFB)
val NebulaPinkDim   = Color(0xFF8A1FA8)
val NebulaPinkGlow  = Color(0x55E040FB)

// ── Tertiary Accent — Aurora Cyan ────────────────────────────────────────────
val AuroraCyan      = Color(0xFF00E5FF)
val AuroraCyanLight = Color(0xFF80F0FF)
val AuroraCyanDim   = Color(0xFF007A99)
val AuroraCyanGlow  = Color(0x5500E5FF)

// ── Star Gold ────────────────────────────────────────────────────────────────
val StarGold        = Color(0xFFFFD54F)
val StarGoldLight   = Color(0xFFFFEA80)
val StarGoldGlow    = Color(0x55FFD54F)

// ── Text ─────────────────────────────────────────────────────────────────────
val TextPrimary     = Color(0xFFF0EEFF)
val TextSecondary   = Color(0xFFB0A8D0)
val TextTertiary    = Color(0xFF6B6080)
val TextDisabled    = Color(0xFF3D3558)

// ── Semantic ─────────────────────────────────────────────────────────────────
val SuccessGreen    = Color(0xFF00E676)
val SuccessGlow     = Color(0x5500E676)
val ErrorRed        = Color(0xFFFF5252)
val ErrorGlow       = Color(0x55FF5252)
val WarningAmber    = Color(0xFFFFAB40)

// ── Glass / Border ────────────────────────────────────────────────────────────
val GlassBorder     = Color(0x33FFFFFF)
val GlassBorderSoft = Color(0x1AFFFFFF)
val GlowWhite       = Color(0x80FFFFFF)

// ═══════════════════════════════════════════════════════════════════════════════
// MOOD COLOR SYSTEM
// ═══════════════════════════════════════════════════════════════════════════════

// ── Happy ────────────────────────────────────────────────────────────────────
val MoodHappyPrimary    = Color(0xFFFFD54F)   // warm gold
val MoodHappySecondary  = Color(0xFFFF8F00)   // amber
val MoodHappyTertiary   = Color(0xFFFFF176)   // soft yellow
val MoodHappyGlow       = Color(0x55FFD54F)
val MoodHappySurface    = Color(0xFF1A1400)

// ── Melancholic ───────────────────────────────────────────────────────────────
val MoodMelancholicPrimary   = Color(0xFF5C7CFA)   // steel blue
val MoodMelancholicSecondary = Color(0xFF748FFC)
val MoodMelancholicTertiary  = Color(0xFF3A5BD4)
val MoodMelancholicGlow      = Color(0x555C7CFA)
val MoodMelancholicSurface   = Color(0xFF030812)

// ── Hopeful ───────────────────────────────────────────────────────────────────
val MoodHopefulPrimary   = Color(0xFF69F0AE)   // mint green
val MoodHopefulSecondary = Color(0xFF00E676)
val MoodHopefulTertiary  = Color(0xFFB9F6CA)
val MoodHopefulGlow      = Color(0x5569F0AE)
val MoodHopefulSurface   = Color(0xFF001208)

// ── Nostalgic ─────────────────────────────────────────────────────────────────
val MoodNostalgicPrimary   = Color(0xFFFFAB91)   // warm peach/orange
val MoodNostalgicSecondary = Color(0xFFFF7043)
val MoodNostalgicTertiary  = Color(0xFFFFCCBC)
val MoodNostalgicGlow      = Color(0x55FFAB91)
val MoodNostalgicSurface   = Color(0xFF160800)

// ── Anxious ───────────────────────────────────────────────────────────────────
val MoodAnxiousPrimary   = Color(0xFFE040FB)   // electric purple
val MoodAnxiousSecondary = Color(0xFFAA00FF)
val MoodAnxiousTertiary  = Color(0xFFEA80FC)
val MoodAnxiousGlow      = Color(0x55E040FB)
val MoodAnxiousSurface   = Color(0xFF0F0014)

// ── Grateful ──────────────────────────────────────────────────────────────────
val MoodGratefulPrimary   = Color(0xFFFF80AB)   // rose pink
val MoodGratefulSecondary = Color(0xFFF06292)
val MoodGratefulTertiary  = Color(0xFFFFB3C6)
val MoodGratefulGlow      = Color(0x55FF80AB)
val MoodGratefulSurface   = Color(0xFF150009)

// ═══════════════════════════════════════════════════════════════════════════════
// GRADIENT PRESETS
// ═══════════════════════════════════════════════════════════════════════════════

// Used as Brush.linearGradient / radialGradient color lists
val GradientCosmicPurple = listOf(CosmicViolet, NebulaPink)
val GradientAurora       = listOf(AuroraCyan, CosmicViolet)
val GradientNebula       = listOf(NebulaPink, CosmicViolet, AuroraCyan)
val GradientDeepSpace    = listOf(DeepVoid, SurfaceDeep, CosmicVioletDim)
val GradientGold         = listOf(StarGold, WarningAmber)

val GradientMoodHappy      = listOf(MoodHappyPrimary, MoodHappySecondary)
val GradientMoodMelancholic= listOf(MoodMelancholicPrimary, MoodMelancholicSecondary)
val GradientMoodHopeful    = listOf(MoodHopefulPrimary, MoodHopefulSecondary)
val GradientMoodNostalgic  = listOf(MoodNostalgicPrimary, MoodNostalgicSecondary)
val GradientMoodAnxious    = listOf(MoodAnxiousPrimary, MoodAnxiousSecondary)
val GradientMoodGrateful   = listOf(MoodGratefulPrimary, MoodGratefulSecondary)