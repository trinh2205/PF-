package com.example.mainproject.data.repository

//import TimeFilter
import android.util.Log
import com.example.mainproject.data.model.Notification
import com.example.mainproject.viewModel.NotificationViewModel.TimeFilter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NotificationRepository(private val database: FirebaseDatabase) {

    private val notificationsRef = database.getReference("Notifications")
    private val usersRef = database.getReference("users")

    fun getNotificationsForUser(userId: String): Flow<List<Notification>> = callbackFlow {
        val userNotificationsRef = usersRef.child(userId).child("Notifications")
        Log.d("NotificationRepo", "Fetching notifications for user: $userId at path: ${userNotificationsRef}")

        val listener = userNotificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                Log.d("NotificationRepo", "DataSnapshot for user $userId: $snapshot") // Log toàn bộ snapshot

                for (childSnapshot in snapshot.children) {
                    Log.d("NotificationRepo", "Child snapshot key: ${childSnapshot.key}, value: ${childSnapshot.value}") // Log key và value của mỗi child

                    val notificationId = childSnapshot.key
                    val notification = childSnapshot.getValue(Notification::class.java)

                    if (notification != null && notificationId != null) {
                        notification.notificationId = notificationId
                        notifications.add(notification)
                    }
                }
                notifications.sortByDescending { it.timestamp }
                Log.d("NotificationRepo", "Emitting notifications for user $userId: $notifications") // Log danh sách notifications trước khi emit
                trySend(notifications).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NotificationRepo", "Error fetching notifications for user $userId: ${error.message}")
                close(error.toException())
            }
        })
        awaitClose { userNotificationsRef.removeEventListener(listener) }
    }

    suspend fun saveNotification(notification: Notification) {
        notification.notificationId?.let { notificationId ->
            notificationsRef.child("users")
                .child(notification.userId)
                .child("Notifications")
                .child(notificationId) // Sử dụng notificationId (đã kiểm tra null) làm key
                .setValue(notification)
                .await()
        } ?: run {
            // Xử lý trường hợp notificationId là null (ví dụ: log lỗi, throw exception)
            Log.e("NotificationRepository", "Không thể lưu thông báo vì notificationId là null.")
            // Bạn có thể chọn throw một exception để báo hiệu lỗi này
            // throw IllegalArgumentException("Notification ID cannot be null when saving.")
        }
    }

    private fun filterNotifications(
        notifications: List<Notification>,
        selectedFilter: TimeFilter?
    ): List<Notification> {
        if (selectedFilter == null) {
            return notifications // Không có filter, trả về tất cả
        }

        val now = LocalDateTime.now()
        return when (selectedFilter) {
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

    suspend fun markNotificationAsRead(notificationId: String, userId: String) {
        notificationsRef.child(userId).child(notificationId).child("isRead").setValue(true).await()
    }

    companion object {
        fun create(): NotificationRepository {
            return NotificationRepository(FirebaseDatabase.getInstance())
        }
    }
}