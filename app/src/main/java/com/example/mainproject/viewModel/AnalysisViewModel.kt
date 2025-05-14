package com.example.mainproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.model.Expense
import com.example.mainproject.data.model.FinancialGoal
import com.example.mainproject.data.repository.AnalysisRepository
import com.example.mainproject.data.repository.IncomeExpenseSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChartBarData(
    val label: String,
    val expense: Float,
    val income: Float
)

class AnalysisViewModel(private val analysisRepository: AnalysisRepository) : ViewModel() {
    private val _chartData = MutableStateFlow<List<ChartBarData>>(emptyList())
    val chartData: StateFlow<List<ChartBarData>> = _chartData
    private val _selectedChartTab = MutableStateFlow("Daily")
    val selectedChartTab: StateFlow<String> = _selectedChartTab
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // StateFlow để theo dõi tổng thu nhập và chi phí
    private val _incomeExpenseSummary = MutableStateFlow(IncomeExpenseSummary(0.0, 0.0))
    val incomeExpenseSummary: StateFlow<IncomeExpenseSummary> = _incomeExpenseSummary

    // StateFlow để theo dõi danh sách các mục tiêu tài chính
    private val _financialGoals = MutableStateFlow<List<FinancialGoal>>(emptyList())
    val financialGoals: StateFlow<List<FinancialGoal>> = _financialGoals

    // Các StateFlow và logic khác của ViewModel (ví dụ: cho biểu đồ) sẽ ở đây

    // Các hàm để cập nhật _incomeExpenseSummary (ví dụ)
    fun setIncomeExpenseSummary(summary: IncomeExpenseSummary) {
        _incomeExpenseSummary.value = summary
    }

    // Các hàm để cập nhật _financialGoals (ví dụ)
    fun setFinancialGoals(goals: List<FinancialGoal>) {
        _financialGoals.value = goals
    }

    init {
        fetchChartData(_selectedChartTab.value)
    }

    fun setSelectedChartTab(tab: String) {
        _selectedChartTab.value = tab
        fetchChartData(tab)
    }

    private fun fetchChartData(tab: String) {
        viewModelScope.launch {
            when (tab) {
                "Daily" -> analysisRepository.getDailyIncomeExpenseRealtime().collectLatest { (expenses, incomes) ->
                    _chartData.value = mapToChartBarData(expenses, incomes, "daily")
                }
                "Weekly" -> analysisRepository.getWeeklyIncomeExpenseRealtime().collectLatest { (expenses, incomes) ->
                    _chartData.value = mapToChartBarData(expenses, incomes, "weekly")
                }
                "Monthly" -> analysisRepository.getMonthlyIncomeExpenseRealtime().collectLatest { (expenses, incomes) ->
                    _chartData.value = mapToChartBarData(expenses, incomes, "monthly")
                }
                "Year" -> analysisRepository.getYearlyIncomeExpenseRealtime().collectLatest { (expenses, incomes) ->
                    _chartData.value = mapToChartBarData(expenses, incomes, "yearly")
                }
            }
        }
    }

    private fun mapToChartBarData(
        expenses: List<Expense>,
        incomes: List<Expense>,
        period: String
    ): List<ChartBarData> {
        val chartDataList = mutableListOf<ChartBarData>()
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val labels = mutableListOf<String>()
        val expenseMap = mutableMapOf<String, Double>()
        val incomeMap = mutableMapOf<String, Double>()

        when (period) {
            "daily" -> {
                val daysOfWeek = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7").reversed()
                for (i in 0..6) {
                    calendar.time = today
                    calendar.add(Calendar.DAY_OF_MONTH, -i)
                    labels.add(dateFormatter.format(calendar.time))
                }
            }
            "weekly" -> {
                for (i in 0..3) {
                    calendar.time = today
                    calendar.add(Calendar.WEEK_OF_YEAR, -i)
                    val startOfWeekCalendar = calendar.clone() as Calendar
                    startOfWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                    labels.add("Tuần ${getWeekOfMonth(startOfWeekCalendar)}")
                }
            }
            "monthly" -> {
                val currentMonth = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                for (i in 0..5) {
                    calendar.set(year, currentMonth - i, 1)
                    labels.add(SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time))
                }
                labels.reverse()
            }
            "yearly" -> {
                val currentYear = calendar.get(Calendar.YEAR)
                for (i in 0..4) {
                    labels.add((currentYear - i).toString().takeLast(2))
                }
                labels.reverse()
            }
        }
        labels.reverse()

        expenses.forEach { expense ->
            val formattedDate = when (period) {
                "daily" -> expense.date
                "weekly" -> getStartOfWeekDate(expense.date)
                "monthly" -> expense.date.substring(0, 7) // YYYY-MM
                "yearly" -> expense.date.substring(0, 4) // YYYY
                else -> expense.date
            }
            expenseMap[formattedDate] = (expenseMap[formattedDate] ?: 0.0) + expense.amount
        }

        incomes.forEach { income ->
            val formattedDate = when (period) {
                "daily" -> income.date
                "weekly" -> getStartOfWeekDate(income.date)
                "monthly" -> income.date.substring(0, 7) // YYYY-MM
                "yearly" -> income.date.substring(0, 4) // YYYY
                else -> income.date
            }
            incomeMap[formattedDate] = (incomeMap[formattedDate] ?: 0.0) + income.amount
        }

        labels.forEach { label ->
            val expenseValue = expenseMap[label] ?: 0.0
            val incomeValue = incomeMap[label] ?: 0.0
            chartDataList.add(ChartBarData(label, expenseValue.toFloat(), incomeValue.toFloat()))
        }

        Log.d("AnalysisVM", "$period Chart Data: $chartDataList")
        return chartDataList
    }

    private fun getWeekOfMonth(calendar: Calendar): Int {
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        var weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        if (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            weekOfMonth--
        }
        return weekOfMonth
    }

    private fun getStartOfWeekDate(dateString: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = dateFormatter.parse(dateString) ?: Date()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return dateFormatter.format(calendar.time)
    }
}