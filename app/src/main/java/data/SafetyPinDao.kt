package data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SafetyPinDao {

    @Insert
    suspend fun insert(safetyPin: SafetyPin)

    @Query("SELECT * FROM SafetyPin")
    suspend fun getAllPins(): List<SafetyPin>

    @Delete
    suspend fun delete(safetyPin: SafetyPin)

    @Query("DELETE FROM SafetyPin WHERE id = :pinId")
    suspend fun deleteById(pinId: Long)

}