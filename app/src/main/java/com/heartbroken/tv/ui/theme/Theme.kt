package com.heartbroken.tv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * HEARTBROKEN TV is a dark-only application. There is no light theme and no
 * system-following behavior — the obsidian black canvas is part of the brand.
 */
private val HeartbrokenDarkColorScheme = darkColorScheme(
    // Primary accent — Metallic Gold
    primary = Gold,
    onPrimary = Obsidian950,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,

    // Secondary — deeper gold for less-prominent accents
    secondary = GoldDeep,
    onSecondary = Obsidian950,
    secondaryContainer = Obsidian700,
    onSecondaryContainer = GoldLight,

    // Tertiary — light gold for highlights
    tertiary = GoldLight,
    onTertiary = Obsidian950,

    // Backgrounds
    background = Obsidian950,
    onBackground = TextPrimary,

    // Surfaces
    surface = Obsidian900,
    onSurface = TextPrimary,
    surfaceVariant = Obsidian800,
    onSurfaceVariant = TextSecondary,

    // Outlines / dividers
    outline = Obsidian600,
    outlineVariant = Obsidian700,

    // Error
    error = ErrorRed,
    onError = Obsidian950
)

/**
 * Root theme wrapper. Wrap every screen in this so that brand colors and
 * typography are applied consistently.
 */
@Composable
fun HeartbrokenTvTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HeartbrokenDarkColorScheme,
        typography = HeartbrokenTvTypography,
        content = content
    )
}
