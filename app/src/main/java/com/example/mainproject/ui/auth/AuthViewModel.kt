package com.example.mainproject.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(email: String, password: String, phone: String, context: Context) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signInWithGoogle(context: Context) {
        Toast.makeText(context, "Chưa tích hợp Google", Toast.LENGTH_SHORT).show()
    }

    fun signInWithFacebook(context: Context) {
        Toast.makeText(context, "Chưa tích hợp Facebook", Toast.LENGTH_SHORT).show()
    }
}
