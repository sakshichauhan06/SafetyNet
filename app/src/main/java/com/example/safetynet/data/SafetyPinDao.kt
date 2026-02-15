package com.example.safetynet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SafetyPinDao {

    @Insert
    suspend fun insert(safetyPin: SafetyPin)

    @Query("SELECT * FROM safety_pins")
    suspend fun getAllPins(): List<SafetyPin>

    @Delete
    suspend fun delete(safetyPin: SafetyPin)

    @Query("DELETE FROM safety_pins WHERE id = :pinId")
    suspend fun deleteById(pinId: String)

}