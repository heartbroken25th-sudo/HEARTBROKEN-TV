package com.heartbroken.tv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heartbroken.tv.ui.theme.Gold
import com.heartbroken.tv.ui.theme.Obsidian900
import com.heartbroken.tv.ui.theme.Obsidian950
import com.heartbroken.tv.ui.theme.TextSecondary

/**
 * SETTINGS — account, playback, display, network, about.
 *
 * Phase 2: structural placeholder with brand styling.
 * Phase 7: two-pane TV layout + phone category-detail navigation,
 *          plus real, wired-up settings that actually affect the app.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Obsidian950, Obsidian900)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "HEARTBROKEN TV",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Account, playback, display, network, and preferences.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 8.dp)
        ) {
            SettingsRowPlaceholder(label = "Account")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRowPlaceholder(label = "Playback")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRowPlaceholder(label = "Streaming Quality")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRowPlaceholder(label = "Audio")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRowPlaceholder(label = "Display")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRowPlaceholder(label = "Network")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRowPlaceholder(label = "About")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Real settings will be implemented in Phase 7.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun SettingsRowPlaceholder(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    )
}
