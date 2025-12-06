package com.example.safetynet

import MapScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.safetynet.ui.MainScreen
import com.example.safetynet.ui.MapViewModel
import com.example.safetynet.ui.theme.SafetyNetTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import data.LocationRepository
import data.SafetyPinDatabase
import data.SafetyPinRepository
import usecases.DeletePinUseCase
import usecases.GetAllPinsUseCase
import usecases.SavePinUseCase
import utils.LocationUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SafetyNetTheme {
                val context = LocalContext.current

                // database and pin dependencies
                val database = remember { SafetyPinDatabase.getDatabase(context) }
                val safetyPinDao = remember { database.safetyPinDao() }
                val safetyPinRepository = remember { SafetyPinRepository(database.safetyPinDao()) }


                // location dependencies
                val fusedLocationClient = remember {
                    LocationServices.getFusedLocationProviderClient(context)
                }
                val locationRepository = remember {
                    LocationRepository(
                        safetyPinDao,
                        fusedLocationClient
                    )
                }

                // use cases
                val savePinUseCase = remember { SavePinUseCase(safetyPinRepository) }
                val getAllPinsUseCase = remember { GetAllPinsUseCase(safetyPinRepository) }
                val deletePinUseCase = remember { DeletePinUseCase(safetyPinRepository) }

                val mapViewModel = remember {
                    MapViewModel(
                        locationRepository,
                        savePinUseCase,
                        getAllPinsUseCase,
                        deletePinUseCase
                    )
                }

                MainScreen(mapViewModel)
            }
        }
    }
}

