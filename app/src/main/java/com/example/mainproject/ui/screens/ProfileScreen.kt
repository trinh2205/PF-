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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.R
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.EditProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    navController: NavController,
    appViewModel: AppViewModel = viewModel(),
    editProfileViewModel: EditProfileViewModel = viewModel()
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val userInfoState = editProfileViewModel.userInfo.collectAsState()
    val userInfo = userInfoState.value

    val logoutSuccessState = editProfileViewModel.logoutSuccess.collectAsState()
    val logoutErrorState = editProfileViewModel.logoutError.collectAsState()

    // Theo dõi trạng thái logout thành công và điều hướng
    if (logoutSuccessState.value) {
        LaunchedEffect(Unit) {
            navController.navigate(route = Routes.SIGN_IN) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            editProfileViewModel.resetLogoutState() // Reset trạng thái sau khi điều hướng
        }
    }

    // Hiển thị thông báo lỗi logout nếu có
    logoutErrorState.value?.let { errorMessage ->
        // Đây là một ví dụ đơn giản, bạn có thể sử dụng Dialog hoặc Snackbar để hiển thị lỗi tốt hơn
        Text(text = "Lỗi đăng xuất: $errorMessage", color = Color.Red)
        LaunchedEffect(errorMessage) {
            delay(3000) // Hiển thị lỗi trong 3 giây rồi reset
            editProfileViewModel.resetLogoutState()
        }
    }

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
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notification",
                    tint = Color.White
                )
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
                Text(
                    text = userInfo.name.ifEmpty { "Unknown User" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "ID: ${userInfo.userId.ifEmpty { "N/A" }}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Edit Profile Option
                ProfileOption(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    onClick = {
                        navController.navigate(Routes.EDIT_PROFILES) {
                            launchSingleTop = true
                        }
                    }
                )

                // Settings Option
                ProfileOption(
                    icon = Icons.Default.AccountBalance,
                    title = "Bank",
                    onClick = {
                        navController.navigate(Routes.SAVE_BANK) {
                            launchSingleTop = true
                        }
                    }
                )

                ProfileOption(
                    icon = Icons.Default.Settings,
                    title = "Bank",
                    onClick = {
                        navController.navigate(Routes.SETTINGS) {
                            launchSingleTop = true
                        }
                    }
                )

                // Logout Option
                ProfileOption(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    onClick = {
                        // Gọi hàm logout đã sửa trong EditProfileViewModel
                        editProfileViewModel.logout {
                            navController.navigate(route = Routes.SIGN_IN) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
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
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null
) {
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
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF3498DB),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
    }
}