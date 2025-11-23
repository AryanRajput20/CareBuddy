package com.example.carebuddy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(entry: FoodLogEntity)

    @Query("DELETE FROM food_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("DELETE FROM food_logs WHERE date = :date")
    suspend fun clearDay(date: String)

    @Query("SELECT * FROM food_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getLogsForDate(date: String): Flow<List<FoodLogEntity>>

    @Query("""
        SELECT date, SUM(calories) AS totalCalories
        FROM food_logs
        GROUP BY date
        ORDER BY date DESC
        LIMIT :days
    """)
    fun getDailySummaries(days: Int): Flow<List<DailyCaloriesSummary>>
}





