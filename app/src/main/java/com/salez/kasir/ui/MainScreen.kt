package com.salez.kasir.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.salez.kasir.R

@Composable
fun MainScreen(navController: NavHostController) {
    val MontserratFont = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Salez",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = MontserratFont
            )
        )

        Text(
            text = "Pembayaran instan serta akses mudah!",
            style = TextStyle(fontFamily = MontserratFont, fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { navController.navigate("MasukanMenu") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Masukan Data Secara Manual", fontFamily = MontserratFont)
        }

        Button(
            onClick = { navController.navigate("MasukanMenuAutoExcel") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Masukan Data Melalui File Excel", fontFamily = MontserratFont)
        }

        Button(
            onClick = { navController.navigate("MasukanMenuAutoPdf") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Masukan Data Melalui File PDF", fontFamily = MontserratFont)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("list") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Histori List Data", fontFamily = MontserratFont)
        }
    }
}