import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.safetynet.ui.components.PinDetailsDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import data.SafetyPin
import domain.SeverityLevel
import timber.log.Timber
import utils.AppConstants


@Composable
fun MapScreen(mapViewModel: MapViewModel) {

    val userLocation by mapViewModel.userLocation
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    val safetyPins by mapViewModel.safetyPins

    val showDialog by mapViewModel.showDialog
    val tappedLocation by mapViewModel.tappedLocation

    val errorMessage by mapViewModel.errorMessage

    val snackbarHostState = remember { SnackbarHostState() }

    var permissionDenied by remember { mutableStateOf(false) }

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
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                mapViewModel.fetchUserLocation()
            } else -> {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
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
                    android.Manifest.permission.ACCESS_FINE_LOCATION
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

    if (showDialog && tappedLocation != null) {
        PinDetailsDialog(
            onDismiss = { mapViewModel.dismissDialog() },
            onSave = { shortDesc, detailedDesc, severity ->
                val newPin = SafetyPin(
                    latitude = tappedLocation!!.latitude,
                    longitude = tappedLocation!!.longitude,
                    severity = severity,
                    shortDescription = shortDesc,
                    detailedDescription = detailedDesc,
                    timestamp = System.currentTimeMillis(),
                    isAnonymous = true
                )
                mapViewModel.savePin(newPin)
                mapViewModel.dismissDialog()
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
            Box(modifier = Modifier.padding(paddingValues)) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
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
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    location, AppConstants.DEFAULT_MAP_ZOOM
                                )
                            )
                        }
                    }

                    // display all saved pins inside GoogleMap Block
                    safetyPins.forEach { pin ->
                        Marker(
                            state = MarkerState(position = LatLng(pin.latitude, pin.longitude)),
                            title = pin.shortDescription,
                            snippet = "Severity: ${pin.severity}",
                            icon = BitmapDescriptorFactory.defaultMarker(getMarkerColor(pin.severity))
                        )
                    }
                }
            }
        }
    }

}

fun getMarkerColor(severity: SeverityLevel): Float {
    return when(severity) {
        SeverityLevel.RED -> BitmapDescriptorFactory.HUE_RED
        SeverityLevel.ORANGE -> BitmapDescriptorFactory.HUE_ORANGE
        SeverityLevel.YELLOW -> BitmapDescriptorFactory.HUE_YELLOW
        SeverityLevel.GREEN -> BitmapDescriptorFactory.HUE_GREEN
    }
}

@Composable
fun PermissionRequiredScreen(onSettingsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.home_pin_24px),
            contentDescription = "Location Required",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This app requires location access to show nearby safety incidents and help keep you safe.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onSettingsClick) {
            Text(text = "Open Settings")
        }
    }
}

//@Preview
//@Composable
//fun MapScreenPreview() {
//
//}