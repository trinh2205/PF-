package com.example.mainproject.data.model

import androidx.compose.ui.graphics.vector.ImageVector

// Dùng cho UI
data class ListCategories(
    val id: String = "", // Giá trị mặc định là một chuỗi rỗng
    val name: String = "", // Giá trị mặc định là một chuỗi rỗng
    val date: String = "", // Giá trị mặc định là một chuỗi rỗng
    val icon: String? = null, // Giá trị mặc định là null
    val categories: Map<String, Category> = emptyMap() // Giá trị mặc định là một map rỗng
)