package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // Biến isLoading cho NotificationViewModel
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userId != null) {
                    notificationRepository.getNotificationsForUser(userId).collect { fetchedNotifications ->
                        _notifications.value = fetchedNotifications
                        _isLoading.value = false
                    }
                } else {
                    _notifications.value = emptyList()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Xử lý lỗi tải dữ liệu
                _isLoading.value = false
                // Có thể thêm log lỗi ở đây
            }
        }
    }

    companion object {
        fun provideFactory(
            notificationRepository: NotificationRepository,
            userId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                    return NotificationViewModel(notificationRepository, userId) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}