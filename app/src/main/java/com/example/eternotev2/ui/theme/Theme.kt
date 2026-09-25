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
import com.example.eternotev2.ui.theme.UserThemePreference

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

private val EternoteLightColorScheme = darkColorScheme(
    primary              = CosmicViolet,
    onPrimary            = TextPrimary,
    primaryContainer     = CosmicVioletDim,
    onPrimaryContainer   = CosmicVioletLight,
    secondary            = NebulaPink,
    onSecondary          = TextPrimary,
    secondaryContainer   = NebulaPinkDim,
    onSecondaryContainer = NebulaPinkLight,
    tertiary             = AuroraCyan,
    onTertiary           = Color(0xFF1A1535),
    background           = Color(0xFF1A1535),
    onBackground         = Color(0xFFF0EEFF),
    surface              = Color(0xFF231D45),
    onSurface            = Color(0xFFF0EEFF),
    surfaceVariant       = Color(0xFF2D2660),
    onSurfaceVariant     = Color(0xFFB8B0D8),
    surfaceTint          = CosmicViolet,
    outline              = Color(0x40FFFFFF),
    outlineVariant       = Color(0x22FFFFFF),
    error                = ErrorRed,
    onError              = TextPrimary,
    scrim                = Color(0xCC1A1535)
)

// ── Ambient effect opacity per theme ──────────────────────────
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

    val ambientAlpha = if (useDark) 1.0f else 0.45f

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val bgColor = if (useDark) DeepVoid else Color(0xFF1A1535)
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
