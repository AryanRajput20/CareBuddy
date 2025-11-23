package com.example.carebuddy.data.repositories

import com.example.carebuddy.data.MoodEntity
import com.example.carebuddy.data.local.MoodDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val dao: MoodDao
) {
    suspend fun addMood(text: String, timestamp: Long): Long {
        val m = MoodEntity(text = text, timestamp = timestamp)
        return dao.insert(m)
    }

    fun observeMoods(): Flow<List<MoodEntity>> = dao.getAllMoods()

    suspend fun clearAll(): Int = dao.clearAll()

    suspend fun deleteById(id: Long): Int = dao.deleteById(id)

    suspend fun getAllOnce(): List<MoodEntity> = dao.getAllOnce()
}
