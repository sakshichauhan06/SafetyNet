package com.example.safetynet

import MapScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.safetynet.ui.MapViewModel
import com.example.safetynet.ui.theme.SafetyNetTheme
import com.google.android.gms.location.LocationServices
import data.LocationRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafetyNetTheme {
                val context = LocalContext.current
                val fusedLocationClient = remember {
                    LocationServices.getFusedLocationProviderClient(context)
                }
                val locationRepository = remember { LocationRepository(fusedLocationClient) }
                val mapViewModel = remember { MapViewModel(locationRepository) }

                MapScreen(mapViewModel)
            }
        }
    }
}

