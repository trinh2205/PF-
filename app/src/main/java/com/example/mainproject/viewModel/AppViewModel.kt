package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mainproject.data.model.Account
import com.example.mainproject.data.model.Expense
import com.example.mainproject.data.model.ListCategories
import com.example.mainproject.data.model.Notification
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

    // StateFlow cho danh sách tất cả giao dịch BE (cả expense và income)
    private val _allTransactionsBE = MutableStateFlow<List<TransactionBE>>(emptyList())
    val allTransactions: StateFlow<List<TransactionBE>> = _allTransactionsBE.asStateFlow()

    // StateFlow cho danh sách giao dịch BE đã lọc theo thời gian
    private val _dailyTransactions = MutableStateFlow<List<TransactionBE>>(emptyList())
    val dailyTransactions: StateFlow<List<TransactionBE>> = _dailyTransactions.asStateFlow()

    private val _weeklyTransactions = MutableStateFlow<List<TransactionBE>>(emptyList())
    val weeklyTransactions: StateFlow<List<TransactionBE>> = _weeklyTransactions.asStateFlow()

    private val _monthlyTransactions = MutableStateFlow<List<TransactionBE>>(emptyList())
    val monthlyTransactions: StateFlow<List<TransactionBE>> = _monthlyTransactions.asStateFlow()

    private val database = FirebaseDatabase.getInstance()
    private var userId: String? = auth.currentUser?.uid
    val userID: String = userId ?: ""

    private val _listCategories = MutableStateFlow<List<ListCategories>>(emptyList())
    val listCategories: StateFlow<List<ListCategories>> = _listCategories.asStateFlow()

    // Listeners
    private var userListener: ValueEventListener? = null
    private var expenseTransactionsListener: ValueEventListener? = null
    private var incomeTransactionsListener: ValueEventListener? = null
    private var accountsListener: ValueEventListener? = null
    private var budgetsListener: ValueEventListener? = null // Thêm listener cho budget
    private var listCategoriesListener: ValueEventListener? = null

    // Định dạng date để so sánh
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        Log.d("AppViewModel", "AppViewModel initialized for user: $userId")
        loadInitialData()
    }

    private fun loadInitialData() {
        userId?.let { uid ->
            startUserListener(uid)
            startExpenseTransactionsListener(uid)
            startIncomeTransactionsListener(uid)
            startAccountsListener(uid)
            startBudgetsListener(uid) // Bắt đầu lắng nghe budget
            startListCategoriesListener(uid)
        } ?: run {
            Log.d("AppViewModel", "User not logged in, setting default values.")
            _totalExpense.value = 0.0
            _totalBalance.value = 0.0
            _totalBudget.value = 0.0
            _currentUser.value = null
            _isLoggedIn.value = false
            _allTransactionsBE.value = emptyList()
            _dailyTransactions.value = emptyList()
            _weeklyTransactions.value = emptyList()
            _monthlyTransactions.value = emptyList()
        }
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

    private fun startExpenseTransactionsListener(userId: String) {
        val expenseRef = database.getReference("users").child(userId).child("transactionsBE").child("expenses")
        expenseTransactionsListener = expenseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentTotalExpense = _totalExpense.value
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
                Log.d("AppViewModel", "Expense transactions loaded: ${expenseTransactions.size} items")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading expense transactions: ${error.message}")
            }
        })
    }

    private fun startIncomeTransactionsListener(userId: String) {
        val incomeRef = database.getReference("users").child(userId).child("transactionsBE").child("incomes")
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
                Log.d("AppViewModel", "Income transactions loaded: ${incomeTransactions.size} items")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading income transactions: ${error.message}")
            }
        })
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
                Log.e("AppViewModel", "Error parsing date: ${transaction.date}", e)
                false
            }
        }
    }

    private fun startAccountsListener(userId: String) {
        val accountRef = database.getReference("users").child(userId).child("accounts") // Trỏ trực tiếp đến node "account"
        accountsListener = accountRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val account = snapshot.getValue(Account::class.java)
                if (account != null) {
                    _totalBalance.value = account.balance // Lấy balance trực tiếp từ đối tượng Account
                    Log.d("AppViewModel", "Account data loaded in startAccountsListener: $account") // Log đối tượng Account
                } else {
                    _totalBalance.value = 0.0 // Hoặc giá trị mặc định khác nếu account null
                    Log.w("AppViewModel", "No account data found for user: $userId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AppViewModel", "Error reading account: ${error.message}")
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
            database.getReference("users").child(uid).child("transactionsBE").child("expenses")
                .removeEventListener(expenseTransactionsListener ?: return@let)
            database.getReference("users").child(uid).child("transactionsBE").child("incomes")
                .removeEventListener(incomeTransactionsListener ?: return@let)
            database.getReference("users").child(uid).child("accounts").removeEventListener(accountsListener ?: return@let)
            database.getReference("users").child(uid).child("budgets").removeEventListener(budgetsListener ?: return@let)
            Log.d("AppViewModel", "Listeners removed for user: $uid")
        }
        super.onCleared()
    }

    private fun stopListeners() {
        currentUserId?.let { uid ->
            database.getReference("users").child(uid).removeEventListener(userListener ?: return@let)
            database.getReference("users").child(uid).child("transactionsBE").child("expenses")
                .removeEventListener(expenseTransactionsListener ?: return@let)
            database.getReference("users").child(uid).child("transactionsBE").child("incomes")
                .removeEventListener(incomeTransactionsListener ?: return@let)
            database.getReference("users").child(uid).child("accounts").removeEventListener(accountsListener ?: return@let)
            database.getReference("users").child(uid).child("budgets").removeEventListener(budgetsListener ?: return@let)
            Log.d("AppViewModel", "Listeners removed for user: $uid")
        }
        userListener = null
        expenseTransactionsListener = null
        incomeTransactionsListener = null
        accountsListener = null
        budgetsListener = null
    }

    fun saveNotification(notification: Notification) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("AppViewModel", "Cannot save notification: No user is currently logged in")
            return
        }

        val notificationId = notification.notificationId ?: UUID.randomUUID().toString()
        val notificationRef = database.getReference("users") // Thay đổi tham chiếu gốc thành "users"
            .child(userId)
            .child("Notifications") // Đổi tên nhánh thành "Notifications" (chữ hoa)
            .child(notificationId)

        val notificationToSave = notification.copy(userId = userId, notificationId = notificationId)

        notificationRef.setValue(notificationToSave)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Saved notification with ID $notificationId for user $userId at users/$userId/Notifications/$notificationId")
            }
            .addOnFailureListener { error ->
                Log.e("AppViewModel", "Error saving notification for user $userId: ${error.message}")
            }
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
                // Tạo và lưu notification sau khi thêm expense thành công
                val notification = Notification(
                    notificationId = UUID.randomUUID().toString(),
                    title = "Chi phí mới",
                    body = "Bạn vừa thêm chi phí '${expense.title}' với số tiền ${expense.amount}.",
                    timestamp = System.currentTimeMillis(),
                    type = "expense_added",
                    data = mapOf("expenseId" to expense.id)
                )
                saveNotification(notification) // Gọi hàm saveNotification
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

    fun logout() {
        stopListeners() // Thêm dòng này
        _currentUser.value = null
        _isLoggedIn.value = false
        _totalExpense.value = 0.0
        _totalBalance.value = 0.0
        _totalBudget.value = 0.0
        _allTransactionsBE.value = emptyList()
        _dailyTransactions.value = emptyList()
        _weeklyTransactions.value = emptyList()
        _monthlyTransactions.value = emptyList()
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

    companion object {
        fun provideFactory(auth: FirebaseAuth): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppViewModel(auth) as T
            }
        }
    }
}