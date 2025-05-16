package com.example.mainproject.data.model

import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    val choices: List<Choice>? = null
)

data class Choice(
    val message: Message? = null
)

data class Message(
    val content: String? = null
)