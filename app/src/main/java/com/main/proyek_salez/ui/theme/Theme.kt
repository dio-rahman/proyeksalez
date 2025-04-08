package com.main.proyek_salez.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = UnguTua,
    secondary = Oranye,
    background = Putih,
    surface = Jingga,
    onPrimary = Putih,
    onSecondary = UnguTua,
    onBackground = UnguTua,
    onSurface = UnguTua
)

private val DarkColorScheme = darkColorScheme(
    primary = UnguTua,
    secondary = Oranye,
    background = UnguTua,
    surface = Jingga,
    onPrimary = Putih,
    onSecondary = UnguTua,
    onBackground = Putih,
    onSurface = Putih
)

@Composable
fun ProyekSalezTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}