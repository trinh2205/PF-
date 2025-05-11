package com.example.mainproject.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
//import javax.inject.Inject

//@HiltViewModel
class FinanceViewModel (
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    private val _totalIncome = MutableStateFlow(0.0) // Nếu bạn có khoản thu nhập
    val totalIncome: StateFlow<Double> = _totalIncome

    init {
        loadBudgetAndTransactions()
    }

    private fun loadBudgetAndTransactions() {
        val userId = auth.currentUser?.uid
        userId?.let {
            loadBudget(it)
            loadTransactions(it)
        }
    }

    private fun loadBudget(userId: String) {
        val budgetRef = database.getReference("users").child(userId).child("budget")
        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val budgetValue = snapshot.getValue(Double::class.java) ?: 0.0
                _budget.value = budgetValue
                Log.d("FinanceViewModel", "Budget loaded: $budgetValue")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FinanceViewModel", "Error reading budget: ${error.message}")
            }
        })
    }

    private fun loadTransactions(userId: String) {
        val transactionsRef = database.getReference("users").child(userId).child("categories")
        transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var expense = 0.0
                var income = 0.0
                for (transactionSnapshot in snapshot.children) {
                    val amount = transactionSnapshot.child("amount").getValue(Double::class.java) ?: 0.0
                    // Dựa vào logic của bạn (ví dụ: trường 'type' của category)
                    // để phân biệt chi tiêu và thu nhập
                    val typeRaw = transactionSnapshot.child("type").getValue()
                    val type: String? = if (typeRaw is List<*>) {
                        (typeRaw.firstOrNull() as? List<*>)?.firstOrNull() as? String
                    } else {
                        typeRaw as? String // Trường hợp type trực tiếp là String (nếu có)
                    }

                    // Giả sử 'type' là ID của category, và bạn có một map 'listCategories'
                    // để xác định xem category đó là chi tiêu hay thu nhập
                    // (Bạn cần load 'listCategories' tương tự như cách bạn đã làm)

                    // Ví dụ đơn giản: giả sử amount dương là thu nhập, âm là chi tiêu
                    if (amount > 0) {
                        income += amount
                    } else {
                        expense += amount // Lưu ý: expense thường là giá trị tuyệt đối
                    }
                }
                _totalExpense.value = expense
                _totalIncome.value = income
                Log.d("FinanceViewModel", "Total expense: $expense, total income: $income")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FinanceViewModel", "Error reading transactions: ${error.message}")
            }
        })
    }

    // Các hàm khác để thêm/sửa transaction (nếu cần)
}