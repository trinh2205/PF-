package com.example.mainproject.Data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserInfo(
    val userId: String,
    val email: String,
    // Lưu ý: Trong thực tế, bạn sẽ lưu trữ phiên bản đã băm của mật khẩu
    val name:String ,
    val passwordHash: String,
    val isVerified: Boolean,
    val verificationCode: String, // Có thể null sau khi xác minh
    val budget: Double,
    val createdAt: LocalDateTime = LocalDateTime.now() // Sử dụng LocalDateTime mặc định là thời điểm tạo
) {
    // Để dễ hiển thị, có thể thêm getter cho createdAt dưới dạng String
    val createdAtString: String
        get() = createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}