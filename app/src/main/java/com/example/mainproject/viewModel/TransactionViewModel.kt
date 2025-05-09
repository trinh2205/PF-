package com.example.mainproject.viewModel

import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.mainproject.data.model.Account
import com.example.mainproject.data.model.Budget
import com.example.mainproject.data.model.Category
import com.example.mainproject.data.model.Expense
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.model.NotificationType
import com.example.mainproject.data.model.UserInfo
import com.example.mainproject.data.model.Transaction
import com.example.mainproject.data.model.TransactionBE
import com.example.mainproject.data.repository.NotificationRepository
import com.example.mainproject.data.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
//
class TransactionViewModel(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val _accounts = MutableStateFlow<Map<String, Account>>(emptyMap())
    private val _categories = MutableStateFlow<Map<String, Category>>(emptyMap())
    private val _budgets = MutableStateFlow<Map<String, Budget>>(emptyMap())
    private val _transactions = MutableStateFlow<Map<String, Transaction>>(emptyMap())
    private val _transactionsBE = MutableStateFlow<Map<String, TransactionBE>>(emptyMap())
    val transactionsBE: StateFlow<Map<String, TransactionBE>> = _transactionsBE.asStateFlow()
    private val _totalBalance = MutableStateFlow(0.0)
    private val _totalExpense = MutableStateFlow(0.0)
    private val _totalBudget = MutableStateFlow(0.0)
    private var _expenses = mutableStateOf<Map<String, List<Expense>>>(emptyMap())

    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()
    val accounts: StateFlow<Map<String, Account>> = _accounts.asStateFlow()
    val categories: StateFlow<Map<String, Category>> = _categories.asStateFlow()
    val budgets: StateFlow<Map<String, Budget>> = _budgets.asStateFlow()
    val transactions: StateFlow<Map<String, Transaction>> = _transactions.asStateFlow()
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()
    val expenses: State<Map<String, List<Expense>>> = _expenses

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
            database.child("users").child(userId).child("transactionsBE")
                .addValueEventListener(object : ValueEventListener {
                    @OptIn(UnstableApi::class)
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

            // Lấy Expenses
            loadExpenses(userId)
        } else {
            _userInfo.value = null // Đặt trạng thái mặc định nếu chưa đăng nhập
        }
    }

    private fun loadExpenses(userId: String) {
        database.child("users").child(userId).child("categories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newExpenses = mutableMapOf<String, List<Expense>>()
                    snapshot.children.forEach { categorySnapshot ->
                        val categoryId = categorySnapshot.key ?: return@forEach
                        val expensesList = mutableListOf<Expense>()
                        categorySnapshot.child("expenses").children.forEach { expenseSnapshot ->
                            val expense = expenseSnapshot.getValue(Expense::class.java)
                            if (expense != null) {
                                expensesList.add(expense)
                            }
                        }
                        newExpenses[categoryId] = expensesList
                    }
                    _expenses.value = newExpenses
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi
                }
            })
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
                .addOnSuccessListener {
                    // Sau khi thêm expense thành công, tạo và lưu notification
                    sendNewExpenseNotification(userId, newExpense)
                }
        }
    }

    fun getCategoryNameById(categoryId: String?): String? {
        return _categories.value[categoryId]?.name
    }

    private fun sendNewExpenseNotification(userId: String, expense: Expense) {
        val categoryName = getCategoryNameById(expense.categoryId) ?: "Không xác định"
        val body = "Bạn vừa chi tiêu ${expense.amount} cho '${expense.title}' ($categoryName)."

        val notification = Notification(
            userId = userId,
            title = "New Expense",
            body = body,
            type = NotificationType.TRANSACTION.name,
            data = mapOf("expenseId" to expense.id) // Giả sử Expense có ID
        )
        viewModelScope.launch {
            notificationRepository.saveNotification(notification)
        }
    }

    // Bên trong class TransactionViewModel
    companion object {
        fun provideFactory(
            notificationRepository: NotificationRepository,
//            transactionRepository: TransactionRepository? = null,
            userId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                    return TransactionViewModel(notificationRepository, userId) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun getTransactionsByCategoryName(targetCategoryName: String?): Flow<List<TransactionBE>> {
        return _transactionsBE.map { transactionsMap ->
            transactionsMap.values.filter { transaction ->
                val categoryName = getCategoryNameById(transaction.categoryId)
                categoryName == targetCategoryName
            }
        }
    }

    // Data class để kết hợp Transaction với categoryName
    data class TransactionWithCategoryName(
        val transaction: TransactionBE,
        val categoryName: String?
    )

}