package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.UserInfo
import com.example.mainproject.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val appViewModel: AppViewModel, // Nhận AppViewModel
    private val auth: FirebaseAuth // Nhận FirebaseAuth
) : ViewModel() {
    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _pushNotifications = MutableStateFlow(true)
    val pushNotifications: StateFlow<Boolean> = _pushNotifications

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _logoutError = MutableStateFlow<String?>(null)
    val logoutError: StateFlow<String?> = _logoutError

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            userRepository.getUserProfileFlow(userId).collectLatest { userInfo ->
                if (userInfo != null) {
                    _userInfo.value = userInfo
                }
                _isLoading.value = false
            }
        }
    }

    fun updateUserInfo(newUserInfo: UserInfo) {
        _userInfo.value = newUserInfo
    }

    fun updatePushNotifications(enabled: Boolean) {
        _pushNotifications.value = enabled
    }

    fun updateDarkTheme(enabled: Boolean) {
        _darkTheme.value = enabled
    }

    fun saveProfile(onResult: (String) -> Unit) {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            onResult("Vui lòng đăng nhập để cập nhật hồ sơ")
            return
        }

        viewModelScope.launch {
            userRepository.saveUserProfile(userId, _userInfo.value).fold(
                onSuccess = { onResult("Cập nhật hồ sơ thành công") },
                onFailure = { error -> onResult("Lỗi cập nhật: ${error.message}") }
            )
        }
    }

    fun handleUnauthenticated(onNavigateBack: () -> Unit, onShowMessage: (String) -> Unit) {
        if (!userRepository.isAuthenticated()) {
            viewModelScope.launch {
                onShowMessage("Vui lòng đăng nhập để xem hồ sơ")
                onNavigateBack()
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout().fold(
                onSuccess = {
                    _logoutSuccess.value = true
                    _logoutError.value = null
                    appViewModel.logout() // Gọi hàm logout của AppViewModel để gỡ bỏ listeners
                    auth.signOut() // Đăng xuất khỏi Firebase Authentication
                    onLogoutSuccess() // Gọi callback để thông báo cho UI
                },
                onFailure = { error ->
                    _logoutSuccess.value = false
                    _logoutError.value = error.message
                }
            )
            // Không cần reset userInfo ở đây, AppViewModel có thể quản lý
        }
    }

    // Bạn có thể thêm một hàm để reset trạng thái logout nếu cần
    fun resetLogoutState() {
        _logoutSuccess.value = false
        _logoutError.value = null
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            appViewModel: AppViewModel,
            auth: FirebaseAuth
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditProfileViewModel(userRepository, appViewModel, auth) as T
            }
        }
    }
}