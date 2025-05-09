package com.example.mainproject.data.repository

import com.example.mainproject.data.model.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository(private val database: FirebaseDatabase) {

    private val notificationsRef = database.getReference("notifications") // Thay "notifications" bằng path thực tế

    fun getNotificationsForUser(userId: String): Flow<List<Notification>> = callbackFlow {
        val userNotificationsRef = notificationsRef.child(userId) // Lấy thông báo cho user cụ thể

        val listener = userNotificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                for (childSnapshot in snapshot.children) {
                    val notification = childSnapshot.getValue(Notification::class.java)
                    notification?.let { notifications.add(it) }
                }
                // Sắp xếp theo timestamp (mới nhất trước)
                notifications.sortByDescending { it.timestamp }
                trySend(notifications).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { userNotificationsRef.removeEventListener(listener) }
    }

    public suspend fun saveNotification(notification: Notification) { // Đổi thành public suspend
        notificationsRef.child(notification.userId)
            .push()
            .setValue(notification)
            .await() // Thêm await nếu bạn muốn đợi thao tác hoàn thành
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