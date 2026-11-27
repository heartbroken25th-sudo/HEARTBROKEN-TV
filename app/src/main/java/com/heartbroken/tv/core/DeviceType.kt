package com.heartbroken.tv.core

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * The three form factors HEARTBROKEN TV is designed for.
 *
 * - PHONE:  portrait or landscape phone
 * - TABLET: large-screen device (smallest width >= 600dp)
 * - TV:     Android TV device, TV box, or Fire TV — detected via UiModeManager
 */
enum class DeviceType {
    PHONE,
    TABLET,
    TV
}

/**
 * Compose-friendly helper that remembers the detected device type for the
 * lifetime of the calling composition. Recomputes if configuration changes
 * (e.g. rotation, multi-window resize).
 */
@Composable
fun rememberDeviceType(): DeviceType {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(context, configuration) {
        detectDeviceType(context, configuration)
    }
}

/**
 * Raw detection function (usable outside Compose if ever needed — e.g. from
 * a ViewModel or a worker).
 */
fun detectDeviceType(context: Context, configuration: Configuration): DeviceType {
    val uiModeManager =
        context.getSystemService(Context.UI_MODE_SERVICE) as? android.app.UiModeManager
    val isTv = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    if (isTv) return DeviceType.TV

    val smallestWidthDp = configuration.smallestScreenWidthDp
    return if (smallestWidthDp >= 600) DeviceType.TABLET else DeviceType.PHONE
}
