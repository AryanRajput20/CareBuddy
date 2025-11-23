package com.example.carebuddy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carebuddy.data.local.FoodLogDao
import com.example.carebuddy.data.local.FoodLogEntity

@Database(
    entities = [FoodLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodLogDao

}
