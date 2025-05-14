package com.example.mainproject.data.model

data class Account(
    val id: String = "",
    val name: String = "",
    val balance: Double = 0.0,
    val currency: String = "VND",
    val userId: String = "", // ID của người dùng sở hữu tài khoản này
)