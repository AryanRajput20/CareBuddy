package com.example.carebuddy.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface NutritionBackendApi {
    @POST("nutrition")
    suspend fun searchNutrition(@Body req: NutritionBackendRequest): NutritionBackendResponse
}





