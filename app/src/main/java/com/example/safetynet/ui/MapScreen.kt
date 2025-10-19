import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.safetynet.ui.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import data.SafetyPin
import domain.SeverityLevel
import timber.log.Timber


@Composable
fun MapScreen(mapViewModel: MapViewModel) {

    val userLocation by mapViewModel.userLocation
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    val safetyPins by mapViewModel.safetyPins

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(isGranted) {
            mapViewModel.fetchUserLocation()
        } else {
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

    // load pins when screen loads
    LaunchedEffect(Unit) {
        mapViewModel.loadAllPins()
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            val newPin = SafetyPin(
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                severity = SeverityLevel.RED,
                shortDescription = "Unsafe area",
                detailedDescription = "Details here",
                timestamp = System.currentTimeMillis(),
                isAnonymous = true
            )
            mapViewModel.savePin(newPin)
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
                        location, 15f
                    )
                )
            }
        }

        // display all saved pins inside GoogleMap Block
        safetyPins.forEach { pin ->
            Marker(
                state = MarkerState(position = LatLng(pin.latitude, pin.longitude)),
                title = pin.shortDescription,
                snippet = "Severity: ${pin.severity}"
            )
        }
    }

}

//@Preview
//@Composable
//fun MapScreenPreview() {
//
//}