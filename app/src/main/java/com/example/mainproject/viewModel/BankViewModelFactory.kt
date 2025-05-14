package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mainproject.data.repository.NotificationRepository
import com.example.mainproject.data.repository.UserRepository

class BankViewModelFactory(
    private val userRepository: UserRepository,
    private val userIdProvider: () -> String?,
    private val notificationRepository: NotificationRepository // Cần để tạo TransactionViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BankViewModel::class.java)) {
            val transactionViewModel = TransactionViewModel(notificationRepository, userIdProvider())
            @Suppress("UNCHECKED_CAST")
            return BankViewModel(userRepository, userIdProvider, transactionViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}