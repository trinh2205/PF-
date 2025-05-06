package com.example.mainproject.Data.repository

import android.app.Notification
import com.example.mainproject.Data.model.Notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {
    private val database = FirebaseDatabase.getInstance().reference.child("notifications")

    fun getTransactionNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                for (childSnapshot in snapshot.children) {
                    val notification = childSnapshot.getValue(Notification::class.java)
                    if (notification != null && notification.userId == userId && notification.type == "transaction") {
                        notifications.add(notification.copy(notificationId = childSnapshot.key ?: ""))
                    }
                }
                trySend(notifications)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }
}