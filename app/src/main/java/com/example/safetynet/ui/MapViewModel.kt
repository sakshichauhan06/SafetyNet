package com.example.safetynet.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import data.LocationRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel(private val locationRepository: LocationRepository): ViewModel() {


    // state to hold the user's location as LatLng
    private val _userLocation = mutableStateOf<LatLng?>(null)
    // public exposed state
    val userLocation: State<LatLng?> = _userLocation


    public fun fetchUserLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation()
                .onSuccess { latLng ->
                    _userLocation.value = latLng
                }
                .onFailure { exception ->
                    Timber.e(exception, "Failed to fetch user location")
                }
        }
    }

}