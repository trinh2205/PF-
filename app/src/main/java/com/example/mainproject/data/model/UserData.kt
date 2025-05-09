package com.example.mainproject.data.model

data class UserData(
    val profile: UserInfo? = null,
    val categories: Map<String, Category> = emptyMap(),
    val accounts: Map<String, Account> = emptyMap(),
    val transactions: Map<String, Transaction> = emptyMap(),
    val budgets: Map<String, Budget> = emptyMap()
)