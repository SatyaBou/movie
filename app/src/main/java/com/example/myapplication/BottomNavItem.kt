package com.example.myapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object Movies : BottomNavItem("Movies", Icons.Default.PlayArrow)
    object Profile : BottomNavItem("Profile", Icons.Default.Person)
    object Settings : BottomNavItem("Settings", Icons.Default.Settings)
}
