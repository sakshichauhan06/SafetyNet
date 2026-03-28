package com.example.safetynet.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationRequest
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Fetches the device's last known location from Android's location service.
 *
 * Uses FusedLocationProviderClient to retrieve GPS coordinates.
 * Assumes location permission has already been granted by the UI layer.
 *
 * @return Result.success with LatLng if location found, Result.failure if unavailable or error occurs
 */

class LocationRepository @Inject constructor(
    private val safetyPinDao: SafetyPinDao,
    private val fusedLocationClient: FusedLocationProviderClient,
    val context: android.content.Context
) {
    suspend fun getCurrentLocation() : Result<LatLng> {
        return suspendCancellableCoroutine { continuation ->
            try {
                // start async operation (fetch the user's location)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if(location != null) {
                        continuation.resume(Result.success(LatLng(location.latitude, location.longitude)))
                    } else {
                        continuation.resume(Result.failure(Exception("No Location found")))
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
            } catch (e: SecurityException) {
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun searchLocation(query: String): Result<LatLng> {
        return try {
            val geocoder = android.location.Geocoder(context)
            val addresses = geocoder.getFromLocationName(query, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Result.success(LatLng(address.latitude, address.longitude))
            } else {
                Result.failure(Exception("Location not found: $query"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission") // To handle permissions in UI layer
    fun getLocationUpdates(): Flow<LatLng> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // Check every 10 seconds (good balance for batter)
        ).setMinUpdateDistanceMeters(20f) // Only trigger if they move 20 meters
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    trySend(LatLng(location.latitude, location.longitude))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        // If the app closes or ViewModel is cleared, this block
        // stops the GPS to save user's battery
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Deletes a specific SafetyPin from the DB
    suspend fun deletePin(pin: SafetyPin) {
        safetyPinDao.delete(pin)
    }

}