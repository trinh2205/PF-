package com.example.mainproject.Data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Transaction(
    val title: String,
    val time: String,
    val period: String,
    val amount: String,
    val isPositive: Boolean,
    val icon: ImageVector
)