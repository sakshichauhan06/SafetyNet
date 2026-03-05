package com.example.safetynet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SafetyPinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(safetyPin: SafetyPin)

    @Query("SELECT * FROM safety_pins")
    fun getAllPins(): Flow<List<SafetyPin>>

    @Query("SELECT * FROM safety_pins")
    suspend fun getAllPinsAsList(): List<SafetyPin>

    @Delete
    suspend fun delete(safetyPin: SafetyPin)

    @Query("DELETE FROM safety_pins WHERE id = :pinId")
    suspend fun deleteById(pinId: String)

}