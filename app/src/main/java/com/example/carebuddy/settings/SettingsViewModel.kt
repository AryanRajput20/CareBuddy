package com.example.carebuddy.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update



class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleAutoShareLocation(enabled: Boolean) {
        _uiState.update { it.copy(autoShareLocation = enabled) }
    }

    fun toggleSosSiren(enabled: Boolean) {
        _uiState.update { it.copy(sosSirenEnabled = enabled) }
    }

    fun toggleSosVibration(enabled: Boolean) {
        _uiState.update { it.copy(sosVibrationEnabled = enabled) }
    }

    fun toggleSleepReminder(enabled: Boolean) {
        _uiState.update { it.copy(sleepReminderEnabled = enabled) }
    }

    fun updateCalorieGoal(text: String) {
        val value = text.toIntOrNull() ?: return
        _uiState.update { it.copy(dailyCalorieGoal = value) }
    }

    fun updateWaterGoal(text: String) {
        val value = text.toIntOrNull() ?: return
        _uiState.update { it.copy(dailyWaterGoalMl = value) }
    }
}