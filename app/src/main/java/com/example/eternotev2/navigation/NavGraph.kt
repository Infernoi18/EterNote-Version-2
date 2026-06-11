package com.example.eternotev2.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.eternotev2.ui.screens.auth.AuthScreen
import com.example.eternotev2.ui.screens.capsule.CapsuleDetailScreen
import com.example.eternotev2.ui.screens.capsule.CapsuleUnlockScreen
import com.example.eternotev2.ui.screens.capsule.CreateCapsuleScreen
import com.example.eternotev2.ui.screens.corememory.CoreMemoryScreen
import com.example.eternotev2.ui.screens.home.HomeScreen
import com.example.eternotev2.ui.screens.insights.InsightsScreen
import com.example.eternotev2.ui.screens.onboarding.OnboardingScreen
import com.example.eternotev2.ui.screens.settings.SettingsScreen
import com.example.eternotev2.ui.screens.profile.ProfileScreen
import com.example.eternotev2.ui.screens.splash.SplashScreen
import com.example.eternotev2.ui.screens.timeline.TimelineScreen
import com.example.eternotev2.ui.screens.voice.VoiceNoteScreen

private const val NAV_ANIM_DURATION = 400

@Composable
fun EternoteNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Splash.route
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        enterTransition  = {
            fadeIn(tween(NAV_ANIM_DURATION)) +
                    scaleIn(tween(NAV_ANIM_DURATION), initialScale = 0.95f)
        },
        exitTransition   = {
            fadeOut(tween(NAV_ANIM_DURATION)) +
                    scaleOut(tween(NAV_ANIM_DURATION), targetScale = 0.95f)
        },
        popEnterTransition = {
            fadeIn(tween(NAV_ANIM_DURATION)) +
                    scaleIn(tween(NAV_ANIM_DURATION), initialScale = 1.02f)
        },
        popExitTransition  = {
            fadeOut(tween(NAV_ANIM_DURATION)) +
                    scaleOut(tween(NAV_ANIM_DURATION), targetScale = 1.02f)
        }
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(Routes.Splash.route) {
            SplashScreen(
                onSplashComplete = { isFirstLaunch ->
                    val destination = when {
                        isFirstLaunch -> Routes.Onboarding.route
                        else -> Routes.Auth.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Auth ──────────────────────────────────────────────────────────────
        composable(Routes.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ────────────────────────────────────────────────────────
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────
        composable(Routes.Home.route) {
            HomeScreen(
                onCreateCapsule = { navController.navigate(Routes.CreateCapsule.route) },
                onCapsuleClick  = { capsuleId ->
                    navController.navigate(Routes.CapsuleDetail.createRoute(capsuleId))
                },
                onNavigateToTimeline   = { navController.navigate(Routes.Timeline.route) },
                onNavigateToCoreMemory = { navController.navigate(Routes.CoreMemory.route) },
                onNavigateToInsights   = { navController.navigate(Routes.Insights.route) },
                onSettingsClick = { navController.navigate(Routes.Settings.route) },
                onProfileClick = { navController.navigate(Routes.Profile.route) }
            )
        }

        // ── Profile ───────────────────────────────────────────────────────────
        composable(Routes.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Timeline ──────────────────────────────────────────────────────────
        composable(Routes.Timeline.route) {
            TimelineScreen(
                onCapsuleClick       = { capsuleId ->
                    navController.navigate(Routes.CapsuleDetail.createRoute(capsuleId))
                },
                onProfileClick = { navController.navigate(Routes.Profile.route) }
            )
        }

        // ── Core Memory ───────────────────────────────────────────────────────
        composable(Routes.CoreMemory.route) {
            CoreMemoryScreen(
                onCapsuleClick         = { capsuleId ->
                    navController.navigate(Routes.CapsuleDetail.createRoute(capsuleId))
                },
                onProfileClick = { navController.navigate(Routes.Profile.route) }
            )
        }

        // ── Insights ──────────────────────────────────────────────────────────
        composable(Routes.Insights.route) {
            InsightsScreen(
                onProfileClick = { navController.navigate(Routes.Profile.route) }
            )
        }

        // ── Create Capsule ────────────────────────────────────────────────────
        composable(
            route = Routes.CreateCapsule.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(NAV_ANIM_DURATION)
                )
            },
            exitTransition  = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(NAV_ANIM_DURATION)
                )
            }
        ) {
            CreateCapsuleScreen(
                onCapsuleCreated = {
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        // ── Capsule Detail ────────────────────────────────────────────────────
        composable(
            route = Routes.CapsuleDetail.route,
            arguments = listOf(navArgument("capsuleId") { type = NavType.LongType })
        ) { backStack ->
            val capsuleId = backStack.arguments?.getLong("capsuleId") ?: return@composable
            CapsuleDetailScreen(
                capsuleId    = capsuleId,
                onUnlockClick = {
                    navController.navigate(Routes.CapsuleUnlock.createRoute(capsuleId))
                },
                onVoiceNote  = {
                    navController.navigate(Routes.VoiceNote.createRoute(capsuleId))
                },
                onBack       = { navController.popBackStack() }
            )
        }

        // ── Capsule Unlock (Cinematic) ────────────────────────────────────────
        composable(
            route = Routes.CapsuleUnlock.route,
            arguments = listOf(navArgument("capsuleId") { type = NavType.LongType }),
            enterTransition = { fadeIn(tween(600)) },
            exitTransition  = { fadeOut(tween(600)) }
        ) { backStack ->
            val capsuleId = backStack.arguments?.getLong("capsuleId") ?: return@composable
            CapsuleUnlockScreen(
                capsuleId = capsuleId,
                onUnlockComplete = { navController.popBackStack() },
                onBack           = { navController.popBackStack() }
            )
        }

        // ── Voice Note ────────────────────────────────────────────────────────
        composable(
            route = Routes.VoiceNote.route,
            arguments = listOf(navArgument("capsuleId") { type = NavType.LongType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(NAV_ANIM_DURATION)
                )
            },
            exitTransition  = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(NAV_ANIM_DURATION)
                )
            }
        ) { backStack ->
            val capsuleId = backStack.arguments?.getLong("capsuleId") ?: return@composable
            VoiceNoteScreen(
                capsuleId = capsuleId,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable(Routes.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}