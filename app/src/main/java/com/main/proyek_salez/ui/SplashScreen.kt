package com.main.proyek_salez.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.main.proyek_salez.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(duration: Long, onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(duration)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.salez_logo),
            contentDescription = "Salez Logo",
            modifier = Modifier
                .fillMaxWidth(1f)
                .scale(1.5f)
                // --- PERUBAHAN UTAMA DI SINI ---
                // Geser gambar ke kanan secara manual.
                // Ubah nilai '20.dp' ini sesuai kebutuhan.
                .offset(x = 20.dp)
        )
    }
}