package com.example.mainproject.data.model

data class Balance(
    val userId: String = "",
    val currentBalance: Double = 0.0,
    val lastUpdated: String = "" // Thời điểm cập nhật số dư
)