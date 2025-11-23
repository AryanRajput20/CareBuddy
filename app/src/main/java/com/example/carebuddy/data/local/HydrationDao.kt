package com.example.carebuddy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HydrationEntry)

    @Query("SELECT * FROM hydration WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getEntriesSince(since: Long): Flow<List<HydrationEntry>>

    @Query("DELETE FROM hydration WHERE timestamp >= :since")
    suspend fun clearSince(since: Long)
}
