package com.example.app_kotlin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF164E63),
    onPrimaryContainer = Color(0xFFCFFAFE),
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = NeonBlue,
    background = ObsidianBackground,
    onBackground = Color(0xFFF9FAFB),
    surface = ObsidianSurface,
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = Color(0xFF9CA3AF),
    error = DangerRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF075985),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF581C87),
    tertiary = Color(0xFF0284C7),
    background = LightTechBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightTechSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightTechSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    error = DangerRose,
    onError = Color.White
)

@Composable
fun AppkotlinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}