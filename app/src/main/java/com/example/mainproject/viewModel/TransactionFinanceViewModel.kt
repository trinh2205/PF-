package com.example.mainproject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mainproject.Data.model.TransactionFirebase
import com.example.mainproject.FireBase.TransactionRepository

class TransactionFinanceViewModel : ViewModel() {
    private val repo = TransactionRepository()
    private val _transactions = MutableLiveData<List<TransactionFirebase>>()
    val transactions: LiveData<List<TransactionFirebase>> get() = _transactions

    fun loadTransactions() {
        repo.getTransactions {
            _transactions.value = it
        }
    }

    fun addTransaction(title: String, time: String, period: String, amount: String, isPositive: Boolean) {
        val tx = TransactionFirebase(
            title = title,
            time = time,
            period = period,
            amount = amount,
            isPositive = isPositive
        )
        repo.addTransaction(tx)
    }

    fun deleteTransaction(id: String) {
        repo.deleteTransaction(id)
    }
}
