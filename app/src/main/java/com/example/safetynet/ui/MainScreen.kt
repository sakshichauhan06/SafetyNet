package com.example.safetynet.ui

import MapScreen
import android.net.http.SslCertificate.saveState
import android.os.Build
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safetynet.R
import com.example.safetynet.SplashScreen
import com.example.safetynet.ui.auth.AuthViewModel
import com.example.safetynet.ui.auth.LoginScreen
import com.example.safetynet.ui.auth.OtpVerifyScreen
import com.example.safetynet.ui.auth.PhoneLoginScreen
import com.example.safetynet.ui.auth.SignupScreen
import com.example.safetynet.ui.auth.VerifyEmailScreen
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.cameraPosition
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    mapViewModel: MapViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // get the current route from the backstack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = NavigationItem.bottomBarItems.any { it.route == currentRoute }
    val showBottomBar = NavigationItem.bottomBarItems.any { it.route == currentRoute }

    val userLocation by mapViewModel.userLocation
    val hasLocationPermission = userLocation != null

    val cameraPositionState = rememberCameraPositionState()

    // Set it in ViewModel
    LaunchedEffect(cameraPositionState) {
        mapViewModel.setCameraPositionState(cameraPositionState)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = { scope.launch { drawerState.close() } },
                navController = navController,
//                authViewModel = authViewModel
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    TopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLogoClick = { mapViewModel.resetMapView() },
                        onSosClick = { navController.navigate(NavigationItem.SOS.route) },
                        onLocationClick = { mapViewModel.centerOnUserLocation() },
                        hasLocationPermission = hasLocationPermission,
                        isLocationActive = mapViewModel.isTrackingLocation.collectAsState().value
                    )
                }
            },
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
                    MapScreen(
                        mapViewModel = mapViewModel,
                        cameraPositionState = cameraPositionState)
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
}

@Composable
fun FloatingBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color(0xFFDAE7E5),
            tonalElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .border(1.dp, color = Color.White.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem.bottomBarItems.forEach { item ->
                    val isSelected = currentRoute == item.route

                    Surface(
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFE8EAF6) else Color.Transparent,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onMenuClick: () -> Unit,
    onLogoClick: () -> Unit,
    onSosClick: () -> Unit,
    onLocationClick: () -> Unit,
    hasLocationPermission: Boolean,
    isLocationActive: Boolean
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onLogoClick)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ellipse_logo),
                    contentDescription = "Logo Icon",
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit,
//                    tint = Color(0xFF1A237E),
                )
                Text(
                    text = "SafetyNet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }
        },
        // Hamburger Menu
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF1A237E),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        // Left side of TopBar
        actions = {
            // Location Icon
            LocationBeacon(
                hasPermission = hasLocationPermission,
                isActive = isLocationActive,
                onClick = onLocationClick
            )

            Spacer(modifier = Modifier.width(8.dp))

            // SOS Pilll Button
            SosButton(onClick = onSosClick)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFDAE7E5)
        )
    )
}

@Composable
fun LocationBeacon(
    hasPermission: Boolean,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val icon = when {
        !hasPermission -> Icons.Default.LocationDisabled
        isActive -> Icons.Default.MyLocation
        else -> Icons.Default.LocationSearching
    }

    val tint = when {
        !hasPermission -> Color(0xFFB00020)
        isActive -> Color(0xFF2ECC71)
        else -> Color(0xFF1A237E)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "location_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = if (hasPermission) "Re-center map" else "Location denied",
            tint = tint,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun SosButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFB00020),
        modifier = Modifier
            .height(36.dp)
            .padding(end = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Emergency,
                contentDescription = "Emergency SOS",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "SOS",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DrawerContent(
    onClose: () -> Unit,
    navController: NavController
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "SafetyNet",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Menu items
            NavigationDrawerItem(
                label = { Text("Profile") },
                selected = false,
                onClick = {
                    navController.navigate(NavigationItem.Profile.route)
                    onClose()
                }
            )
        }
    }
}


















