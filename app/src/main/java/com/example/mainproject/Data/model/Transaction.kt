package com.example.mainproject.Data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

data class Transaction(
    val id: String =UUID.randomUUID().toString(),
    val title: String = "", // Tên giao dịch (UI)
    val amount: Double = 0.0, // Số tiền (Firebase, tính toán   )
    val type: String = "", // expense, income (Firebase)
    val date: String = "", // Thời gian (Firebase, thay cho time)
    val period: String = "", // monthly, weekly (UI)
    val isPositive: Boolean = false, // true nếu income (UI)
    //@Transient val icon: ImageVector = Icons.Filled.Money
)