//package com.example.mainproject.FireBase
//
//import com.example.mainproject.data.model.*
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Date
//import java.util.Locale
//
//class FirebaseAnalysisService {
//
//    private val firestore = FirebaseFirestore.getInstance()
//    private val usersCollection = firestore.collection("users")
//    private val auth = FirebaseAuth.getInstance()
//
//    private val currentUserId: String?
//        get() = auth.currentUser?.uid
//
//    private fun getUserDocument() = usersCollection.document(currentUserId ?: "")
//    private fun getTransactionsCollection() = getUserDocument().collection("transactionBE")
//    private fun getExpensesCollection() = getTransactionsCollection().collection("expenses")
//    private fun getIncomeCollection() = getTransactionsCollection().collection("income")
//    private fun getBudgetsCollection() = getUserDocument().collection("budgets")
//    private fun getFinancialGoalsCollection() = getUserDocument().collection("financial_goals")
//    private fun getCategoriesCollection() = getUserDocument().collection("categories")
//
//    // Lấy tổng số dư (ví dụ: tính toán từ tất cả giao dịch)
//    fun getTotalBalance(): Flow<Double> = callbackFlow {
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//        val listenerRegistration = getTransactionsCollection()
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                var balance = 0.0
//                snapshot?.documents?.forEach { document ->
//                    val transaction = document.toObject(Transaction::class.java)
//                    transaction?.let {
//                        balance += if (it.type == "income") it.amount else -it.amount
//                    }
//                }
//                trySend(balance).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Lấy tổng chi phí trong một khoảng thời gian
//    fun getTotalExpense(period: String): Flow<Double> = callbackFlow {
//        // ... (hàm này giữ nguyên nếu bạn vẫn cần nó)
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//        val calendar = Calendar.getInstance()
//        val endDate = calendar.timeInMillis
//        val startDate = when (period) {
//            "daily" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
//            "weekly" -> calendar.apply { add(Calendar.WEEK_OF_YEAR, -1) }.timeInMillis
//            "monthly" -> calendar.apply { add(Calendar.MONTH, -1) }.timeInMillis
//            "yearly" -> calendar.apply { add(Calendar.YEAR, -1) }.timeInMillis
//            else -> 0L
//        }
//
//        val listenerRegistration = getExpensesCollection()
//            .whereGreaterThanOrEqualTo("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startDate)))
//            .whereLessThanOrEqualTo("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDate)))
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                var totalExpense = 0.0
//                snapshot?.documents?.forEach { document ->
//                    val expense = document.toObject(Transaction::class.java) // Hoặc model Expense riêng
//                    expense?.let { totalExpense += it.amount }
//                }
//                trySend(totalExpense).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Lấy ngân sách chi tiêu (cần query theo category và period)
//    fun getExpenseBudget(): Flow<Budget?> = callbackFlow {
//        // ... (hàm này giữ nguyên nếu bạn vẫn cần nó)
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//        // Giả định bạn có một cách để xác định categoryId cho "Expense Budget" và period hiện tại
//        val expenseCategoryId = "expense_budget_id"
//        val period = "monthly"
//        // Cần điều chỉnh query để phù hợp với cấu trúc dữ liệu budget của bạn
//        val listenerRegistration = getBudgetsCollection()
//            .whereEqualTo("categoryId", expenseCategoryId)
//            .whereEqualTo("period", period)
//            .limit(1) // Lấy budget hiện tại
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                val budget = snapshot?.documents?.firstOrNull()?.toObject(Budget::class.java)
//                trySend(budget).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Lấy tất cả mục tiêu tài chính
//    fun getAllFinancialGoals(): Flow<List<FinancialGoal>> = callbackFlow {
//        // ... (hàm này giữ nguyên nếu bạn vẫn cần nó)
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//        val listenerRegistration = getFinancialGoalsCollection()
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                val goals = snapshot?.documents?.mapNotNull { it.toObject(FinancialGoal::class.java) } ?: emptyList()
//                trySend(goals).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Lấy tóm tắt thu nhập và chi phí (cần tổng hợp theo khoảng thời gian)
//    fun getIncomeExpenseTotals(period: String): Flow<Pair<Double, Double>> = callbackFlow {
//        // ... (hàm này giữ nguyên nếu bạn vẫn cần nó)
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//
//        val calendar = Calendar.getInstance()
//        val endDate = calendar.timeInMillis
//        val startDate = when (period) {
//            "daily" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
//            "weekly" -> calendar.apply { add(Calendar.WEEK_OF_YEAR, -1) }.timeInMillis
//            "monthly" -> calendar.apply { add(Calendar.MONTH, -1) }.timeInMillis
//            "yearly" -> calendar.apply { add(Calendar.YEAR, -1) }.timeInMillis
//            else -> 0L
//        }
//
//        val listenerRegistration = getTransactionsCollection()
//            .whereGreaterThanOrEqualTo("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startDate)))
//            .whereLessThanOrEqualTo("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDate)))
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                var totalIncome = 0.0
//                var totalExpense = 0.0
//                snapshot?.documents?.forEach { document ->
//                    val transaction = document.toObject(Transaction::class.java)
//                    transaction?.let {
//                        if (it.type == "income") totalIncome += it.amount else totalExpense += it.amount
//                    }
//                }
//                trySend(Pair(totalIncome, totalExpense)).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Hàm chung để lấy dữ liệu theo ngày
//    fun getTotalByDay(transactionType: String): Flow<Map<String, Double>> = callbackFlow {
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//
//        val collection = if (transactionType.lowercase(Locale.getDefault()) == "expense") getExpensesCollection() else getIncomeCollection()
//
//        val listenerRegistration = collection
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val dailyData = mutableMapOf<String, Double>()
//                snapshot?.documents?.forEach { document ->
//                    val date = document.getString("date")
//                    val amount = document.getDouble("amount")
//
//                    if (date != null && amount != null) {
//                        dailyData[date] = (dailyData[date] ?: 0.0) + amount
//                    }
//                }
//                trySend(dailyData).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Hàm chung để lấy dữ liệu theo tuần
//    fun getTotalByWeek(transactionType: String): Flow<Map<String, Double>> = callbackFlow {
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//
//        val collection = if (transactionType.lowercase(Locale.getDefault()) == "expense") getExpensesCollection() else getIncomeCollection()
//        val calendar = Calendar.getInstance(Locale.getDefault())
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        val listenerRegistration = collection
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val weeklyData = mutableMapOf<String, Double>()
//
//                snapshot?.documents?.forEach { document ->
//                    val dateString = document.getString("date")
//                    val amount = document.getDouble("amount")
//
//                    if (dateString != null && amount != null) {
//                        try {
//                            val date = dateFormat.parse(dateString)
//                            calendar.time = date
//                            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
//                            val year = calendar.get(Calendar.YEAR)
//                            val weekKey = "$year-W$weekOfYear"
//                            weeklyData[weekKey] = (weeklyData[weekKey] ?: 0.0) + amount
//                        } catch (e: Exception) {
//                            println("Error parsing date: $dateString - ${e.message}")
//                        }
//                    }
//                }
//                trySend(weeklyData).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Hàm chung để lấy dữ liệu theo năm
//    fun getTotalByYear(transactionType: String): Flow<Map<String, Double>> = callbackFlow {
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//
//        val collection = if (transactionType.lowercase(Locale.getDefault()) == "expense") getExpensesCollection() else getIncomeCollection()
//        val calendar = Calendar.getInstance(Locale.getDefault())
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        val listenerRegistration = collection
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val yearlyData = mutableMapOf<String, Double>()
//
//                snapshot?.documents?.forEach { document ->
//                    val dateString = document.getString("date")
//                    val amount = document.getDouble("amount")
//
//                    if (dateString != null && amount != null) {
//                        try {
//                            val date = dateFormat.parse(dateString)
//                            calendar.time = date
//                            val year = calendar.get(Calendar.YEAR)
//                            val yearKey = year.toString()
//                            yearlyData[yearKey] = (yearlyData[yearKey] ?: 0.0) + amount
//                        } catch (e: Exception) {
//                            println("Error parsing date: $dateString - ${e.message}")
//                        }
//                    }
//                }
//                trySend(yearlyData).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Lấy tất cả danh mục
//    fun getAllCategories(): Flow<List<Category>> = callbackFlow {
//        // ... (hàm này giữ nguyên nếu bạn vẫn cần nó)
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//        val listenerRegistration = getCategoriesCollection()
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                val categories = snapshot?.documents?.mapNotNull { it.toObject(Category::class.java) } ?: emptyList()
//                trySend(categories).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//
//    // Lấy các giao dịch trong một khoảng thời gian
//    fun getTransactionsByPeriod(period: String): Flow<List<TransactionBE>> = callbackFlow {
//        // ... (hàm này giữ nguyên nếu bạn vẫn cần nó)
//        val userId = currentUserId
//        if (userId == null) {
//            close(Exception("User not authenticated"))
//            return@callbackFlow
//        }
//
//        val calendar = Calendar.getInstance()
//        val endDateMillis = calendar.timeInMillis
//        val startDateMillis = when (period) {
//            "day" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis // Hôm qua
//            "week" -> calendar.apply {
//                val dayOfWeek = get(Calendar.DAY_OF_WEEK)
//                val diff = (dayOfWeek - Calendar.MONDAY + 7) % 7
//                add(Calendar.DAY_OF_YEAR, -diff) // Lấy ngày đầu tuần
//            }.timeInMillis
//            "month" -> calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis // Ngày đầu tháng
//            "year" -> calendar.apply { set(Calendar.DAY_OF_YEAR, 1) }.timeInMillis // Ngày đầu năm
//            else -> 0L
//        }
//
//        val listenerRegistration = getTransactionsCollection()
//            .whereGreaterThanOrEqualTo("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startDateMillis)))
//            .whereLessThanOrEqualTo("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDateMillis)))
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                val transactions = snapshot?.documents?.mapNotNull { it.toObject(TransactionBE::class.java) } ?: emptyList()
//                trySend(transactions).isSuccess
//            }
//        awaitClose { listenerRegistration.remove() }
//    }
//}