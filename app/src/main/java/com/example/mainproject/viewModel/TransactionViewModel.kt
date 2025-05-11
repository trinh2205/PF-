package com.example.mainproject.viewModel

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.Data.model.Account
import com.example.mainproject.Data.model.Budget
import com.example.mainproject.Data.model.Category
import com.example.mainproject.Data.model.Expense
import com.example.mainproject.Data.model.UserInfo
import com.example.mainproject.Data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID


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
    private var _expenses = mutableStateOf<Map<String, List<Expense>>>(emptyMap())
    private val _transactionBE = MutableStateFlow<List<Transaction>>(emptyList())

    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()
    val accounts: StateFlow<Map<String, Account>> = _accounts.asStateFlow()
    val categories: StateFlow<Map<String, Category>> = _categories.asStateFlow()
    val budgets: StateFlow<Map<String, Budget>> = _budgets.asStateFlow()
    val transactions: StateFlow<Map<String, Transaction>> = _transactions.asStateFlow()
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()
    val expenses: State<Map<String, List<Expense>>> = _expenses
    val transactionBE: StateFlow<List<Transaction>> = _transactionBE.asStateFlow()

    init {
        Log.e("fdfd", "fdfdfd")

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
                        Log.e("TransactionViewModel", "Error fetching userInfo: ${error.message}")
                    }
                })

            // Lấy Accounts và tính totalBalance
            database.child("users").child(userId).child("accounts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newAccounts = mutableMapOf<String, Account>()
                        var balance = 0.0
                        snapshot.children.forEach { data ->
                            val key = data.key!!
                            if (isValidFirebaseKey(key)) {
                                val account = data.getValue(Account::class.java)
                                if (account != null) {
                                    newAccounts[key] = account
                                    balance += account.balance
                                }
                            } else {
                                Log.w("TransactionViewModel", "Skipping invalid account key: $key")
                            }
                        }
                        _accounts.value = newAccounts
                        _totalBalance.value = balance
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TransactionViewModel", "Error fetching accounts: ${error.message}")
                    }
                })

            // Lấy Categories
            database.child("users").child(userId).child("categories")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newCategories = mutableMapOf<String, Category>()
                        snapshot.children.forEach { data ->
                            val key = data.key!!
                            if (isValidFirebaseKey(key)) {

                                val category = data.getValue(Category::class.java)
                                if (category != null) {
                                    newCategories[key] = category
                                }
                            } else {
                                Log.w("TransactionViewModel", "Skipping invalid category key: $key")
                            }
                        }
                        _categories.value = newCategories
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TransactionViewModel", "Error fetching categories: ${error.message}")
                    }
                })

            // Lấy Budgets và tính totalBudget
            database.child("users").child(userId).child("budgets")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newBudgets = mutableMapOf<String, Budget>()
                        var totalBudget = 0.0
                        snapshot.children.forEach { data ->
                            val key = data.key!!
                            if (isValidFirebaseKey(key)) {
                                val budget = data.getValue(Budget::class.java)
                                if (budget != null) {
                                    newBudgets[key] = budget
                                    totalBudget += budget.amount
                                }
                            } else {
                                Log.w("TransactionViewModel", "Skipping invalid budget key: $key")
                            }
                        }
                        _budgets.value = newBudgets
                        _totalBudget.value = totalBudget
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TransactionViewModel", "Error fetching budgets: ${error.message}")
                    }
                })
            // Lấy Transactions và tính totalExpense
            database.child("users").child(userId).child("transactionsBE")
                .addValueEventListener(object : ValueEventListener {
                    @OptIn(UnstableApi::class)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newTransactions = mutableMapOf<String, Transaction>()
                        var expense = 0.0
                        snapshot.children.forEach { data ->
                            val key = data.key!!
                            if (isValidFirebaseKey(key)) {
                                try {
                                    val title = data.child("title").getValue(String::class.java) ?: ""
                                    val amount = data.child("amount").getValue(Double::class.java) ?: 0.0
                                    val type = data.child("type").getValue(String::class.java) ?: ""
                                    val date = data.child("date").getValue(String::class.java) ?: ""
                                    val period = data.child("period").getValue(String::class.java) ?: ""
                                    val isPositive = data.child("isPositive").getValue(Boolean::class.java) ?: false

                                    val transaction = Transaction(
                                        id = key,
                                        title = title,
                                        amount = amount,
                                        type = type,
                                        date = date,
                                        period = period,
                                        isPositive = isPositive
                                    )
                                    newTransactions[key] = transaction
                                    if (type == "expense") {
                                        expense += amount
                                    }
                                } catch (e: Exception) {
                                    Log.e("TransactionViewModel", "Failed to parse transaction $key: ${e.message}")
                                }
                            } else {
                                Log.w("TransactionViewModel", "Skipping invalid transaction key: $key")
                            }
                        }
                        _transactions.value = newTransactions
                        _totalExpense.value = expense
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TransactionViewModel", "Error fetching transactions: ${error.message}")
                    }
                })

            // Lấy Expenses
            loadExpenses(userId)

            // Lấy TransactionBE
            loadTransactionBE(userId)
        } else {
            _userInfo.value = null
        }
    }

    private fun loadExpenses(userId: String) {
        database.child("users").child(userId).child("categories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newExpenses = mutableMapOf<String, List<Expense>>()
                    snapshot.children.forEach { categorySnapshot ->
                        val categoryId = categorySnapshot.key ?: return@forEach
                        if (isValidFirebaseKey(categoryId)) {
                            val expensesList = mutableListOf<Expense>()
                            categorySnapshot.child("expenses").children.forEach { expenseSnapshot ->
                                val expense = expenseSnapshot.getValue(Expense::class.java)
                                if (expense != null) {
                                    expensesList.add(expense)
                                }
                            }
                            newExpenses[categoryId] = expensesList
                        } else {
                            Log.w("TransactionViewModel", "Skipping invalid category key for expenses: $categoryId")
                        }
                    }
                    _expenses.value = newExpenses
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Error fetching expenses: ${error.message}")
                }
            })
    }

    private fun loadTransactionBE(userId: String) {
        database.child("users").child(userId).child("transactionsBE")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactions = snapshot.children.mapNotNull { data ->
                        val key = data.key!!
                        if (isValidFirebaseKey(key)) {
                            try {
                                val title = data.child("title").getValue(String::class.java) ?: ""
                                val amount = data.child("amount").getValue(Double::class.java)?.toDouble() ?: 0.0
                                val type = data.child("type").getValue(String::class.java) ?: ""
                                val date = data.child("date").getValue(String::class.java) ?: ""
                                val period = data.child("period").getValue(String::class.java) ?: ""
                                val isPositive = data.child("isPositive").getValue(Boolean::class.java) ?: false

                                Transaction(
                                    id = key,
                                    title = title,
                                    amount = amount,
                                    type = type,
                                    date = date,
                                    period = period,
                                    isPositive = isPositive
                                )
                            } catch (e: Exception) {
                                Log.e("TransactionViewModel", "Failed to parse transactionBE $key: ${e.message}")
                                null
                            }
                        } else {
                            Log.w("TransactionViewModel", "Skipping invalid transactionBE key: $key")
                            null
                        }
                    }
                    _transactionBE.value = transactions
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Error fetching transactionBE: ${error.message}")
                }
            })
    }

    fun generateCategoryId(): String {
        return UUID.randomUUID().toString().replace("[^a-zA-Z0-9-]", "")
    }

    fun addCategory(category: Category) {
        val userId = auth.currentUser?.uid ?: return
        val categoryId = generateCategoryId()
        database.child("users").child(userId).child("categories").child(categoryId)
            .setValue(category.copy(id = categoryId))
    }

    fun getExpenses(categoryId: String): List<Expense> {
        return _expenses.value[categoryId] ?: emptyList()
    }

    fun addExpense(newExpense: Expense) {
        viewModelScope.launch {
            val updatedMap = _expenses.value.toMutableMap()
            val currentList = updatedMap[newExpense.categoryId]?.toMutableList() ?: mutableListOf()
            currentList.add(0, newExpense)
            updatedMap[newExpense.categoryId] = currentList
            _expenses.value = updatedMap

            _totalExpense.update { it + newExpense.amount }

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val dbRef = FirebaseDatabase.getInstance().reference
            val expenseId = dbRef.child("users")
                .child(userId)
                .child("categories")
                .child(newExpense.categoryId)
                .child("expenses")
                .push()
                .key ?: return@launch

            dbRef.child("users")
                .child(userId)
                .child("categories")
                .child(newExpense.categoryId)
                .child("expenses")
                .child(expenseId)
                .setValue(newExpense)
        }
    }

    fun getTransactions(): List<Transaction> {
        return _transactionBE.value
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val dbRef = FirebaseDatabase.getInstance().reference
            val transactionId = dbRef.child("users")
                .child(userId)
                .child("transactionsBE")
                .push()
                .key ?: return@launch

            // Log transaction and write details
            Log.d("TransactionViewModel", "Adding transaction: $transaction")
            Log.d("TransactionViewModel", "Transaction ID: $transactionId")
            Log.d("TransactionViewModel", "Write path: users/$userId/transactionsBE/$transactionId")

            // Sanitize transaction fields
            val sanitizedTransaction = transaction.copy(
                id = "", // Clear id to avoid invalid keys
                title = transaction.title.replace("[/#$\\[\\]]".toRegex(), ""),
                type = transaction.type.replace("[/#$\\[\\]]".toRegex(), ""),
                date = transaction.date.replace("[/#$\\[\\]]".toRegex(), ""),
                period = transaction.period.replace("[/#$\\[\\]]".toRegex(), ""),
                amount = transaction.amount.toDouble() // Ensure amount is Double
            )

            val newTransaction = sanitizedTransaction.copy(id = transactionId)
            Log.d("TransactionViewModel", "Sanitized transaction: $newTransaction")

            try {
                dbRef.child("users")
                    .child(userId)
                    .child("transactionsBE")
                    .child(transactionId)
                    .setValue(newTransaction)
                    .await()

                _transactions.update { current ->
                    current + (transactionId to newTransaction)
                }

                if (transaction.type == "income") {
                    _totalBalance.update { it + transaction.amount.toDouble() }
                } else if (transaction.type == "expense") {
                    _totalExpense.update { it + transaction.amount.toDouble() }
                    _totalBalance.update { it - transaction.amount.toDouble() }
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Failed to add transaction $transactionId: ${e.message}", e)
                // Fallback: Try writing to a test node
                try {
                    dbRef.child("users")
                        .child(userId)
                        .child("test_transactionsBE")
                        .child(transactionId)
                        .setValue(newTransaction)
                        .await()
                    Log.d("TransactionViewModel", "Successfully wrote to test_transactions")
                } catch (fallbackError: Exception) {
                    Log.e("TransactionViewModel", "Fallback write failed: ${fallbackError.message}", fallbackError)
                }
            }
        }
    }

    private fun isValidFirebaseKey(key: String): Boolean {
        return !key.contains("[/#$\\[\\]]".toRegex())
    }
}