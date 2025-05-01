package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.Data.model.Account
import com.example.mainproject.Data.model.Transaction
import com.example.mainproject.Data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {

    // StateFlow cho thông tin người dùng và trạng thái đăng nhập
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // StateFlow cho dữ liệu tài chính tổng quan
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance

    private val _totalBudget = MutableStateFlow(0.0)
    val totalBudget: StateFlow<Double> = _totalBudget

    // StateFlow cho danh sách giao dịch
    private val _transactions = MutableStateFlow<Map<String, List<Transaction>>>(emptyMap())
    val transactions: StateFlow<Map<String, List<Transaction>>> = _transactions

    private val database = FirebaseDatabase.getInstance()
    private var userId: String? = auth.currentUser?.uid

    // Listeners
    private var userListener: ValueEventListener? = null
    private var transactionsListener: ValueEventListener? = null
    private var accountsListener: ValueEventListener? = null
    private var budgetsListener: ValueEventListener? = null // Thêm listener cho budget

    init {
        Log.d("AppViewModel", "AppViewModel initialized for user: $userId")
        loadInitialData()
    }

    private fun loadInitialData() {
        userId?.let { uid ->
            startUserListener(uid)
            startTransactionsListener(uid)
            startAccountsListener(uid)
            startBudgetsListener(uid) // Bắt đầu lắng nghe budget
        } ?: run {
            Log.d("AppViewModel", "User not logged in, setting default values.")
            _totalExpense.value = 0.0
            _totalBalance.value = 0.0
            _totalBudget.value = 0.0
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }

    private fun startUserListener(userId: String) {
        val userRef = database.getReference("users").child(userId).child("profile")
        userListener = userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserInfo::class.java)
                _currentUser.value = user
                _isLoggedIn.value = user != null
                Log.d("AppViewModel", "User data loaded in startUserListener: $user") // Thêm log
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading user info: ${error.message}")
            }
        })
    }

    private fun startTransactionsListener(userId: String) {
        val transactionsRef = database.getReference("users").child(userId).child("transactions")
        transactionsListener = transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalExpense = 0.0
                val transactionsMap = mutableMapOf<String, MutableList<Transaction>>() // Để log transactions
                snapshot.children.forEach { transactionSnapshot ->
                    val transactionListForDate = mutableListOf<Transaction>()
                    transactionSnapshot.children.forEach { transactionChildSnapshot ->
                        val transaction = transactionChildSnapshot.getValue(Transaction::class.java)
                        transaction?.let {
                            if (!it.isPositive) {
                                totalExpense += it.amount
                            }
                            transactionListForDate.add(it)
                        }
                    }
                    transactionsMap[transactionSnapshot.key.toString()] = transactionListForDate
                }
                _totalExpense.value = totalExpense
                _transactions.value = transactionsMap // Cập nhật transactions để có dữ liệu log
                Log.d("AppViewModel", "Transactions data loaded in startTransactionsListener: ${_transactions.value}") // Thêm log
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading transactions: ${error.message}")
            }
        })
    }

    private fun startAccountsListener(userId: String) {
        val accountsRef = database.getReference("users").child(userId).child("accounts")
        accountsListener = accountsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalBalance = 0.0
                val accountsList = mutableListOf<Account>() // Để log accounts
                snapshot.children.forEach { accountSnapshot ->
                    val account = accountSnapshot.getValue(Account::class.java)
                    account?.let {
                        totalBalance += it.balance
                        accountsList.add(it)
                    }
                }
                _totalBalance.value = totalBalance
                Log.d("AppViewModel", "Accounts data loaded in startAccountsListener: $accountsList") // Thêm log
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading accounts: ${error.message}")
            }
        })
    }

    private fun startBudgetsListener(userId: String) {
        val budgetsRef = database.getReference("users").child(userId).child("budgets")
        budgetsListener = budgetsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalBudget = 0.0
                val budgetsMap = mutableMapOf<String, Double>() // Để log budgets
                snapshot.children.forEach { budgetSnapshot ->
                    val budgetAmount = budgetSnapshot.child("amount").getValue(Double::class.java) ?: 0.0
                    totalBudget += budgetAmount
                    budgetsMap[budgetSnapshot.key.toString()] = budgetAmount
                }
                _totalBudget.value = totalBudget
                Log.d("AppViewModel", "Budgets data loaded in startBudgetsListener: $budgetsMap") // Thêm log
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading budgets: ${error.message}")
            }
        })
    }

    override fun onCleared() {
        userId?.let { uid ->
            database.getReference("users").child(uid).removeEventListener(userListener ?: return@let)
            database.getReference("users").child(uid).child("transactions").removeEventListener(transactionsListener ?: return@let)
            database.getReference("users").child(uid).child("accounts").removeEventListener(accountsListener ?: return@let)
            database.getReference("users").child(uid).child("budgets").removeEventListener(budgetsListener ?: return@let)
            Log.d("AppViewModel", "Listeners removed for user: $uid")
        }
        super.onCleared()
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _totalExpense.value = 0.0
        _totalBalance.value = 0.0
        _totalBudget.value = 0.0
        Log.d("AppViewModel", "User logged out.")
        // Optionally, sign out from Firebase Authentication
        // FirebaseAuth.getInstance().signOut()
    }

    fun createDefaultAccount(userId: String) {
        val defaultAccount = mapOf(
            "id" to "default_acc",
            "name" to "Tài khoản mặc định",
            "balance" to 1000.0,
            "currency" to "VND"
        )

        database.getReference("users").child(userId).child("accounts").child("default_acc")
            .setValue(defaultAccount)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Tài khoản mặc định đã được tạo cho người dùng: $userId")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Lỗi khi tạo tài khoản mặc định cho người dùng $userId: ${error.message}")
            }
    }
}