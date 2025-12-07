package com.example.safetynet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.safetynet.R
import com.example.safetynet.ui.theme.Typography

// Set of Material typography styles to start with

val HostGrotesk = FontFamily(
    // 1. Regular/Normal Weight
    Font(R.font.host_grotesk_regular, FontWeight.Normal),

    // 2. Light Weight
    Font(R.font.host_grotesk_light, FontWeight.Light),

    // 3. Medium Weight
    Font(R.font.host_grotesk_medium, FontWeight.Medium),

    // 4. Semibold Weight
    Font(R.font.host_grotesk_semibold, FontWeight.SemiBold),

    // 5. Bold Weight
    Font(R.font.host_grotesk_bold, FontWeight.Bold),

    // 6. Medium Italic (Requires FontStyle)
    Font(R.font.host_grotesk_medium_italic, FontWeight.Medium, style = FontStyle.Italic)
)
val Typography = Typography(
    // 1) SPLASH SCREEN HEADING: Bold (128)
    displayLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 128.sp
    ),

    // 1) SPLASH SCREEN TAGLINE: Medium Italic (48)
    headlineLarge = TextStyle(
        fontFamily = HostGrotesk,
        // Use the actual medium weight now that the FontFamily is correct
        fontWeight = FontWeight.Medium,
        fontSize = 48.sp,
        // Apply the italic style directly to load the italic font file
        fontStyle = FontStyle.Italic
    ),

    // 2) DIALOG TITLE: Semibold (48)
    headlineMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),

    // ✅ DIALOG BODY: Regular (32) - Now the only definition for bodyLarge
    bodyLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
        // Add default line heights/letter spacing if needed, e.g.,
        // lineHeight = 40.sp, letterSpacing = 0.sp
    ),

    // 2) DIALOG BUTTON: Medium (32)
    labelLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp
    ),

    // 2) REPORT DIALOG BUTTON: Semibold (36)
    titleLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp
    ),

    // 2) DIALOG OPTIONAL TEXT: Light (24)
    labelSmall = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Light,
        fontSize = 24.sp
    )

    /* You can now delete the commented-out default text styles */
)