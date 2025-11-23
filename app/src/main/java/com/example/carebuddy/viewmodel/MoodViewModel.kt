package com.example.carebuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carebuddy.data.MoodEntity
import com.example.carebuddy.data.repositories.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val repo: MoodRepository
) : ViewModel() {

    private val _lastMood = MutableStateFlow<String?>(null)
    val lastMood: StateFlow<String?> = _lastMood.asStateFlow()

    init {
        // load latest mood (non-blocking)
        viewModelScope.launch {
            val list = repo.getAllOnce()
            if (list.isNotEmpty()) {
                _lastMood.value = list.maxByOrNull { it.timestamp }?.text
            }
        }
    }

    fun saveMood(text: String) {
        viewModelScope.launch {
            repo.addMood(text, System.currentTimeMillis())
            _lastMood.value = text
        }
    }
}



