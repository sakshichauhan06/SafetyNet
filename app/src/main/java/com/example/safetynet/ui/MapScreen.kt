import android.Manifest
import android.R.attr.onClick
import android.R.attr.text
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.Settings
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.safetynet.R
import com.example.safetynet.ui.MapViewModel
import com.example.safetynet.ui.ReportIncidentDialog
import com.example.safetynet.ui.components.EmptySafetyPinState
import com.example.safetynet.ui.components.ViewPinDetailsDialog
import com.example.safetynet.ui.theme.ColorDarkBackground
import com.example.safetynet.ui.theme.ColorError
import com.example.safetynet.ui.theme.ColorOnSurface
import com.example.safetynet.ui.theme.ColorPrimaryVariant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import data.SafetyPin
import domain.SeverityLevel
import timber.log.Timber
import utils.AppConstants


@Composable
fun MapScreen(mapViewModel: MapViewModel) {

    val userLocation by mapViewModel.userLocation
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val isLoading by mapViewModel.isLoading

    val safetyPins by mapViewModel.safetyPins

    val showDialog by mapViewModel.showDialog
    val tappedLocation by mapViewModel.tappedLocation

    val selectedPin by mapViewModel.selectedPin

    val errorMessage by mapViewModel.errorMessage

    val snackbarHostState = remember { SnackbarHostState() }

    var permissionDenied by remember { mutableStateOf(false) }

    val showEmptyState = !isLoading && safetyPins.isEmpty()

    var hasInitiallyCentered by remember { mutableStateOf(false) }


    val mapProperties = remember {
        MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        )
    }

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
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

    // Load pins whenever userLocation changes
    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            mapViewModel.loadAllPins()
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
                mapViewModel.deletePin(pin)
            }
        )
    }

    // Show report incident dialog
    if (showDialog && tappedLocation != null) {
        ReportIncidentDialog(
            onDismiss = { mapViewModel.dismissDialog() },
            onSubmit = { incidentType, details ->
                val newPin = SafetyPin(
                    latitude = tappedLocation!!.latitude,
                    longitude = tappedLocation!!.longitude,
                    incidentType = incidentType,
                    severity = incidentType.severity,
                    shortDescription = incidentType.displayName,
                    detailedDescription = details.ifEmpty { "No Additional details" },
                    timestamp = System.currentTimeMillis(),
                    isAnonymous = true
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
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(0.dp)) {
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
                        Marker(
                            state = MarkerState(position = location),
                            title = "Your Location"
                        )
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

}

@Composable
fun SafetyMarker(
    position: LatLng,
    severity: SeverityLevel,
    title: String,
    onMarkerClick: () -> Unit
) {
    val markerState = rememberMarkerState(position = position)

    val (markerColor, icon) = when (severity) {
        SeverityLevel.RED  -> Color(0xFFEB2A34) to Icons.Default.Warning
        SeverityLevel.ORANGE -> Color(0xFFE67E22) to Icons.Default.Warning
        SeverityLevel.YELLOW -> Color(0xFFF1C40F) to Icons.Default.LocationOn
        SeverityLevel.GREEN -> Color(0xFF2ECC71) to Icons.Default.LocationOn
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0FF))
    ) {
        ElevatedCard (
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(42.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .background(Color(0xFFE0E0FF))
                .padding(46.dp)
                .size(width = 311.dp, height = 400.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(21.dp)
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.pin),
                    modifier = Modifier.size(88.dp),
                    contentDescription = "Location Permission Required",
                    colorFilter = null,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Location Permission Required",
                    color = ColorOnSurface,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This app requires location access to show nearby safety incidents and help keep you safe.",
                    color = ColorOnSurface,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .width(158.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = true,
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
                    ) {
                    Text(
                        text = "Open Settings",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}



//@Preview
//@Composable
//fun MapScreenPreview() {
//
//}