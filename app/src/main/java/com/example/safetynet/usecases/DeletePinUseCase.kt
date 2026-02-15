package com.example.safetynet.usecases

import com.example.safetynet.data.SafetyPinRepository
import javax.inject.Inject

/**
 * Deletes a specific safety pin by ID from the local database.
 *
 * @param pinId The ID of the pin to delete
 * @return Result.success if deleted, Result.failure if database error occurs
 */
class DeletePinUseCase @Inject constructor(
    private val safetyPinRepository: SafetyPinRepository
) {
    suspend operator fun invoke(pinId: String): Result<Unit> {
        return safetyPinRepository.deletePin(pinId)
    }
}