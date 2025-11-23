package com.example.carebuddy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carebuddy.emergency.EmergencyScreen
import com.example.carebuddy.settings.SettingsViewModel
import com.example.carebuddy.ui.components.BottomNavigationBar
import com.example.carebuddy.ui.components.AnimatedHomeScaffold
import com.example.carebuddy.ui.screens.MoodChatScreen
import com.example.carebuddy.ui.screens.NutritionScreen
import com.example.carebuddy.ui.screens.ProfileScreen
import com.example.carebuddy.ui.screens.LoginScreen
import com.example.carebuddy.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "login"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                composable("login") {
                    val authViewModel: AuthViewModel = hiltViewModel()

                    // agar already logged-in ho to direct home
                    LaunchedEffect(authViewModel.currentUser.collectAsState().value) {
                        if (authViewModel.currentUser.value != null) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoggedIn = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable("home") {
                    AnimatedHomeScaffold(navController = navController)
                }

                composable("nutrition") {
                    NutritionScreen()
                }

                composable("emergency") {
                    EmergencyScreen()
                }

                composable("chat") {
                    MoodChatScreen(onBack = { navController.popBackStack() })
                }

                composable("profile") {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val firebaseUser by authViewModel.currentUser.collectAsState()

                    ProfileScreen(
                        settingsViewModel = settingsViewModel,
                        userName = "",
                        userEmail = firebaseUser?.email ?: "guest@carebuddy.com",
                        userPhotoUrl = firebaseUser?.photoUrl?.toString(),
                        onManageEmergency = { navController.navigate("emergency") },
                        onLogout = {
                            authViewModel.logout {
                                navController.navigate("login") {
                                    popUpTo(0)          // clear back stack
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}