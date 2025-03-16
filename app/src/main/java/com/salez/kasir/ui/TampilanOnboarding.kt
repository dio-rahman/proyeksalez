package com.salez.kasir.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salez.kasir.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TampilanOnboarding(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val context = LocalContext.current
    val MontserratFont = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        Image(
            painter = painterResource(pages[pagerState.currentPage].imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RectangleShape),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = pages[page].title,
                        style = TextStyle(
                            fontFamily = MontserratFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = pages[page].description,
                        style = TextStyle(
                            fontFamily = MontserratFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )
                    )
                    Spacer(Modifier.height(screenHeight * 0.05f))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Kembali")
                    }
                } else {
                    Spacer(Modifier.width(8.dp))
                }
                Button(
                    onClick = {
                        if (pagerState.currentPage == pages.size - 1) {
                            setOnboardingCompleted(context)
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (pagerState.currentPage == pages.size - 1) "Mulai" else "Selanjutnya")
                }
            }
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

val pages = listOf(
    OnboardingPage("Selamat Datang", "Gunakan Secara Gratis Tanpa Ada Hambatan Apa Pun!", R.drawable.god_highschool),
    OnboardingPage("Mudahkan Keseharianmu", "Dapatkan Pengalaman Terbaikmu Disini!", R.drawable.demon_slayer),
    OnboardingPage("Gunakan Secara Instan", "Gunakan Tanpa Ribet dan Perkerjaanmu Semakin Lancar!", R.drawable.solo_leveling)
)