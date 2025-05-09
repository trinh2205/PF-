package com.example.mainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Budget
import com.example.mainproject.data.model.FinancialGoal
import com.example.mainproject.data.repository.AnalysisRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AnalysisViewModel(private val repository: AnalysisRepository) : ViewModel() {

    private val _totalBalance = MutableStateFlow("Loading...")
    val totalBalance: StateFlow<String> = _totalBalance.asStateFlow()

    private val _totalExpense = MutableStateFlow("Loading...")
    val totalExpense: StateFlow<String> = _totalExpense.asStateFlow()

    private val _expenseBudgetProgress = MutableStateFlow<Float?>(null)
    val expenseBudgetProgress: StateFlow<Float?> = _expenseBudgetProgress.asStateFlow()

    private val _expenseBudgetTotal = MutableStateFlow<String?>("Loading...")
    val expenseBudgetTotal: StateFlow<String?> = _expenseBudgetTotal.asStateFlow()

    private val _financialGoals = MutableStateFlow<List<FinancialGoal>>(emptyList())
    val financialGoals: StateFlow<List<FinancialGoal>> = _financialGoals.asStateFlow()

    private val _incomeExpenseSummary = MutableStateFlow(Pair("Loading...", "Loading..."))
    val incomeExpenseSummary: StateFlow<Pair<String, String>> = _incomeExpenseSummary.asStateFlow()

    private val _chartData = MutableStateFlow(Pair(emptyList<Double>(), emptyList<Double>()))
    val chartData: StateFlow<Pair<List<Double>, List<Double>>> = _chartData.asStateFlow()

    init {
        startRealtimeUpdates()
    }

    private fun startRealtimeUpdates() {
        viewModelScope.launch {
            repository.getTotalBalanceRealtime()
                .map { formatCurrency(it) }
                .collectLatest { _totalBalance.value = it }
        }

        viewModelScope.launch {
            repository.getTotalExpense("month")
                .map { formatCurrency(it, isExpense = true) }
                .collectLatest {
                    _totalExpense.value = it
                    updateExpenseBudgetProgress(it)
                }
        }

        viewModelScope.launch {
            repository.getExpenseBudget()
                .map { it?.amount?.let { amount -> formatCurrency(amount) } ?: "N/A" }
                .collectLatest { _expenseBudgetTotal.value = it }
        }

        viewModelScope.launch {
            combine(_totalExpense, repository.getExpenseBudget()) { totalExpenseStr, budget ->
                val totalExpenseValue = totalExpenseStr.replace(Regex("[^\\d.-]"), "").toDoubleOrNull() ?: 0.0
                budget?.let { if (it.amount > 0) (totalExpenseValue / it.amount).toFloat().coerceIn(0f, 1f) else 0f }
            }
                .collectLatest { _expenseBudgetProgress.value = it }
        }

        viewModelScope.launch {
            repository.getAllFinancialGoals()
                .collectLatest { _financialGoals.value = it }
        }

        viewModelScope.launch {
            repository.getIncomeExpenseTotals("month")
                .map { (income, expense) -> Pair(formatCurrency(income), formatCurrency(expense, isExpense = true)) }
                .collectLatest { _incomeExpenseSummary.value = it }
        }

        getChartData("Year") // Initial load for chart data
    }

    fun getChartData(selectedTab: String) {
        viewModelScope.launch {
            repository.getChartData(selectedTab)
                .collectLatest { _chartData.value = it }
        }
    }

    private fun updateExpenseBudgetProgress(currentExpenseStr: String) {
        viewModelScope.launch {
            val currentExpense = currentExpenseStr.replace(Regex("[^\\d.-]"), "").toDoubleOrNull() ?: 0.0
            repository.getExpenseBudget()
                .collectLatest { budget ->
                    budget?.let { if (it.amount > 0) (currentExpense / it.amount).toFloat().coerceIn(0f, 1f) else 0f }
                        ?.let { _expenseBudgetProgress.value = it }
                }
        }
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return format.format(amount)
    }

    private fun formatCurrency(amount: Double, isExpense: Boolean = false): String {
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return (if (isExpense && amount != 0.0) "-" else "") + format.format(kotlin.math.abs(amount))
    }
}