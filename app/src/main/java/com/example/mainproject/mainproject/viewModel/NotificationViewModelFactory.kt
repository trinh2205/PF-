package com.example.mainproject.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mainproject.data.repository.NotificationRepository

class NotificationViewModelFactory(
    private val notificationRepository: NotificationRepository,
    private val userIdProvider: () -> String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(notificationRepository, userIdProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}