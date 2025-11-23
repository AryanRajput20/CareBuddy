package com.example.carebuddy.data.remote

// Request for your backend
data class NutritionBackendRequest(val query: String)

// Generic response wrapper from your backend
data class NutritionBackendResponse(
    val items: List<NutritionItemDto> = emptyList()
)

// DTO representing one item returned from the backend (defensive - many optional fields)
// NutritionItemDto.kt
data class NutritionItemDto(
    val name: String? = null,
    val food_name: String? = null,

    val calories: Double? = null,
    val kcal: Double? = null,
    val energy_kcal: Double? = null,

    val protein_g: Double? = null,
    val protein: Double? = null,

    val carbs_g: Double? = null,
    val carbohydrate_g: Double? = null,
    val total_carbohydrate_g: Double? = null,
    val carbs: Double? = null,

    // 👇 NEW: API Ninjas ka actual field
    val carbohydrates_total_g: Double? = null,

    val fat_g: Double? = null,
    val total_fat_g: Double? = null,
    val fat: Double? = null,

    val fiber_g: Double? = null,
    val dietary_fiber_g: Double? = null,
    val fiber: Double? = null,

    val serving_qty: Double? = null,
    val serving_size_g: Double? = null,
    val serving_unit: String? = null
)
