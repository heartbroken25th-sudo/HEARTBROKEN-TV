package com.heartbroken.tv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HeartbrokenDarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Obsidian950,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,

    secondary = GoldDeep,
    onSecondary = Obsidian950,
    secondaryContainer = Obsidian700,
    onSecondaryContainer = GoldLight,

    tertiary = GoldLight,
    onTertiary = Obsidian950,

    background = Obsidian950,
    onBackground = TextPrimary,

    surface = Obsidian900,
    onSurface = TextPrimary,
    surfaceVariant = Obsidian800,
    onSurfaceVariant = TextSecondary,

    outline = Obsidian600,
    outlineVariant = Obsidian700,

    error = ErrorRed,
    onError = Obsidian950
)

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
