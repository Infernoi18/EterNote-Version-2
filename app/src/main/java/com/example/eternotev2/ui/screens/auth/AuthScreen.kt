package com.example.eternotev2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.components.ambient.StarField
import com.example.eternotev2.ui.theme.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

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

                        val birthDateText = if (uiState.birthDate != null) {
                            dateFormatter.format(Date(uiState.birthDate!!))
                        } else ""

                        Box(modifier = Modifier.fillMaxWidth()) {
                            AuthTextField(
                                value = birthDateText,
                                onValueChange = { },
                                label = "Birth Date (dd/mm/yyyy)",
                                icon = Icons.Default.Cake,
                                readOnly = true
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        val calendar = Calendar.getInstance()
                                        android.app.DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                val selected = Calendar.getInstance().apply {
                                                    set(year, month, dayOfMonth)
                                                }
                                                viewModel.onBirthDateChange(selected.timeInMillis)
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                            )
                        }
                    }

                    AuthTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Email Address",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    AuthTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        isPasswordVisible = uiState.isPasswordVisible,
                        onPasswordToggle = { viewModel.togglePasswordVisibility() }
                    )

                    if (!uiState.isLogin) {
                        AuthTextField(
                            value = uiState.confirmPassword,
                            onValueChange = { viewModel.onConfirmPasswordChange(it) },
                            label = "Confirm Password",
                            icon = Icons.Default.LockReset,
                            isPassword = true,
                            isPasswordVisible = uiState.isPasswordVisible,
                            onPasswordToggle = { viewModel.togglePasswordVisibility() }
                        )
                    }

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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(
                                    brush = Brush.horizontalGradient(listOf(CosmicViolet, NebulaPink)),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.performAuth() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.isLogin) "Enter Now" else "Create Identity",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        readOnly = readOnly,
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (isPassword && onPasswordToggle != null) {
            {
                IconButton(onClick = onPasswordToggle) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
