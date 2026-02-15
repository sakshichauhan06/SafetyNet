package com.example.safetynet.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.LocationRepository
import com.example.safetynet.data.SafetyPin
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import timber.log.Timber
import com.example.safetynet.usecases.DeletePinUseCase
import com.example.safetynet.usecases.GetAllPinsUseCase
import com.example.safetynet.usecases.SavePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the MapScreen
 *
 * It holds and updates the following states:
 * 1. userLocation: LatLng? (user's location)
 * 2. safetyPins: List<SafetyPin> (state for all saved pins)
 * 3. showDialog: Boolean
 * 4. tappedLocation: LatLng?
 * 5. errorMessage: String?
 *
 *
 * @param locationRepository to fetch the user's location
 * @param savePinUseCase to save a pin to the database
 * @param getAllPinsUseCase to load all pins from the database
 *
 */

@HiltViewModel
class MapViewModel @Inject constructor (
    private val locationRepository: LocationRepository,
    private val savePinUseCase: SavePinUseCase,
    private val getAllPinsUseCase: GetAllPinsUseCase,
    private val deletePinUseCase: DeletePinUseCase
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

    // state for selected pin to show it's details
    private val _selectedPin = mutableStateOf<SafetyPin?>(null)
    val selectedPin: State<SafetyPin?> = _selectedPin

    // state for loading indicator
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /**
     * Fetches the user's current location and loads nearby safety pins.
     *
     * Automatically triggers pin loading once location is obtained.
     */
    public fun fetchUserLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            locationRepository.getCurrentLocation()
                .onSuccess { latLng ->
                    _userLocation.value = latLng
                    // since we got the user location now, we can load the nearby pins
                    loadAllPins()
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _isLoading.value = false
                    Timber.e(exception, "Failed to fetch user location")
                }
        }
    }

    // to load all pins from our database
    fun loadAllPins() {
        viewModelScope.launch {
            // if we are already loading
            if(!_isLoading.value) {
                _isLoading.value = true
            }

            getAllPinsUseCase(userLocation.value)
                .onSuccess { pins ->
                    _safetyPins.value = pins
                    _isLoading.value = false // END loading on success
                }
                .onFailure { exception ->
                    Timber.e(exception, "Failed to load pins")
                    _isLoading.value = false // END loading on failure
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

    fun onPinSelected(pin: SafetyPin) {
        _selectedPin.value = pin
    }

    fun onPinDetailsDialogDismiss() {
        _selectedPin.value = null
    }

    // Deletes specific selected SafetyPin & reloads the pins list
    fun deletePin(pin: SafetyPin) {
        viewModelScope.launch {
            deletePinUseCase(pin.id)
                .onSuccess {
                    // pin deleted successfully
                    onPinDetailsDialogDismiss()
                    loadAllPins()
                }
                .onFailure { exception ->
                    _errorMessage.value = "Failed to delete pin: ${exception.message}"
                    Timber.e(exception, "Failed to delete pin")
                }
        }
    }


    /**
     * Saves a new safety pin to the database.
     *
     * Validates for duplicates, shows error if pin exists within 50m,
     * otherwise saves and reloads the pin list.
     *
     * @param safetyPin The pin to save
     */
    fun savePin(safetyPin: SafetyPin) {
        viewModelScope.launch {
            _isLoading.value = true // START loading for save operation
            savePinUseCase(safetyPin)
                .onSuccess {
                    dismissDialog()
                    loadAllPins()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Failed to save pin"
                    Timber.e(exception, "Failed to save pin")
                    _isLoading.value = false // END loading on failure
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}