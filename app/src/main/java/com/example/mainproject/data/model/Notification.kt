package com.example.mainproject.data.model

data class Notification(
    var notificationId: String? = null, // ID duy nhất của thông báo (có thể do Firebase tạo hoặc bạn tự tạo)
    val userId: String = "",         // UID của người dùng nhận thông báo
    val title: String = "",          // Tiêu đề ngắn gọn của thông báo
    val body: String = "",           // Nội dung chi tiết của thông báo
    val timestamp: Long = System.currentTimeMillis(), // Thời điểm thông báo được tạo (timestamp Unix)
    val type: String = NotificationType.GENERAL.name, // Loại thông báo (ví dụ: "transaction", "reminder", "ai_alert")
    val isRead: Boolean = false,      // Trạng thái đã đọc của thông báo
    val data: Map<String, String> = emptyMap() // Dữ liệu bổ sung (ví dụ: ID giao dịch, ID nhắc nhở, thông tin cảnh báo AI)
)

enum class NotificationType {
    GENERAL,
    TRANSACTION,
    REMINDER,
    AI_ALERT,
    SYSTEM // Các loại thông báo khác nếu cần
}