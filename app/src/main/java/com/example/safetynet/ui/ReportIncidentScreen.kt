package com.example.safetynet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightbulbCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetynet.domain.IncidentType
import com.example.safetynet.domain.SeverityLevel
import com.google.android.gms.maps.model.LatLng

@Composable
fun ReportIncidentScreen(
    initialLocation: LatLng,
    onSubmit: (
            incidentType: IncidentType,
            severity: SeverityLevel,
            details: String,
            isAnonymous: Boolean,
            finalLocation: LatLng
            ) -> Unit,
    onCancel: () -> Unit,
    onSosClick: () -> Unit
) {
    var selectedIncident by remember { mutableStateOf<IncidentType?>(null) }
    var incidentDetails by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var selectedSeverity by remember { mutableStateOf<SeverityLevel?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header TopBar
        SafetyHeaderCard(onSosClick = onSosClick)

        Spacer(modifier = Modifier.height(24.dp))

        // Severity Section
        Text(
            "RISK SEVERITY",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        SeveritySelector(
            selectedSeverity = selectedSeverity,
            onSeveritySelected = { selectedSeverity = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Anonymous Section
        AnonymousToggle(
            isAnonymous = isAnonymous,
            onToggle = { isAnonymous = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Incident Section
        Text(
            "INCIDENT DETAILS",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = incidentDetails,
            onValueChange = { incidentDetails = it },
            placeholder = { Text("Describe what happened...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F7),
                focusedContainerColor = Color(0xFFF5F5F7)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Location Section
        IncidentLocationSection(
            location = selectedLocation,
            onChangeLocation = { showLocationPicker = true }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons Section
        Button(
            onClick = {
                selectedIncident?.let { incident ->
                    // Map severity to incident type or use separate incident type selector
                    onSubmit(incident, selectedSeverity!!, incidentDetails, isAnonymous, selectedLocation)
                }
            },
            enabled = selectedSeverity != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A237E)
            )
        ) {
            Text("Submit Report", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Cancel", color = Color(0xFF1A237E))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guidelines section
        Text(
            text = "BY SUBMITTING, YOU AGREE TO OUR COMMUNITY GUIDELINES",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 911 fallback
        TextButton(
            onClick = { /* Launch dialer with 911 */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Immediate danger? Call 911",
                color = Color(0xFFB00020),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SafetyHeaderCard(onSosClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1A237E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Warning bulb icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LightbulbCircle,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Stay calm. Your safety is priority.",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Find a secure location before completing this report. " +
                            "If you are in immediate danger, user the SOS button above.",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onSosClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE57373))
                ) {
                    Text("Use SOS →", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun SeveritySelector(
    selectedSeverity: SeverityLevel?,
    onSeveritySelected: (SeverityLevel) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val severities = listOf(
            SeverityLevel.RED to "CRITICAL",
            SeverityLevel.YELLOW to "MEDIUM",
            SeverityLevel.GREEN to "LOW"
        )

        severities.forEach { (severity, label) ->
            val isSelected = selectedSeverity == severity
            val color = when(severity) {
                SeverityLevel.RED -> Color(0xFFEB2A3A)
                SeverityLevel.YELLOW -> Color(0xFFF1C40F)
                SeverityLevel.GREEN -> Color(0xFF2ECC71)
                else -> Color.Gray
            }

            Surface(
                onClick = { onSeveritySelected(severity) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) color.copy(alpha = 0.15f) else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) color else Color.LightGray
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(severity) {
                                SeverityLevel.RED -> Icons.Default.Warning
                                SeverityLevel.YELLOW -> Icons.Default.Warning
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        label,
                        color = if (isSelected) color else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else
                            FontWeight.Normal,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun AnonymousToggle(isAnonymous: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Anonymous Report",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Hide you identity from others",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Switch(
                checked = isAnonymous,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF1A237E),
                    checkedTrackColor = Color(0xFF1A237E).copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun IncidentLocationSection(location: LatLng, onChangeLocation: () -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "INCIDENT LOCATION",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF2ECC71),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "GPS Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2ECC71)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Map snippet placeholder
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFE8EAF6),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text("Map Preview\n${location.latitude}, ${location.longitude}")

                // Pin in center
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF1A237E),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected location address
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF1A237E)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "SELECTED LOCATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1A237E),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${location.latitude}, ${location.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = onChangeLocation,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A237E)
                    )
                ) {
                    Text("Change")
                }
            }
        }
    }
}






















