package com.example.mainproject.data.model

data class TransactionBE(
    val id: String = "", // Firebase key
    val categoryId: String = "", // Liên kết với Category.id
    val title: String = "", // Tên giao dịch
    val amount: Double = 0.0, // Số tiền
    val type: String = "", // "expense" hoặc "income"
    val date: String = "", // Thời gian giao dịch (ISO 8601)
    val message: String = "" // Mô tả thêm (tùy chọn)
)