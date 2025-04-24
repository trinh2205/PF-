package com.example.mainproject.Data.model

// Lưu trữ trong Firebase
data class Category(
    val id: String = "", // Firebase push() key
    val name: String = "",
    val type: String = "", // expense, income, transfer
    val iconId: String = "", // Tên tài nguyên biểu tượng, ví dụ: "ic_food"
    val date: String = "" // Thời gian tạo, định dạng ISO 8601
)