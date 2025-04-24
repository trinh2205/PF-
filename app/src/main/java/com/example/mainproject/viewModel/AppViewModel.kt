package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.Data.model.Transaction
import com.example.mainproject.Data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val database = FirebaseDatabase.getInstance()
    private var userListener: ValueEventListener? = null
    private var categoriesListener: ValueEventListener? = null
    private var transactionsListener: ValueEventListener? = null

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    private val _totalBudget = MutableStateFlow(0.0)
    val totalBudget: StateFlow<Double> = _totalBudget

    private val _transactions = MutableStateFlow<Map<String, List<Transaction>>>(emptyMap())
    val transactions: StateFlow<Map<String, List<Transaction>>> = _transactions

    init {
        Log.d("AppViewModel", "AppViewModel initialized")
        loadUserDataAndExpenses()
    }

    private fun startTransactionsListener(userId: String) {
        val transactionsRef = database.getReference("users").child(userId).child("transactions")
        transactionsListener = transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactionsMap = mutableMapOf<String, List<Transaction>>()

                for (transactionSnapshot in snapshot.children) {
                    val transactionList = mutableListOf<Transaction>()

                    // Giả sử dữ liệu trong transactionSnapshot có cấu trúc như sau:
                    for (transactionChildSnapshot in transactionSnapshot.children) {
                        val transaction = transactionChildSnapshot.getValue(Transaction::class.java)
                        if (transaction != null) {
                            transactionList.add(transaction)
                        }
                    }

                    // Cập nhật giá trị map với khóa là danh mục (hoặc ngày, hoặc ID khác) và giá trị là danh sách giao dịch
                    transactionsMap[transactionSnapshot.key ?: ""] = transactionList
                }

                // Cập nhật StateFlow
                _transactions.value = transactionsMap
                Log.d("AppViewModel", "Transactions updated: $transactionsMap")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading transactions: ${error.message}")
            }
        })
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

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        Log.d("AppViewModel", "User logged out.")
        // Optionally, sign out from Firebase Authentication
        // FirebaseAuth.getInstance().signOut()
    }
}