import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.room.util.query
import com.example.safetynet.R
import com.example.safetynet.data.SafetyPin
import com.example.safetynet.ui.MapViewModel
import com.example.safetynet.ui.ReportIncidentDialog
import com.example.safetynet.ui.components.EmptySafetyPinState
import com.example.safetynet.ui.components.ViewPinDetailsDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.safetynet.domain.SeverityLevel
import com.example.safetynet.ui.TopBar
import com.example.safetynet.ui.components.SafetySearchBar
import com.example.safetynet.ui.components.SeverityFilterBar
import timber.log.Timber
import com.example.safetynet.utils.AppConstants
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.rememberUpdatedMarkerState


@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    cameraPositionState: CameraPositionState
) {

    val userLocation by mapViewModel.userLocation
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val isLoading by mapViewModel.isLoading

    val safetyPins by mapViewModel.safetyPins.collectAsStateWithLifecycle()

    val showDialog by mapViewModel.showDialog
    val tappedLocation by mapViewModel.tappedLocation

    val selectedPin by mapViewModel.selectedPin

    val errorMessage by mapViewModel.errorMessage

    val snackbarHostState = remember { SnackbarHostState() }

    var permissionDenied by remember { mutableStateOf(false) }

    val showEmptyState = !isLoading && safetyPins.isEmpty()

    var hasInitiallyCentered by remember { mutableStateOf(false) }

    val showDeleteConfirmation by mapViewModel.showDeleteConfirmation

    val auth = Firebase.auth
    val currentUserId = auth.currentUser?.uid ?: "anonymous_user"

    var searchQuery by remember { mutableStateOf("") }
    var activeFilters by remember { mutableStateOf(setOf<SeverityLevel>()) }
    val recentSearches by mapViewModel.recentSearches.collectAsStateWithLifecycle()


    val mapProperties = remember {
        MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        )
    }

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }

    // Voice launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(
                android.speech.RecognizerIntent.EXTRA_RESULTS
            )
            matches?.firstOrNull()?.let { spokenText ->
                searchQuery = spokenText
                mapViewModel.searchLocation(spokenText)
            }
        }
    }

    fun launchVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Where are you heading?")
        }
        try {
           voiceLauncher.launch(intent)
        } catch (e: Exception) {
            // when voice search is not supported on this device
            Timber.e(e, "Voice search not available")
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(isGranted) {
            mapViewModel.fetchUserLocation()
            permissionDenied = false
        } else {
            permissionDenied = true
            Timber.e("Location permission denied")
        }
    }

    // Request permission when screen loads
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                mapViewModel.fetchUserLocation()
            } else -> {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Re-check permission when app resumes from settings
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // re-check permission when user return to app
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission && permissionDenied) {
                    permissionDenied = false
                    mapViewModel.fetchUserLocation()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    // Show error snackbar when error occurs
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            mapViewModel.clearError()
        }
    }

    // Show view pin details dialog
    selectedPin?.let { pin ->
        ViewPinDetailsDialog(
            pin = pin,
            onDismiss = { mapViewModel.onPinDetailsDialogDismiss() },
            onDelete = {
                mapViewModel.onDeleteClicked()
            }
        )
    }

    // Delete Confirmation Dialog
    if(showDeleteConfirmation) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mapViewModel.dismissDeleteConfirmation() },
            title = {
                Text(
                    text = "Delete Incident?"
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this incident? This action cannot be undone"
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { mapViewModel.confirmDelete() }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { mapViewModel.dismissDeleteConfirmation() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Show report incident dialog
    if (showDialog && tappedLocation != null) {
        ReportIncidentDialog(
            onDismiss = { mapViewModel.dismissDialog() },
            onSubmit = { incidentType, details ->
                val newPin = SafetyPin(
                    id = java.util.UUID.randomUUID().toString(),
                    latitude = tappedLocation!!.latitude,
                    longitude = tappedLocation!!.longitude,
                    incidentType = incidentType,
                    severity = incidentType.severity,
                    shortDescription = incidentType.displayName,
                    detailedDescription = details.ifEmpty { "No Additional details" },
                    timestamp = System.currentTimeMillis(),
                    isAnonymous = true,
                    userId = currentUserId
                )
                mapViewModel.savePin(newPin)
            }
        )
    }



    if (permissionDenied) {
       // show permission required screen
        PermissionRequiredScreen(
            onSettingsClick = {
                // open settings app
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapClick = { latLng ->
                    mapViewModel.onMapTapped(latLng)
                }
            ) {
                // user location marker
                userLocation?.let { location ->
                    UserLocationMarker(position = location)

                    LaunchedEffect(location) {
                        if (!hasInitiallyCentered) {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(location, AppConstants.DEFAULT_MAP_ZOOM)
                            )
                            hasInitiallyCentered = true
                        }
                    }
                }

                // display all saved pins inside GoogleMap Block
                safetyPins.forEach { pin ->
                    key(pin.id) {
                        SafetyMarker(
                            position = LatLng(pin.latitude, pin.longitude),
                            severity = pin.severity,
                            title = pin.shortDescription,
                            onMarkerClick = {
                                mapViewModel.onPinSelected(pin)
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Search bar
                SafetySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        mapViewModel.searchLocation(searchQuery)
                    },
                    onVoiceSearch = {
                        // launch voice intent
                        launchVoiceSearch()
                    },
                    recentSearches = recentSearches,
                    onRecentSearchClick = {
                        searchQuery = it
                        mapViewModel.searchLocation(it)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Severity Filters
                SeverityFilterBar(
                    activeFilters = activeFilters,
                    onFilterToggle = { severity ->
                        activeFilters = if (activeFilters.contains(severity)) {
                            activeFilters - severity
                        } else {
                            activeFilters + severity
                        }
                        mapViewModel.setSeverityFilter(activeFilters)
                    }
                )
            }

            if (showEmptyState) {
                EmptySafetyPinState()
            }

            if(isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

}

@Composable
fun SafetyMarker(
    position: LatLng,
    severity: SeverityLevel,
    title: String,
    onMarkerClick: () -> Unit
) {
    val markerState = rememberUpdatedMarkerState(position = position)

    val (markerColor, icon) = when (severity) {
        SeverityLevel.RED  -> Color(0xFFEB2A34) to Icons.Default.Warning
        SeverityLevel.ORANGE -> Color(0xFFE67E22) to Icons.Default.Warning
        SeverityLevel.YELLOW -> Color(0xFFF1C40F) to Icons.Default.LocationOn
        SeverityLevel.GREEN -> Color(0xFF2ECC71) to Icons.Default.LocationOn
        SeverityLevel.GREY -> Color(0xFFBAC3CC) to Icons.Default.LocationOn
    }

    MarkerComposable(
        state = markerState,
        title = title,
        onClick = {
            onMarkerClick()
            true
        }
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(markerColor, CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PermissionRequiredScreen(onSettingsClick: () -> Unit) {

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF001C3E),
            Color(0xFF08254D),
            Color(0xFF123260),
            Color(0xFF193B6D),
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pin),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 28.dp)
            )

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(600)) {
                    it / 4
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Safety needs your location to \nalert contacts " +
                                "and \nemergency services if you're \nin danger.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    PermissionInfoTag(
                        icon = Icons.Outlined.Shield,
                        text = "Real-time safety monitoring"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PermissionInfoTag(
                        icon = Icons.Default.Sos,
                        text = "Instant emergency SOS alerts"
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = onSettingsClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF001B3D)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "OPEN SETTINGS",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ENCRYPTED PRIVATE DATA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionInfoTag(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(64.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(21.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun UserLocationMarker(position: LatLng) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val animationDuration = 3000
    val userLocationColor = Color(0xFF1A237E)

    val rings = listOf(0f, 0.33f, 0.66f)

    MarkerComposable(
        state = rememberUpdatedMarkerState(position = position),
        anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(userLocationColor, CircleShape)
                .border(2.dp, userLocationColor, CircleShape)
        )
    }

    rings.forEachIndexed { index, phaseOffset ->
        val delay = (phaseOffset * animationDuration).toInt()

        val radius by infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = animationDuration
                    10f at delay using LinearEasing
                    40f at (delay + animationDuration / 3) using LinearEasing
                    80f at animationDuration using LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "radius_$index"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = animationDuration
                    0f at delay using LinearEasing
                    0.6f at (delay + 100) using LinearEasing
                    0.3f at (delay + animationDuration / 2) using LinearEasing
                    0f at animationDuration using LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha_$index"
        )

        if (alpha > 0.01f) {
            Circle(
                center = position,
                radius = radius.toDouble(),
                fillColor = userLocationColor.copy(alpha = alpha * 0.25f),
                strokeColor = userLocationColor.copy(alpha = alpha * 0.6f),
                strokeWidth = 2f,
                clickable = false
            )
        }
    }
}













