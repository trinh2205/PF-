@file:Suppress("UNCHECKED_CAST")

package com.example.mainproject.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mainproject.data.repository.AnalysisRepository
import com.example.mainproject.ui.viewmodel.AnalysisViewModel

@Suppress("UNCHECKED_CAST")
class AnalysisViewModelFactory(private val analysisViewModelRepository: AnalysisRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
            // Giả sử bạn có một UserRepository
            // return AnalysisViewModel(UserRepository()) as T
            // Nếu ViewModel không có dependency, bạn vẫn có thể tạo nó
            return AnalysisViewModel(analysisViewModelRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}