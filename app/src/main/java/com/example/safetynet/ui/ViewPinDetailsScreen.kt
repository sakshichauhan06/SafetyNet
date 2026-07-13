package com.example.safetynet.ui

import androidx.compose.foundation.Image
import com.example.safetynet.R
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.safetynet.data.SafetyPin
import com.example.safetynet.domain.SeverityLevel
import com.example.safetynet.ui.components.StatusTimeline
import com.example.safetynet.ui.components.TimelineEvent
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
                        style = MaterialTheme.typography.titleMedium
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
                .padding(vertical = 8.dp, horizontal = 24.dp)
                .padding(paddingValues)
        ) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

                // Timestamp
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Incident Reported Date",
                        tint = Color.Gray,
                        modifier = Modifier.size(11.dp)
                    )

                    val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(pin.timestamp))
                    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(pin.timestamp))
                    Text(
                        text = "$formattedDate  •  $formattedTime",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Incident Title
            Text(
                text = pin.shortDescription,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Map Image
            Image(
                painter = painterResource(id = R.drawable.location_placeholder),
                contentDescription = "Location Placeholder",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(36.dp))
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Incident Details
            Text(
                text = "REPORT OVERVIEW",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = pin.detailedDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Timeline
            val timelineEvents = listOf(
                TimelineEvent(
                    title = "Reported",
                    description = "Incident logged and broadcast to local network.",
                    timestamp = pin.timestamp,
                    isCompleted = true
                ),
                TimelineEvent(
                    title = "Under Review",
                    description = "Community moderators are verifying the report details.",
                    timestamp = pin.timestamp + 3600000,
                    isCompleted = false
                ),
                TimelineEvent(
                    title = "Resolved",
                    description = "Incident marked as closed by the reporter.",
                    timestamp = 0,
                    isCompleted = false
                )
            )
            StatusTimeline(
                events = timelineEvents,
                modifier = Modifier.padding(vertical = 16.dp)
            )


            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            // Download Report Button
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = "Download Incident PDF",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Text("Download Incident Report", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Support Button
            OutlinedButton (
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Contact Support",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )

                    Text("Contact Support", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete Button
            OutlinedButton (
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Incident",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


















