package com.example.safetynet.usecases

import com.example.safetynet.data.SafetyPin
import com.example.safetynet.data.SafetyPinRepository
import com.google.android.gms.maps.model.LatLng
import com.example.safetynet.utils.AppConstants
import com.example.safetynet.utils.LocationUtils
import javax.inject.Inject

/**
 * Saves a safety pin to the database after validating no duplicates exist nearby.
 *
 * Prevents users from creating multiple pins at the same location by checking
 * if any existing pin is within 50 meters of the new pin's location.
 * This helps maintain data quality and prevents spam.
 *
 * @param safetyPin The safety pin to be saved with location, severity, and descriptions
 * @return Result.success(Unit) if saved successfully, Result.failure if duplicate found or save failed
 */

class SavePinUseCase @Inject constructor(
    private val repository: SafetyPinRepository
) {


    suspend operator fun invoke(safetyPin: SafetyPin): Result<Unit> {

        // get all the existing pins
        val existingPinsResult = repository.getAllPinsFromCloud()

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

            if (distance < AppConstants.DUPLICATE_DETECTION_RADIUS_METERS) {
                // duplicate found!! return error
                return Result.failure(Exception("Pin already exists within ${AppConstants.DUPLICATE_DETECTION_RADIUS_METERS}"))
            }
        }

        // duplicate checking
        return repository.savePin(safetyPin)
    }

}

