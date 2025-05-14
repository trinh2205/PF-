package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.*
import com.example.mainproject.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TransactionViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var notificationRepository: NotificationRepository
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val _account = MutableStateFlow<Account?>(null)
    private val _categories = MutableStateFlow<Map<String, Category>>(emptyMap())
    private val _budgets = MutableStateFlow<Map<String, Budget>>(emptyMap())
    private val _incomes = MutableStateFlow<Map<String, Income>>(emptyMap())
    private val _expenses = MutableStateFlow<Map<String, TransactionBE>>(emptyMap())
    private val _totalBalance = MutableStateFlow(0.0)
    private val _totalExpense = MutableStateFlow(0.0)
    private val _totalBudget = MutableStateFlow(0.0)
    private val _selectedTransactionType = MutableStateFlow("all") // "all", "income", "expense"

    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()
    val account: StateFlow<Account?> = _account.asStateFlow()
    val categories: StateFlow<Map<String, Category>> = _categories.asStateFlow()
    private val _listCategories = MutableStateFlow<Map<String, ListCategories>>(emptyMap())
    val listCategories: StateFlow<Map<String, ListCategories>> = _listCategories.asStateFlow()
    val budgets: StateFlow<Map<String, Budget>> = _budgets.asStateFlow()
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()

    private val _isLoadingTransactions = MutableStateFlow(true)
    val isLoadingTransactions: StateFlow<Boolean> = _isLoadingTransactions.asStateFlow()

    // Unified transaction data class for UI
    data class UnifiedTransaction(
        val id: String,
        val categoryId: String,
        val title: String,
        val amount: Double,
        val type: String,
        val date: String,
        val message: String
    )

    // Filtered transactions combining Income and TransactionBE
    val filteredTransactions: StateFlow<List<UnifiedTransaction>> = combine(
        _incomes,
        _expenses,
        _selectedTransactionType
    ) { incomes, expenses, type ->
        val unified = mutableListOf<UnifiedTransaction>()

        incomes.values.forEach { income ->
            unified.add(
                UnifiedTransaction(
                    id = income.id,
                    categoryId = income.categoryId,
                    title = income.title,
                    amount = income.amount,
                    type = "income",
                    date = income.date,
                    message = income.message
                )
            )
        }

        expenses.values.forEach { expense ->
            unified.add(
                UnifiedTransaction(
                    id = expense.id,
                    categoryId = expense.categoryId,
                    title = expense.title,
                    amount = expense.amount,
                    type = "expense",
                    date = expense.date,
                    message = expense.message
                )
            )
        }

        unified.filter { transaction ->
            when (type) {
                "income" -> transaction.type == "income"
                "expense" -> transaction.type == "expense"
                else -> true // "all"
            }
        }.sortedByDescending { it.date }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    init {
        if (userId != null) {
            loadUserProfile(userId)
            loadAccount(userId)
            loadListCategories(userId)
            loadBudgets(userId)
            loadTransactionsBE(userId)
        } else {
            _userInfo.value = null
            _isLoadingTransactions.value = false
        }
    }

    // Set the transaction type filter when a UI component is clicked
    fun setTransactionType(type: String) {
        if (type in listOf("all", "income", "expense")) {
            _selectedTransactionType.value = type
            Log.d("TransactionViewModel", "Selected transaction type: $type")
        } else {
            Log.w("TransactionViewModel", "Invalid transaction type: $type")
        }
    }

    private fun loadUserProfile(userId: String) {
        database.child("users").child(userId).child("profile")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _userInfo.value = snapshot.getValue(UserInfo::class.java)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Lỗi tải UserInfo: ${error.message}")
                    _isLoadingTransactions.value = false
                }
            })
    }

    private fun loadAccount(userId: String) {
        database.child("users").child(userId).child("account")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _account.value = snapshot.getValue(Account::class.java)
                    _totalBalance.value = _account.value?.balance ?: 0.0
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Lỗi tải Account: ${error.message}")
                    _isLoadingTransactions.value = false
                }
            })
    }

    private fun loadListCategories(userId: String) {
        database.child("users").child(userId).child("listCategories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newListCategoriesMap = mutableMapOf<String, ListCategories>()
                    snapshot.children.forEach { listCategorySnapshot ->
                        listCategorySnapshot.getValue(ListCategories::class.java)?.let {
                            newListCategoriesMap[listCategorySnapshot.key!!] = it
                        }
                    }
                    _listCategories.value = newListCategoriesMap
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Lỗi tải ListCategories: ${error.message}")
                    _isLoadingTransactions.value = false
                }
            })
    }

    private fun loadBudgets(userId: String) {
        database.child("users").child(userId).child("budgets")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newBudgets = mutableMapOf<String, Budget>()
                    var totalBudget = 0.0
                    snapshot.children.forEach { data ->
                        data.getValue(Budget::class.java)?.let { budget ->
                            newBudgets[data.key!!] = budget
                            totalBudget += budget.amount
                        }
                    }
                    _budgets.value = newBudgets
                    _totalBudget.value = totalBudget
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Lỗi tải Budgets: ${error.message}")
                    _isLoadingTransactions.value = false
                }
            })
    }

    @OptIn(UnstableApi::class)
    private fun loadTransactionsBE(userId: String) {
        viewModelScope.launch {
            try {
                // Load incomes and expenses in parallel using async
                val incomesDeferred = async {
                    suspendCancellableCoroutine<Map<String, Income>> { continuation ->
                        database.child("users").child(userId).child("transactionsBE").child("incomes")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val newIncomes = mutableMapOf<String, Income>()
                                    snapshot.children.forEach { data ->
                                        data.getValue(Income::class.java)?.let { income ->
                                            newIncomes[data.key!!] = income
                                        }
                                    }
                                    continuation.resume(newIncomes)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    continuation.resumeWithException(error.toException())
                                }
                            })
                    }
                }

                val expensesDeferred = async {
                    suspendCancellableCoroutine<Pair<Map<String, TransactionBE>, Double>> { continuation ->
                        database.child("users").child(userId).child("transactionsBE").child("expenses")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val newExpenses = mutableMapOf<String, TransactionBE>()
                                    var expenseTotal = 0.0
                                    snapshot.children.forEach { data ->
                                        data.getValue(TransactionBE::class.java)?.let { transaction ->
                                            newExpenses[data.key!!] = transaction
                                            expenseTotal += transaction.amount
                                        }
                                    }
                                    continuation.resume(newExpenses to expenseTotal)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    continuation.resumeWithException(error.toException())
                                }
                            })
                    }
                }

                // Await both results
                val incomes = incomesDeferred.await()
                val (expenses, expenseTotal) = expensesDeferred.await()

                // Update StateFlows
                _incomes.value = incomes
                _expenses.value = expenses
                _totalExpense.value = expenseTotal
                _isLoadingTransactions.value = false

                Log.d("TransactionViewModel", "Loaded ${incomes.size} incomes and ${expenses.size} expenses in parallel")
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("TransactionViewModel", "Lỗi tải transactions: ${e.message}", e)
                    _isLoadingTransactions.value = false
                }
            }
        }
    }

    fun generateCategoryId(): String {
        return UUID.randomUUID().toString()
    }

    fun addListCategory(listCategory: ListCategories) {
        val userId = auth.currentUser?.uid ?: return
        val listCategoryId = generateCategoryId()
        database.child("users").child(userId).child("listCategories").child(listCategoryId)
            .setValue(listCategory.copy(id = listCategoryId))
            .addOnSuccessListener {
                Log.d("addListCategory", "Đã thêm ListCategory ${listCategory.name} với ID: $listCategoryId")
            }
            .addOnFailureListener { error ->
                Log.e("addListCategory", "Lỗi khi thêm ListCategory ${listCategory.name}: ${error.message}")
            }
    }

    fun addCategory(listCategoryId: String, category: Category, onCategoryAdded: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val categoryId = generateCategoryId()
        database.child("users").child(userId).child("listCategories").child(listCategoryId)
            .child("categories").child(categoryId)
            .setValue(category.copy(id = categoryId))
            .addOnSuccessListener {
                Log.d("addCategory", "Đã thêm category ${category.name} vào ListCategory $listCategoryId với ID: $categoryId")
                _listCategories.update { currentMap ->
                    currentMap[listCategoryId]?.let { existingListCategory ->
                        val updatedCategories = existingListCategory.categories + (categoryId to category.copy(id = categoryId))
                        currentMap + (listCategoryId to existingListCategory.copy(categories = updatedCategories))
                    } ?: currentMap
                }
                onCategoryAdded(categoryId)
            }
            .addOnFailureListener { error ->
                Log.e("addCategory", "Lỗi khi thêm category ${category.name} vào ListCategory $listCategoryId: ${error.message}")
            }
    }

    fun addExpense(listCategoryId: String, categoryId: String, newExpense: Expense) {
        Log.d("AddExpense", "Bắt đầu thêm expense với ListCategory ID: $listCategoryId, Category ID: $categoryId, Amount: ${newExpense.amount}")
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.w("AddExpense", "Người dùng chưa xác thực.")
                return@launch
            }

            val dbRef = database
            val currentAccount = _account.value
            if (currentAccount != null) {
                val userBalanceRef = dbRef.child("users").child(userId).child("account").child("balance")
                val currentBalance = currentAccount.balance ?: 0.0
                val newBalance = currentBalance - newExpense.amount

                userBalanceRef.setValue(newBalance).addOnSuccessListener {
                    Log.d("AddExpense", "Cập nhật balance thành công. Balance mới: $newBalance")
                    _totalBalance.update { it - newExpense.amount }
                    saveNewExpenseToTransactionBE(userId, newExpense, categoryId)
                }.addOnFailureListener { error ->
                    Log.e("AddExpense", "Lỗi khi cập nhật balance: ${error.message}")
                }
            } else {
                Log.w("AddExpense", "Không tìm thấy tài khoản để trừ tiền.")
            }

            _totalExpense.update { it + newExpense.amount }
        }
    }

    private fun saveNewExpenseToTransactionBE(userId: String, newExpense: Expense, categoryId: String) {
        val dbRef = database
        val expenseId = dbRef.child("users").child(userId).child("transactionsBE").child("expenses").push().key ?: return

        val transaction = TransactionBE(
            id = expenseId,
            categoryId = categoryId,
            title = newExpense.title,
            amount = newExpense.amount,
            type = "expense",
            date = newExpense.date ?: LocalDate.now().toString(),
            message = newExpense.message
        )

        dbRef.child("users").child(userId).child("transactionsBE").child("expenses").child(expenseId)
            .setValue(transaction)
            .addOnSuccessListener {
                Log.d("SaveExpenseBE", "Lưu expense vào transactionsBE/expenses thành công: $expenseId")
                sendNewExpenseNotification(userId, newExpense)
            }
            .addOnFailureListener { error ->
                Log.e("SaveExpenseBE", "Lỗi khi lưu expense vào transactionsBE/expenses: ${error.message}")
            }
    }

    fun addIncome(categoryId: String, newIncome: Income) {
        Log.d("AddIncome", "Bắt đầu thêm income với Category ID: $categoryId, Amount: ${newIncome.amount}")
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.w("AddIncome", "Người dùng chưa xác thực.")
                return@launch
            }

            val dbRef = database
            val currentAccount = _account.value
            if (currentAccount != null) {
                val userBalanceRef = dbRef.child("users").child(userId).child("account").child("balance")
                val currentBalance = currentAccount.balance ?: 0.0
                val newBalance = currentBalance + newIncome.amount

                userBalanceRef.setValue(newBalance).addOnSuccessListener {
                    Log.d("AddIncome", "Cập nhật balance thành công. Balance mới: $newBalance")
                    _totalBalance.update { it + newIncome.amount }
                    saveNewIncomeToTransactionBE(userId, newIncome, categoryId)
                }.addOnFailureListener { error ->
                    Log.e("AddIncome", "Lỗi khi cập nhật balance: ${error.message}")
                }
            } else {
                Log.w("AddIncome", "Không tìm thấy tài khoản để cộng tiền.")
            }
        }
    }

    private fun saveNewIncomeToTransactionBE(userId: String, newIncome: Income, categoryId: String) {
        val dbRef = database
        val incomeId = dbRef.child("users").child(userId).child("transactionsBE").child("incomes").push().key ?: return

        val income = Income(
            id = incomeId,
            categoryId = categoryId,
            title = newIncome.title,
            amount = newIncome.amount,
            date = newIncome.date ?: LocalDate.now().toString(),
            message = newIncome.message
        )

        dbRef.child("users").child(userId).child("transactionsBE").child("incomes").child(incomeId)
            .setValue(income)
            .addOnSuccessListener {
                Log.d("SaveIncomeBE", "Lưu income vào transactionsBE/incomes thành công: $incomeId")
                sendNewIncomeNotification(userId, newIncome)
            }
            .addOnFailureListener { error ->
                Log.e("SaveIncomeBE", "Lỗi khi lưu income vào transactionsBE/incomes: ${error.message}")
            }
    }

    private fun sendNewIncomeNotification(userId: String, income: Income) {
        val categoryName = getCategoryNameById(income.categoryId) ?: "Không xác định"
        val body = "Bạn vừa nhận ${income.amount} từ '${income.title}' ($categoryName)."

        val notification = Notification(
            userId = userId,
            title = "New Income",
            body = body,
            type = NotificationType.TRANSACTION.name,
            data = mapOf("incomeId" to income.id)
        )
        viewModelScope.launch {
            notificationRepository.saveNotification(notification)
        }
    }

    private fun sendNewExpenseNotification(userId: String, expense: Expense) {
        val categoryName = getCategoryNameById(expense.categoryId) ?: "Không xác định"
        val body = "Bạn vừa chi tiêu ${expense.amount} cho '${expense.title}' ($categoryName)."

        val notification = Notification(
            userId = userId,
            title = "New Expense",
            body = body,
            type = NotificationType.TRANSACTION.name,
            data = mapOf("expenseId" to expense.id)
        )
        viewModelScope.launch {
            notificationRepository.saveNotification(notification)
        }
    }

    fun deleteListCategory(categoryId: String) {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("listCategories").child(categoryId).removeValue()
            .addOnSuccessListener {
                _listCategories.update { currentMap ->
                    currentMap.filterKeys { it != categoryId }
                }
                Log.d("TransactionViewModel", "Đã xóa ListCategory với ID: $categoryId")
            }
            .addOnFailureListener { error ->
                Log.e("TransactionViewModel", "Lỗi khi xóa ListCategory với ID: $categoryId: ${error.message}")
            }
    }

    fun deleteExpense(listCategoryId: String, expenseId: String) {
        val userId = auth.currentUser?.uid ?: return
        val expenseRef = database.child("users").child(userId).child("transactionsBE").child("expenses").child(expenseId)
        expenseRef.get().addOnSuccessListener { snapshot ->
            val deletedExpense = snapshot.getValue(TransactionBE::class.java)
            val deletedExpenseAmount = deletedExpense?.amount ?: 0.0

            expenseRef.removeValue()
                .addOnSuccessListener {
                    _expenses.update { currentMap ->
                        currentMap.filterKeys { it != expenseId }
                    }
                    _totalBalance.update { it + deletedExpenseAmount }
                    _totalExpense.update { it - deletedExpenseAmount }
                    Log.d("TransactionViewModel", "Đã xóa chi phí với ID: $expenseId")
                }
                .addOnFailureListener { error ->
                    Log.e("TransactionViewModel", "Lỗi khi xóa chi phí với ID: $expenseId: ${error.message}")
                }
        }.addOnFailureListener { error ->
            Log.e("TransactionViewModel", "Lỗi khi lấy thông tin chi phí trước khi xóa: ${error.message}")
        }
    }

    fun getCategoryNameById(categoryId: String?): String? {
        return _categories.value[categoryId]?.name
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                    return TransactionViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun getTransactionsByCategoryName(targetCategoryName: String?): Flow<List<UnifiedTransaction>> {
        return filteredTransactions.map { transactions ->
            transactions.filter { transaction ->
                getCategoryNameById(transaction.categoryId) == targetCategoryName
            }
        }
    }

    data class TransactionWithCategoryName(
        val transaction: UnifiedTransaction,
        val categoryName: String?
    )
}