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
fun DeleteAccountScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Làm mờ nền khi dialog hiện
        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
            )
        }

        // Nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4FFF9))
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    Text("Delete Account", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Are You Sure You Want To Delete\nYour Account?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Box cảnh báo
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFDFF6E7))
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Column {
                    Text(
                        "This action will permanently delete all of your data, and you will not be able to recover it. Please keep the following in mind before proceeding:",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5C5C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• All your expenses, income and associated transactions will be eliminated.", fontSize = 13.sp, color = Color(0xFF4B5C5C))
                    Text("• You will not be able to access your account or any related information.", fontSize = 13.sp, color = Color(0xFF4B5C5C))
                    Text("• This action cannot be undone.", fontSize = 13.sp, color = Color(0xFF4B5C5C))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Please Enter Your Password To Confirm\nDeletion Of Your Account.",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                placeholder = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFDFF6E7)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFDFF6E7),
                    focusedContainerColor = Color(0xFFDFF6E7),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nút xác nhận và hủy
            Button(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text("Yes, Delete Account", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* TODO: Handle cancel */ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFDFF6E7)),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text("Cancel", color = Color(0xFF4B5C5C), fontSize = 16.sp)
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

        // Dialog xác nhận
        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
                    .wrapContentSize(Alignment.Center)
            ) {
                DeleteAccountDialog(
                    onConfirm = { /* TODO: Handle delete */ showDialog = false },
                    onCancel = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun DeleteAccountDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(24.dp)
            .width(320.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Delete Account", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Are You Sure You Want To Log Out?",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "By deleting your account, you agree that you understand the consequences of this action and that you agree to permanently delete your account and all associated data.",
                fontSize = 14.sp,
                color = Color(0xFF4B5C5C),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Yes, Delete Account", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFDFF6E7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Cancel", color = Color(0xFF4B5C5C), fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteAccountScreenPreview() {
    val navController = rememberNavController()
    DeleteAccountScreen(navController)
}