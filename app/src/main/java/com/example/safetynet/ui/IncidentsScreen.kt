package com.example.safetynet.ui

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt
import com.example.safetynet.domain.SeverityLevel

@Composable
fun IncidentsScreen(mapViewModel: MapViewModel) {

    // ---------- To see if notification alerts are working or not---------
//    val context = LocalContext.current
//    val notificationHelper = remember { com.example.safetynet.utils.NotificationHelper(context) }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp)
//    ) {
//        Button(
//            onClick = {
//                notificationHelper.sendHighRiskAlert(
//                    "Test Alert",
//                    "If you see this, notifications are working"
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("TAP TO TEST NOTIFICATION")
//        }
//    }
//
//    Spacer(Modifier.height(16.dp))

    val pins by mapViewModel.safetyPins.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Reported Incidents",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        if (pins.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No incidents reported yet")
            }
        } else {
            // Simple list of reported incidents
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pins) { pin ->
                    IncidentCard(pin)
                }
            }
        }
    }
}

@Composable
fun IncidentCard(pin: com.example.safetynet.data.SafetyPin) {
    val baseSeverityColor = try {
        Color(pin.severity.colorHex.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }

    val badgeTextColor = when (pin.severity) {
        SeverityLevel.YELLOW -> Color(0xFF7A5900)
        SeverityLevel.GREEN -> Color(0xFF005000)
        else -> baseSeverityColor
    }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row (
            modifier = Modifier
                .height(IntrinsicSize.Min) // Matches the side bar to content height
                .fillMaxWidth()
        ) {
            // 1. Severity side bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(baseSeverityColor)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = pin.shortDescription,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Severity Badge
                    Surface(
                        color = baseSeverityColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = pin.severity.displayName.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeTextColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Description of the Incident
                if (pin.detailedDescription.isNotBlank() && pin.shortDescription != pin.detailedDescription) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pin.detailedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Timestamp
                val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(pin.timestamp))
                val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(pin.timestamp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reported",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = " • ",
                        color = MaterialTheme.colorScheme.outline
                    )

                    Text(
                        text = "$formattedDate at $formattedTime",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}