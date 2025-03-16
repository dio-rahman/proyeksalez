package com.salez.kasir.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.salez.kasir.R
import com.salez.kasir.viewmodel.DataViewModel
import com.salez.kasir.data.DataEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasukanMenu(navController: NavHostController, viewModel: DataViewModel) {
    val context = LocalContext.current
    var nama_menu by remember { mutableStateOf("") }
    var biaya_menu by remember { mutableStateOf("") }
    var jenis_pembayaran_menu by remember { mutableStateOf("") }
    val MontserratFont = FontFamily(
        Font(com.salez.kasir.R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold)
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Data", fontFamily = MontserratFont) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nama_menu,
                onValueChange = { nama_menu = it },
                label = { Text("Nama Menu", fontFamily = MontserratFont) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = biaya_menu,
                onValueChange = { biaya_menu = it },
                label = { Text("Biaya Menu", fontFamily = MontserratFont) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = jenis_pembayaran_menu,
                onValueChange = { jenis_pembayaran_menu = it },
                label = { Text("Jenis Pembayaran Menu", fontFamily = MontserratFont) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val data = DataEntity(
                        nama_menu = nama_menu,
                        biaya_menu = biaya_menu.toIntOrNull() ?: 0,
                        jenis_pembayaran_menu = jenis_pembayaran_menu,
                    )

                    viewModel.insertData(data)
                    Toast.makeText(context, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    navController.navigate("list")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Data", fontFamily = MontserratFont)
            }
        }
    }
}