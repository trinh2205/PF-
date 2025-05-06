package com.example.mainproject.Data.model

data class TransactionFirebase(
    var id: String = "",
    var title: String = "",
    var time: String = "",
    var period: String = "",
    var amount: String = "",
    var isPositive: Boolean = true
)
