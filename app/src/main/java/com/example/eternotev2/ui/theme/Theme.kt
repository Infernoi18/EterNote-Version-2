package com.example.eternotev2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark scheme — unchanged from original ─────────────────────
private val EternoteDarkColorScheme = darkColorScheme(
    primary              = CosmicViolet,
    onPrimary            = TextPrimary,
    primaryContainer     = CosmicVioletDim,
    onPrimaryContainer   = CosmicVioletLight,
    secondary            = NebulaPink,
    onSecondary          = TextPrimary,
    secondaryContainer   = NebulaPinkDim,
    onSecondaryContainer = NebulaPinkLight,
    tertiary             = AuroraCyan,
    onTertiary           = DeepVoid,
    tertiaryContainer    = AuroraCyanDim,
    onTertiaryContainer  = AuroraCyanLight,
    background           = DeepVoid,
    onBackground         = TextPrimary,
    surface              = SurfaceDeep,
    onSurface            = TextPrimary,
    surfaceVariant       = SurfaceMid,
    onSurfaceVariant     = TextSecondary,
    surfaceTint          = CosmicViolet,
    outline              = GlassBorder,
    outlineVariant       = GlassBorderSoft,
    error                = ErrorRed,
    onError              = TextPrimary,
    scrim                = Color(0xCC03020A)
)

// ── Light scheme — navy-indigo, noticeably lighter than dark ──
private val EternoteLightColorScheme = darkColorScheme(
    // Still uses darkColorScheme because text is light on dark-navy.
    // Only backgrounds shift — all accents stay identical.
    primary              = CosmicViolet,
    onPrimary            = TextPrimary,
    primaryContainer     = CosmicVioletDim,
    onPrimaryContainer   = CosmicVioletLight,
    secondary            = NebulaPink,
    onSecondary          = TextPrimary,
    secondaryContainer   = NebulaPinkDim,
    onSecondaryContainer = NebulaPinkLight,
    tertiary             = AuroraCyan,
    onTertiary           = LightBgPrimary,
    tertiaryContainer    = AuroraCyanDim,
    onTertiaryContainer  = AuroraCyanLight,
    background           = LightBgPrimary,
    onBackground         = LightTextPrimary,
    surface              = LightBgSecondary,
    onSurface            = LightTextPrimary,
    surfaceVariant       = LightBgSurface,
    onSurfaceVariant     = LightTextSecondary,
    surfaceTint          = CosmicViolet,
    outline              = LightGlassBorder,
    outlineVariant       = LightGlassBorderSoft,
    error                = ErrorRed,
    onError              = TextPrimary,
    scrim                = Color(0xCC1A1535)
)

// ── Ambient effect opacity per theme ──────────────────────────
// Pass this to AmbientBackground, ParticleField, StarField
// so light mode tones them down without removing them.
val LocalAmbientAlpha = androidx.compose.runtime.staticCompositionLocalOf { 1.0f }

@Composable
fun EternoteV2Theme(
    themePreference: UserThemePreference = UserThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()

    val useDark = when (themePreference) {
        UserThemePreference.DARK   -> true
        UserThemePreference.LIGHT  -> false
        UserThemePreference.SYSTEM -> systemIsDark
    }

    val colorScheme = if (useDark) EternoteDarkColorScheme
                      else         EternoteLightColorScheme

    val bgColor     = if (useDark) DeepVoid else LightBgPrimary
    val ambientAlpha = if (useDark) 1.0f else 0.45f

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = bgColor.toArgb()
            window.navigationBarColor = bgColor.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalAmbientAlpha provides ambientAlpha
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = EternoteTypography,
            content     = content
        )
    }
}

// ── Helper — read current ambient alpha anywhere ──────────────
// Usage: val alpha = LocalAmbientAlpha.current
// Pass to ParticleField, StarField, GlowOrb as opacity multiplier
