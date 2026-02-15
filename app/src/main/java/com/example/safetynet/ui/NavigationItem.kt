package com.example.safetynet.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // Auth Screens (No icons needed here)
    object Login : NavigationItem("login", "Login")
    object Signup : NavigationItem("signup", "Signup")

    // Main Screens (Icons needed)
    object Incidents : NavigationItem("incidents", "Incidents", Icons.Filled.SafetyCheck)
    object Map : NavigationItem("map", "Map", Icons.Filled.Map)
    object Profile : NavigationItem("profile", "Profile", Icons.Filled.Person)

    // Helper list for the Bottom Bar to loop through
    companion object {
        val bottomBarItems = listOf(Incidents, Map, Profile)
    }
}