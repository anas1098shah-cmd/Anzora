package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AnzoraDarkColorScheme = darkColorScheme(
    primary = AnzoraElectricBlue,
    onPrimary = AnzoraNavyPrimary,
    primaryContainer = AnzoraNavyCard,
    onPrimaryContainer = AnzoraElectricBlue,
    secondary = AnzoraElectricBlueDark,
    onSecondary = AnzoraWhite,
    secondaryContainer = AnzoraNavyBorder,
    onSecondaryContainer = AnzoraSlateLight,
    tertiary = AnzoraWhatsAppGreen,
    onTertiary = AnzoraNavyPrimary,
    background = AnzoraNavyPrimary,
    onBackground = AnzoraWhite,
    surface = AnzoraNavySurface,
    onSurface = AnzoraWhite,
    surfaceVariant = AnzoraNavyCard,
    onSurfaceVariant = AnzoraSlateLight,
    outline = AnzoraNavyBorder,
    error = AnzoraError,
    onError = AnzoraWhite
)

private val AnzoraLightColorScheme = lightColorScheme(
    primary = AnzoraElectricBlueDark,
    onPrimary = AnzoraWhite,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = AnzoraNavyPrimary,
    secondary = AnzoraNavyPrimary,
    onSecondary = AnzoraWhite,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = AnzoraNavyPrimary,
    tertiary = AnzoraWhatsAppGreen,
    onTertiary = AnzoraNavyPrimary,
    background = AnzoraOffWhite,
    onBackground = AnzoraTextDark,
    surface = AnzoraWhite,
    onSurface = AnzoraTextDark,
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = AnzoraTextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    error = AnzoraError,
    onError = AnzoraWhite
)

@Composable
fun AnzoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AnzoraDarkColorScheme else AnzoraLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    AnzoraTheme(darkTheme = darkTheme, content = content)
}
