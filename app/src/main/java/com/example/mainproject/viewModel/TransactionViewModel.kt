package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainproject.Data.model.ListCategories
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
class TransactionViewModel (
    private val database: FirebaseDatabase, // Inject FirebaseDatabase
    private val auth: FirebaseAuth // Inject FirebaseAuth
) : ViewModel() {

    private val _categories = MutableStateFlow<Map<String, ListCategories>>(emptyMap())
    val categories: StateFlow<Map<String, ListCategories>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val userId = auth.currentUser?.uid // Lấy userId của người dùng hiện tại
        userId?.let {
            val categoriesRef = database.getReference("users").child(it).child("listCategories") // Đi theo tài khoản user

            categoriesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedCategories = mutableMapOf<String, ListCategories>()
                    for (categorySnapshot in snapshot.children) {
                        val category = categorySnapshot.getValue(ListCategories::class.java)
                        category?.let { fetchedCategories[categorySnapshot.key!!] = it }
                    }
                    _categories.value = fetchedCategories
                    Log.d("TransactionViewModel", "Categories loaded: ${_categories.value}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TransactionViewModel", "Error reading categories: ${error.message}")
                }
            })
        } ?: run {
            Log.d("TransactionViewModel", "User not logged in, categories not loaded.")
            _categories.value = emptyMap()
        }
    }

    // Bạn có thể thêm các hàm khác để thêm, sửa, xóa category nếu cần
}