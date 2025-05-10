package com.example.mainproject.Data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@IgnoreExtraProperties
data class UserInfo(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
) {
    // Constructor rỗng cần thiết cho Firebase
    constructor() : this("", "", "", "", false, "")

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "name" to name,
            "phone" to phone,
            "isVerified" to isVerified,
            "createdAt" to createdAt
        )
    }
}