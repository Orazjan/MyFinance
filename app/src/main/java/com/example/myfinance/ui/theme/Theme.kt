package com.example.myfinance.ui.theme

import BackgroundDark
import BackgroundLight
import ErrorLight
import OnPrimaryDark
import OnPrimaryLight
import OutlineVariantDark
import OutlineVariantLight
import PrimaryContainerDark
import PrimaryContainerLight
import PrimaryDark
import PrimaryLight
import SurfaceContainerDark
import SurfaceContainerHighestDark
import SurfaceContainerHighestLight
import SurfaceContainerLight
import SurfaceContainerLowDark
import SurfaceContainerLowLight
import SurfaceContainerLowestDark
import SurfaceContainerLowestLight
import SurfaceDark
import SurfaceLight
import TextPrimaryDark
import TextPrimaryLight
import TextSecondaryDark
import TextSecondaryLight
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorLight,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorLight,
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

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.onPrimary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        // typography = Typography,
        content = content
    )
}