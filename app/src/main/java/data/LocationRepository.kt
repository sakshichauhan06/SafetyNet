package data

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationRepository(private val fusedLocationClient: FusedLocationProviderClient) {
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
}