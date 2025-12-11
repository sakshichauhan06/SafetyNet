package com.example.safetynet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetynet.R
import com.example.safetynet.ui.theme.Typography

// Set of Material typography styles to start with

@OptIn(ExperimentalTextApi::class)
val HostGrotesk = FontFamily(
    // 1. Regular/Normal Weight
    Font(
        R.font.host_grotesk_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            settings = arrayOf(
                FontVariation.weight(400),
                FontVariation.width(30f),
            )
        )
    ),

    // 2. Light Weight
    Font(
        R.font.host_grotesk_variable,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            settings = arrayOf(
                FontVariation.weight(300),
                FontVariation.width(30f),
            )
        )
    ),

    // 3. Medium Weight
    Font(
        R.font.host_grotesk_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            settings = arrayOf(
                FontVariation.weight(500),
                FontVariation.width(30f),
            )
        )
    ),

    // 6. Medium Italic
    Font(
        R.font.host_grotesk_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            settings = arrayOf(
                FontVariation.weight(500),
                FontVariation.width(30f),
            )
        )
    ),
    // 4. Semibold Weight
    Font(
        R.font.host_grotesk_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            settings = arrayOf(
                FontVariation.weight(600),
                FontVariation.width(30f),
            )
        )
    ),

    // 5. Bold Weight
    Font(
        R.font.host_grotesk_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            settings = arrayOf(
                FontVariation.weight(700),
                FontVariation.width(30f),
            )
        )
    ),
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp
    ),

    // hero layout, marketing, large headlines
    headlineLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 21.sp
    ),

    // content titles
    titleLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    // soft verbs, body copy, descriptions
    bodyLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 14.sp
    ),

    // buttons, badges, microcopy
    labelLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    )
)