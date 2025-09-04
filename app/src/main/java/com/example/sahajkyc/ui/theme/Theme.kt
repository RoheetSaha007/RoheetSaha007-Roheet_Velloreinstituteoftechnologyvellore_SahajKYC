package com.example.sahajkyc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    background = Color(0xFFF9F9F9),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1F1F1F),
    onSurface = Color(0xFF1F1F1F),
    secondary = Color(0xFF0D47A1)
)

val SahajGreen = Color(0xFF4CAF50)
val SahajBlue = Color(0xFF2196F3)

@Composable
fun SahajKYCTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
