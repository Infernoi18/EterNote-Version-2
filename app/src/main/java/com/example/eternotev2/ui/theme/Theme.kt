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

// Eternote is always dark — it's a night sky, not a spreadsheet.
private val EternoteDarkColorScheme = darkColorScheme(
    primary            = CosmicViolet,
    onPrimary          = TextPrimary,
    primaryContainer   = CosmicVioletDim,
    onPrimaryContainer = CosmicVioletLight,

    secondary          = NebulaPink,
    onSecondary        = TextPrimary,
    secondaryContainer = NebulaPinkDim,
    onSecondaryContainer = NebulaPinkLight,

    tertiary           = AuroraCyan,
    onTertiary         = DeepVoid,
    tertiaryContainer  = AuroraCyanDim,
    onTertiaryContainer = AuroraCyanLight,

    background         = DeepVoid,
    onBackground       = TextPrimary,

    surface            = SurfaceDeep,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceMid,
    onSurfaceVariant   = TextSecondary,

    surfaceTint        = CosmicViolet,

    outline            = GlassBorder,
    outlineVariant     = GlassBorderSoft,

    error              = ErrorRed,
    onError            = TextPrimary,
    errorContainer     = Color(0xFF3B0A0A),
    onErrorContainer   = ErrorRed,

    scrim              = Color(0xCC03020A),
)

@Composable
fun EternoteV2Theme(
    content: @Composable () -> Unit
) {
    // Always force dark — the app has no light mode
    val colorScheme = EternoteDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = DeepVoid.toArgb()
            window.navigationBarColor = DeepVoid.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = EternoteTypography,
        content     = content
    )
}