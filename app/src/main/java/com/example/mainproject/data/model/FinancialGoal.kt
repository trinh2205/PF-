package com.example.mainproject.data.model

data class FinancialGoal(
    val id: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val startDate: String = "",
    val endDate: String = ""
    // Có thể thêm trạng thái (completed, inProgress)
)