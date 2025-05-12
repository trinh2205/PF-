package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.R
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.NavigationItem

@Composable
fun ProfileScreen(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                Icon(Icons.Default.Notifications, contentDescription = "Notification", tint = Color.White)
            }
        }

        // White rounded background
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Châu Trinh", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("ID: 22052005", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                ProfileOption(icon = Icons.Default.Person, title = "Edit Profile", showArrow = true) {
                    navController.navigate(Routes.EDIT_PROFILE)
                }
                ProfileOption(icon = Icons.Default.Settings, title = "Settings", showArrow = true) {
                    navController.navigate(Routes.SETTINGS)
                }
                ProfileOption(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Logout",
                    showArrow = true
                ) {
                    showLogoutDialog = true
                }
            }
        }

        // Avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.LightGray, CircleShape)
                    .shadow(4.dp, CircleShape)
            )
        }

        // Bottom Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFEFFFF6))
        ) {
            BottomNavigationBar(
                selectedItem = currentRoute ?: NavigationItem.DefaultItems.first().route,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Hiển thị dialog và làm mờ nền
        if (showLogoutDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
                    .wrapContentSize(Alignment.Center)
            ) {
                EndSessionDialog(
                    onConfirm = {
                        // Handle confirm
                    },
                    onCancel = {
                        showLogoutDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, title: String, showArrow: Boolean, onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick?.invoke() }
            .padding(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF3498DB), modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
        if (showArrow) {
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material.Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.clickable { onClick?.invoke() }
            )
        }
    }
}

@Composable
fun EndSessionDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(24.dp)
            .width(320.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "End Session",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF22313F)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Are you sure you want to log out?",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
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
                Text("Yes, End Session", color = Color.White, fontSize = 16.sp)
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
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController)
}
