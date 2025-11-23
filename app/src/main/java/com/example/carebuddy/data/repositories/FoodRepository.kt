package com.example.carebuddy.data.repositories

import com.example.carebuddy.data.local.DailyCaloriesSummary
import com.example.carebuddy.data.local.FoodLogDao
import com.example.carebuddy.data.local.FoodLogEntity
import com.example.carebuddy.data.remote.NutritionFood
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val dao: FoodLogDao
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun today(): String = dateFormat.format(Date())

    fun getToday(): Flow<List<FoodLogEntity>> =
        dao.getLogsForDate(today())

    fun getDailySummaries(days: Int): Flow<List<DailyCaloriesSummary>> =
        dao.getDailySummaries(days)

    suspend fun add(entry: FoodLogEntity) =
        dao.insertLog(entry)

    suspend fun addFromNutritionFood(nf: NutritionFood, quantity: Double = 1.0) {
        val q = if (quantity <= 0.0) 1.0 else quantity
        val now = System.currentTimeMillis()
        val date = today()

        val entry = FoodLogEntity(
            name         = nf.food_name,
            calories     = (nf.calories * q).toInt(),
            carbs        = nf.carbs * q,
            protein      = nf.protein * q,
            fat          = nf.fat * q,
            quantity     = q,
            servingSizeG = nf.servingSizeG,
            timestamp    = now,
            date         = date
        )

        dao.insertLog(entry)
    }

    suspend fun clearToday() =
        dao.clearDay(today())
}

