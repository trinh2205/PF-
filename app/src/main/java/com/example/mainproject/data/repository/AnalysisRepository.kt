package com.example.mainproject.data.repository

import com.example.mainproject.data.model.*
import com.example.mainproject.FireBase.FirebaseAnalysisService
import kotlinx.coroutines.flow.Flow

class AnalysisRepository(private val firebaseService: FirebaseAnalysisService) {
    fun getTotalBalanceRealtime(): Flow<Double> = firebaseService.getTotalBalance()
    fun getTotalExpense(period: String): Flow<Double> = firebaseService.getTotalExpense(period)
    fun getExpenseBudget(): Flow<Budget?> = firebaseService.getExpenseBudget()
    fun getAllFinancialGoals(): Flow<List<FinancialGoal>> = firebaseService.getAllFinancialGoals()
    fun getIncomeExpenseTotals(period: String): Flow<Pair<Double, Double>> = firebaseService.getIncomeExpenseTotals(period)
    fun getChartData(selectedTab: String): Flow<Pair<List<Double>, List<Double>>> = firebaseService.getChartData(selectedTab)

    // ... các hàm khác để tương tác với Firebase
}