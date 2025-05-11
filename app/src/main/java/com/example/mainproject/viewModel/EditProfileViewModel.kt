package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.UserInfo
import com.example.mainproject.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileViewModel(private val userRepository: UserRepository = UserRepository()) : ViewModel() {
    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _pushNotifications = MutableStateFlow(true)
    val pushNotifications: StateFlow<Boolean> = _pushNotifications

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme

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
}