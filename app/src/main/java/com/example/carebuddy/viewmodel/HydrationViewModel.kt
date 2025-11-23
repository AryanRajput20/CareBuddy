package com.example.carebuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carebuddy.data.repositories.HydrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val repo: HydrationRepository
) : ViewModel() {

    // expose today's list and total
    val todayEntries = repo.getTodayEntries() // Flow<List<HydrationEntry>>

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getTodayEntries().collect { list ->
                _total.value = list.sumOf { it.ml }
            }
        }
    }

    fun addWater(ml: Int) {
        viewModelScope.launch { repo.insertEntry(ml) }
    }

    fun resetToday() {
        viewModelScope.launch { repo.clearSinceStartOfDay() }
    }
}


