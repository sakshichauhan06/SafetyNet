package com.example.safetynet.ui

import MapScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.navigation.NavHost // incorrect import
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import domain.Screen


@Composable
fun MainScreen(mapViewModel: MapViewModel) {
    val navController = rememberNavController()


    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, "Incidents") },
                    label = { Text("Incidents") },
                    selected = currentRoute == Screen.Incidents.route,
                    onClick = { navController.navigate(Screen.Incidents.route) }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, "Map") },
                    label = { Text("Map") },
                    selected = currentRoute == Screen.Map.route,
                    onClick = { navController.navigate(Screen.Map.route) }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == Screen.Profile.route,
                    onClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Map.route) { MapScreen(mapViewModel) }
            composable(Screen.Incidents.route) { IncidentsScreen(mapViewModel) }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}