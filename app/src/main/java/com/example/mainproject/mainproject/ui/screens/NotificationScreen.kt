package com.example.mainproject.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mainproject.R // Import R của ứng dụng bạn
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.NotificationRepository
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.NotificationViewModel
import com.example.mainproject.viewModel.NotificationViewModelFactory

@Composable
fun NotificationScreen(navController: NavController, appViewModel: AppViewModel) {
    val userId = appViewModel.currentUser.collectAsState().value?.userId ?: ""
    val notificationRepository = remember { NotificationRepository() }
    val userIdProvider = remember(userId) { { userId } }
    val viewModel: NotificationViewModel = viewModel(
        factory = remember(notificationRepository, userIdProvider) {
            NotificationViewModelFactory(notificationRepository = notificationRepository, userIdProvider = userIdProvider)
        }
    )
    val transactionNotifications = viewModel.transactionNotifications.collectAsState().value // Sửa lỗi 1

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 40.dp)
                    .background(color = colorResource(id = R.color.mainColor)) // Sửa lỗi 2
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Notifications",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 60.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(transactionNotifications) { notification -> // Đảm bảo lambda nhận đúng kiểu Notification
                NotificationItem(notification = notification) // Sửa lỗi 3 (ngầm)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(text = notification.title, fontSize = 18.sp)
        Text(text = notification.body, fontSize = 14.sp, color = Color.Gray)
        Text(text = notification.timestamp.toString(), fontSize = 12.sp, color = Color.LightGray)
    }
}