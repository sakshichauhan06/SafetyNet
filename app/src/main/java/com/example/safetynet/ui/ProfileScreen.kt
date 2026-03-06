package com.example.safetynet.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.safetynet.ui.auth.AuthState
import com.example.safetynet.ui.auth.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    val authState = authViewModel.authState.collectAsState()
    val user by profileViewModel.currentUser.collectAsState()

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    val userName = user?.name ?: "Loading..."
    val inital = userName.take(1).uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Circle Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFE0E0FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inital,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A0A1C)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A0A1C)
        )

        Spacer(modifier = Modifier.height(40.dp))

        val options = listOf(
            "Manage Profile",
            "SOS",
            "Helpline numbers",
            "FAQ",
            "Report a bug"
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            options.forEach { option ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp)
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                profileViewModel.logout()
                authViewModel.signOut()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .padding(bottom = 40.dp)
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, Color.Red)
        ) {
            Text(
                text = "Log Out",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}