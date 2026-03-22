package com.example.safetynet.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorOnPrimary,
    primaryContainer = ColorPrimary,
    onPrimaryContainer = ColorOnDarkBackground,

    secondary = ColorSecondary,
    onSecondary = ColorOnSecondary,
    secondaryContainer = ColorSecondary,
    onSecondaryContainer = ColorOnDarkBackground,

    tertiary = ColorTertiary,
    onTertiary = ColorOnTertiary,

    background = ColorDarkBackground,
    onBackground = ColorOnDarkBackground,
    surface = ColorDarkBackground,
    onSurface = ColorOnDarkBackground,

    error = ColorError,
    onError = ColorOnError,
)

private val LightColorScheme = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorOnPrimary,
    primaryContainer = ColorPrimaryVariant,
    onPrimaryContainer = ColorPrimary,

    secondary = ColorSecondary,
    onSecondary = ColorOnSecondary,
    secondaryContainer = ColorSecondaryVariant,
    onSecondaryContainer = ColorSecondary,

    tertiary = ColorTertiary,
    onTertiary = ColorOnTertiary,

    background = ColorLightBackground,
    onBackground = ColorOnLightBackground,
    surface = ColorSurface,
    onSurface = ColorOnSurface,

    error = ColorError,
    onError = ColorOnError,
)

@Composable
fun SafetyNetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false if you want your custom colors to show instead of system wallpaper colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // This block ensures the Status Bar matches your theme color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}