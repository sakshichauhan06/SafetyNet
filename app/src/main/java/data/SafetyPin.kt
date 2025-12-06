package data

import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.IncidentType
import domain.SeverityLevel

@Entity
data class SafetyPin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val incidentType: IncidentType,
    val severity: SeverityLevel, // used enum class
    val shortDescription: String,
    val detailedDescription: String,
    val timestamp: Long,
    val isAnonymous: Boolean
)
