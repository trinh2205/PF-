package com.example.mainproject.data.model

import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val message: String = ""
)