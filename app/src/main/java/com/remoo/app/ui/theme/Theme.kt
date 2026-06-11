package com.remoo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentGlow,
    onPrimaryContainer = AccentLight,
    secondary = AccentLight,
    onSecondary = Background,
    background = Background,
    onBackground = OnBackground,
    surface = Surface1,
    onSurface = OnSurface,
    surfaceVariant = Surface2,
    onSurfaceVariant = OnSurfaceDim,
    outline = Divider,
    error = Error,
    onError = Color.White
)

@Composable
fun RemooTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
