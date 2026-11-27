package com.heartbroken.tv.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The three primary navigation destinations of HEARTBROKEN TV.
 *
 * These are the ONLY top-level destinations in the app. Do not add more without
 * updating the navigation architecture — Home, Live TV, and Settings are the
 * entire product surface.
 *
 * Each destination carries:
 *  - route:         the string used with NavHost / NavController
 *  - label:         human-readable name shown in nav UI
 *  - selectedIcon:  filled variant (when the destination is active)
 *  - unselectedIcon: outlined variant (when it is not active)
 */
sealed class Destination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Destination(
        route = "home",
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object LiveTv : Destination(
        route = "livetv",
        label = "Live TV",
        selectedIcon = Icons.Filled.LiveTv,
        unselectedIcon = Icons.Outlined.LiveTv
    )

    object Settings : Destination(
        route = "settings",
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        /** Ordered list used by both the phone bottom bar and the TV nav rail. */
        val all: List<Destination> = listOf(Home, LiveTv, Settings)
    }
}
