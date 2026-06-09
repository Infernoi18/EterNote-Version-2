package com.example.eternotev2.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

// ── Mood Enum ─────────────────────────────────────────────────────────────────
enum class Mood(val label: String, val emoji: String) {
    HAPPY("Happy", "☀️"),
    MELANCHOLIC("Melancholic", "🌊"),
    HOPEFUL("Hopeful", "🌱"),
    NOSTALGIC("Nostalgic", "🍂"),
    ANXIOUS("Anxious", "⚡"),
    GRATEFUL("Grateful", "🌸")
}

// ── Mood Colors Data Class ────────────────────────────────────────────────────
@Stable
data class MoodColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val glow: Color,
    val surface: Color,
    val gradient: List<Color>
)

// ── Mood Color Resolver ───────────────────────────────────────────────────────
fun moodColors(mood: Mood): MoodColors = when (mood) {
    Mood.HAPPY -> MoodColors(
        primary   = MoodHappyPrimary,
        secondary = MoodHappySecondary,
        tertiary  = MoodHappyTertiary,
        glow      = MoodHappyGlow,
        surface   = MoodHappySurface,
        gradient  = GradientMoodHappy
    )
    Mood.MELANCHOLIC -> MoodColors(
        primary   = MoodMelancholicPrimary,
        secondary = MoodMelancholicSecondary,
        tertiary  = MoodMelancholicTertiary,
        glow      = MoodMelancholicGlow,
        surface   = MoodMelancholicSurface,
        gradient  = GradientMoodMelancholic
    )
    Mood.HOPEFUL -> MoodColors(
        primary   = MoodHopefulPrimary,
        secondary = MoodHopefulSecondary,
        tertiary  = MoodHopefulTertiary,
        glow      = MoodHopefulGlow,
        surface   = MoodHopefulSurface,
        gradient  = GradientMoodHopeful
    )
    Mood.NOSTALGIC -> MoodColors(
        primary   = MoodNostalgicPrimary,
        secondary = MoodNostalgicSecondary,
        tertiary  = MoodNostalgicTertiary,
        glow      = MoodNostalgicGlow,
        surface   = MoodNostalgicSurface,
        gradient  = GradientMoodNostalgic
    )
    Mood.ANXIOUS -> MoodColors(
        primary   = MoodAnxiousPrimary,
        secondary = MoodAnxiousSecondary,
        tertiary  = MoodAnxiousTertiary,
        glow      = MoodAnxiousGlow,
        surface   = MoodAnxiousSurface,
        gradient  = GradientMoodAnxious
    )
    Mood.GRATEFUL -> MoodColors(
        primary   = MoodGratefulPrimary,
        secondary = MoodGratefulSecondary,
        tertiary  = MoodGratefulTertiary,
        glow      = MoodGratefulGlow,
        surface   = MoodGratefulSurface,
        gradient  = GradientMoodGrateful
    )
}

// ── Animated Mood Colors (animates on mood change) ────────────────────────────
@Stable
data class AnimatedMoodColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val glow: Color,
    val surface: Color
)

@Composable
fun animatedMoodColors(mood: Mood): AnimatedMoodColors {
    val colors = moodColors(mood)
    val animSpec = tween<Color>(durationMillis = 800)

    val primary   by animateColorAsState(colors.primary,   animSpec, label = "moodPrimary")
    val secondary by animateColorAsState(colors.secondary, animSpec, label = "moodSecondary")
    val tertiary  by animateColorAsState(colors.tertiary,  animSpec, label = "moodTertiary")
    val glow      by animateColorAsState(colors.glow,      animSpec, label = "moodGlow")
    val surface   by animateColorAsState(colors.surface,   animSpec, label = "moodSurface")

    return AnimatedMoodColors(primary, secondary, tertiary, glow, surface)
}

// ── Mood Quote Map ─────────────────────────────────────────────────────────────
val moodQuotes: Map<Mood, List<String>> = mapOf(
    Mood.HAPPY to listOf(
        "Your future self is smiling back at you.",
        "Joy preserved is joy multiplied.",
        "This moment deserved to be remembered."
    ),
    Mood.MELANCHOLIC to listOf(
        "Even the rain has its own kind of beauty.",
        "Some feelings are meant to be held gently.",
        "The depth of your feeling is a gift."
    ),
    Mood.HOPEFUL to listOf(
        "The best chapters haven't been written yet.",
        "You're planting seeds for a future you'll love.",
        "Every tomorrow holds infinite possibility."
    ),
    Mood.NOSTALGIC to listOf(
        "Memory is the greatest time machine.",
        "The past is a country you can always revisit.",
        "What once was, shaped all that you are."
    ),
    Mood.ANXIOUS to listOf(
        "You survived every storm before this one.",
        "Breathe. The future is not yet written.",
        "Uncertainty is just possibility in disguise."
    ),
    Mood.GRATEFUL to listOf(
        "Gratitude turns ordinary moments into magic.",
        "A thankful heart is a full heart.",
        "The more you appreciate, the more there is."
    )
)

fun randomMoodQuote(mood: Mood): String =
    moodQuotes[mood]?.random() ?: "Your story is worth preserving."