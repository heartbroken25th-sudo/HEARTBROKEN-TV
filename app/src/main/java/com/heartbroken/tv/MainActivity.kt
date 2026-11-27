package com.heartbroken.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heartbroken.tv.core.DeviceType
import com.heartbroken.tv.core.rememberDeviceType
import com.heartbroken.tv.navigation.Destination
import com.heartbroken.tv.navigation.PhoneBottomBar
import com.heartbroken.tv.navigation.TvNavigationRail
import com.heartbroken.tv.ui.home.HomeScreen
import com.heartbroken.tv.ui.livetv.LiveTvScreen
import com.heartbroken.tv.ui.settings.SettingsScreen
import com.heartbroken.tv.ui.theme.HeartbrokenTvTheme
import com.heartbroken.tv.ui.theme.Obsidian950

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeartbrokenTvTheme {
                HeartbrokenTvApp()
            }
        }
    }
}

@Composable
private fun HeartbrokenTvApp() {
    val navController = rememberNavController()
    val deviceType = rememberDeviceType()

    when (deviceType) {
        DeviceType.TV -> TvLayout(navController)
        DeviceType.PHONE, DeviceType.TABLET -> PhoneLayout(navController)
    }
}

@Composable
private fun TvLayout(
    navController: androidx.navigation.NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian950)
    ) {
        TvNavigationRail(navController = navController)
        AppNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PhoneLayout(
    navController: androidx.navigation.NavHostController
) {
    Scaffold(
        containerColor = Obsidian950,
        bottomBar = {
            PhoneBottomBar(navController = navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
private fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier
    ) {
        composable(Destination.Home.route) {
            HomeScreen()
        }
        composable(Destination.LiveTv.route) {
            LiveTvScreen()
        }
        composable(Destination.Settings.route) {
            SettingsScreen()
        }
    }
}
