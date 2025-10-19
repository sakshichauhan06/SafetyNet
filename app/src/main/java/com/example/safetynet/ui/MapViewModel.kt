package com.example.safetynet.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import data.LocationRepository
import data.SafetyPin
import kotlinx.coroutines.launch
import timber.log.Timber
import usecases.GetAllPinsUseCase
import usecases.SavePinUseCase

class MapViewModel(
    private val locationRepository: LocationRepository,
    private val savePinUseCase: SavePinUseCase,
    private val getAllPinsUseCase: GetAllPinsUseCase
): ViewModel() {

    // state to hold the user's location as LatLng
    private val _userLocation = mutableStateOf<LatLng?>(null)
    // public exposed state
    val userLocation: State<LatLng?> = _userLocation

    // state for all saved pins
    private val _safetyPins = mutableStateOf<List<SafetyPin>>(emptyList())
    val safetyPins: State<List<SafetyPin>> = _safetyPins

    // state to show or hide dialog
    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    // state for tapped location to save
    private val _tappedLocation = mutableStateOf<LatLng?>(null)
    val tappedLocation: State<LatLng?> = _tappedLocation

    // state for error in duplication
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // to fetch user's location
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

    // to load all pins from our database
    fun loadAllPins() {
        viewModelScope.launch {
            getAllPinsUseCase()
                .onSuccess { pins ->
                    _safetyPins.value = pins
                }
                .onFailure { exception ->
                    Timber.e(exception, "Failed to load pins")
                }

        }
    }


    fun onMapTapped(location: LatLng) {
        _tappedLocation.value = location
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
    }

    // save a pin to the database
    fun savePin(safetyPin: SafetyPin) {
        viewModelScope.launch {
            savePinUseCase(safetyPin)
                .onSuccess {
                    dismissDialog()
                    loadAllPins()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Failed to save pin"
                    Timber.e(exception, "Failed to save pin")
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}