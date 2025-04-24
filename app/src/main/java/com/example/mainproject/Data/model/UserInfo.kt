package com.example.mainproject.Data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserInfo(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val verificationCode: String = "",
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)