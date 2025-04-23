package com.main.proyek_salez.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.main.proyek_salez.R
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingKepala(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.salez_logo),
            contentDescription = "Salez Logo",
            modifier = Modifier
                .size(600.dp)
                .scale(1.5f)
                .padding(vertical = 2.dp)
        )
    }
}

@Composable
fun OnboardingIsi(headline: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            headline.uppercase().forEach { char ->
                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnboardingTombol(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClick,
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .shadow(
                    elevation = 15.dp,
                    shape = RoundedCornerShape(50)
                )
                .width(250.dp)
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Selanjutnya",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun OnboardingTampilan(headline: String, description: String, onSelanjutnya: () -> Unit) {
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
            )
    ) {
        OnboardingKepala(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 36.dp, y = (-300).dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-80).dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingIsi(headline, description)
            Spacer(modifier = Modifier.height(32.dp))
            OnboardingTombol(onClick = onSelanjutnya)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun OnboardingAlur(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val onSelanjutnya: () -> Unit = {
            if (page < 2) {
                scope.launch { pagerState.animateScrollToPage(page + 1) }
            } else {
                onFinish()
            }
        }

        when (page) {
            0 -> OnboardingTampilan(
                headline = "MUDAHKAN AKTIVITAS!",
                description = "Buat keseharianmu lancar tanpa hambatan serta buat menjadi lebih gesit dan instan!",
                onSelanjutnya = onSelanjutnya
            )
            1 -> OnboardingTampilan(
                headline = "BUAT LEBIH CEPAT!",
                description = "Manfaatkan semaksimal mungkin tanpa kesulitan dan buat pekerjaanmu menjadi lebih efektif!",
                onSelanjutnya = onSelanjutnya
            )
            2 -> OnboardingTampilan(
                headline = "BERIKAN CERITA!",
                description = "Bentuk momen - momen keseharianmu jadi lebih berwarna dan ciptakan pengalaman baru!",
                onSelanjutnya = onSelanjutnya
            )
        }
    }
}

@Composable
fun OnboardingApp(onFinish: () -> Unit) {
    ProyekSalezTheme {
        OnboardingAlur(onFinish = onFinish)
    }
}