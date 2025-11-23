package com.example.carebuddy.emergency

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EmergencyContact::class],
    version = 1,
    exportSchema = false
)
abstract class EmergencyDatabase : RoomDatabase() {

    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        @Volatile
        private var INSTANCE: EmergencyDatabase? = null

        fun getInstance(context: Context): EmergencyDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    EmergencyDatabase::class.java,
                    "emergency_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
