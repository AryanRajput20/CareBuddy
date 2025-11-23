package com.example.carebuddy.data.remote

import com.google.gson.annotations.SerializedName

// simple DTOs used by local AiApi
data class ChatMessageDto(
    val role: String,
    val text: String
)

data class ChatRequest(
    val conversation: List<ChatMessageDto>
)

data class ChatResponse(
    @SerializedName("reply")
    val reply: String
)


