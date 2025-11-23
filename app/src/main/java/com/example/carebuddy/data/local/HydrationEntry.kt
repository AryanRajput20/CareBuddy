package com.example.carebuddy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hydration")
data class HydrationEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val ml: Int
)
