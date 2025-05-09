package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        userId?.let { fetchNotifications(it) }
    }

    private fun fetchNotifications(userId: String) {
        viewModelScope.launch {
            notificationRepository.getNotificationsForUser(userId)
                .collectLatest { fetchedNotifications ->
                    _notifications.value = fetchedNotifications
                }
        }
    }

    fun markNotificationAsRead(notification: Notification) {
        userId?.let {
            viewModelScope.launch {
                notificationRepository.markNotificationAsRead(notification.notificationId, it)
            }
        }
    }

    companion object {
        fun provideFactory(
            notificationRepository: NotificationRepository,
            userId: String?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                    return NotificationViewModel(notificationRepository, userId) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}