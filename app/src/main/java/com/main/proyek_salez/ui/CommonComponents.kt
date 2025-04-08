package com.main.proyek_salez.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.main.proyek_salez.R
import com.main.proyek_salez.ui.theme.*

@Composable
fun SidebarMenu(onCloseDrawer: () -> Unit) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Jingga,
            Oranye
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(brush = gradientBackground)
            .padding(horizontal = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.salez_logo),
                contentDescription = "Salez Logo",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopStart)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Halo",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Dio!",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = UnguTua,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Periksa Aktivitas Keseharian Anda Disini!",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(40.dp))
        MenuItem(
            text = "Profil",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold
            )
        )
        MenuItem(
            text = "List Menu",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold
            )
        )
        MenuItem(
            text = "Cek Histori",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold
            )
        )
        MenuItem(
            text = "Log Out",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun MenuItem(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = Putih,
        fontWeight = FontWeight.Bold
    )
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = style
        )
    }
}