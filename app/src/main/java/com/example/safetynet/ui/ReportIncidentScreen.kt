package com.example.safetynet.ui

import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.safetynet.R
import com.example.safetynet.domain.IncidentType
import com.example.safetynet.domain.SeverityLevel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Report Incident",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleLarge,
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

            Text(
                text = "Your report helps keep the community safe.",
                color = Color(0xFF44474E),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header TopBar
            StayCalmCard(onSosClick = onSosClick)

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

            Spacer(modifier = Modifier.height(28.dp))

            // Anonymous Section
            AnonymousToggle(
                isAnonymous = isAnonymous,
                onToggle = { isAnonymous = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Incident Section
            Text(
                "INCIDENT DETAILS",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = incidentDetails,
                onValueChange = { incidentDetails = it },
                placeholder = { Text("Describe what happened...", color = Color(0xFF74777F).copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE4E9ED),
                    unfocusedContainerColor = Color(0xFFE4E9ED),
                    disabledContainerColor = Color(0xFFE4E9ED),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
//                colors = TextFieldDefaults.colors(Color(0xFFE4E9ED))
            )

            Spacer(modifier = Modifier.height(28.dp))

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
                    containerColor = Color.Black
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
                Text("Cancel", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guidelines section
            Text(
                text = "BY SUBMITTING, YOU AGREE TO OUR COMMUNITY GUIDELINES",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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
}

@Composable
fun StayCalmCard(onSosClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF001B3D),
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
                    .background(Color(0xFFB00020),
                        RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
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

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Find a secure location before completing this report. " +
                            "If you are in immediate danger, user the SOS button above.",
                    color = Color(0xFF6F84AC),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Use SOS →",
                    color = Color(0xFFE57373),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onSosClick)
                        .padding(vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge
                )
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
            SeverityLevel.GREY to "LOW"
        )

        severities.forEach { (severity, label) ->
            val isSelected = selectedSeverity == severity
            val color = when(severity) {
                SeverityLevel.RED -> Color(0xFFEB2A3A)
                SeverityLevel.YELLOW -> Color(0xFFF1C40F)
                SeverityLevel.GREY -> Color(0xFF74777F)
                else -> Color.Gray
            }

            Surface(
                onClick = { onSeveritySelected(severity) },
                shape = RoundedCornerShape(24.dp),
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
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(severity) {
                                SeverityLevel.RED -> Icons.Default.Emergency
                                SeverityLevel.YELLOW -> Icons.Default.Warning
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(34.dp)
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
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF0F4F9),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = Color.Black
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Anonymous Report",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    "Hide you identity from others",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            Switch(
                checked = isAnonymous,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun IncidentLocationSection(location: LatLng, onChangeLocation: () -> Unit) {
    val context = LocalContext.current
    var addressText by remember(location) {
        mutableStateOf("${location.latitude}, ${location.longitude}")
    }

    LaunchedEffect(location) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addressText = addresses?.firstOrNull()?.getAddressLine(0) ?: addressText
        } catch (_: Exception) { }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "LOCATION CONFIRMATION",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "GPS Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        // Interactive map
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, 15f)
        }
        val context = LocalContext.current
        val markerIcon = remember(context) {
            try {
                ContextCompat.getDrawable(context, R.drawable.marker)?.toBitmap()?.let { bitmap ->
                    // Resize to desired dimensions (width, height in pixels)
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, false)
                    BitmapDescriptorFactory.fromBitmap(scaledBitmap)
                }
            } catch (e: Exception) {
                null
            }
        }


        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
//                properties = MapProperties(),
                googleMapOptionsFactory = {
                    GoogleMapOptions().mapId("d2224b87c7462b956e6b738b")
                },
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = false,
                    zoomGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    rotationGesturesEnabled = false,
                    compassEnabled = false,
                    mapToolbarEnabled = false
                ),
                onMapClick = { onChangeLocation() }
            ) {
                Marker(
                    state = rememberMarkerState(position = location),
                    icon = markerIcon,
                    title = "Incident Location"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Address card
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
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = addressText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onChangeLocation,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "EDIT",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}










































