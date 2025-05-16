package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Account
import com.example.mainproject.data.model.AccountBank
import com.example.mainproject.data.model.Income
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.UserRepository
import com.example.mainproject.ui.screens.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID // Đảm bảo import UUID
import kotlin.String

class BankViewModel(
    private val userRepository: UserRepository,
    private val userIdProvider: () -> String?,
    private val transactionViewModel: TransactionViewModel // Vẫn giữ lại
) : ViewModel() {
    private val _accountBankInfo = MutableStateFlow<AccountBank?>(null)
    val accountBankInfo: StateFlow<AccountBank?> = _accountBankInfo

    private val _account = MutableStateFlow<Account?>(null)
    val account: StateFlow<Account?> = _account

    private val database = FirebaseDatabase.getInstance()

    init {
        loadAccountBankInfo()
        loadAccount()
    }

    private fun loadAccountBankInfo() {
        viewModelScope.launch {
            userIdProvider()?.let { userId ->
                userRepository.getAccountBankInfo(userId).collectLatest { accountBank ->
                    _accountBankInfo.value = accountBank
                }
            }
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            userIdProvider()?.let { userId ->
                Log.d("AccountLoad", "loadAccount() called for userId: $userId")
                userRepository.getAccount(userId).collectLatest { account ->
                    _account.value = account
                    Log.d("AccountLoad", "Account data loaded: $account")
                }
            } ?: Log.d("AccountLoad", "userIdProvider returned null in loadAccount()")
        }
    }

    fun saveNotification(notification: Notification) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("AppViewModel", "Cannot save notification: No user is currently logged in")
            return
        }

        val notificationId = notification.notificationId ?: UUID.randomUUID().toString()
        val notificationRef = database.getReference("users") // Thay đổi tham chiếu gốc thành "users"
            .child(userId)
            .child("Notifications") // Đổi tên nhánh thành "Notifications" (chữ hoa)
            .child(notificationId)

        val notificationToSave = notification.copy(userId = userId, notificationId = notificationId)

        notificationRef.setValue(notificationToSave)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Saved notification with ID $notificationId for user $userId at users/$userId/Notifications/$notificationId")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error saving notification for user $userId: ${error.message}")
            }
    }

    fun transferFromBankToAccount(amount: Double, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            Log.d("TransferLog", "Attempting to transfer amount: $amount")
            val currentBankInfo = _accountBankInfo.value
            val currentAccount = _account.value
            val userId = userIdProvider()

            Log.d("TransferLog", "Current Bank Info: $currentBankInfo")
            Log.d("TransferLog", "Current Account: $currentAccount")
            Log.d("TransferLog", "User ID: $userId")

            if (currentBankInfo != null && currentAccount != null && userId != null && amount > 0 && currentBankInfo.balance >= amount) {
                Log.d("TransferLog", "Transfer conditions met.")

                // Trừ tiền từ tài khoản ngân hàng
                val newBankBalance = currentBankInfo.balance - amount
                val updatedBankInfo = currentBankInfo.copy(balance = newBankBalance)
                Log.d("TransferLog", "Updated Bank Info: $updatedBankInfo")

                // Cộng tiền vào tài khoản khác (nếu cần)
                val newAccountBalance = currentAccount.balance + amount
                val updatedAccount = currentAccount.copy(balance = newAccountBalance, userId = userId)
                Log.d("TransferLog", "Updated Account: $updatedAccount")

                // Cập nhật cả hai trong Firebase
                userRepository.saveAccountBankInfo(updatedBankInfo) { bankSuccess, bankMessage ->
                    Log.d("TransferLog", "saveAccountBankInfo result - Success: $bankSuccess, Message: $bankMessage")
                    if (bankSuccess) {
                        userRepository.updateAccountBalance(updatedAccount) { accountSuccess, accountMessage ->
                            Log.d("TransferLog", "updateAccountBalance result - Success: $accountSuccess, Message: $accountMessage")
                            if (accountSuccess) {
                                _accountBankInfo.value = updatedBankInfo // Cập nhật state BankInfo
                                _account.value = updatedAccount // Cập nhật state Account

                                // Tạo đối tượng Income
                                val incomeNew = Income(
                                    id = UUID.randomUUID().toString(),
                                    categoryId = "transfer_in", // Sử dụng category ID phù hợp
                                    title = "Chuyển tiền từ ngân hàng",
                                    amount = amount,
                                    date = LocalDate.now().toString(),
                                    message = "Chuyển ${amount} từ tài khoản ngân hàng vào ví"
                                )

                                transactionViewModel.sendNewIncomeNotification(userId, incomeNew)
                                transactionViewModel.saveNewTransactionForIncome(userId, incomeNew)

                                // Tạo và lưu notification sử dụng hàm saveNotification từ AppViewModel
                                val transferNotification = com.example.mainproject.data.model.Notification( // Đảm bảo import đúng model Notification
                                    title = "Chuyển khoản thành công",
                                    body = "Bạn đã chuyển ${amount} từ tài khoản ngân hàng vào ví.",
                                    timestamp = System.currentTimeMillis(),
                                    type = "transfer_successful",
                                    data = mapOf("amount" to amount.toString()),
                                    notificationId = UUID.randomUUID().toString()
                                )
                                saveNotification(transferNotification) // Gọi hàm saveNotification
                                callback(true, "Transfer successful!")
                                Log.d("TransferLog", "Transfer successful!")
                            } else {
                                callback(false, "Transfer failed (updating account): $accountMessage")
                                Log.e("TransferLog", "Transfer failed (updating account): $accountMessage")
                            }
                        }
                    } else {
                        callback(false, "Transfer failed (updating bank info): $bankMessage")
                        Log.e("TransferLog", "Transfer failed (updating bank info): $bankMessage")
                    }
                }
            } else {
                val errorMessage = when {
                    currentBankInfo == null -> "Bank account information not available."
                    currentAccount == null -> "Account information not available."
                    userId == null -> "User not logged in."
                    amount <= 0 -> "Invalid transfer amount."
                    currentBankInfo.balance < amount -> "Insufficient balance in bank account."
                    else -> "Unknown error."
                }
                callback(false, errorMessage)
                Log.e("TransferLog", "Transfer failed - Reason: $errorMessage")
            }
        }
    }

    fun refreshAccount() {
        loadAccount()
    }

    fun saveAccountBankInfo(
        bankCode: String,
        userName: String,
        cardHolderName: String,
        bankName: String,
        balance: Double, // Nhận tham số balance
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            userIdProvider()?.let { userId ->
                val currentAccountBank = _accountBankInfo.value ?: AccountBank(userId = userId, id = UUID.randomUUID().toString()) // Tạo ID mới nếu chưa có
                val updatedAccountBank = currentAccountBank.copy(
                    bankCode = bankCode,
                    userName = userName,
                    cardHolderName = cardHolderName,
                    bankName = bankName,
                    balance = balance
                )
                userRepository.saveAccountBankInfo(updatedAccountBank) { success, message ->
                    callback(success, message)
                }
            } ?: run {
                callback(false, "Người dùng chưa đăng nhập")
            }
        }
    }

    fun refreshAccountBankInfo() {
        loadAccountBankInfo()
    }
}