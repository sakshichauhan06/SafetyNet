package usecases

import com.google.android.gms.maps.model.LatLng
import data.SafetyPin
import data.SafetyPinRepository
import domain.SeverityLevel
import utils.AppConstants
import utils.LocationUtils

/**
 * Retrieves all safety pins within a 5km radius of the user's location.
 *
 * Filters pins by distance to show only relevant nearby incidents.
 * Results are sorted by severity (most dangerous first) and then by distance (closest first).
 *
 * @param userLocation The user's current location, or null to return all pins unfiltered
 * @return Result.success with filtered and sorted list of pins, or Result.failure if query fails
 */

class GetAllPinsUseCase(private val repository: SafetyPinRepository) {

    suspend operator fun invoke(userLocation: LatLng?): Result<List<SafetyPin>> {
        // radius filtering and sorting

        // 1. get all the pins from the repository
        val allPins = repository.getAllPins()

        // 2. if no user location, return all pins (can't filter)
        if (userLocation == null) {
            return allPins
        }

        // 3. filter pins within MAX_RADIUS_METERS
        val allPinsList = allPins.getOrNull() ?: emptyList()
        val radiusFilteredList = allPinsList.filter { pin ->
            val distance = LocationUtils.calculateDistance(
                LatLng(pin.latitude, pin.longitude),
                LatLng(userLocation.latitude, userLocation.longitude)
            )

            distance <= AppConstants.MAX_PIN_DISPLAY_RADIUS_METERS
        }

        // 4. sort by severity (red -> orange -> yellow -> green)
        // and then by distance
        val sortedPins = radiusFilteredList.sortedWith(compareBy(
            { severityPriority(it.severity) },
            { LocationUtils.calculateDistance(LatLng(it.latitude, it.longitude), userLocation) }
        ))

        // 5. wrap in Result / return filtered list
        return Result.success(sortedPins)
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