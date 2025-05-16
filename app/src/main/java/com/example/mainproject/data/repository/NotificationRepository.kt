package com.example.mainproject.data.repository

//import TimeFilter
import android.util.Log
import com.example.mainproject.data.model.Notification
import com.example.mainproject.ui.screens.auth
import com.example.mainproject.viewModel.NotificationViewModel.TimeFilter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class NotificationRepository(private val database: FirebaseDatabase) {

    private val usersRef = database.getReference("users")

    fun getNotificationsForUser(userId: String): Flow<List<Notification>> = callbackFlow {
        val userNotificationsRef = usersRef.child(userId).child("Notifications")
        Log.d("NotificationRepo", "Fetching notifications for user: $userId at path: ${userNotificationsRef}")

        val listener = userNotificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                Log.d("NotificationRepo", "DataSnapshot for user $userId: $snapshot")

                for (childSnapshot in snapshot.children) {
                    Log.d("NotificationRepo", "Child snapshot key: ${childSnapshot.key}, value: ${childSnapshot.value}")

                    val notificationId = childSnapshot.key
                    val notification = childSnapshot.getValue(Notification::class.java)

                    if (notification != null && notificationId != null) {
                        notification.notificationId = notificationId
                        notifications.add(notification)
                    }
                }
                notifications.sortByDescending { it.timestamp }
                Log.d("NotificationRepo", "Emitting notifications for user $userId: $notifications")
                trySend(notifications).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NotificationRepo", "Error fetching notifications for user $userId: ${error.message}")
                close(IOException("Failed to fetch notifications for user $userId: ${error.message}", error.toException()))
            }
        })
        awaitClose { userNotificationsRef.removeEventListener(listener) }
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

    suspend fun markNotificationAsRead(notificationId: String, userId: String) {
        try {
            usersRef.child(userId)
                .child("Notifications")
                .child(notificationId)
                .child("isRead")
                .setValue(true)
                .await()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Lỗi khi đánh dấu thông báo đã đọc: ${e.message}")
            throw IOException("Failed to mark notification as read: ${e.message}", e)
        }
    }

    companion object {
        fun create(): NotificationRepository {
            return NotificationRepository(FirebaseDatabase.getInstance())
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
}