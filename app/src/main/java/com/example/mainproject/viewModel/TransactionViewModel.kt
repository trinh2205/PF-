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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class TransactionViewModel(
    private val notificationRepository: NotificationRepository,
    private val userId: String?
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    private val _account = MutableStateFlow<Account?>(null) // Đã thay đổi
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

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfile(userId)
            loadAccount(userId) // Đã thay đổi tên hàm
            loadListCategories(userId)
            loadBudgets(userId)
            loadTransactionsBE(userId)
        } else {
            _userInfo.value = null
            _isLoadingTransactions.value = false
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
        database.child("users").child(userId).child("account")
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
    private fun loadTransactionsBE(userId: String) {
        database.child("users").child(userId).child("transactionsBE")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newTransactions = mutableMapOf<String, TransactionBE>()
                    var expense = 0.0
                    snapshot.children.forEach { data ->
                        data.getValue(TransactionBE::class.java)?.let { transaction ->
                            newTransactions[data.key!!] = transaction
                            if (transaction.type == "expense") {
                                expense += transaction.amount
                            }
                        }
                    }
                    _transactionsBE.value = newTransactions
                    _totalExpense.value = expense
                    _isLoadingTransactions.value = false
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Lỗi tải TransactionsBE: ${error.message}")
                    _isLoadingTransactions.value = false
                }
            })
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

    fun addCategory(listCategoryId: String, category: Category, onCategoryAdded: (String) -> Unit) {
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
            Log.d("AddExpense", "User ID hiện tại: $userId")
            if (userId == null) {
                Log.w("AddExpense", "Người dùng chưa xác thực.")
                return@launch
            }

            val dbRef = database
            Log.d("AddExpense", "Tham chiếu Database: $dbRef")
            Log.d("AddExpense", "Giá trị _account hiện tại: ${_account.value}") // Đã thay đổi
            val currentAccount = _account.value // Lấy trực tiếp đối tượng Account
            Log.d("AddExpense", "Thông tin tài khoản hiện tại: $currentAccount")

            if (currentAccount != null) {
                val userBalanceRef = dbRef.child("users").child(userId).child("account").child("balance") // Truy cập trực tiếp
                Log.d("AddExpense", "Tham chiếu đến balance: $userBalanceRef")
                val currentBalance = currentAccount.balance ?: 0.0
                Log.d("AddExpense", "Balance hiện tại: $currentBalance")
                val newBalance = currentBalance - newExpense.amount
                Log.d("AddExpense", "Balance mới sau khi trừ: $newBalance")

                userBalanceRef.setValue(newBalance).addOnSuccessListener {
                    Log.d("AddExpense", "Cập nhật balance thành công. Balance mới: $newBalance")
                    _totalBalance.update { it - newExpense.amount }
                    saveNewExpenseToTransactionBE(userId, newExpense)
                    saveNewTransactionForExpense(userId, newExpense)
                }.addOnFailureListener { error ->
                    Log.e("AddExpense", "Lỗi khi cập nhật balance: ${error.message}")
                }
            } else {
                Log.w("AddExpense", "Không tìm thấy tài khoản để trừ tiền.")
            }

            // Không cập nhật _expenses nữa vì chúng ta không lưu expense riêng trong categories
            _totalExpense.update { it + newExpense.amount }
            Log.d("AddExpense", "Tổng chi phí sau khi thêm: ${_totalExpense.value}")
        }
    }

    private fun saveNewExpenseToTransactionBE(userId: String, newExpense: Expense) {
        val dbRef = database
        val expenseId = dbRef.child("users")
            .child(userId)
            .child("transactionsBE")
            .push()
            .key ?: return

        dbRef.child("users")
            .child(userId)
            .child("transactionsBE")
            .child(expenseId)
            .setValue(newExpense.copy(id = expenseId))
            .addOnSuccessListener {
                Log.d("SaveExpenseBE", "Lưu expense vào transactionsBE thành công...")
                sendNewExpenseNotification(userId, newExpense)
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
            val expenseRef = database.child("users").child(uid).child("transactionsBE").child(expenseId)
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

    private fun saveNewTransactionForExpense(userId: String, expense: Expense) {
        val transactionId = database.child("users").child(userId).child("transactionsBE").push().key ?: return
        val newTransaction = TransactionBE(
            id = transactionId,
            categoryId = expense.categoryId,
            title = expense.title,
            amount = expense.amount, // Số tiền âm cho chi phí
            type = "expense",
            date = LocalDate.now().toString(),
            message = "Chi tiêu cho '${expense.title}'"
        )
        database.child("users").child(userId).child("transactionsBE").child(transactionId)
            .setValue(newTransaction)
            .addOnSuccessListener {
                Log.d("SaveTransaction", "Lưu transaction thành công cho expense: $transactionId")
            }
            .addOnFailureListener { error ->
                Log.e("SaveTransaction", "Lỗi khi lưu transaction cho expense: ${error.message}")
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