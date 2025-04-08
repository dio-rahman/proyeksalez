package com.salez.kasir.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF004D90),
    secondary = Color(0xFF689F38),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDEFD0),
    onSecondaryContainer = Color(0xFF2E6605),
    tertiary = Color(0xFFF57C00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFF9A4D00),
    error = Color(0xFFD32F2F),
    errorContainer = Color(0xFFFFCDD2),
    onError = Color.White,
    onErrorContainer = Color(0xFF8C0000),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF121212),
    surface = Color.White,
    onSurface = Color(0xFF121212),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF004D90),
    primaryContainer = Color(0xFF0069C2),
    onPrimaryContainer = Color(0xFFD6E6FF),
    secondary = Color(0xFFAED581),
    onSecondary = Color(0xFF2E6605),
    secondaryContainer = Color(0xFF4C8121),
    onSecondaryContainer = Color(0xFFD8EECA),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF9A4D00),
    tertiaryContainer = Color(0xFFD36A00),
    onTertiaryContainer = Color(0xFFFFE0B2),
    error = Color(0xFFEF9A9A),
    errorContainer = Color(0xFF8C0000),
    onError = Color(0xFF8C0000),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
)

@Composable
fun CafeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}