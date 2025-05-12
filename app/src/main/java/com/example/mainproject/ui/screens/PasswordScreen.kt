package com.example.mainproject.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.ui.components.BottomNavigationBar

@Composable
fun PasswordSettingsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFF3498DB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                Text("Password Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }

        // Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF4FFF9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PasswordField(label = "Current Password")
                Spacer(modifier = Modifier.height(16.dp))
                PasswordField(label = "New Password")
                Spacer(modifier = Modifier.height(16.dp))
                PasswordField(label = "Confirm New Password")
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { /* TODO: Handle change password */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Change Password", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFEFFFF6))
        ) {
            BottomNavigationBar(
                selectedItem = "", // Set your selected route
                onItemClick = { /* TODO: Handle navigation */ }
            )
        }
    }
}

@Composable
fun PasswordField(label: String) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 15.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFDFF6E7)),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFDFF6E7),
                focusedContainerColor = Color(0xFFDFF6E7),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordSettingsScreenPreview() {
    val navController = rememberNavController()
    PasswordSettingsScreen(navController)
}

@Composable
fun PasswordChangeSuccessScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3498DB)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Vẽ vòng tròn với chấm tròn bên trong
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    drawCircle(
                        color = Color(0xFFDFF6E7),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 8.dp.toPx())
                    )
                }
                Canvas(modifier = Modifier.size(20.dp)) {
                    drawCircle(
                        color = Color(0xFFDFF6E7),
                        radius = size.minDimension / 2
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Password Has Been\nChanged Successfully",
                color = Color(0xFFDFF6E7),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

