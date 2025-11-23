package com.example.carebuddy.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AiApi {
    @POST("chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}

