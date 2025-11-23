package com.example.carebuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carebuddy.data.local.FoodLogEntity
import com.example.carebuddy.data.remote.NutritionFood
import com.example.carebuddy.data.repositories.BackendNutritionRepository
import com.example.carebuddy.data.repositories.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val backendRepo: BackendNutritionRepository,
    private val localRepo: FoodRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<NutritionFood>>(emptyList())
    val searchResults: StateFlow<List<NutritionFood>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    // --- Today logs ---
    val todayEntries = localRepo.getToday() // Flow<List<FoodLogEntity>>

    private val _todayTotal = MutableStateFlow(0)
    val todayTotal: StateFlow<Int> = _todayTotal.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            todayEntries.collect { list: List<FoodLogEntity> ->
                _todayTotal.value = list.sumOf { it.calories }
            }
        }
    }

    fun searchFoods(query: String) {
        if (query.isBlank()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(200) // small debounce
            _isSearching.value = true
            _searchError.value = null
            try {
                val remoteList = backendRepo.searchFoods(query.trim())
                _searchResults.value = remoteList
            } catch (e: Exception) {
                e.printStackTrace()
                _searchError.value = e.message ?: "Search failed"
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun addNutritionFood(nf: NutritionFood, quantity: Double = 1.0) {
        viewModelScope.launch {
            localRepo.addFromNutritionFood(nf, quantity)
        }
    }

    fun clearToday() {
        viewModelScope.launch {
            localRepo.clearToday()
        }
    }
}

