package com.example.mainproject.data.repository

import android.util.Log
import com.example.mainproject.data.model.ChatCompletionRequest
import com.example.mainproject.data.model.ChatCompletionResponse
import com.example.mainproject.data.model.ChatMessage
import com.example.mainproject.data.remote.ChatbotApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class ChatbotRepository(private val chatbotApiService: ChatbotApiService) {
    fun getChatbotResponse(userMessage: String): Flow<String?> = flow {
        try {
            val request = ChatCompletionRequest(
                model = "deepseek/deepseek-r1:free",
                messages = listOf(ChatMessage(role = "user", content = userMessage))
            )
            val response = chatbotApiService.getChatCompletion(request)
            emit(response.choices?.firstOrNull()?.message?.content)
        } catch (e: Exception) {
            Log.e("ChatbotRepository", "Error fetching chatbot response: ${e.message}", e)
            throw IOException("Failed to get chatbot response", e)
        }
    }

    companion object {
        fun create(): ChatbotRepository {
            val retrofit = Retrofit.Builder()
                .baseUrl(ChatbotApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(ChatbotApiService::class.java)
            return ChatbotRepository(apiService)
        }
    }
}