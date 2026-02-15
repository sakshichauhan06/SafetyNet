package com.example.safetynet.data

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
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
    private val fusedLocationClient: FusedLocationProviderClient
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

    // Deletes a specific SafetyPin from the DB
    suspend fun deletePin(pin: SafetyPin) {
        safetyPinDao.delete(pin)
    }

}