package com.example.eternotev2

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eternotev2.navigation.BottomNavTab
import com.example.eternotev2.navigation.EternoteNavGraph
import com.example.eternotev2.navigation.Routes
import com.example.eternotev2.ui.components.common.EternoteBottomBar
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.EternoteV2Theme
import com.example.eternotev2.ui.theme.UserThemePreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var unlockCapsuleIdState = mutableLongStateOf(-1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val initialId = intent.getLongExtra("unlock_capsule_id", -1L)
        unlockCapsuleIdState.longValue = initialId

        setContent {
            val prefsRepo = remember {
                (application as com.example.eternotev2.EternoteApplication)
                    .userPreferencesRepository
            }
            val themePreference by prefsRepo.themePreference
                .collectAsState(initial = UserThemePreference.SYSTEM)

            val view = LocalView.current
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    // Status bar icons: false = white icons (correct for dark bg)
                    isAppearanceLightStatusBars = false

                    // Nav bar icons: false = white icons (correct for dark/navy bg)
                    // This makes back button, home pill, recents WHITE
                    // so they are always visible on our dark backgrounds.
                    isAppearanceLightNavigationBars = false
                }

                // Force navigation bar to be transparent so our
                // background color shows through and nav icons
                // contrast against it via the flag above.
                window.navigationBarColor =
                    android.graphics.Color.TRANSPARENT

                // Required on API 29+ to allow drawing behind nav bar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }

            EternoteV2Theme(themePreference = themePreference) {
                EternoteApp(
                    unlockCapsuleId = unlockCapsuleIdState.longValue,
                    onNotificationHandled = { unlockCapsuleIdState.longValue = -1L }
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        val capsuleId = intent.getLongExtra("unlock_capsule_id", -1L)
        if (capsuleId != -1L) {
            unlockCapsuleIdState.longValue = capsuleId
        }
    }
}

@Composable
fun EternoteApp(
    unlockCapsuleId: Long = -1L,
    onNotificationHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(unlockCapsuleId) {
        if (unlockCapsuleId != -1L) {
            navController.navigate(Routes.CapsuleUnlock.createRoute(unlockCapsuleId))
            onNotificationHandled()
        }
    }

    // Runtime Permissions Request
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    var hasRequestedPermissions by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        if (!hasRequestedPermissions && currentRoute != null && currentRoute != Routes.Splash.route) {
            val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionsLauncher.launch(permissions.toTypedArray())
            hasRequestedPermissions = true
        }
    }

    val showBottomBar = BottomNavTab.entries.any { it.route == currentRoute }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = currentRoute == BottomNavTab.HOME.route) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Eternote?") },
            text = { Text("Are you sure you want to close Eternote?") },
            confirmButton = {
                TextButton(onClick = { 
                    showExitDialog = false
                    (navController.context as? ComponentActivity)?.finish()
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Stay")
                }
            },
            containerColor = DeepVoid,
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f)
        )
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                EternoteBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = DeepVoid
        ) {
            EternoteNavGraph(navController = navController)
        }
    }
}
