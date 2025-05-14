package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mainproject.data.repository.NotificationRepository
import com.example.mainproject.data.repository.TransactionRepository

class TransactionViewModelFactory(
    private val notificationRepository: NotificationRepository,
    private val transactionRepository: TransactionRepository? = null, // TransactionRepository có thể là null nếu ViewModel không cần nó trực tiếp
    private val userId: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}