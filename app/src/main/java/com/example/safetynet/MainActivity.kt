package com.example.safetynet

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.safetynet.ui.MainScreen
import com.example.safetynet.ui.MapViewModel
import com.example.safetynet.ui.auth.AuthState
import com.example.safetynet.ui.auth.AuthViewModel
import com.example.safetynet.ui.theme.SafetyNetTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // ------- Permission Check ---------
        requestAppPermissions()

        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            authViewModel.authState.value is AuthState.Loading
        }

        setContent {
            SafetyNetTheme {
                MainScreen(mapViewModel, authViewModel)
            }
        }
    }

    private fun requestAppPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 2. Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 101)
        }
    }
}

