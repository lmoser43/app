package com.raebai.dashcam.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── RaeB AI Brand Colors ─────────────────────────────────────────────
private val RaeBPrimary = Color(0xFF1E88E5)       // Blue
private val RaeBAccent = Color(0xFFFF5252)        // Red (Recording)
private val RaeBSurfaceDark = Color(0xFF121212)   // Dark surface
private val RaeBSurfaceLight = Color(0xFFF5F5F5)  // Light surface

private val DarkColorScheme = darkColorScheme(
    primary = RaeBPrimary,
    secondary = RaeBAccent,
    background = RaeBSurfaceDark,
    surface = RaeBSurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RaeBPrimary,
    secondary = RaeBAccent,
    background = RaeBSurfaceLight,
    surface = RaeBSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun RaeBAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
