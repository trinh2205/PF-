package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mainproject.data.model.Account
import com.example.mainproject.data.model.Expense
import com.example.mainproject.data.model.ListCategories
import com.example.mainproject.data.model.TransactionBE
import com.example.mainproject.data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class AppViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {
    private var currentUserId: String? = auth.currentUser?.uid

    // StateFlows
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance

    private val _totalBudget = MutableStateFlow(0.0)
    val totalBudget: StateFlow<Double> = _totalBudget

    private val _allTransactionsBE = MutableStateFlow<List<TransactionBE>>(emptyList())
    val allTransactions: StateFlow<List<TransactionBE>> = _allTransactionsBE.asStateFlow()

    private val _dailyTransactions = MutableStateFlow<List<TransactionBE>>(emptyList())
    val dailyTransactions: StateFlow<List<TransactionBE>> = _dailyTransactions.asStateFlow()

    private val _weeklyTransactions = MutableStateFlow<List<TransactionBE>>(emptyList())
    val weeklyTransactions: StateFlow<List<TransactionBE>> = _weeklyTransactions.asStateFlow()

    private val _monthlyTransactions = MutableStateFlow<List<TransactionBE>>(emptyList())
    val monthlyTransactions: StateFlow<List<TransactionBE>> = _monthlyTransactions.asStateFlow()

    private val _listCategories = MutableStateFlow<List<ListCategories>>(emptyList())
    val listCategories: StateFlow<List<ListCategories>> = _listCategories.asStateFlow()

    private val database = FirebaseDatabase.getInstance()
    private var userId: String? = auth.currentUser?.uid
    val userID: String
        get() = userId ?: ""

    // Listeners
    private var userListener: ValueEventListener? = null
    private var expenseTransactionsListener: ValueEventListener? = null
    private var incomeTransactionsListener: ValueEventListener? = null
    private var accountsListener: ValueEventListener? = null
    private var budgetsListener: ValueEventListener? = null
    private var listCategoriesListener: ValueEventListener? = null

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        Log.d("AppViewModel", "Initialized with userId: $userId")
        setupAuthStateListener()
        loadInitialData()
    }

    private fun setupAuthStateListener() {
        auth.addAuthStateListener { firebaseAuth ->
            val newUserId = firebaseAuth.currentUser?.uid
            if (newUserId != userId) {
                Log.d("AppViewModel", "Auth state changed: userId from $userId to $newUserId")
                stopListeners()
                userId = newUserId
                loadInitialData()
            }
        }
    }

    private fun loadInitialData() {
        userId?.let { uid ->
            Log.d("AppViewModel", "Loading data for user: $uid")
            startUserListener(uid)
            startExpenseTransactionsListener(uid)
            startIncomeTransactionsListener(uid)
            startAccountsListener(uid)
            startBudgetsListener(uid)
            startListCategoriesListener(uid)
        } ?: run {
            Log.w("AppViewModel", "No user logged in, resetting state")
            resetState()
        }
    }

    private fun resetState() {
        _totalExpense.value = 0.0
        _totalBalance.value = 0.0
        _totalBudget.value = 0.0
        _currentUser.value = null
        _isLoggedIn.value = false
        _allTransactionsBE.value = emptyList()
        _dailyTransactions.value = emptyList()
        _weeklyTransactions.value = emptyList()
        _monthlyTransactions.value = emptyList()
        _listCategories.value = emptyList()
    }

    private fun startUserListener(userId: String) {
        val userRef = database.getReference("users").child(userId).child("profile")
        userListener?.let { userRef.removeEventListener(it) }
        userListener = userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserInfo::class.java)
                _currentUser.value = user
                _isLoggedIn.value = user != null
                Log.d("AppViewModel", "User profile loaded for $userId: $user")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading user profile for $userId: ${error.message}")
            }
        })
    }

    private fun startExpenseTransactionsListener(userId: String) {
        val expenseRef = database.getReference("users").child(userId).child("transactionsBE").child("expenses")
        expenseTransactionsListener?.let { expenseRef.removeEventListener(it) }
        expenseTransactionsListener = expenseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentTotalExpense = 0.0
                val expenseTransactions = mutableListOf<TransactionBE>()
                snapshot.children.forEach { transactionSnapshot ->
                    val transaction = transactionSnapshot.getValue(TransactionBE::class.java)
                    transaction?.let {
                        currentTotalExpense += it.amount
                        expenseTransactions.add(it)
                    }
                }
                _totalExpense.value = currentTotalExpense
                updateAllTransactions(expenseTransactions)
                filterAndSetTransactions()
                Log.d("AppViewModel", "Loaded ${expenseTransactions.size} expense transactions for $userId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading expenses for $userId: ${error.message}")
            }
        })
    }

    private fun startIncomeTransactionsListener(userId: String) {
        val incomeRef = database.getReference("users").child(userId).child("transactionsBE").child("income")
        incomeTransactionsListener?.let { incomeRef.removeEventListener(it) }
        incomeTransactionsListener = incomeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val incomeTransactions = mutableListOf<TransactionBE>()
                snapshot.children.forEach { transactionSnapshot ->
                    val transaction = transactionSnapshot.getValue(TransactionBE::class.java)
                    transaction?.let {
                        incomeTransactions.add(it)
                    }
                }
                updateAllTransactions(incomeTransactions)
                filterAndSetTransactions()
                Log.d("AppViewModel", "Loaded ${incomeTransactions.size} income transactions for $userId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading income for $userId: ${error.message}")
            }
        })
    }

    private fun startAccountsListener(userId: String) {
        val accountRef = database.getReference("users").child(userId).child("account")
        accountsListener?.let { accountRef.removeEventListener(it) }
        accountsListener = accountRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val account = snapshot.getValue(Account::class.java)
                if (account != null) {
                    _totalBalance.value = account.balance
                    Log.d("AppViewModel", "Account loaded for $userId: $account")
                } else {
                    _totalBalance.value = 0.0
                    Log.w("AppViewModel", "No account found for $userId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading account for $userId: ${error.message}")
            }
        })
    }

    private fun startBudgetsListener(userId: String) {
        val budgetsRef = database.getReference("users").child(userId).child("budgets")
        budgetsListener?.let { budgetsRef.removeEventListener(it) }
        budgetsListener = budgetsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalBudget = 0.0
                val budgetsMap = mutableMapOf<String, Double>()
                snapshot.children.forEach { budgetSnapshot ->
                    val budgetAmount = budgetSnapshot.child("amount").getValue(Double::class.java) ?: 0.0
                    totalBudget += budgetAmount
                    budgetsMap[budgetSnapshot.key.toString()] = budgetAmount
                }
                _totalBudget.value = totalBudget
                Log.d("AppViewModel", "Budgets loaded for $userId: $budgetsMap")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading budgets for $userId: ${error.message}")
            }
        })
    }

    private fun startListCategoriesListener(userId: String) {
        val listCategoriesRef = database.getReference("users").child(userId).child("ListCategories")
        listCategoriesListener?.let { listCategoriesRef.removeEventListener(it) }
        listCategoriesListener = listCategoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoriesList = mutableListOf<ListCategories>()
                snapshot.children.forEach { categorySnapshot ->
                    val category = categorySnapshot.getValue(ListCategories::class.java)
                    category?.let {
                        categoriesList.add(it)
                    }
                }
                _listCategories.value = categoriesList.sortedBy { it.name }
                Log.d("AppViewModel", "Loaded ${categoriesList.size} categories for $userId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading categories for $userId: ${error.message}")
            }
        })
    }

    // CRUD Operations for ListCategories
    fun addListCategory(name: String, icon: String?) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("AppViewModel", "Cannot add category: No user is currently logged in")
            return
        }
        val categoryId = UUID.randomUUID().toString()
        val currentDate = LocalDate.now().format(dateFormatter)
        val newCategory = ListCategories(
            id = categoryId,
            name = name,
            date = currentDate,
            icon = icon,
            categories = emptyMap()
        )
        database.getReference("users").child(uid).child("ListCategories").child(categoryId)
            .setValue(newCategory)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Added category '$name' with ID $categoryId for user $uid")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error adding category '$name' for user $uid: ${error.message}")
            }
    }

    fun deleteListCategory(categoryId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("AppViewModel", "Cannot delete category: No user is currently logged in")
            return
        }
        database.getReference("users").child(uid).child("ListCategories").child(categoryId)
            .removeValue()
            .addOnSuccessListener {
                Log.d("AppViewModel", "Deleted category $categoryId for user $uid")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error deleting category $categoryId for user $uid: ${error.message}")
            }
    }

    // CRUD Operations for Expenses
    fun addExpense(categoryId: String, expense: Expense) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("AppViewModel", "Cannot add expense: No user is currently logged in")
            return
        }
        val expenseRef = database.getReference("users").child(uid).child("transactionsBE").child("expenses").child(expense.id)
        val transactionBE = TransactionBE(
            id = expense.id,
            title = expense.title,
            amount = expense.amount,
            date = expense.date,
            categoryId = categoryId,
            type = "expense",
            message = expense.message
        )
        expenseRef.setValue(transactionBE)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Added expense '${expense.title}' with ID ${expense.id} for user $uid")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error adding expense '${expense.title}' for user $uid: ${error.message}")
            }
    }

    fun deleteExpense(expenseId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("AppViewModel", "Cannot delete expense: No user is currently logged in")
            return
        }
        database.getReference("users").child(uid).child("transactionsBE").child("expenses").child(expenseId)
            .removeValue()
            .addOnSuccessListener {
                Log.d("AppViewModel", "Deleted expense $expenseId for user $uid")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error deleting expense $expenseId for user $uid: ${error.message}")
            }
    }

    fun generateCategoryId(): String {
        return UUID.randomUUID().toString()
    }

    private fun updateAllTransactions(newTransactions: List<TransactionBE>) {
        _allTransactionsBE.value = (_allTransactionsBE.value + newTransactions).sortedByDescending { it.date }
    }

    private fun filterAndSetTransactions() {
        _dailyTransactions.value = filterTransactionsByTimeBE(_allTransactionsBE.value, "daily")
        _weeklyTransactions.value = filterTransactionsByTimeBE(_allTransactionsBE.value, "weekly")
        _monthlyTransactions.value = filterTransactionsByTimeBE(_allTransactionsBE.value, "monthly")
    }

    private fun filterTransactionsByTimeBE(transactions: List<TransactionBE>, filter: String): List<TransactionBE> {
        val now = LocalDate.now()
        return transactions.filter { transaction ->
            try {
                val transactionDate = LocalDate.parse(transaction.date, dateFormatter)
                when (filter.lowercase(Locale.ROOT)) {
                    "daily" -> transactionDate.isEqual(now)
                    "weekly" -> {
                        val currentWeek = now.get(java.time.temporal.WeekFields.ISO.weekOfYear())
                        val transactionWeek = transactionDate.get(java.time.temporal.WeekFields.ISO.weekOfYear())
                        val currentYear = now.year
                        val transactionYear = transactionDate.year
                        transactionWeek == currentWeek && transactionYear == currentYear
                    }
                    "monthly" -> transactionDate.month == now.month && transactionDate.year == now.year
                    else -> true
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error parsing date '${transaction.date}' for $userId", e)
                false
            }
        }
    }

    fun createDefaultAccount(userId: String) {
        val defaultAccount = mapOf(
            "id" to "default_acc",
            "name" to "Tài khoản mặc định",
            "balance" to 1000.0,
            "currency" to "VND"
        )
        database.getReference("users").child(userId).child("account")
            .setValue(defaultAccount)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Created default account for user $userId")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error creating default account for $userId: ${error.message}")
            }
    }

    fun logout() {
        stopListeners()
        resetState()
        Log.d("AppViewModel", "User logged out")
        auth.signOut()
    }

    override fun onCleared() {
        stopListeners()
        Log.d("AppViewModel", "ViewModel cleared for user $userId")
        super.onCleared()
    }

    private fun stopListeners() {
        userId?.let { uid ->
            listOf(
                userListener to database.getReference("users").child(uid),
                expenseTransactionsListener to database.getReference("users").child(uid).child("transactionsBE").child("expenses"),
                incomeTransactionsListener to database.getReference("users").child(uid).child("transactionsBE").child("income"),
                accountsListener to database.getReference("users").child(uid).child("account"),
                budgetsListener to database.getReference("users").child(uid).child("budgets"),
                listCategoriesListener to database.getReference("users").child(uid).child("ListCategories")
            ).forEach { (listener, ref) ->
                listener?.let { ref.removeEventListener(it) }
            }
            Log.d("AppViewModel", "All listeners removed for user $uid")
        }
        userListener = null
        expenseTransactionsListener = null
        incomeTransactionsListener = null
        accountsListener = null
        budgetsListener = null
        listCategoriesListener = null
    }

    companion object {
        fun provideFactory(auth: FirebaseAuth): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppViewModel(auth) as T
            }
        }
    }
}