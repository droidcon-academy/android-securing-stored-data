package com.droidcon.vaultkeeper.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF006A6A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF63A1A1),
    onPrimaryContainer = Color(0xFF002020),
    secondary = Color(0xFF4A6363),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E7),
    onSecondaryContainer = Color(0xFF051F1F),
    tertiary = Color(0xFF4B607C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD3E4FF),
    onTertiaryContainer = Color(0xFF031D36),
    background = Color(0xFFFAFDFD),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFF8FAFA),
    onSurface = Color(0xFF191C1C)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4FDADA),
    onPrimary = Color(0xFF003737),
    primaryContainer = Color(0xFF004F4F),
    onPrimaryContainer = Color(0xFF6FF7F7),
    secondary = Color(0xFFB0CCCC),
    onSecondary = Color(0xFF1B3434),
    secondaryContainer = Color(0xFF324B4B),
    onSecondaryContainer = Color(0xFFCCE8E7),
    tertiary = Color(0xFFB1C5E8),
    onTertiary = Color(0xFF1A324B),
    tertiaryContainer = Color(0xFF334863),
    onTertiaryContainer = Color(0xFFD3E4FF),
    background = Color(0xFF191C1C),
    onBackground = Color(0xFFE1E3E3),
    surface = Color(0xFF101414),
    onSurface = Color(0xFFE1E3E3)
)

@Composable
fun VaultKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
} 