package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
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
import com.example.mainproject.Data.model.UserInfo
import com.example.mainproject.R
import com.example.mainproject.viewModel.EditProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfile(navController: NavController, viewModel: EditProfileViewModel = viewModel()) {
    val userInfoState = viewModel.userInfo.collectAsState()
    val isLoadingState = viewModel.isLoading.collectAsState()

    val userInfo = userInfoState.value
    val isLoading = isLoadingState.value

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

                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // Nền trắng bo góc + nội dung
        EditProfileBackgroundBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp),
            userInfo = userInfo,
            onUserInfoChange = { viewModel.updateUserInfo(it) },
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

        // Snackbar
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Text("Đang tải dữ liệu...", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Text(
                    userInfo.name.ifEmpty { "Châu Trinh" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    "ID: ${userInfo.userId.ifEmpty { "22052005" }}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Cài đặt Tài khoản",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0A2F35)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Column {
                        Text(
                            text = "Tên người dùng",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextField(
                            value = userInfo.name,
                            onValueChange = { onUserInfoChange(userInfo.copy(name = it)) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFDFF7E7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                textColor = Color.Black
                            ),
                            textStyle = TextStyle(fontSize = 15.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Số điện thoại",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextField(
                            value = userInfo.phone,
                            onValueChange = { onUserInfoChange(userInfo.copy(phone = it)) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFDFF7E7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                textColor = Color.Black
                            ),
                            textStyle = TextStyle(fontSize = 15.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Địa chỉ Email",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextField(
                            value = userInfo.email,
                            onValueChange = { onUserInfoChange(userInfo.copy(email = it)) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFDFF7E7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                textColor = Color.Black
                            ),
                            textStyle = TextStyle(fontSize = 15.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onUpdateProfile,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3498DB)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp)
                ) {
                    Text("Cập nhật Hồ sơ", color = Color.White, fontSize = 17.sp)
                }
            }
        }
    }
}
