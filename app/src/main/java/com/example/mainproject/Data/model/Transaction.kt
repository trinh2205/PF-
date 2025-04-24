package com.example.mainproject.Data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.ui.graphics.vector.ImageVector

data class Transaction(
    val id: String = "", // Firebase key
    val title: String = "", // Tên giao dịch (UI)
    val amount: Double = 0.0, // Số tiền (Firebase, tính toán)
    val type: String = "", // expense, income (Firebase)
    val date: String = "", // Thời gian (Firebase, thay cho time)
    val period: String = "", // monthly, weekly (UI)
    val isPositive: Boolean = false, // true nếu income (UI)
    val icon: ImageVector = Icons.Filled.Money // Biểu tượng (UI)
)