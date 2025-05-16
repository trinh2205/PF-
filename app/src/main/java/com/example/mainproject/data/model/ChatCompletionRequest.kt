package com.example.mainproject.data.model

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val role: String,
    val content: String
)