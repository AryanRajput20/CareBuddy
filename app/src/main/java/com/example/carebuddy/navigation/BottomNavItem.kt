package com.example.carebuddy.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Nutrition : BottomNavItem("nutrition", "Nutrition", Icons.Default.Fastfood)
    object Emergency : BottomNavItem("emergency", "Emergency", Icons.Default.LocalDrink)
    object Chat : BottomNavItem("chat", "Chat", Icons.Default.Chat)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}
