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
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class TransactionViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val _account = MutableStateFlow<Account?>(null)
    private val _categories = MutableStateFlow<Map<String, Category>>(emptyMap())
    private val _budgets = MutableStateFlow<Map<String, Budget>>(emptyMap())
    private val _transactionsBE = MutableStateFlow<Map<String, TransactionBE>>(emptyMap())
    val transactionsBE: StateFlow<Map<String, TransactionBE>> = _transactionsBE.asStateFlow()
    private val _totalBalance = MutableStateFlow(0.0) // Sẽ được cập nhật từ _account
    private val _totalExpense = MutableStateFlow(0.0)
    private val _totalBudget = MutableStateFlow(0.0)

    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()
    val account: StateFlow<Account?> = _account.asStateFlow() // Đã thay đổi
    val categories: StateFlow<Map<String, Category>> = _categories.asStateFlow()
    private val _listCategories = MutableStateFlow<Map<String, ListCategories>>(emptyMap())
    val listCategories: StateFlow<Map<String, ListCategories>> = _listCategories.asStateFlow()
    val budgets: StateFlow<Map<String, Budget>> = _budgets.asStateFlow()
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()

    private val _isLoadingTransactions = MutableStateFlow(true)
    val isLoadingTransactions: StateFlow<Boolean> = _isLoadingTransactions.asStateFlow()


    private val _isLoadingAccount = MutableStateFlow(true) // New flag for account loading state
    val isLoadingAccount: StateFlow<Boolean> = _isLoadingAccount.asStateFlow()
    private val _incomes = MutableStateFlow<Map<String, Income>>(emptyMap())
    private val _expenses = MutableStateFlow<Map<String, TransactionBE>>(emptyMap())
    private val _selectedTransactionType = MutableStateFlow("all") // "all", "income", "expense"

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
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfile(userId)
            loadAccount(userId) // Đã thay đổi tên hàm
            loadListCategories(userId)
            loadBudgets(userId)
            loadTransactionsBE(userId)
            setupRealtimeListeners(userId)
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

    fun addIncome(categoryId: String, newIncome: Income) {
        Log.d("AddIncome", "Bắt đầu thêm income với Category ID: $categoryId, Amount: ${newIncome.amount}")
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.w("AddIncome", "Người dùng chưa xác thực.")
                return@launch
            }

            // Wait until account is loaded
            while (_isLoadingAccount.value) {
                delay(100)
            }

            val currentAccount = _account.value
            Log.d("AddIncome", "uid $userId, account: $currentAccount")

            if (currentAccount != null) {
                val userBalanceRef = database.child("users").child(userId).child("accounts").child("balance")
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
                Log.e("AddIncome", "Tài khoản vẫn không tồn tại sau khi chờ load.")
            }
        }
    }

    private fun setupRealtimeListeners(userId: String) {
        // Listener for incomes
        database.child("users").child(userId).child("transactionsBE").child("incomes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newIncomes = mutableMapOf<String, Income>()
                    snapshot.children.forEach { data ->
                        val key = data.key
                        val income = data.getValue(Income::class.java)
                        if (key != null && income != null) {
                            newIncomes[key] = income
                        }
                    }
                    _incomes.value = newIncomes
                    updateTotalBalance()
                    Log.d("TransactionViewModel", "Updated ${newIncomes.size} incomes")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Failed to update incomes: ${error.message}")
                }
            })

        // Listener for expenses
        database.child("users").child(userId).child("transactionsBE").child("expenses")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newExpenses = mutableMapOf<String, TransactionBE>()
                    var expenseTotal = 0.0
                    snapshot.children.forEach { data ->
                        val key = data.key
                        val expense = data.getValue(TransactionBE::class.java)
                        if (key != null && expense != null) {
                            newExpenses[key] = expense
                            expenseTotal += expense.amount
                        }
                    }
                    _expenses.value = newExpenses
                    _totalExpense.value = expenseTotal
                    updateTotalBalance()
                    Log.d("TransactionViewModel", "Updated ${newExpenses.size} expenses")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Failed to update expenses: ${error.message}")
                }
            })

        // Listener for account
        database.child("users").child(userId).child("accounts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val account = snapshot.getValue(Account::class.java)
                    _account.value = account
                    _totalBalance.value = account?.balance ?: 0.0
                    Log.d("TransactionViewModel", "Updated account balance: ${_totalBalance.value}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Failed to update account: ${error.message}")
                }
            })
    }

    private fun updateTotalBalance() {
        val incomeTotal = _incomes.value.values.sumOf { it.amount }
        val expenseTotal = _expenses.value.values.sumOf { it.amount }
        val calculatedBalance = incomeTotal - expenseTotal

        // Update the account balance in the database
        userId?.let { uid ->
            database.child("users").child(uid).child("accounts").child("balance")
                .setValue(calculatedBalance)
                .addOnSuccessListener {
                    Log.d("TransactionViewModel", "Cập nhật balance trong database: $calculatedBalance")
                }
                .addOnFailureListener { error ->
                    Log.e("TransactionViewModel", "Lỗi khi cập nhật balance trong database: ${error.message}")
                }
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

    private fun loadAccount(userId: String) { // Đã thay đổi tên hàm
        database.child("users").child(userId).child("accounts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _account.value = snapshot.getValue(Account::class.java) // Đọc trực tiếp Account
                    _totalBalance.value = _account.value?.balance ?: 0.0 // Cập nhật totalBalance
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Lỗi tải Account: ${error.message}")
                    _isLoadingTransactions.value = false
                }
            })
    }

    private fun loadListCategories(userId: String) {
        database.child("users").child(userId).child("ListCategories")
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
//    private fun loadTransactionsBE(userId: String) {
//        _isLoadingTransactions.value = true
//        val expensesRef = database.child("users").child(userId).child("transactionsBE").child("expenses")
//        val allTransactions = mutableMapOf<String, TransactionBE>()
//        var totalExpense = 0.0
//
//        val expenseListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach { data ->
//                    data.getValue(TransactionBE::class.java)?.let { transaction ->
//                        allTransactions[data.key!!] = transaction
//                        if (transaction.type == "expense") {
//                            totalExpense += transaction.amount
//                        }
//                    }
//                }
//                _transactionsBE.value = allTransactions // Cập nhật khi có dữ liệu expense
//                _totalExpense.value = totalExpense
//                // Không set isLoading false ở đây, đợi income listener
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("TransactionViewModel", "Lỗi tải Expenses: ${error.message}")
//                _isLoadingTransactions.value = false
//            }
//        }
//        expensesRef.addValueEventListener(expenseListener)
//    }
    private fun loadTransactionsBE(userId: String) {
        viewModelScope.launch {
            try {
                // Load incomes and expenses in parallel using async
                val incomesDeferred = async {
                    database.child("users").child(userId).child("transactionsBE").child("incomes")
                        .get()
                        .await()
                        .let { snapshot ->
                            val newIncomes = mutableMapOf<String, Income>()
                            snapshot.children.forEach { data ->
                                data.getValue(Income::class.java)?.let { income ->
                                    newIncomes[data.key!!] = income
                                }
                            }
                            newIncomes
                        }
                }

                val expensesDeferred = async {
                    database.child("users").child(userId).child("transactionsBE").child("expenses")
                        .get()
                        .await()
                        .let { snapshot ->
                            val newExpenses = mutableMapOf<String, TransactionBE>()
                            var expenseTotal = 0.0
                            snapshot.children.forEach { data ->
                                data.getValue(TransactionBE::class.java)?.let { transaction ->
                                    newExpenses[data.key!!] = transaction
                                    expenseTotal += transaction.amount
                                }
                            }
                            newExpenses to expenseTotal
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
                Log.e("TransactionViewModel", "Lỗi tải transactions: ${e.message}", e)
                _isLoadingTransactions.value = false
            }
        }
    }

    fun generateCategoryId(): String {
        return UUID.randomUUID().toString()
    }

    fun addListCategory(listCategory: ListCategories) {
        val userId = auth.currentUser?.uid ?: return
        val listCategoryId = generateCategoryId()
        database.child("users").child(userId).child("ListCategories").child(listCategoryId)
            .setValue(listCategory.copy(id = listCategoryId))
            .addOnSuccessListener {
                Log.d("addListCategory", "Đã thêm ListCategory ${listCategory.name} với ID: $listCategoryId")
            }
            .addOnFailureListener { error ->
                Log.e("addListCategory", "Lỗi khi thêm ListCategory ${listCategory.name}: ${error.message}")
            }
    }

    fun addCategory(listCategoryId: String, category: Category) {
        val userId = auth.currentUser?.uid ?: return
        val categoryId = generateCategoryId()
        database.child("users").child(userId).child("ListCategories").child(listCategoryId)
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
            }
            .addOnFailureListener { error ->
                Log.e("addCategory", "Lỗi khi thêm category ${category.name} vào ListCategory $listCategoryId: ${error.message}")
            }
    }

    fun addExpense(listCategoryId: String, categoryId: String, newExpense: Expense) {
        Log.d("AddExpense", "Bắt đầu thêm expense với ListCategory ID: $listCategoryId, Category ID: $categoryId, Amount: ${newExpense.amount}")
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            Log.d("AddExpense", "User ID hiện tại: $userId")
            if (userId == null) {
                Log.w("AddExpense", "Người dùng chưa xác thực.")
                return@launch
            }

            val dbRef = database
            val currentAccount = _account.value

            if (currentAccount != null) {
                val userBalanceRef = dbRef.child("users").child(userId).child("accounts").child("balance")
                val currentBalance = currentAccount.balance ?: 0.0
                val newBalance = currentBalance - newExpense.amount

                userBalanceRef.setValue(newBalance).addOnSuccessListener {
                    Log.d("AddExpense", "Cập nhật balance thành công. Balance mới: $newBalance")
                    _totalBalance.update { it - newExpense.amount }
                    saveNewTransactionForExpense(userId, newExpense) { savedExpense -> // Nhận lại savedExpense với ID
                        // Cập nhật _transactionsBE sau khi expense được lưu thành công
                        _transactionsBE.update { currentMap ->
                            currentMap + (savedExpense.id to TransactionBE(
                                id = savedExpense.id,
                                categoryId = savedExpense.categoryId,
                                title = savedExpense.title,
                                amount = savedExpense.amount,
                                type = "expense",
                                date = savedExpense.date,
                                message = "Chi tiêu cho '${savedExpense.title}'"
                            ))
                        }
                        _totalExpense.update { it + savedExpense.amount }
                        Log.d("AddExpense", "_transactionsBE sau khi thêm: ${_transactionsBE.value}")
                    }
//                    saveNewTransactionForExpense(userId, newExpense)
                }.addOnFailureListener { error ->
                    Log.e("AddExpense", "Lỗi khi cập nhật balance: ${error.message}")
                }
            } else {
                Log.w("AddExpense", "Không tìm thấy tài khoản để trừ tiền.")
            }
        }
    }

    private fun saveNewExpenseToTransactionBE(userId: String, newExpense: Expense, onComplete: (Expense) -> Unit) {
        val dbRef = database
        val expenseRef = dbRef.child("users")
            .child(userId)
            .child("transactionsBE")
            .child("expenses")
            .push()

        val expenseId = expenseRef.key ?: return
        val savedExpense = newExpense.copy(id = expenseId)

        expenseRef.setValue(savedExpense)
            .addOnSuccessListener {
                Log.d("SaveExpenseBE", "Lưu expense vào transactionsBE thành công với ID: $expenseId")
                sendNewExpenseNotification(userId, savedExpense)
                onComplete(savedExpense) // Gọi callback và truyền savedExpense
            }
            .addOnFailureListener { error ->
                Log.e("SaveExpenseBE", "Lỗi khi lưu expense vào transactionsBE: ${error.message}")
            }
    }

    fun deleteListCategory(categoryId: String) {
        userId?.let { uid ->
            database.child("users").child(uid).child("ListCategories").child(categoryId).removeValue()
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
    }

    fun deleteExpense(listCategoryId: String, expenseId: String) {
        userId?.let { uid ->
            // 1. Lấy thông tin chi phí trước khi xóa
            val expenseRef = database.child("users").child(uid).child("transactionsBE").child("expenses").child(expenseId)
            expenseRef.get().addOnSuccessListener { snapshot ->
                val deletedExpense = snapshot.getValue(TransactionBE::class.java)
                val deletedExpenseAmount = deletedExpense?.amount ?: 0.0

                // 2. Xóa chi phí khỏi Firebase
                expenseRef.removeValue()
                    .addOnSuccessListener {
                        // 3. Cập nhật StateFlows và balance/expense
                        _transactionsBE.update { currentMap ->
                            currentMap.filterKeys { it != expenseId }
                        }
                        _totalBalance.update { it + deletedExpenseAmount } // Cộng lại balance
                        _totalExpense.update { it - deletedExpenseAmount } // Trừ khỏi totalExpense

                        Log.d("TransactionViewModel", "Đã xóa chi phí với ID: $expenseId")
                    }
                    .addOnFailureListener { error ->
                        Log.e("TransactionViewModel", "Lỗi khi xóa chi phí với ID: $expenseId: ${error.message}")
                    }
            }.addOnFailureListener { error ->
                Log.e("TransactionViewModel", "Lỗi khi lấy thông tin chi phí trước khi xóa: ${error.message}")
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
            data = mapOf("expenseId" to expense.id)
        )
        viewModelScope.launch {
            notificationRepository.saveNotification(notification)
        }
    }

    private fun saveNewTransactionForExpense(userId: String, expense: Expense, onComplete: (Expense) -> Unit) {
        val transactionId = database.child("users").child(userId).child("transactionsBE").child("expenses").push().key ?: return
        val newTransaction = TransactionBE(
            id = transactionId,
            categoryId = expense.categoryId,
            title = expense.title,
            amount = expense.amount, // Số tiền âm cho chi phí
            type = "expense",
            date = LocalDate.now().toString(),
            message = "Chi tiêu cho '${expense.title}'"
        )
        database.child("users").child(userId).child("transactionsBE").child("expenses").child(transactionId)
            .setValue(newTransaction)
            .addOnSuccessListener {
                Log.d("SaveTransaction", "Lưu transaction thành công cho expense: $transactionId")
                sendNewExpenseNotification(userId, expense)
            }
            .addOnFailureListener { error ->
                Log.e("SaveTransaction", "Lỗi khi lưu transaction cho expense: ${error.message}")
            }
    }

    fun sendNewIncomeNotification(userId: String, income: Income) {
        val categoryName = getCategoryNameById(income.categoryId) ?: "Không xác định"
        val body = "Bạn vừa nhận được ${income.amount} cho '${income.title}' ($categoryName)."

        val notification = Notification(
            notificationId = UUID.randomUUID().toString(),
            userId = userId,
            title = "New Income",
            body = body,
            timestamp = LocalDate.now().toEpochDay(),
            type = "Income",
            data = mapOf("incomeId" to income.id)
        )
        viewModelScope.launch {
            notificationRepository.saveNotification(notification)
        }
    }

    fun saveNewTransactionForIncome(userId: String, income: Income) {
        val transactionId = database.child("users").child(userId).child("transactionsBE").child("incomes").push().key ?: return
        val newTransaction = TransactionBE(
            id = transactionId,
            categoryId = "transfer_in",
            title = income.title,
            amount = income.amount, // Số tiền dương cho thu nhập
            type = "income",
            date = LocalDate.now().toString(),
            message = "Thu nhập từ '${income.title}'"
        )
        database.child("users").child(userId).child("transactionsBE").child("incomes").child(transactionId)
            .setValue(newTransaction)
            .addOnSuccessListener {
                Log.d("SaveTransaction", "Lưu transaction thành công cho income: $transactionId")
            }
            .addOnFailureListener { error ->
                Log.e("SaveTransaction", "Lỗi khi lưu transaction cho income: ${error.message}")
            }
    }

    companion object {
        fun provideFactory(
            notificationRepository: NotificationRepository,
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
                getCategoryNameById(transaction.categoryId) == targetCategoryName
            }
        }
    }

    data class TransactionWithCategoryName(
        val transaction: TransactionBE,
        val categoryName: String?
    )
}