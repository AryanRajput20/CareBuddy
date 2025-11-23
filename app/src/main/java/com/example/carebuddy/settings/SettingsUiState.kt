package com.example.carebuddy.settings

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val autoShareLocation: Boolean = true,
    val sosSirenEnabled: Boolean = true,
    val sosVibrationEnabled: Boolean = true,
    val dailyCalorieGoal: Int = 2000,
    val dailyWaterGoalMl: Int = 2500,
    val sleepReminderEnabled: Boolean = false
)

