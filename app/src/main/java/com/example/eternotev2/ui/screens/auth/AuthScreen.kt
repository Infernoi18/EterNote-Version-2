package com.example.eternotev2.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.ui.components.common.GlowButton
import com.example.eternotev2.ui.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
    ) {
        StarField()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Eternote",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Text(
                text = "Cinematic Time Capsule",
                style = MaterialTheme.typography.bodySmall,
                color = CosmicVioletLight,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (uiState.isLogin) "Welcome Back" else "Join the Void",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (!uiState.isLogin) {
                        AuthTextField(
                            value = uiState.username,
                            onValueChange = { viewModel.onUsernameChange(it) },
                            label = "Username",
                            icon = Icons.Default.Person
                        )
                    }

                    AuthTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Email Address",
                        icon = Icons.Default.Email
                    )

                    AuthTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = CosmicViolet)
                    } else {
                        GlowButton(
                            text = if (uiState.isLogin) "Enter Now" else "Create Identity",
                            onClick = { viewModel.performAuth() },
                            glowColor = CosmicViolet,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(
                        text = if (uiState.isLogin) "Don't have an account? Sign Up" else "Already a voyager? Log In",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { viewModel.toggleAuthMode() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = CosmicViolet,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor = CosmicViolet,
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
            unfocusedLeadingIconColor = Color.White.copy(alpha = 0.5f),
            focusedLeadingIconColor = CosmicViolet
        ),
        singleLine = true
    )
}
