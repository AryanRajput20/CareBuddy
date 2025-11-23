package com.example.carebuddy.data.repositories

import com.example.carebuddy.data.local.HydrationDao
import com.example.carebuddy.data.local.HydrationEntry
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HydrationRepository @Inject constructor(
    private val dao: HydrationDao
) {
    fun getTodayEntries(): Flow<List<HydrationEntry>> = dao.getEntriesSince(startOfDay())

    suspend fun insertEntry(ml: Int) {
        dao.insert(HydrationEntry(ml = ml))
    }

    suspend fun clearSinceStartOfDay() {
        dao.clearSince(startOfDay())
    }

    private fun startOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
