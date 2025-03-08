package com.opendata.jabar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Main Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Teks di tengah layar
            Text(
                text = "Mana yang akan Anda pilih?",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol pertama: Menuju DataEntryScreen
            Button(
                onClick = { navController.navigate("DataEntryScreen") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Input Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol kedua: Menuju DataEntryScreen 2
            Button(
                onClick = { navController.navigate("DataEntryScreenExcel") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Data")
            }

            // Tombol ketiga: Menuju DataEntryScreen 3
            Button(
                onClick = { navController.navigate("DataEntryScreenPdf") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Data")
            }
        }
    }
}