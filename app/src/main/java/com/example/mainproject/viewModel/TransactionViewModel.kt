package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import com.example.mainproject.Data.model.Account
import com.example.mainproject.Data.model.Budget
import com.example.mainproject.Data.model.Category
import com.example.mainproject.Data.model.UserInfo
import com.example.mainproject.Data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
//
class TransactionViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val _accounts = MutableStateFlow<Map<String, Account>>(emptyMap())
    private val _categories = MutableStateFlow<Map<String, Category>>(emptyMap())
    private val _budgets = MutableStateFlow<Map<String, Budget>>(emptyMap())
    private val _transactions = MutableStateFlow<Map<String, Transaction>>(emptyMap())
    private val _totalBalance = MutableStateFlow(0.0)
    private val _totalExpense = MutableStateFlow(0.0)
    private val _totalBudget = MutableStateFlow(0.0)

    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()
    val accounts: StateFlow<Map<String, Account>> = _accounts.asStateFlow()
    val categories: StateFlow<Map<String, Category>> = _categories.asStateFlow()
    val budgets: StateFlow<Map<String, Budget>> = _budgets.asStateFlow()
    val transactions: StateFlow<Map<String, Transaction>> = _transactions.asStateFlow()
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Lấy UserInfo
            database.child("users").child(userId).child("profile")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(UserInfo::class.java)
                        _userInfo.value = user
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi
                    }
                })

            // Lấy Accounts và tính totalBalance
            database.child("users").child(userId).child("accounts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newAccounts = mutableMapOf<String, Account>()
                        var balance = 0.0
                        snapshot.children.forEach { data ->
                            val account = data.getValue(Account::class.java)
                            if (account != null) {
                                newAccounts[data.key!!] = account
                                balance += account.balance
                            }
                        }
                        _accounts.value = newAccounts
                        _totalBalance.value = balance
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi
                    }
                })

            // Lấy Categories
            database.child("users").child(userId).child("categories")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newCategories = mutableMapOf<String, Category>()
                        snapshot.children.forEach { data ->
                            val category = data.getValue(Category::class.java)
                            if (category != null) {
                                newCategories[data.key!!] = category
                            }
                        }
                        _categories.value = newCategories
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi
                    }
                })

            // Lấy Budgets và tính totalBudget
            database.child("users").child(userId).child("budgets")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newBudgets = mutableMapOf<String, Budget>()
                        var totalBudget = 0.0
                        snapshot.children.forEach { data ->
                            val budget = data.getValue(Budget::class.java)
                            if (budget != null) {
                                newBudgets[data.key!!] = budget
                                totalBudget += budget.amount
                            }
                        }
                        _budgets.value = newBudgets
                        _totalBudget.value = totalBudget
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi
                    }
                })

            // Lấy Transactions và tính totalExpense
            database.child("users").child(userId).child("transactions")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newTransactions = mutableMapOf<String, Transaction>()
                        var expense = 0.0
                        snapshot.children.forEach { data ->
                            val transaction = data.getValue(Transaction::class.java)
                            if (transaction != null) {
                                newTransactions[data.key!!] = transaction
                                if (transaction.type == "expense") {
                                    expense += transaction.amount
                                }
                            }
                        }
                        _transactions.value = newTransactions
                        _totalExpense.value = expense
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi
                    }
                })
        } else {
            _userInfo.value = null // Đặt trạng thái mặc định nếu chưa đăng nhập
        }
    }

    fun generateCategoryId(): String {
        return UUID.randomUUID().toString()
    }

    fun addCategory(category: Category) {
        val userId = auth.currentUser?.uid ?: return
        val categoryId = generateCategoryId()
        database.child("users").child(userId).child("categories").child(categoryId)
            .setValue(category.copy(id = categoryId))
    }

    fun addBudget(budget: Budget) {
        val userId = auth.currentUser?.uid ?: return
        val budgetId = UUID.randomUUID().toString()
        database.child("users").child(userId).child("budgets").child(budgetId)
            .setValue(budget.copy(id = budgetId))
    }

    fun addTransaction(transaction: Transaction) {
        val userId = auth.currentUser?.uid ?: return
        val transactionId = UUID.randomUUID().toString()
        database.child("users").child(userId).child("transactions").child(transactionId)
            .setValue(transaction.copy(id = transactionId))
    }
}