package com.example.mainproject.mainproject.Data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserInfo(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)