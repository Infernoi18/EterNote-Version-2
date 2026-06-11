package com.example.eternotev2.navigation

// ── Type-safe route definitions ───────────────────────────────────────────────
sealed class Routes(val route: String) {

    // ── Auth / Intro ──────────────────────────────────────────────────────────
    data object Splash      : Routes("splash")
    data object Auth        : Routes("auth")
    data object Onboarding  : Routes("onboarding")

    // ── Main Tabs ─────────────────────────────────────────────────────────────
    data object Home        : Routes("home")
    data object Timeline    : Routes("timeline")
    data object CoreMemory  : Routes("core_memory")
    data object Insights    : Routes("insights")

    // ── Capsule Flow ──────────────────────────────────────────────────────────
    data object CreateCapsule : Routes("create_capsule")

    data object CapsuleDetail : Routes("capsule_detail/{capsuleId}") {
        fun createRoute(capsuleId: Long) = "capsule_detail/$capsuleId"
    }

    data object CapsuleUnlock : Routes("capsule_unlock/{capsuleId}") {
        fun createRoute(capsuleId: Long) = "capsule_unlock/$capsuleId"
    }

    // ── Voice Note ────────────────────────────────────────────────────────────
    data object VoiceNote : Routes("voice_note/{capsuleId}") {
        fun createRoute(capsuleId: Long) = "voice_note/$capsuleId"
    }

    // ── Settings / Profile ────────────────────────────────────────────────────
    data object Settings : Routes("settings")
    data object Profile  : Routes("profile")
}

// ── Bottom Navigation Tabs ────────────────────────────────────────────────────
enum class BottomNavTab(
    val route: String,
    val label: String,
    val iconUnselected: String,   // we'll use Material Icons by name in NavGraph
    val iconSelected: String
) {
    HOME(
        route          = Routes.Home.route,
        label          = "Capsules",
        iconUnselected = "HourglassEmpty",
        iconSelected   = "HourglassFull"
    ),
    TIMELINE(
        route          = Routes.Timeline.route,
        label          = "Timeline",
        iconUnselected = "Timeline",
        iconSelected   = "Timeline"
    ),
    CORE_MEMORY(
        route          = Routes.CoreMemory.route,
        label          = "Core Memory",
        iconUnselected = "FavoriteBorder",
        iconSelected   = "Favorite"
    ),
    INSIGHTS(
        route          = Routes.Insights.route,
        label          = "Insights",
        iconUnselected = "AutoAwesome",
        iconSelected   = "AutoAwesome"
    )
}