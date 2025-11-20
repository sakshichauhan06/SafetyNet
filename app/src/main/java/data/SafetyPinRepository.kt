package data


/**
 * Inserts a new safety pin into the local database.
 *
 * @param safetyPin The pin to insert
 * @return Result.success if inserted, Result.failure if database error occurs
 */
class SafetyPinRepository(private val safetyPinDao: SafetyPinDao) {

    suspend fun insertPin(safetyPin: SafetyPin): Result<Unit> {
        return try {
            safetyPinDao.insert(safetyPin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves all safety pins from the local database.
     *
     * @return Result.success with list of all pins, or empty list if none exist
     */
    suspend fun getAllPins(): Result<List<SafetyPin>> {
        return try {
            val pins = safetyPinDao.getAllPins()
            Result.success(pins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a specific safety pin by ID from the local database.
     */
    suspend fun deletePin(pinId: Long): Result<Unit> {
        return try {
            safetyPinDao.deleteById(pinId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}