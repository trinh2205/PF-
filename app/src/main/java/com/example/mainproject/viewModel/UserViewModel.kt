package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(userId: String, email: String, passwordPlain: String) {
        // Trong thực tế, bạn sẽ lấy mật khẩu đã băm từ cơ sở dữ liệu
        // và so sánh với phiên bản băm của 'passwordPlain'
        val hashedPasswordFromDb = "dummyHashedPassword" // Thay thế bằng mật khẩu thực tế từ DB

        // Giả sử có một hàm băm mật khẩu
        fun hashPassword(password: String): String {
            // Triển khai logic băm mật khẩu an toàn ở đây
            return password // **KHÔNG LÀM VẬY TRONG ỨNG DỤNG THỰC TẾ**
        }

        if (hashPassword(passwordPlain) == hashedPasswordFromDb) {
            _currentUser.value = UserInfo(
                userId = userId,
                email = email,
                passwordHash = hashedPasswordFromDb, // Lưu trữ hash
                isVerified = true, // Giả định đã xác minh
                verificationCode = null,
                budget = 0.0 // Giá trị mặc định, có thể lấy từ DB hoặc cho người dùng nhập sau
            )
            _isLoggedIn.value = true
            _errorMessage.value = null
        } else {
            _errorMessage.value = "Đăng nhập thất bại. Sai email hoặc mật khẩu."
            _isLoggedIn.value = false
        }
    }

    fun register(userId: String, email: String, passwordPlain: String) {
        // Triển khai logic đăng ký: băm mật khẩu, lưu vào DB, gửi mã xác minh...
        val hashedPassword = "hashed_" + passwordPlain // **CHỈ LÀ VÍ DỤ**
        _currentUser.value = UserInfo(
            userId = userId,
            email = email,
            passwordHash = hashedPassword,
            isVerified = false,
            verificationCode = "someVerificationCode",
            budget = 0.0
        )
        _isLoggedIn.value = false // Chưa đăng nhập sau đăng ký, thường chuyển sang màn hình xác minh
        _errorMessage.value = null
        // Gửi mã xác minh (ví dụ)
    }

    fun verifyEmail(verificationCode: String) {
        if (_currentUser.value?.verificationCode == verificationCode) {
            _currentUser.value = _currentUser.value?.copy(isVerified = true, verificationCode = null)
            _errorMessage.value = null
            _isLoggedIn.value = true // Có thể đăng nhập sau khi xác minh
        } else {
            _errorMessage.value = "Mã xác minh không đúng."
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}