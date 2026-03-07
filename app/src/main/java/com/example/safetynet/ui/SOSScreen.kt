package com.example.safetynet.ui

import android.R
import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.core.net.toUri
import kotlinx.coroutines.delay

@Composable
fun SOSScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Get data from ROOM
    val contactName = user?.emergencyContactName ?: "Emergency Contact"
    val phoneNumber = user?.emergencyContact ?: ""

    // Timer states
    var timeLeft by remember { mutableIntStateOf(5) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // When isTimerRunning becomes true, start the countdown
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // When timer hits 0, trigger the call
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:$phoneNumber".toUri()
                }
                context.startActivity(intent)
            }
            // Reset timer for next time
            isTimerRunning = false
            timeLeft = 5
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isTimerRunning) {
            Text(
                text = "EMERGENCY SOS",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Press the button below to call your emergency contact: $contactName",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (phoneNumber.isNotEmpty()) {
                        isTimerRunning = true
                    }
                },
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "SOS",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Countdown UI
            Text(
                text = "CALLING $contactName IN...",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$timeLeft",
                fontSize = 120.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(40.dp))

            // The "I'm Safe" / Cancel button
            Button(
                onClick = {
                    isTimerRunning = false
                    timeLeft = 5
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("CANCEL (I'M SAFE)", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (phoneNumber.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No contact number found. Please add one in 'Manage Profile'",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}