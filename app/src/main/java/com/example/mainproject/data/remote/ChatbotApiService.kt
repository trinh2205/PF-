package com.example.mainproject.data.remote

import com.example.mainproject.data.model.ChatCompletionRequest
import com.example.mainproject.data.model.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatbotApiService {
    @POST("v1/chat/completions")
    @Headers(
        "Authorization: Bearer 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", // Thay bằng API key thực tế
        "HTTP-Referer: https://www.sitename.com",    // Thay bằng referer thực tế
        "X-Title: SiteName",                         // Thay bằng title thực tế
        "Content-Type: application/json"
    )
    suspend fun getChatCompletion(
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/"
    }
}