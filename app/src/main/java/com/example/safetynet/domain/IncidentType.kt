package com.example.safetynet.domain

enum class IncidentType(
    val displayName: String,
    val severity: SeverityLevel
) {
    HARASSMENT("Harassment", SeverityLevel.RED),
    ASSAULT("Assault", SeverityLevel.RED),
    THEFT_ROBBERY("Theft/Robbery", SeverityLevel.ORANGE),
    FLOODED_AREA("Flooded Area", SeverityLevel.ORANGE),
    SUSPICIOUS_ACTIVITY("Suspicious Activity", SeverityLevel.YELLOW),
    POOR_LIGHTING("Poor Lighting", SeverityLevel.YELLOW),
    UNEVEN_ROAD("Uneven Road", SeverityLevel.YELLOW),
    OTHER("Other", SeverityLevel.YELLOW)
}