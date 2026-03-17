package com.example.safetynet.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun VerifyEmailScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Navigate to Map automatically once the staus changes to Authenticated
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate("map") {
                popUpTo("verify_email") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verify your Email",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've sent a link to your email. Please click it to continue.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.checkEmailVerificationStatus() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I've Verified My Email")
        }

        TextButton(onClick = { viewModel.sendVerificationEmail() }) {
            Text("Resend Verification Email")
        }

        TextButton(onClick = {
            navController.navigate("login") { popUpTo(0) }
        }) {
            Text("Back to Login", color = Color.Blue)
        }
    }
}