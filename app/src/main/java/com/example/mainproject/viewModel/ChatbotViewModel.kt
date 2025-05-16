package com.example.mainproject.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mainproject.data.repository.ChatbotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException

class ChatbotViewModel(private val chatbotRepository: ChatbotRepository) : ViewModel() {
    private val _chatbotResponse = MutableStateFlow<String?>(null)
    val chatbotResponse: StateFlow<String?> = _chatbotResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getResponse(userMessage: String) {
        _isLoading.value = true
        viewModelScope.launch {
            chatbotRepository.getChatbotResponse(userMessage)
                .catch { error ->
                    _errorMessage.value = error.message
                    _isLoading.value = false
                }
                .collect { response ->
                    _chatbotResponse.value = response
                    _isLoading.value = false
                }
        }
    }

    fun clearResponse() {
        _chatbotResponse.value = null
    }

    companion object {
        fun provideFactory(chatbotRepository: ChatbotRepository = ChatbotRepository.create()): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatbotViewModel(chatbotRepository) as T
                }
            }
    }
}