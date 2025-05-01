package com.example.mainproject.mainproject.Data.repository

import com.example.mainproject.Data.model.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val database: FirebaseDatabase
){
    private val usersRef = database.getReference("users")

    fun getUserById(userId: String) : Flow<UserInfo> = callbackFlow {
        val userRef = usersRef.child(userId)
        val listener = object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
                val userInfo = snapshot.getValue(UserInfo::class.java)
                userInfo?.let { trySend(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                close(error.toException())
            }
        }
        userRef.addValueEventListener(listener)
        awaitClose {
            userRef.removeEventListener(listener)
        }
    }
}