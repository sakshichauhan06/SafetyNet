package usecases

import com.google.android.gms.maps.model.LatLng
import data.SafetyPin
import data.SafetyPinRepository
import domain.SeverityLevel
import utils.LocationUtils

class GetAllPinsUseCase(private val repository: SafetyPinRepository) {

    companion object {
        private const val MAX_RADIUS_METERS = 5000.0
    }

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

            distance <= MAX_RADIUS_METERS
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