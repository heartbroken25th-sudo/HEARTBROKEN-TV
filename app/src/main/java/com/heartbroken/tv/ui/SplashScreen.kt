package com.heartbroken.tv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heartbroken.tv.R
import com.heartbroken.tv.ui.theme.Gold
import com.heartbroken.tv.ui.theme.Obsidian950
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Wait 2.5 seconds
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Obsidian950, Obsidian950, Brush.lighten(Obsidian950))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Loads the custom app_icon.png you uploaded earlier
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "Heartbroken TV Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(180.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "HEARTBROKEN TV",
                color = Gold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "WATCH • ENJOY • BE ENTERTAINED",
                color = Gold.copy(alpha = 0.7f),
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

// Helper to make a slightly lighter version of black for the bottom gradient
private fun Brush.Companion.lighten(color: androidx.compose.ui.graphics.Color): androidx.compose.ui.graphics.Color {
    return androidx.compose.ui.graphics.Color(
        red = (color.red + 0.1f).coerceAtMost(1f),
        green = (color.green + 0.1f).coerceAtMost(1f),
        blue = (color.blue + 0.1f).coerceAtMost(1f),
        alpha = color.alpha
    )
}
