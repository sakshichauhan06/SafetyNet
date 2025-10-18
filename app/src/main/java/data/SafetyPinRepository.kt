package data

class SafetyPinRepository(private val safetyPinDao: SafetyPinDao) {

    suspend fun insertPin(safetyPin: SafetyPin): Result<Unit> {
        return try {
            safetyPinDao.insert(safetyPin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllPins(): Result<List<SafetyPin>> {
        return try {
            val pins = safetyPinDao.getAllPins()
            Result.success(pins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePin(safetyPin: SafetyPin): Result<Unit> {
        return try {
            safetyPinDao.delete(safetyPin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}