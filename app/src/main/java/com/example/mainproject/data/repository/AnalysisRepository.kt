package com.example.mainproject.data.repository

import android.util.Log
import com.example.mainproject.data.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class IncomeExpenseSummary(val totalIncome: Double, val totalExpense: Double)

class AnalysisRepository {

    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private val expenseReference = userId?.let { database.getReference("users/$it/transactionsBE/expenses") }
    private val incomeReference = userId?.let { database.getReference("users/$it/transactionsBE/income") }
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getTransactionsForDateRange(
        snapshot: DataSnapshot,
        startDate: Date,
        endDate: Date
    ): List<Expense> {
        val transactionsList = mutableListOf<Expense>()
        for (childSnapshot in snapshot.children) {
            val transaction = childSnapshot.getValue(Expense::class.java)
            transaction?.let {
                try {
                    val transactionDate = dateFormatter.parse(it.date)
                    if (transactionDate != null &&
                        (transactionDate.after(startDate) || transactionDate.equals(startDate)) &&
                        (transactionDate.before(endDate) || transactionDate.equals(endDate))
                    ) {
                        transactionsList.add(it)
                    }
                } catch (e: Exception) {
                    Log.e("AnalysisRepo", "Error parsing date: ${it.date}", e)
                }
            }
        }
        return transactionsList
    }

    fun getDailyIncomeExpenseRealtime(): Flow<Pair<List<Expense>, List<Expense>>> = callbackFlow {
        val expenseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val calendar = Calendar.getInstance()
                val today = calendar.time
                calendar.add(Calendar.DAY_OF_MONTH, -6)
                val sevenDaysAgo = calendar.time
                val expenses = getTransactionsForDateRange(snapshot, sevenDaysAgo, today)
                Log.d("AnalysisRepo", "Daily Expenses (${dateFormatter.format(sevenDaysAgo)} - ${dateFormatter.format(today)}): ${expenses.joinToString { it.amount.toString() }}")
                trySend(Pair(expenses, emptyList()))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Expense listener cancelled", error.toException())
                close(error.toException())
            }
        }

        val incomeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val calendar = Calendar.getInstance()
                val today = calendar.time
                calendar.add(Calendar.DAY_OF_MONTH, -6)
                val sevenDaysAgo = calendar.time
                val incomes = getTransactionsForDateRange(snapshot, sevenDaysAgo, today)
                Log.d("AnalysisRepo", "Daily Incomes (${dateFormatter.format(sevenDaysAgo)} - ${dateFormatter.format(today)}): ${incomes.joinToString { it.amount.toString() }}")
                trySend(Pair(emptyList(), incomes))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Income listener cancelled", error.toException())
                close(error.toException())
            }
        }

        expenseReference?.addValueEventListener(expenseListener)
        incomeReference?.addValueEventListener(incomeListener)

        awaitClose {
            expenseReference?.removeEventListener(expenseListener)
            incomeReference?.removeEventListener(incomeListener)
        }
    }

    fun getWeeklyIncomeExpenseRealtime(): Flow<Pair<List<Expense>, List<Expense>>> = callbackFlow {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        val expenseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calendar.time = today
                calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_WEEK, -28)
                val fourWeeksAgo = calendar.time
                calendar.time = today
                calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + Calendar.SATURDAY)
                val endOfWeek = calendar.time
                val expenses = getTransactionsForDateRange(snapshot, fourWeeksAgo, endOfWeek)
                Log.d("AnalysisRepo", "Weekly Expenses (${dateFormatter.format(fourWeeksAgo)} - ${dateFormatter.format(endOfWeek)}): ${expenses.joinToString { it.amount.toString() }}")
                trySend(Pair(expenses, emptyList()))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Expense listener cancelled (weekly)", error.toException())
                close(error.toException())
            }
        }

        val incomeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calendar.time = today
                calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_WEEK, -28)
                val fourWeeksAgo = calendar.time
                calendar.time = today
                calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + Calendar.SATURDAY)
                val endOfWeek = calendar.time
                val incomes = getTransactionsForDateRange(snapshot, fourWeeksAgo, endOfWeek)
                Log.d("AnalysisRepo", "Weekly Incomes (${dateFormatter.format(fourWeeksAgo)} - ${dateFormatter.format(endOfWeek)}): ${incomes.joinToString { it.amount.toString() }}")
                trySend(Pair(emptyList(), incomes))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Income listener cancelled (weekly)", error.toException())
                close(error.toException())
            }
        }

        expenseReference?.addValueEventListener(expenseListener)
        incomeReference?.addValueEventListener(incomeListener)

        awaitClose {
            expenseReference?.removeEventListener(expenseListener)
            incomeReference?.removeEventListener(incomeListener)
        }
    }

    fun getMonthlyIncomeExpenseRealtime(): Flow<Pair<List<Expense>, List<Expense>>> = callbackFlow {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        val expenseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calendar.time = today
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val currentMonthStart = calendar.time
                calendar.add(Calendar.MONTH, -5)
                val sixMonthsAgoStart = calendar.time
                calendar.time = today
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val currentMonthEnd = calendar.time
                val expenses = getTransactionsForDateRange(snapshot, sixMonthsAgoStart, currentMonthEnd)
                Log.d("AnalysisRepo", "Monthly Expenses (${dateFormatter.format(sixMonthsAgoStart)} - ${dateFormatter.format(currentMonthEnd)}): ${expenses.joinToString { it.amount.toString() }}")
                trySend(Pair(expenses, emptyList()))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Expense listener cancelled (monthly)", error.toException())
                close(error.toException())
            }
        }

        val incomeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calendar.time = today
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val currentMonthStart = calendar.time
                calendar.add(Calendar.MONTH, -5)
                val sixMonthsAgoStart = calendar.time
                calendar.time = today
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val currentMonthEnd = calendar.time
                val incomes = getTransactionsForDateRange(snapshot, sixMonthsAgoStart, currentMonthEnd)
                Log.d("AnalysisRepo", "Monthly Incomes (${dateFormatter.format(sixMonthsAgoStart)} - ${dateFormatter.format(currentMonthEnd)}): ${incomes.joinToString { it.amount.toString() }}")
                trySend(Pair(emptyList(), incomes))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Income listener cancelled (monthly)", error.toException())
                close(error.toException())
            }
        }

        expenseReference?.addValueEventListener(expenseListener)
        incomeReference?.addValueEventListener(incomeListener)

        awaitClose {
            expenseReference?.removeEventListener(expenseListener)
            incomeReference?.removeEventListener(incomeListener)
        }
    }

    fun getYearlyIncomeExpenseRealtime(): Flow<Pair<List<Expense>, List<Expense>>> = callbackFlow {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        val expenseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calendar.time = today
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val currentYearStart = calendar.time
                calendar.add(Calendar.YEAR, -4)
                val fiveYearsAgoStart = calendar.time
                calendar.time = today
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
                val currentYearEnd = calendar.time
                val expenses = getTransactionsForDateRange(snapshot, fiveYearsAgoStart, currentYearEnd)
                Log.d("AnalysisRepo", "Yearly Expenses (${dateFormatter.format(fiveYearsAgoStart)} - ${dateFormatter.format(currentYearEnd)}): ${expenses.joinToString { it.amount.toString() }}")
                trySend(Pair(expenses, emptyList()))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Expense listener cancelled (yearly)", error.toException())
                close(error.toException())
            }
        }

        val incomeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calendar.time = today
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val currentYearStart = calendar.time
                calendar.add(Calendar.YEAR, -4)
                val fiveYearsAgoStart = calendar.time
                calendar.time = today
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
                val currentYearEnd = calendar.time
                val incomes = getTransactionsForDateRange(snapshot, fiveYearsAgoStart, currentYearEnd)
                Log.d("AnalysisRepo", "Yearly Incomes (${dateFormatter.format(fiveYearsAgoStart)} - ${dateFormatter.format(currentYearEnd)}): ${incomes.joinToString { it.amount.toString() }}")
                trySend(Pair(emptyList(), incomes))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AnalysisRepo", "Income listener cancelled (yearly)", error.toException())
                close(error.toException())
            }
        }

        expenseReference?.addValueEventListener(expenseListener)
        incomeReference?.addValueEventListener(incomeListener)

        awaitClose {
            expenseReference?.removeEventListener(expenseListener)
            incomeReference?.removeEventListener(incomeListener)
        }
    }
}