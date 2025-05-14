package com.example.mainproject.viewModel.NotificationViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.util.Log
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.NotificationRepository
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

enum class TimeFilter {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    OLDER
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _selectedTimeFilter = MutableStateFlow<TimeFilter?>(null)
    val selectedTimeFilter: StateFlow<TimeFilter?> = _selectedTimeFilter

    init {
        Log.d("NotificationViewModel", "loadNotifications started for user: $userId")
        loadNotifications(userId) // Sử dụng userId đã nhận
    }

    fun loadNotifications(userId: String?) {
        if (userId == null) return
        _isLoading.value = true
        Log.d("NotificationViewModel", "isLoading set to true")

        notificationRepository.getNotificationsForUser(userId)
            .onEach { notificationsList ->
                _notifications.value = filterNotifications(notificationsList, _selectedTimeFilter.value)
                _isLoading.value = false
                Log.d("NotificationViewModel", "isLoading set to false, notifications: $notificationsList")
            }
            .catch { error ->
                Log.e("NotificationViewModel", "Error fetching notifications: $error")
                _isLoading.value = false
                Log.d("NotificationViewModel", "isLoading set to false due to error: $error")
            }
            .launchIn(viewModelScope)
    }

    fun refreshNotifications() {
        _isRefreshing.value = true
        loadNotifications(userId) // Sử dụng userId đã nhận
        _isRefreshing.value = false
    }

    private suspend fun loadNotificationsInternal() {
        userId?.let {
            notificationRepository.getNotificationsForUser(it)
                .collect { fetchedNotifications ->
                    android.util.Log.d("NotificationVM", "Fetched notifications: $fetchedNotifications")
                    _notifications.value = filterNotifications(fetchedNotifications, _selectedTimeFilter.value)
                    android.util.Log.d("NotificationVM", "Filtered notifications: ${_notifications.value}")
                }
        }
    }

    fun setTimeFilter(filter: TimeFilter?) {
        _selectedTimeFilter.value = filter
        loadNotifications(userId)
    }

    private fun filterNotifications(
        notifications: List<Notification>,
        selectedFilter: TimeFilter?
    ): List<Notification> {
        android.util.Log.d("NotificationVM", "Filtering notifications with filter: $selectedFilter")
        if (selectedFilter == null) {
            return notifications // Không có filter, trả về tất cả
        }

        val now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")) // Sử dụng múi giờ Việt Nam
        val filteredList = when (selectedFilter) {
            TimeFilter.TODAY -> notifications.filter {
                isSameDay(it.timestamp, now)
            }
            TimeFilter.YESTERDAY -> notifications.filter {
                isSameDay(it.timestamp, now.minusDays(1))
            }
            TimeFilter.THIS_WEEK -> notifications.filter {
                isWithinThisWeek(it.timestamp, now)
            }
            TimeFilter.THIS_MONTH -> notifications.filter {
                isWithinThisMonth(it.timestamp, now)
            }
            TimeFilter.THIS_YEAR -> notifications.filter {
                isWithinThisYear(it.timestamp, now)
            }
            TimeFilter.OLDER -> notifications.filter {
                isBeforeThisYear(it.timestamp, now)
            }
        }
        android.util.Log.d("NotificationVM", "Result after filtering: $filteredList")
        return filteredList
    }

    private fun isSameDay(timestamp: Long, dateTime: LocalDateTime): Boolean {
        val notificationDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return notificationDateTime.toLocalDate() == dateTime.toLocalDate()
    }

    private fun isWithinThisWeek(timestamp: Long, dateTime: LocalDateTime): Boolean {
        val notificationDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val startOfWeek = dateTime.with(java.time.DayOfWeek.MONDAY).toLocalDate()
        val endOfWeek = dateTime.with(java.time.DayOfWeek.SUNDAY).toLocalDate()
        return !notificationDateTime.toLocalDate().isBefore(startOfWeek) && !notificationDateTime.toLocalDate().isAfter(endOfWeek)
    }

    private fun isWithinThisMonth(timestamp: Long, dateTime: LocalDateTime): Boolean {
        val notificationDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return notificationDateTime.year == dateTime.year && notificationDateTime.month == dateTime.month
    }

    private fun isWithinThisYear(timestamp: Long, dateTime: LocalDateTime): Boolean {
        val notificationDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return notificationDateTime.year == dateTime.year
    }

    private fun isBeforeThisYear(timestamp: Long, dateTime: LocalDateTime): Boolean {
        val notificationDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return notificationDateTime.year < dateTime.year
    }

    companion object {
        fun provideFactory(
            notificationRepository: NotificationRepository,
            userId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                    return NotificationViewModel(notificationRepository, userId) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }

            // Thêm hàm này nếu bạn chưa có
            fun provideRepository(): NotificationRepository {
                return NotificationRepository.create()
            }
        }
    }
}