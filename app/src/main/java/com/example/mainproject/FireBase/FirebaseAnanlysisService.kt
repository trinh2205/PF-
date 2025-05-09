package com.example.mainproject.FireBase

import com.example.mainproject.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirebaseAnalysisService {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    private fun getUserDocument() = usersCollection.document(currentUserId ?: "")
    private fun getTransactionsCollection() = getUserDocument().collection("transactionBE")
    private fun getBudgetsCollection() = getUserDocument().collection("budgets")
    private fun getFinancialGoalsCollection() = getUserDocument().collection("financial_goals")
    private fun getCategoriesCollection() = getUserDocument().collection("categories")

    // Lấy tổng số dư (ví dụ: tính toán từ tất cả giao dịch)
    fun getTotalBalance(): Flow<Double> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }
        val listenerRegistration = getTransactionsCollection()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                var balance = 0.0
                snapshot?.documents?.forEach { document ->
                    val transaction = document.toObject(Transaction::class.java)
                    transaction?.let {
                        balance += if (it.type == "income") it.amount else -it.amount
                    }
                }
                trySend(balance).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Lấy tổng chi phí trong một khoảng thời gian
    fun getTotalExpense(period: String): Flow<Double> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        val startDate = when (period) {
            "daily" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
            "weekly" -> calendar.apply { add(Calendar.WEEK_OF_YEAR, -1) }.timeInMillis
            "monthly" -> calendar.apply { add(Calendar.MONTH, -1) }.timeInMillis
            "yearly" -> calendar.apply { add(Calendar.YEAR, -1) }.timeInMillis
            else -> 0L
        }

        val listenerRegistration = getTransactionsCollection()
            .whereEqualTo("type", "expense")
            .whereGreaterThanOrEqualTo("date", startDate.toString())
            .whereLessThanOrEqualTo("date", endDate.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                var totalExpense = 0.0
                snapshot?.documents?.forEach { document ->
                    val expense = document.toObject(Transaction::class.java)
                    expense?.let { totalExpense += it.amount }
                }
                trySend(totalExpense).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Lấy ngân sách chi tiêu (cần query theo category và period)
    fun getExpenseBudget(): Flow<Budget?> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }
        // Giả định bạn có một cách để xác định categoryId cho "Expense Budget" và period hiện tại
        val expenseCategoryId = "expense_budget_id"
        val period = "monthly"
        // Cần điều chỉnh query để phù hợp với cấu trúc dữ liệu budget của bạn
        val listenerRegistration = getBudgetsCollection()
            .whereEqualTo("categoryId", expenseCategoryId)
            .whereEqualTo("period", period)
            .limit(1) // Lấy budget hiện tại
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val budget = snapshot?.documents?.firstOrNull()?.toObject(Budget::class.java)
                trySend(budget).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Lấy tất cả mục tiêu tài chính
    fun getAllFinancialGoals(): Flow<List<FinancialGoal>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }
        val listenerRegistration = getFinancialGoalsCollection()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val goals = snapshot?.documents?.mapNotNull { it.toObject(FinancialGoal::class.java) } ?: emptyList()
                trySend(goals).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Lấy tóm tắt thu nhập và chi phí (cần tổng hợp theo khoảng thời gian)
    fun getIncomeExpenseTotals(period: String): Flow<Pair<Double, Double>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }

        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        val startDate = when (period) {
            "daily" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
            "weekly" -> calendar.apply { add(Calendar.WEEK_OF_YEAR, -1) }.timeInMillis
            "monthly" -> calendar.apply { add(Calendar.MONTH, -1) }.timeInMillis
            "yearly" -> calendar.apply { add(Calendar.YEAR, -1) }.timeInMillis
            else -> 0L
        }

        val listenerRegistration = getTransactionsCollection()
            .whereGreaterThanOrEqualTo("date", startDate.toString())
            .whereLessThanOrEqualTo("date", endDate.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                var totalIncome = 0.0
                var totalExpense = 0.0
                snapshot?.documents?.forEach { document ->
                    val transaction = document.toObject(Transaction::class.java)
                    transaction?.let {
                        if (it.type == "income") totalIncome += it.amount else totalExpense += it.amount
                    }
                }
                trySend(Pair(totalIncome, totalExpense)).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Hàm để lấy dữ liệu biểu đồ (cần tổng hợp theo khoảng thời gian và loại giao dịch)
    fun getChartData(selectedTab: String): Flow<Pair<List<Double>, List<Double>>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }

        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        val startDate = when (selectedTab) {
            "daily" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis // Lấy 7 ngày gần nhất
            "weekly" -> calendar.apply { add(Calendar.WEEK_OF_YEAR, -4) }.timeInMillis // Lấy 4 tuần gần nhất
            "monthly" -> calendar.apply { add(Calendar.MONTH, -6) }.timeInMillis // Lấy 6 tháng gần nhất
            "year" -> calendar.apply { add(Calendar.YEAR, -1) }.timeInMillis // Lấy 1 năm gần nhất
            else -> 0L
        }

        val listenerRegistration = getTransactionsCollection()
            .whereGreaterThanOrEqualTo("date", startDate.toString())
            .whereLessThanOrEqualTo("date", endDate.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val incomeData = mutableMapOf<String, Double>()
                val expenseData = mutableMapOf<String, Double>()

                snapshot?.documents?.forEach { document ->
                    val transaction = document.toObject(Transaction::class.java)
                    transaction?.let {
                        val formattedDate = when (selectedTab) {
                            "daily" -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(it.date.toLong()))
                            "weekly" -> "Week " + Calendar.getInstance().apply { timeInMillis = it.date.toLong() }.get(Calendar.WEEK_OF_YEAR)
                            "monthly" -> SimpleDateFormat("MMM", Locale.getDefault()).format(Date(it.date.toLong()))
                            "year" -> SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(it.date.toLong()))
                            else -> ""
                        }

                        if (it.type == "income") {
                            incomeData[formattedDate] = (incomeData[formattedDate] ?: 0.0) + it.amount
                        } else {
                            expenseData[formattedDate] = (expenseData[formattedDate] ?: 0.0) + it.amount
                        }
                    }
                }

                val sortedIncome = incomeData.toList().sortedBy { it.first }.toMap().values.toList()
                val sortedExpense = expenseData.toList().sortedBy { it.first }.toMap().values.toList()
                trySend(Pair(sortedIncome, sortedExpense)).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    // ... các hàm khác để thêm/sửa/xóa dữ liệu nếu cần
}