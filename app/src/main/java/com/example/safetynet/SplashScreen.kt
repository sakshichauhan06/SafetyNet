package com.example.safetynet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.safetynet.ui.auth.AuthViewModel
import com.example.safetynet.ui.theme.safeContentPadding
import com.google.android.play.integrity.internal.u
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel) {
    // Controls when the bottom buttons appear
    var showAuthButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // 1. Force a refresh from the server
        try {
            user?.getIdToken(true)?.await()
            user?.reload()?.await()
            // Log the result so we can see it in Android Studio
            android.util.Log.d("SafetyNet", "Reload complete. Verified status: ${auth.currentUser?.isEmailVerified}")
        } catch (e: Exception) {
            android.util.Log.e("SafetyNet", "Reload failed: ${e.message}")
        }

        delay(1000)

        val finalUser = FirebaseAuth.getInstance().currentUser

        if (finalUser != null) {
            val isVerified = finalUser.isEmailVerified
            val isPasswordUser = finalUser.providerData.any { it.providerId == "password" }

            if (isPasswordUser && !isVerified) {
                navController.navigate("verify_email") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("map") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            showAuthButtons = true
        }
    }

    Box( // layer 0: The Indigo Background
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(color = Color(0xFF001B3D))
    ) {
        // --- LAYER 1: THE GLASS CARD SHELL ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp) // The 0.8cm inset
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.01f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                )
        ) {
            // --- LAYER 2: THE CORNER GLOWS ---
            // Top-Left Quarter Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.09f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(0f, 0f),
                            radius = 400f
                        )
                    )
            )

            // Bottom-Right Quarter Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                            radius = 500f
                        )
                    )
            )

            // --- LAYER 3: THE ACTUAL CONTENT ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // TOP SECTION: Logo & Branding (Always Visible)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.circular_logo),
                            contentDescription = "SafetyNet Logo",
                            modifier = Modifier.size(90.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Text(
                        text = "SafetyNet",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        modifier = Modifier.padding(top = 28.dp)
                    )

                    Text(
                        text = "See what's ahead, skip the risk.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6F84AC),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // BOTTOM SECTION: Buttons & Footer (Animated Visibility)
                AnimatedVisibility(
                    visible = showAuthButtons,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800)) +
                            slideInVertically(
                                // Starts the animation slightly below its final resting position
                                initialOffsetY = { fullHeight -> fullHeight / 3 },
                                animationSpec = tween(durationMillis = 800)
                            )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                // TODO: Navigate to registration/signup screen
                                // navController.navigate("signup")
                            },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF001B3D)
                            )
                        ) {
                            Text("Get Started →", modifier = Modifier.padding(vertical = 8.dp))
                        }

                        OutlinedButton(
                            onClick = {
                                // Navigate to Login Screen
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            shape = RoundedCornerShape(50.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                        ) {
                            Text("Log In", color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = "🛡️ TRUSTED SECURITY PROTOCOL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}