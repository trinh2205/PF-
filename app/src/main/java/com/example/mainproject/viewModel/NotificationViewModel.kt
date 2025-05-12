package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class TimeFilter {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    OLDER
}

fun Notification.isWithinTimeFilter(filter: TimeFilter): Boolean {
    val notificationDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    val now = LocalDate.now()
    return when (filter) {
        TimeFilter.TODAY -> notificationDate.isEqual(now)
        TimeFilter.YESTERDAY -> notificationDate.isEqual(now.minusDays(1))
        TimeFilter.THIS_WEEK -> !notificationDate.isBefore(now.minusWeeks(1).plusDays(1)) && !notificationDate.isAfter(now)
        TimeFilter.THIS_MONTH -> notificationDate.year == now.year && notificationDate.month == now.month
        TimeFilter.THIS_YEAR -> notificationDate.year == now.year
        TimeFilter.OLDER -> notificationDate.isBefore(now.minusYears(1))
    }
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedTimeFilter = MutableStateFlow<TimeFilter?>(null)
    val selectedTimeFilter: StateFlow<TimeFilter?> = _selectedTimeFilter.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userId != null) {
                    notificationRepository.getNotificationsForUser(userId)
                        .collect { fetchedNotifications ->
                            val filteredByTime = if (_selectedTimeFilter.value != null) {
                                fetchedNotifications.filter { it.isWithinTimeFilter(_selectedTimeFilter.value!!) }
                            } else {
                                fetchedNotifications
                            }

                            _notifications.value = if (_selectedTag.value != null) {
                                filteredByTime.filter { it.type == _selectedTag.value }
                            } else {
                                filteredByTime
                            }
                            _isLoading.value = false
                        }
                } else {
                    _notifications.value = emptyList()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Xử lý lỗi
            }
        }
    }

    fun setTimeFilter(filter: TimeFilter?) {
        _selectedTimeFilter.update { filter }
        loadNotifications()
    }

    fun setFilterTag(tag: String?) {
        _selectedTag.update { tag }
        loadNotifications()
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
        }
    }
}