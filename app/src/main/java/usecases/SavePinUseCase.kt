package usecases

import data.SafetyPin
import data.SafetyPinRepository

class SavePinUseCase(private val repository: SafetyPinRepository) {

    suspend operator fun invoke(safetyPin: SafetyPin): Result<Unit> {
        // duplicate checking
        return repository.insertPin(safetyPin)
    }

}