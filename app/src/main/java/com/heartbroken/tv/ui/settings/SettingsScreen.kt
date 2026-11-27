package com.heartbroken.tv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartbroken.tv.core.DeviceType
import com.heartbroken.tv.ui.theme.Gold
import com.heartbroken.tv.ui.theme.Obsidian700
import com.heartbroken.tv.ui.theme.Obsidian800
import com.heartbroken.tv.ui.theme.Obsidian900
import com.heartbroken.tv.ui.theme.Obsidian950
import com.heartbroken.tv.ui.theme.TextSecondary

private val categories = listOf(
    "Playback",
    "Display",
    "Network",
    "About"
)

@Composable
fun SettingsScreen(
    deviceType: DeviceType,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (deviceType == DeviceType.TV) {
        TvSettingsLayout(state, viewModel, modifier)
    } else {
        PhoneSettingsLayout(state, viewModel, modifier)
    }
}

// ==========================================
// TV TWO-PANE LAYOUT
// ==========================================
@Composable
private fun TvSettingsLayout(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(listOf(Obsidian950, Obsidian900)))
            .padding(32.dp) // TV Safe Margins
    ) {
        // Left Rail (Categories)
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(Obsidian800, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = Gold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSelected) Gold else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory = category }
                        .background(
                            if (isSelected) Obsidian700 else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.width(32.dp))

        // Right Panel (Details)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Obsidian800, RoundedCornerShape(16.dp))
                .padding(32.dp)
        ) {
            Text(
                text = selectedCategory,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Spacer(Modifier.height(32.dp))
            SettingsDetailsPanel(selectedCategory, state, viewModel)
        }
    }
}

// ==========================================
// PHONE SINGLE-PANE LAYOUT
// ==========================================
@Composable
private fun PhoneSettingsLayout(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var activeDetail by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Obsidian950, Obsidian900)))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (activeDetail != null) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Gold,
                    modifier = Modifier
                        .clickable { activeDetail = null }
                        .padding(end = 16.dp)
                )
            }
            Text(
                text = activeDetail ?: "Settings",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (activeDetail == null) {
            // List of categories
            LazyColumn(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Obsidian800, RoundedCornerShape(16.dp))
                            .clickable { activeDetail = category }
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = TextSecondary
                        )
                    }
                }
            }
        } else {
            // Detail Panel
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SettingsDetailsPanel(activeDetail!!, state, viewModel)
            }
        }
    }
}

// ==========================================
// REUSABLE DETAILS PANEL
// ==========================================
@Composable
private fun SettingsDetailsPanel(
    category: String,
    state: SettingsUiState,
    viewModel: SettingsViewModel
) {
    when (category) {
        "Playback" -> {
            SettingToggle(
                title = "Autoplay",
                description = "Automatically start playing channels when opened.",
                checked = state.autoplay,
                onCheckedChange = viewModel::setAutoplay
            )
            HorizontalDivider(color = Obsidian700, modifier = Modifier.padding(vertical = 16.dp))
            SettingDropdown(
                title = "Preferred Quality",
                currentValue = state.preferredQuality,
                options = listOf("Auto", "1080p", "720p", "480p"),
                onValueSelected = viewModel::setPreferredQuality
            )
        }
        "Display" -> {
            SettingDropdown(
                title = "App Theme",
                currentValue = state.appTheme,
                options = listOf("Dark", "System Default"),
                onValueSelected = viewModel::setAppTheme
            )
        }
        "Network" -> {
            SettingToggle(
                title = "Stream over Wi-Fi only",
                description = "Save mobile data by preventing streaming on cellular networks.",
                checked = state.wifiOnly,
                onCheckedChange = viewModel::setWifiOnly
            )
        }
        "About" -> {
            Text(
                text = "HEARTBROKEN TV",
                style = MaterialTheme.typography.titleLarge,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "A premium native Android TV and Mobile Live TV application.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Obsidian950,
                checkedTrackColor = Gold,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Obsidian700
            )
        )
    }
}

@Composable
private fun SettingDropdown(
    title: String,
    currentValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option == currentValue
                Text(
                    text = option,
                    color = if (isSelected) Obsidian950 else TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .background(
                            if (isSelected) Gold else Obsidian700,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onValueSelected(option) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
