package com.example.safetynet.usecases

import com.example.safetynet.data.SafetyPin
import com.example.safetynet.data.SafetyPinRepository
import com.google.android.gms.maps.model.LatLng
import com.example.safetynet.domain.SeverityLevel
import com.example.safetynet.utils.AppConstants
import com.example.safetynet.utils.LocationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Retrieves all safety pins within a 5km radius of the user's location.
 *
 * Filters pins by distance to show only relevant nearby incidents.
 * Results are sorted by severity (most dangerous first) and then by distance (closest first).
 *
 * @param userLocation The user's current location, or null to return all pins unfiltered
 * @return Result.success with filtered and sorted list of pins, or Result.failure if query fails
 */

class GetAllPinsUseCase @Inject constructor(
    private val repository: SafetyPinRepository
) {

    operator fun invoke(userLocation: LatLng?): Flow<List<SafetyPin>> {
        // 1. Get the raw real-time stream from the repo
        return repository.getPinsRealtime().map { allPins ->

            // 2. If no user location, return all pins unsorted
            if (userLocation == null) {
                return@map allPins
            }

            // 3. Filter pins within MAX_PIN_DISPLAY_RADIUS_METERS
            val radiusFilteredList = allPins.filter { pin ->
                val distance = LocationUtils.calculateDistance(
                    LatLng(pin.latitude, pin.longitude),
                    userLocation
                )
                distance <= AppConstants.MAX_PIN_DISPLAY_RADIUS_METERS
            }

            // 4. Sort by severity (Red -> Green) and then by distance
            radiusFilteredList.sortedWith(compareBy(
                { severityPriority(it.severity) },
                { LocationUtils.calculateDistance(LatLng(it.latitude, it.longitude), userLocation) }
            ))
        }
    }

    private fun severityPriority(severity: SeverityLevel): Int {
        return when (severity) {
            SeverityLevel.RED -> 0
            SeverityLevel.ORANGE -> 1
            SeverityLevel.YELLOW -> 2
            SeverityLevel.GREEN -> 3
        }
    }
}