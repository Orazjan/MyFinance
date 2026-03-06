package com.example.myfinance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Светлая тема
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    primaryContainer = PrimaryBlueVariant,
    secondary = AccentGreen,
    onSecondary = White,
    secondaryContainer = AccentGreenVariant,
    background = BackgroundLight,
    onBackground = TextDarkPrimary,
    surface = CardBackgroundLight,
    onSurface = TextDarkPrimary,
    error = AlertRed,
    onError = White
)

// Тёмная тема
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryBlueVariantDark,
    secondary = AccentGreenDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = AccentGreenVariantDark,
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    error = AlertRedDark,
    onError = Black
)

@Composable
fun MyFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // typography = Typography,
        content = content
    )
}