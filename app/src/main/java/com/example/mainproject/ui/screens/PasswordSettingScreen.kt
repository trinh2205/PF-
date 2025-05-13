package com.example.mainproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.ui.components.BottomNavigationBar
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth

@Composable
fun PasswordSettingScreen(navController: NavController) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF3498DB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
                Text(
                    "Password Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notification",
                    tint = Color.White
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF4FFF9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Password Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3498DB)
                )

                // Current Password
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Current Password")
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (currentPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                }

                // New Password
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "New Password")
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                }

                // Confirm New Password
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Confirm New Password")
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Change Password Button
                Button(
                    onClick = {
                        val user = Firebase.auth.currentUser
                        val email = user?.email

                        if (user != null && email != null) {
                            val credential = EmailAuthProvider.getCredential(email, currentPassword)

                            user.reauthenticate(credential)
                                .addOnCompleteListener { reAuthTask ->
                                    if (reAuthTask.isSuccessful) {
                                        if (newPassword == confirmPassword) {
                                            user.updatePassword(newPassword)
                                                .addOnCompleteListener { updateTask ->
                                                    if (updateTask.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Password updated successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.popBackStack()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to update password: ${updateTask.exception?.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "New password and confirmation do not match",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Re-authentication failed: ${reAuthTask.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                context,
                                "User not logged in or missing email",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))
                ) {
                    Text(text = "Change Password", fontSize = 16.sp)
                }
            }
        }

        // Bottom Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFEFFFF6))
        ) {
            BottomNavigationBar(
                selectedItem = Routes.PASSWORD_SETTINGS,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}