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
val MoodHappyPrimary    = Color(0xFFFFD52E)   // Radiant Yellow
val MoodHappySecondary  = Color(0xFFFFB300)   // Warm Amber
val MoodHappyTertiary   = Color(0xFFFFEA80)
val MoodHappyGlow       = Color(0x33FFD52E)   // 20% alpha for glow
val MoodHappySurface    = Color(0xFF1A1400)

// ── Lovely ───────────────────────────────────────────────────────────────────
val MoodLovelyPrimary    = Color(0xFFFF4D85)   // Vibrant Rose
val MoodLovelySecondary  = Color(0xFFD500F9)   // Electric Violet
val MoodLovelyTertiary   = Color(0xFFFFC1E3)
val MoodLovelyGlow       = Color(0x33FF4D85)
val MoodLovelySurface    = Color(0xFF1A0009)

// ── Melancholic ───────────────────────────────────────────────────────────────
val MoodMelancholicPrimary   = Color(0xFF21B6FF)   // Clear Azure
val MoodMelancholicSecondary = Color(0xFF0050D5)   // Deep Ocean Blue
val MoodMelancholicTertiary  = Color(0xFF80D8FF)
val MoodMelancholicGlow      = Color(0x3321B6FF)
val MoodMelancholicSurface   = Color(0xFF030812)

// ── Hopeful ───────────────────────────────────────────────────────────────────
val MoodHopefulPrimary   = Color(0xFF00E676)   // Spring Green
val MoodHopefulSecondary = Color(0xFF00A855)   // Rich Emerald
val MoodHopefulTertiary  = Color(0xFFB9F6CA)
val MoodHopefulGlow      = Color(0x3300E676)
val MoodHopefulSurface   = Color(0xFF001208)

// ── Nostalgic ─────────────────────────────────────────────────────────────────
val MoodNostalgicPrimary   = Color(0xFFFF9500)   // Glowing Amber
val MoodNostalgicSecondary = Color(0xFFE65100)   // Burnt Orange
val MoodNostalgicTertiary  = Color(0xFFFFCCBC)
val MoodNostalgicGlow      = Color(0x33FF9500)
val MoodNostalgicSurface   = Color(0xFF160800)

// ── Anxious ───────────────────────────────────────────────────────────────────
val MoodAnxiousPrimary   = Color(0xFFD500F9)   // High-Voltage Violet
val MoodAnxiousSecondary = Color(0xFF651FFF)   // Deep Indigo
val MoodAnxiousTertiary  = Color(0xFFEA80FC)
val MoodAnxiousGlow      = Color(0x33D500F9)
val MoodAnxiousSurface   = Color(0xFF0F0014)

// ── Grateful ──────────────────────────────────────────────────────────────────
val MoodGratefulPrimary   = Color(0xFF00D0B4)   // Bright Teal
val MoodGratefulSecondary = Color(0xFF00829B)   // Deep Marine
val MoodGratefulTertiary  = Color(0xFFB2EBF2)
val MoodGratefulGlow      = Color(0x3300D0B4)
val MoodGratefulSurface   = Color(0xFF001512)

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
val GradientMoodLovely     = listOf(MoodLovelyPrimary, MoodLovelySecondary)
val GradientMoodGrateful   = listOf(MoodGratefulPrimary, MoodGratefulSecondary)