package com.example.carebuddy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.carebuddy.data.MoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mood: MoodEntity): Long

    @Query("SELECT * FROM moods ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    @Query("DELETE FROM moods")
    suspend fun clearAll(): Int

    @Query("DELETE FROM moods WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM moods ORDER BY timestamp DESC")
    suspend fun getAllOnce(): List<MoodEntity>
}
