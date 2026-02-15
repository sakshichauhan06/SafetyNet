package com.example.safetynet.ui

import MapScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SafetyCheck
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.NavHost // incorrect import
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safetynet.SplashScreen
import com.example.safetynet.domain.Screen
import com.example.safetynet.ui.auth.AuthViewModel
import com.example.safetynet.ui.auth.LoginScreen
import com.example.safetynet.ui.auth.SignupScreen


@Composable
fun MainScreen(
    mapViewModel: MapViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    // get the current route from the backstack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = NavigationItem.bottomBarItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Login.route) {
                LoginScreen(authViewModel, navController)
            }
            composable(NavigationItem.Signup.route) {
                SignupScreen(authViewModel, navController)
            }
            composable(NavigationItem.Incidents.route) {
                IncidentsScreen(mapViewModel)
            }
            composable(NavigationItem.Map.route) {
                MapScreen(mapViewModel)
            }
            composable(NavigationItem.Profile.route) {
                ProfileScreen(authViewModel, navController)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        // Observe where we are in the app and update the bottom bar icon's highlight accordingly
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // loop through the list of which we created in NavigationItem.kt
        NavigationItem.bottomBarItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // pop up to the start destination of the graph to avoid massive backstack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same screen when re-selecting
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = item.icon!!,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}




















