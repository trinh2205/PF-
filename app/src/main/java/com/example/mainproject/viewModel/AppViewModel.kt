package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.Data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val database = FirebaseDatabase.getInstance()
    private var userListener: ValueEventListener? = null
    private var categoriesListener: ValueEventListener? = null

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    init {
        Log.d("AppViewModel", "AppViewModel initialized")
        loadUserDataAndExpenses()
    }

    private fun loadUserDataAndExpenses() {
        val userId = auth.currentUser?.uid
        Log.d("AppViewModel", "Current user ID: $userId")
        if (userId != null) {
            startUserListener(userId)
            startCategoriesListener(userId)
        } else {
            Log.d("AppViewModel", "User not logged in, setting default values.")
            _totalExpense.value = 0.0
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }

    private fun startUserListener(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userListener = userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserInfo::class.java)
                _currentUser.value = user
                _isLoggedIn.value = user != null
                Log.d("AppViewModel", "User data loaded: $user")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading user info: ${error.message}")
            }
        })
    }

    private fun startCategoriesListener(userId: String) {
        val categoriesRef = database.getReference("users").child(userId).child("categories")
        categoriesListener = categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalExpense = 0.0
                for (categorySnapshot in snapshot.children) {
                    val amount = categorySnapshot.child("amount").getValue(Double::class.java) ?: 0.0
                    totalExpense += amount
                }
                _totalExpense.value = totalExpense
                Log.d("AppViewModel", "Total expense updated: $totalExpense")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading expense info: ${error.message}")
            }
        })
    }

    override fun onCleared() {
        userListener?.let {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                database.getReference("users").child(userId).removeEventListener(it)
                Log.d("AppViewModel", "User listener removed for user: $userId")
            }
        }
        categoriesListener?.let {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                database.getReference("users").child(userId).child("categories").removeEventListener(it)
                Log.d("AppViewModel", "Categories listener removed for user: $userId")
            }
        }
        super.onCleared()
    }

    // Không cần hàm setCurrentUser nữa, listener sẽ tự động cập nhật _currentUser

    // Hàm này không cần thiết nếu bạn chỉ dựa vào listener
    // fun fetchCurrentUser() {
    //     val userId = auth.currentUser?.uid
    //     userId?.let {
    //         startUserListener(it)
    //     }
    // }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        Log.d("AppViewModel", "User logged out.")
        // Optionally, sign out from Firebase Authentication
        // FirebaseAuth.getInstance().signOut()
    }
}