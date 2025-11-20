package usecases

import data.SafetyPin
import data.SafetyPinRepository

/**
 * Deletes a specific safety pin by ID from the local database.
 *
 * @param pinId The ID of the pin to delete
 * @return Result.success if deleted, Result.failure if database error occurs
 */
class DeletePinUseCase(private val safetyPinRepository: SafetyPinRepository) {
    suspend operator fun invoke(pinId: Long): Result<Unit> {
        return safetyPinRepository.deletePin(pinId)
    }
}