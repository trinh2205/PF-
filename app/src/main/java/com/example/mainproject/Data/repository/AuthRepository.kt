package com.example.mainproject.Data.repository

import android.util.Log
import com.example.mainproject.Data.model.Account
import com.example.mainproject.Data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        callback: (Boolean, String?, String?, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    if (uid == null) {
                        callback(false, "User ID is null", null, null)
                        return@addOnCompleteListener
                    }

                    val userInfo = UserInfo(
                        userId = uid,
                        email = email,
                        name = fullName,
                        phone = phone,
                        isVerified = true,
                        createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )

                    val accountId = database.child("users").child(uid).child("accounts").push().key
                    if (accountId == null) {
                        callback(false, "Failed to generate account ID", null, null)
                        Log.e("exit: ","exit here")
                        return@addOnCompleteListener
                    }

                    val defaultAccount = Account(
                        id = accountId,
                        name = fullName,
                        balance = 1000.0,
                        currency = "VND"
                    )

                    // Create user structure
                    val userData = mapOf(
                        "profile" to userInfo,
                        "categories" to emptyMap<String, Any>(),
                        "accounts" to mapOf(accountId to defaultAccount),
                        "transactions" to emptyMap<String, Any>(),
                        "budgets" to emptyMap<String, Any>(),
                        "templates" to emptyMap<String, Any>()
                    )

                    // Save to Realtime Database
                    database.child("users").child(uid)
                        .setValue(userData)
                        .addOnCompleteListener { saveTask ->
                            if (saveTask.isSuccessful) {
                                Log.d("Firebase", "User data saved successfully.")
                                callback(true, null, email, password)
                            } else {
                                Log.e("Task exception: ","Exception" + task.exception?.message)
                                callback(false, saveTask.exception?.message, null, null)
                            }
                        }
                } else {
                    callback(false, task.exception?.message, null, null)
                }
            }
    }

    fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String?, com.google.firebase.auth.FirebaseUser?) -> Unit // Thêm FirebaseUser? vào callback
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message, auth.currentUser) // Truyền auth.currentUser
            }
    }
}