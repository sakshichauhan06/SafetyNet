package com.example.safetynet.ui

import MapScreen
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safetynet.SplashScreen
import com.example.safetynet.ui.auth.AuthViewModel
import com.example.safetynet.ui.auth.LoginScreen
import com.example.safetynet.ui.auth.OtpVerifyScreen
import com.example.safetynet.ui.auth.PhoneLoginScreen
import com.example.safetynet.ui.auth.SignupScreen
import com.example.safetynet.ui.auth.VerifyEmailScreen


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
                FloatingBottomBar(navController)
            }
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Log.d("Padding", "Bottom: ${innerPadding.calculateBottomPadding()}")
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("splash") {
                SplashScreen(navController, authViewModel)
            }
            composable(NavigationItem.Login.route) {
                LoginScreen(authViewModel, navController)
            }
            composable(NavigationItem.Signup.route) {
                SignupScreen(authViewModel, navController)
            }
            composable("verify_email") {
                VerifyEmailScreen(authViewModel, navController)
            }
            composable(NavigationItem.PhoneLogin.route) {
                PhoneLoginScreen(authViewModel, navController)
            }
            composable("otp_verify/{verificationId}") { backStackEntry ->
                val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                OtpVerifyScreen(authViewModel, navController, verificationId)
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
            composable(NavigationItem.ManageProfile.route) {
                ManageProfileScreen(navController)
            }
            composable(NavigationItem.Helpline.route) {
                HelplineScreen(navController)
            }
            composable(NavigationItem.SOS.route) {
                SOSScreen()
            }
            composable(NavigationItem.ReportBug.route) {
                ReportBugScreen(navController)
            }
            composable(NavigationItem.FAQ.route) {
                FAQScreen()
            }
        }
    }
}

@Composable
fun FloatingBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem.bottomBarItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Color(0xFFE8EAF6)
                                else Color.Transparent
                            )
                            .clickable(onClick = onClick)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon!!,
                                contentDescription = item.title,
                                tint = if (isSelected) Color(0xFF1A237E)
                                else Color.Gray
                            )
                            Text(
                                text = item.title,
                                color = if (isSelected) Color(0xFF1A237E)
                                else Color.Gray,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}




















