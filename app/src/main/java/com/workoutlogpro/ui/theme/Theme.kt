package com.workoutlogpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GitHubDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnBackground,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnBackground,
    tertiary = Tertiary,
    background = Background,
    surface = Surface,
    onBackground = OnBackground,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Color(0xFF30363D),
    outlineVariant = Color(0xFF21262D)
)

private val GitHubLightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = BackgroundLight,
    primaryContainer = SurfaceLight,
    onPrimaryContainer = OnBackgroundLight,
    secondary = Color(0xFF1F883D),
    onSecondary = BackgroundLight,
    background = BackgroundLight,
    surface = BackgroundLight,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF656D76),
    outline = Color(0xFFD0D7DE)
)

@Composable
fun WorkoutLogProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GitHubDarkColorScheme else GitHubLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
