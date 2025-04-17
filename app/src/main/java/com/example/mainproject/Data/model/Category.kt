package com.example.mainproject.Data.model

data class Category(
    var id: Int? = null,
    var title: String? = null,
    var type: List<ListCategories>? = null,
    var detail: String? = null,
    var amount: Double? = null,
    var date: String? = null
)