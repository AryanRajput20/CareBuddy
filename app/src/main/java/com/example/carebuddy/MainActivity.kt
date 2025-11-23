package com.example.carebuddy
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carebuddy.navigation.AppNavHost
import com.example.carebuddy.settings.SettingsViewModel
import com.example.carebuddy.ui.theme.CareBuddyTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint   // 👈 IMPORTANT
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Simple (non-Hilt) SettingsViewModel
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()

            CareBuddyTheme(
                darkTheme = settingsState.isDarkMode   // 👈 theme toggle yaha se
            ) {
                AppNavHost(
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}



