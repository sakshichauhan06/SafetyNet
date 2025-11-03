package usecases

import data.SafetyPin
import data.SafetyPinRepository

class DeletePinUseCase(
    private val safetyPinRepository: SafetyPinRepository
) {
    suspend operator fun invoke(pin: SafetyPin): Result<Unit> {
        return try {
            safetyPinRepository.deletePin(pin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}