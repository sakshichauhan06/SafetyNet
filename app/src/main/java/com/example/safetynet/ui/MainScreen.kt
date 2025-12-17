package com.example.safetynet.ui

import MapScreen
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SafetyCheck
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.navigation.NavHost // incorrect import
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import domain.Screen


@Composable
fun MainScreen(mapViewModel: MapViewModel) {
    val navController = rememberNavController()
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }


    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                bottomNavItems.forEachIndexed { index, bottomNavItem ->
                    NavigationBarItem(
                        selected = index == selectedItemIndex,
                        onClick = { selectedItemIndex = index },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if(bottomNavItem.badges != 0) {
                                        Badge {
                                            Text(
                                                text = bottomNavItem.badges.toString()
                                            )
                                        }
                                    } else if(bottomNavItem.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (index == selectedItemIndex)
                                            bottomNavItem.selectedItem
                                        else
                                            bottomNavItem.unselectedItem,
                                    contentDescription = bottomNavItem.title
                                )
                            }
                        },
                        label = {
                            Text(bottomNavItem.title)
                        }
                    )
                }

//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.List, "Incidents") },
//                    label = { Text("Incidents") },
//                    selected = currentRoute == Screen.Incidents.route,
//                    onClick = { navController.navigate(Screen.Incidents.route) }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Place, "Map") },
//                    label = { Text("Map") },
//                    selected = currentRoute == Screen.Map.route,
//                    onClick = { navController.navigate(Screen.Map.route) }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Person, "Profile") },
//                    label = { Text("Profile") },
//                    selected = currentRoute == Screen.Profile.route,
//                    onClick = { navController.navigate(Screen.Profile.route) }
//                )
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

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Incidents",
        route = Screen.Incidents.route,
        selectedItem = Icons.Filled.SafetyCheck,
        unselectedItem = Icons.Outlined.SafetyCheck,
        hasNews = false,
        badges = 0
    ),
    BottomNavItem(
        title = "Map",
        route = Screen.Map.route,
        selectedItem = Icons.Filled.Map,
        unselectedItem = Icons.Outlined.Map,
        hasNews = false,
        badges = 0
    ),
    BottomNavItem(
        title = "Profile",
        route = Screen.Profile.route,
        selectedItem = Icons.Filled.Person,
        unselectedItem = Icons.Outlined.Person,
        hasNews = false,
        badges = 0
    ),
)

data class BottomNavItem (
    val title: String,
    val route: String,
    val selectedItem: ImageVector,
    val unselectedItem: ImageVector,
    val hasNews: Boolean, // dot
    val badges: Int
)