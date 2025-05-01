package com.example.mainproject.mainproject.Data.model

// Ngân sách cho danh mục
data class Budget(
    val id: String = "",
    val categoryId: String = "", // Liên kết với Category.id
    val amount: Double = 0.0,
    val period: String = "", // monthly, weekly, yearly
    val startDate: String = "",
    val endDate: String = ""
)