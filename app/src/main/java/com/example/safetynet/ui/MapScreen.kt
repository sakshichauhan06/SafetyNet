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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import timber.log.Timber


@Composable
fun MapScreen(mapViewModel: MapViewModel) {

    val userLocation by mapViewModel.userLocation
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

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

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
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
    }

}

//@Preview
//@Composable
//fun MapScreenPreview() {
//
//}