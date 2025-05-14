package com.example.mainproject.data.model

data class AccountBank(
    val id: String = "",
    val userId: String = "", // ID của người dùng sở hữu thông tin ngân hàng này
    val accountId: String = "", // ID của Account mà thông tin ngân hàng này liên kết đến
    val bankCode: String = "",
    val userName: String = "",
    val cardHolderName: String = "",
    val bankName: String = "",
    val balance: Double = 0.0 // Thêm trường số dư cho tài khoản ngân hàng cụ thể
)