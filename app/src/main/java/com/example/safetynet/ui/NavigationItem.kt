package com.example.safetynet.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Splash : NavigationItem("splash", "Splash")
    // Auth Screens (No icons needed here)
    object Login : NavigationItem("login", "Login")
    object Signup : NavigationItem("signup", "Signup")

    object PhoneLogin : NavigationItem("phone_login", "Phone Login")

    // Main Screens (Icons needed)
    object Map : NavigationItem("map", "MAP", Icons.Filled.Map)
    object Incidents : NavigationItem("incidents", "ALERTS", Icons.Filled.NotificationsActive)
    object Connect: NavigationItem("connect", "CONNECT", Icons.Filled.Groups)
    object Profile : NavigationItem("profile", "PROFILE", Icons.Filled.Person)

    object ManageProfile : NavigationItem("manage_profile", "Manage Profile")

    object Helpline : NavigationItem("helpline", "Helpline")

    object SOS : NavigationItem("sos", "SOS")

    object ReportBug : NavigationItem("report_bug", "Report Bug")

    object FAQ: NavigationItem("faq", "FAQ")

    // Helper list for the Bottom Bar to loop through
    companion object {
        val bottomBarItems = listOf(Map, Incidents, Connect, Profile)
    }
}