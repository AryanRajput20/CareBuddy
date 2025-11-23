package com.example.carebuddy.data.remote

import com.google.gson.annotations.SerializedName

data class NutritionFood(
    @SerializedName("name")
    val food_name: String,

    @SerializedName("calories")
    val calories: Double,

    @SerializedName("serving_size_g")
    val servingSizeG: Double?,

    @SerializedName("fat_total_g")
    val fat: Double,

    @SerializedName("protein_g")
    val protein: Double,

    @SerializedName("carbohydrates_total_g")
    val carbs: Double,

    @SerializedName("fiber_g")
    val fiber: Double,


)







