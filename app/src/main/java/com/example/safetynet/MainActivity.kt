package com.example.safetynet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.safetynet.ui.theme.SafetyNetTheme
import com.example.safetynet.ui.MapScreen
import com.example.safetynet.ui.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafetyNetTheme {
                val mapViewModel = MapViewModel()
                MapScreen(mapViewModel)
            }
        }
    }
}

