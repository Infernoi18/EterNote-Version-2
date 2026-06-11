package com.example.eternotev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eternotev2.navigation.BottomNavTab
import com.example.eternotev2.navigation.EternoteNavGraph
import com.example.eternotev2.navigation.Routes
import com.example.eternotev2.ui.components.common.EternoteBottomBar
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.EternoteV2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EternoteV2Theme {
                EternoteApp()
            }
        }
    }
}

@Composable
fun EternoteApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
