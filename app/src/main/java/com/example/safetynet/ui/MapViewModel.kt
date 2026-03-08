package com.example.safetynet.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.LocationRepository
import com.example.safetynet.data.SafetyPin
import com.example.safetynet.data.SafetyPinRepository
import com.example.safetynet.domain.SeverityLevel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import timber.log.Timber
import com.example.safetynet.usecases.DeletePinUseCase
import com.example.safetynet.usecases.GetAllPinsUseCase
import com.example.safetynet.usecases.SavePinUseCase
import com.example.safetynet.utils.NotificationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the MapScreen
 *
 * It holds and updates the following states:
 * 1. userLocation: LatLng? (user's location)
 * 2. safetyPins: List<SafetyPin> (state for all saved pins)
 * 3. showDialog: Boolean
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
    private val repository: SafetyPinRepository,
    private val locationRepository: LocationRepository,
    private val getAllPinsUseCase: GetAllPinsUseCase,
    private val savePinUseCase: SavePinUseCase,
    private val deletePinUseCase: DeletePinUseCase,
    private val notificationHelper: NotificationHelper
): ViewModel() {

    init {
        repository.startFirebaseSync()
    }

    // ----------------- UI States ------------
    // state to hold the user's location as LatLng
    private val _userLocation = mutableStateOf<LatLng?>(null)
    // public exposed state
    val userLocation: State<LatLng?> = _userLocation

    // state for loading indicator
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Real-time Data state
    // whenever a pin is added/deleted in the cloud, this list updates itself
    @OptIn(ExperimentalCoroutinesApi::class)
    val safetyPins: StateFlow<List<SafetyPin>> = snapshotFlow { _userLocation.value }
        .flatMapLatest { location ->
            if(location == null) flowOf(emptyList())
            else getAllPinsUseCase(location)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ----------------UI States------------------

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

    private val _showDeleteConfirmation = mutableStateOf(false)
    val showDeleteConfirmation: State<Boolean> = _showDeleteConfirmation

    private val alertedPinIds = mutableSetOf<String>()
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    // -------------------------- Actions -----------------------
    /**
     * Fetches the user's current location and loads nearby safety pins.
     *
     * Automatically triggers pin loading once location is obtained.
     */
    fun fetchUserLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            locationRepository.getCurrentLocation()
                .onSuccess { latLng ->
                    _userLocation.value = latLng
                    _isLoading.value = false

                    // Trigger the Check whenever location is updated
                    checkProximityToDanger(latLng.latitude, latLng.longitude)
                }
                .onFailure { exception ->
                    _isLoading.value = false
                    Timber.e(exception, "Failed to fetch user location")
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
        // Attatch the real User ID before saving
        val pinWithUser = safetyPin.copy(userId = currentUserId)

        viewModelScope.launch {
            _isLoading.value = true
//            dismissDialog()
            try {
                savePinUseCase(pinWithUser)
                    .onSuccess {
                        dismissDialog()
                        Timber.d("Pin saved successfully")
                    }
                    .onFailure { exception ->
                        _errorMessage.value = exception.message
                        Timber.e(exception, "Failed to save save pin")
                    }
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Deletes specific selected SafetyPin & reloads the pins list

    fun onDeleteClicked() {
        _showDeleteConfirmation.value = true
    }

    fun dismissDeleteConfirmation() {
        _showDeleteConfirmation.value = false
    }

    fun confirmDelete() {
        val pin = selectedPin.value
        if (pin != null) {
            deletePin(pin)
            _showDeleteConfirmation.value = false
        }
    }
    fun deletePin(pin: SafetyPin) {
        viewModelScope.launch {
            deletePinUseCase(pin.id)
                .onSuccess {
                    onPinDetailsDialogDismiss()
                    Timber.d("Pin deleted successfully")
                }
                .onFailure { exception ->
                    _errorMessage.value = "Failed to delete pin: ${exception.message}"
                    Timber.e(exception, "Failed to delete pin")
                }
        }
    }

    // ------------ Critical Incidents Notifications -----------

    private fun checkProximityToDanger(userLat: Double, userLong: Double) {
        val dangerPins = safetyPins.value.filter {
            it.severity == SeverityLevel.RED || it.severity == SeverityLevel.ORANGE
        }

        for (pin in dangerPins) {
            val results = FloatArray(1)

            // Use android's built-in distance calculator
            android.location.Location.distanceBetween(
                userLat, userLong,
                pin.latitude, pin.longitude,
                results
            )

            val distanceInMeters = results[0]

            // Only alert if within 1 km and not already alerted
            if (distanceInMeters <= 1000 && !alertedPinIds.contains(pin.id)) {
                val alertHeader = if (pin.severity == SeverityLevel.RED) {
                    "⚠️ CRITICAL INCIDENT"
                } else {
                    "🔸 HIGH RISK AREA"
                }

                notificationHelper.sendHighRiskAlert(
                    alertHeader,
                    "Nearby: ${pin.shortDescription}. Stay alert!"
                )

                alertedPinIds.add(pin.id)
            } else if (distanceInMeters > 1000) { // Rest if they move away (> 1km)
                alertedPinIds.remove(pin.id)
            }
        }
    }


    // ------------- UI Helper methods ----------------

    fun onMapTapped(location: LatLng) {
        _tappedLocation.value = location
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
        _tappedLocation.value = null
    }

    fun onPinSelected(pin: SafetyPin) {
        _selectedPin.value = pin
    }

    fun onPinDetailsDialogDismiss() {
        _selectedPin.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

}
