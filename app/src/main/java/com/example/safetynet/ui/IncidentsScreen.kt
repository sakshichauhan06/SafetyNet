package com.example.safetynet.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IncidentsScreen(mapViewModel: MapViewModel) {
    val pins by mapViewModel.safetyPins

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Your Reported Incidents", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        if (pins.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No incidents reported yet")
            }
        } else {
            // Simple list of reported incidents
            pins.forEach { pin ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(pin.shortDescription, style = MaterialTheme.typography.titleMedium)
                        Text(pin.severity.displayName, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

}