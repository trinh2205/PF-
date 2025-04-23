package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.mainproject.Data.model.UserInfo
import java.time.LocalDateTime

class UserViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Hàm này sẽ được gọi khi người dùng đăng nhập thành công
    fun login(
        userId: String,
        userName:String,
        email: String,
        passwordHash: String, // Nhận mật khẩu đã băm
        isVerified: Boolean,
        verificationCode: String,
        budget: Double,
        createdAt: LocalDateTime
    ) {
        _currentUser.value = UserInfo(
            name = userName,
            userId = userId,
            email = email,
            passwordHash = passwordHash,
            isVerified = isVerified,
            verificationCode = verificationCode,
            budget = budget,
            createdAt = createdAt
        )
        _isLoggedIn.value = true
        _errorMessage.value = null
    }

    // Hàm này có thể được gọi khi cần cập nhật thông tin người dùng (ví dụ: cập nhật budget)
    fun updateUserInfo(updatedUserInfo: UserInfo) {
        _currentUser.value = updatedUserInfo
    }

    // Hàm này có thể được gọi khi cần cập nhật trạng thái đã xác minh
    fun updateUserVerificationStatus(isVerified: Boolean) {
        _currentUser.value = _currentUser.value?.copy(isVerified = isVerified)
    }

    // Hàm này có thể được gọi khi cần cập nhật ngân sách
    fun updateUserBudget(newBudget: Double) {
        _currentUser.value = _currentUser.value?.copy(budget = newBudget)
    }

    // Hàm này sẽ được gọi khi người dùng đăng xuất
    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    // Hàm để đặt thông báo lỗi (ví dụ: lỗi đăng nhập)
    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    // Hàm để xóa thông báo lỗi
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}