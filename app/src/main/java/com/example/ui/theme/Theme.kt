package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberDarkColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = CyberBg,
    primaryContainer = Color(0xFF00381C),
    onPrimaryContainer = NeonEmeraldGlow,
    secondary = ElectricPurple,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFF381559),
    onSecondaryContainer = ElectricPurpleGlow,
    tertiary = CyberGold,
    onTertiary = CyberBg,
    tertiaryContainer = Color(0xFF4D3800),
    onTertiaryContainer = CyberGold,
    background = CyberBg,
    onBackground = TextWhite,
    surface = CyberSurface,
    onSurface = TextWhite,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextGray,
    error = CyberCrimson,
    onError = TextWhite,
    outline = CyberCardBorder
)

@Composable
fun HabitQuestTheme(
    content: @Composable () -> Unit
) {
    // Default to Cyber-Fantasy Dark Mode for authentic RPG atmosphere
    MaterialTheme(
        colorScheme = CyberDarkColorScheme,
        typography = Typography,
        content = content
    )
}
