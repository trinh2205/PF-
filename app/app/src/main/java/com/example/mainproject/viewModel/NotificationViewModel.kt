package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    // Cách bạn lấy userId mà không dùng Hilt
    // Ví dụ: có thể truyền userId khi tạo ViewModel hoặc lấy từ một StateFlow/LiveData toàn cục
    private val userIdProvider: () -> String = { "userUid1" } // Placeholder
) : ViewModel() {

    private val _transactionNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val transactionNotifications: StateFlow<List<Notification>> = _transactionNotifications

    init {
        fetchTransactionNotifications(userIdProvider())
    }

    private fun fetchTransactionNotifications(userId: String) {
        viewModelScope.launch {
            notificationRepository.getTransactionNotifications(userId)
                .collectLatest { notifications ->
                    _transactionNotifications.value = notifications
                }
        }
    }
}