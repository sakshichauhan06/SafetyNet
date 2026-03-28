package com.example.safetynet.domain

import androidx.compose.ui.graphics.Color

/**
 *
 * Enum class where the severity of a pin is defined
 * based on the score and colorHex
 *
 */

enum class SeverityLevel(
    val displayName: String,
    val score: Int,
    val colorHex: String
) {
    RED("Critical", 4, "#FF0000"),
    ORANGE("High", 3, "#FF8800"),
    YELLOW("Medium", 2, "#FFFF00"),
    GREEN("Low", 0, "#00FF00"),

    GREY("Other", 1, "BAC3CC")
}