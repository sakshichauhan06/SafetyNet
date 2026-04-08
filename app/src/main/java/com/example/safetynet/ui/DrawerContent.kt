package com.example.safetynet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DrawerContent(
    onClose: () -> Unit,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    mapViewModel: MapViewModel,
    currentRoute: String?
) {
    val user by profileViewModel.currentUser.collectAsState()
    val userName = user?.name ?: "User Name"
    val userEmail = user?.email ?: "user@example.com"
    val guardianLevel = 4 // Later
    val isProtected = mapViewModel.isTrackingLocation.collectAsState().value

    ModalDrawerSheet(
        drawerContentColor = Color(0xFFF5F5F7),
        modifier = Modifier.width(250.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // User Profile Header
            Column(
                modifier = Modifier.padding(top = 40.dp, bottom = 24.dp)
            ) {
                // Profile picture with verification badge
                Box(
                    modifier = Modifier.size(64.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFE57373), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Verification badge
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color(0xFF1A237E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "verified",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User name
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )

                // Safety Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Status dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (isProtected) Color(0xFF2ECC71) else Color.Gray,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isProtected) "Safety Status: Protected" else "Safety Status: Inactive",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isProtected) Color(0xFF2ECC71) else Color.Gray
                    )
                }

                // Guardian level
                Text(
                    text = "GUARDIAN LEVEL $guardianLevel",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFF1A237E).copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Menu item - Safety Vault
            DrawerMenuItem(
                icon = Icons.Default.Lock,
                label = "Safety Vault",
                isSelected = currentRoute == "safety_vault",
                isHighlighted = true,
                onClick = {
                    navController.navigate("safety_vault")
                    onClose()
                }
            )

            // Menu item - Trusted Contacts
            DrawerMenuItem(
                icon = Icons.Default.PeopleAlt,
                label = "Trusted Contacts",
                isSelected = currentRoute == NavigationItem.Profile.route,
                onClick = {
                    navController.navigate(NavigationItem.Profile.route)
                    onClose()
                }
            )

            // Menu item - Activity Log
            DrawerMenuItem(
                icon = Icons.Default.History,
                label = "Activity Log",
                isSelected = currentRoute == "activity_log",
                onClick = {
                    navController.navigate("activity_log")
                }
            )

            // Menu item - Settings
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = currentRoute == "settings",
                onClick = {
                    navController.navigate("settings")
                    onClose()
                }
            )

            // Menu items - Help Center
            DrawerMenuItem(
                icon = Icons.Default.Help,
                label = "Help Center",
                isSelected = currentRoute == NavigationItem.FAQ.route,
                onClick = {
                    navController.navigate(NavigationItem.FAQ.route)
                    onClose()
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Emergency System Status Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE8EAF6),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(Color(0xFFB00020), RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Emergency System",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFB00020),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Direct connection to local responders is currently enabled.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Sign out button
            TextButton(
                onClick = {
                    profileViewModel.logout()
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "SIGN OUT",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected && isHighlighted -> Color(0xFF1A237E)
        isSelected -> Color(0xFF1A237E).copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isSelected && isHighlighted -> Color.White
        isSelected -> Color(0xFF1A237E)
        else -> Color(0xFF1A237E).copy(alpha = 0.7f)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )

            if (isSelected && isHighlighted) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}