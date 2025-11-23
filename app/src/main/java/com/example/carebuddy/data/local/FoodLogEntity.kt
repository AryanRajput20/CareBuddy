package com.example.carebuddy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val calories: Int,        // kcal
    val carbs: Double,        // grams
    val protein: Double,      // grams
    val fat: Double,          // grams
    val quantity: Double,     // 1, 2, 0.5 etc
    val servingSizeG: Double?,// per serving gram size if known
    val timestamp: Long,
    val date: String          // yyyy-MM-dd
)













