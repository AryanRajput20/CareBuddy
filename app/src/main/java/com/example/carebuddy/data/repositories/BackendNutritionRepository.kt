package com.example.carebuddy.data.repositories

import com.example.carebuddy.data.remote.NutritionBackendApi
import com.example.carebuddy.data.remote.NutritionBackendRequest
import com.example.carebuddy.data.remote.NutritionFood
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackendNutritionRepository @Inject constructor(
    private val api: NutritionBackendApi
) {

    suspend fun searchFoods(query: String): List<NutritionFood> {
        val resp = api.searchNutrition(
            NutritionBackendRequest(query = query)
        )

        // resp.items = List<NutritionItemDto>
        return resp.items.map { item ->
            NutritionFood(
                food_name    = item.name ?: item.food_name ?: query,
                calories     = item.calories ?: 0.0,
                servingSizeG = item.serving_size_g,
                fat          = item.fat_g ?: 0.0,
                protein      = item.protein_g ?: 0.0,
                carbs        = item.carbohydrates_total_g ?: 0.0,
                fiber        = item.fiber_g ?: 0.0)
        }
    }
}

