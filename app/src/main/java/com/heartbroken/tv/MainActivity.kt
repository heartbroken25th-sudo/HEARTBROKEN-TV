package com.heartbroken.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.heartbroken.tv.core.DeviceType
import com.heartbroken.tv.core.rememberDeviceType
import com.heartbroken.tv.navigation.Destination
import com.heartbroken.tv.navigation.PhoneBottomBar
import com.heartbroken.tv.navigation.TvNavigationRail
import com.heartbroken.tv.ui.home.HomeScreen
import com.heartbroken.tv.ui.livetv.LiveTvScreen
import com.heartbroken.tv.ui.player.PlayerScreen
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isPlayerScreen = currentRoute?.startsWith("player") == true

    when (deviceType) {
        DeviceType.TV -> TvLayout(navController, isPlayerScreen)
        DeviceType.PHONE, DeviceType.TABLET -> PhoneLayout(navController, isPlayerScreen)
    }
}

@Composable
private fun TvLayout(navController: NavHostController, isPlayerScreen: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian950)
    ) {
        if (!isPlayerScreen) {
            TvNavigationRail(navController = navController)
        }
        AppNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PhoneLayout(navController: NavHostController, isPlayerScreen: Boolean) {
    Scaffold(
        containerColor = Obsidian950,
        bottomBar = {
            if (!isPlayerScreen) {
                PhoneBottomBar(navController = navController)
            }
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
    navController: NavHostController,
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
            // This is the part that was missing!
            LiveTvScreen(
                onChannelClick = { channel ->
                    val encodedUrl = java.net.URLEncoder.encode(channel.streamUrl, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(channel.name, "UTF-8")
                    navController.navigate("player?url=$encodedUrl&name=$encodedName")
                }
            )
        }
        
        composable(Destination.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = "player?url={url}&name={name}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val url = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("url") ?: "", "UTF-8")
            val name = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("name") ?: "", "UTF-8")
            
            PlayerScreen(
                streamUrl = url,
                channelName = name,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
