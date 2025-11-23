package com.example.carebuddy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carebuddy.data.MoodEntity

@Database(
    entities = [
        MoodEntity::class,
        HydrationEntry::class,
        FoodLogEntity::class
    ],
    version = 4,            // 👈 VERSION BUMP kiya
    exportSchema = false
)
abstract class CareBuddyDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun foodDao(): FoodLogDao
}



