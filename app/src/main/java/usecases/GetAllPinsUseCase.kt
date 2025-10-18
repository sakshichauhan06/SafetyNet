package usecases

import data.SafetyPin
import data.SafetyPinRepository

class GetAllPinsUseCase(private val repository: SafetyPinRepository) {

    suspend operator fun invoke(): Result<List<SafetyPin>> {
        // radius filtering and sorting
        return repository.getAllPins()
    }
}