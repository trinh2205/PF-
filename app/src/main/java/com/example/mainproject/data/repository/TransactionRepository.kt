package com.example.mainproject.data.repository

import com.example.mainproject.data.model.Category

interface TransactionRepository {
    suspend fun getAllTransactions(): List<Category>
    suspend fun getTransactionsByCategoryName(categoryName: String): List<Category>
    suspend fun addTransaction(category: Category): String? // Trả về ID hoặc null nếu thất bại
    suspend fun updateTransaction(category: Category): Boolean
    suspend fun deleteTransaction(transactionId: String): Boolean
    suspend fun getTransactionById(transactionId: String): Category?
    // Các hàm để lấy giao dịch theo tháng, khoảng thời gian, v.v.
}