package com.example.mainproject.data.model

import java.util.UUID

data class Income(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String = "", // Có thể có danh mục thu nhập
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val message: String = ""
)