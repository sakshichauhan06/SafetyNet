package com.example.safetynet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.safetynet.data.LocationRepository
import com.example.safetynet.data.SafetyPinDatabase
import com.example.safetynet.data.SafetyPinRepository
import com.example.safetynet.ui.MainScreen
import com.example.safetynet.ui.MapViewModel
import com.example.safetynet.ui.auth.AuthViewModel
import com.example.safetynet.ui.theme.SafetyNetTheme
import com.google.android.gms.location.LocationServices
import com.example.safetynet.usecases.DeletePinUseCase
import com.example.safetynet.usecases.GetAllPinsUseCase
import com.example.safetynet.usecases.SavePinUseCase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SafetyNetTheme {
                MainScreen(mapViewModel, authViewModel)
            }
        }
    }
}

