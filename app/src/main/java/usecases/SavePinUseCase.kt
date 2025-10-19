package usecases

import com.google.android.gms.maps.model.LatLng
import data.SafetyPin
import data.SafetyPinRepository
import utils.LocationUtils

class SavePinUseCase(private val repository: SafetyPinRepository) {

    companion object {
        private const val MIN_DISTANCE_METERS = 50
    }

    suspend operator fun invoke(safetyPin: SafetyPin): Result<Unit> {

        // get all the existing pins
        val existingPinsResult = repository.getAllPins()

        // handle if getting pins fails
        if (existingPinsResult.isFailure) {
            return Result.failure(existingPinsResult.exceptionOrNull()!!)
        }

        // get the list of pins
        val existingPins = existingPinsResult.getOrNull() ?: emptyList()

        // check each existing pin's distance
        existingPins.forEach { existingPin ->
            val distance = LocationUtils.calculateDistance(
                LatLng(existingPin.latitude, existingPin.longitude),
                LatLng(safetyPin.latitude, safetyPin.longitude)
            )

            if (distance < MIN_DISTANCE_METERS) {
                // duplicate found!! return error
                return Result.failure(Exception("Pin already exists within 50m"))
            }
        }

        // duplicate checking
        return repository.insertPin(safetyPin)
    }

}

