package com.example.safetynet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.safetynet.domain.IncidentType
import com.example.safetynet.domain.SeverityLevel

@Entity(tableName = "safety_pins")
data class SafetyPin(
    @PrimaryKey
    val id: String = "", // Firestore document ID
    val userId: String = "", // Who created it
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val incidentType: IncidentType = IncidentType.OTHER,
    val severity: SeverityLevel = SeverityLevel.YELLOW, // used enum class
    val shortDescription: String = "",
    val detailedDescription: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isAnonymous: Boolean = false
)
