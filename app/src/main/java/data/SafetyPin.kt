package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SafetyPin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val severity: String, // later
    val shortDescription: String,
    val detailedDescription: String,
    val timestamp: Long,
    val isAnonymous: Boolean
)
