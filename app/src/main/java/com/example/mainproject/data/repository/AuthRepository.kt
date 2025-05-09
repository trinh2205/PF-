package com.example.mainproject.data.repository

import com.example.mainproject.data.model.Account
import com.example.mainproject.data.model.UserInfo
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
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userInfo = UserInfo(
                        userId = uid,
                        email = email,
                        name = fullName,
                        phone = phone,
                        isVerified = true,
                        createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )

                    val defaultAccount = Account(
                        id = database.child("users").child(uid).child("accounts").push().key ?: "",
                        name = fullName,
                        balance = 1000.0,
                        currency = "VND"
                    )

                    val userData = mapOf(
                        "profile" to userInfo,
                        "categories" to emptyMap<String, Any>(),
                        "accounts" to mapOf(defaultAccount.id to defaultAccount),
                        "transactions" to emptyMap<String, Any>(),
                        "budgets" to emptyMap<String, Any>(),
                        "templates" to emptyMap<String, Any>()
                    )

                    database.child("users").child(uid)
                        .setValue(userData)
                        .addOnCompleteListener { saveTask ->
                            if (saveTask.isSuccessful) {
                                callback(true, null, email, password)
                            } else {
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