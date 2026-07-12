package com.example.safetynet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.safetynet.data.SafetyPin
import com.example.safetynet.domain.SeverityLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPinDetailsScreen(
    pin: SafetyPin,
    onCancel: () -> Unit,
    onSosClick: () -> Unit,
    onDelete: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Incident Details",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    SosButton(onClick = onSosClick)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFFF6FAFF))
                .padding(18.dp)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            val baseSeverityColor = try {
                Color(pin.severity.colorHex.toColorInt())
            } catch (e: Exception) {
                Color.Gray
            }

            val tagTextColor = when (pin.severity) {
                SeverityLevel.YELLOW -> Color(0xFF7A5900)
                SeverityLevel.GREEN -> Color(0xFF005000)
                else -> baseSeverityColor
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tags
                Surface(
                    color = baseSeverityColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = pin.severity.displayName.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = tagTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

//        Spacer(modifier = Modifier.height(8.dp))

                // Timestamp
                val formattedDate = SimpleDateFormat("MMM D, YYYY", Locale.getDefault()).format(Date(pin.timestamp))
                val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(pin.timestamp))
                Text(
                    text = "$formattedDate at $formattedTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }


            // Incident Title
            // Map Image
            // Incident Details
            // Detailed Info
            // Status Timeline
            // Download Report Button
            // Contact Lead Investigator Button
            // Delete Button
        }
    }
}


















