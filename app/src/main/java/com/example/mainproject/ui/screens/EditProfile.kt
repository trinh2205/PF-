package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mainproject.data.model.UserInfo
import com.example.mainproject.R
import com.example.mainproject.viewModel.EditProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfile(navController: NavController, viewModel: EditProfileViewModel = viewModel()) {
    val userInfoState = viewModel.userInfo.collectAsState()
    val isLoadingState = viewModel.isLoading.collectAsState()
    val pushNotificationsState = viewModel.pushNotifications.collectAsState()
    val darkThemeState = viewModel.darkTheme.collectAsState()

    val userInfo = userInfoState.value
    val isLoading = isLoadingState.value
    val pushNotifications = pushNotificationsState.value
    val darkTheme = darkThemeState.value

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Xử lý trường hợp chưa đăng nhập
    LaunchedEffect(Unit) {
        viewModel.handleUnauthenticated(
            onNavigateBack = { navController.popBackStack() },
            onShowMessage = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Nền xanh phía trên
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF3498DB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Hồ sơ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }

        // Nền trắng bo góc
        EditProfileBackgroundBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp),
            userInfo = userInfo,
            onUserInfoChange = { viewModel.updateUserInfo(it) },
            pushNotifications = pushNotifications,
            onPushNotificationsChange = { viewModel.updatePushNotifications(it) },
            darkTheme = darkTheme,
            onDarkThemeChange = { viewModel.updateDarkTheme(it) },
            onUpdateProfile = {
                viewModel.saveProfile { message ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            },
            isLoading = isLoading
        )

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

        // Snackbar để hiển thị thông báo
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun EditProfileBackgroundBar(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    onUserInfoChange: (UserInfo) -> Unit,
    pushNotifications: Boolean,
    onPushNotificationsChange: (Boolean) -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onUpdateProfile: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFF4FFF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Text("Đang tải dữ liệu...", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                // Tên và ID
                Text(
                    userInfo.name.ifEmpty { "Châu Trinh" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    "ID: ${userInfo.userId.ifEmpty { "22052005" }}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Tiêu đề Cài đặt Tài khoản
                Text(
                    text = "Cài đặt Tài khoản",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0A2F35),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Tên người dùng
                Text(
                    text = "Tên người dùng",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = userInfo.name,
                    onValueChange = { onUserInfoChange(userInfo.copy(name = it)) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xFFDFF7E7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = Color.Black
                    ),
                    textStyle = TextStyle(fontSize = 10.sp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Số điện thoại
                Text(
                    text = "Số điện thoại",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = userInfo.phone,
                    onValueChange = { onUserInfoChange(userInfo.copy(phone = it)) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xFFDFF7E7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = Color.Black
                    ),
                    textStyle = TextStyle(fontSize = 10.sp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Email
                Text(
                    text = "Địa chỉ Email",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = userInfo.email,
                    onValueChange = { onUserInfoChange(userInfo.copy(email = it)) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xFFDFF7E7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = Color.Black
                    ),
                    textStyle = TextStyle(fontSize = 10.sp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Công tắc Thông báo đẩy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thông báo đẩy", fontSize = 16.sp)
                    Switch(
                        checked = pushNotifications,
                        onCheckedChange = onPushNotificationsChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3498DB))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Công tắc Giao diện tối
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bật Giao diện Tối", fontSize = 16.sp)
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onDarkThemeChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3498DB))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Nút Cập nhật Hồ sơ
                Button(
                    onClick = onUpdateProfile,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3498DB)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Cập nhật Hồ sơ", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}