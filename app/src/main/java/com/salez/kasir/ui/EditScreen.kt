package com.salez.kasir.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import com.salez.kasir.data.DataEntity
import com.salez.kasir.viewmodel.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavHostController,
    viewModel: DataViewModel,
    dataId: Int
) {
    val context = LocalContext.current
    val dataState by viewModel.getDataById(dataId).observeAsState()

    var nama_menu by remember { mutableStateOf("") }
    var jenis_pembayaran_menu by remember { mutableStateOf("") }
    var biaya_menu by remember { mutableStateOf("") }

    LaunchedEffect(dataState) {
        dataState?.let { data ->
            nama_menu = data.nama_menu
            biaya_menu = data.biaya_menu.toString()
            jenis_pembayaran_menu = data.jenis_pembayaran_menu
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Data") },
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
                label = { Text("Nama Menu") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = biaya_menu,
                onValueChange = { biaya_menu = it },
                label = { Text("Biaya Menu") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = jenis_pembayaran_menu,
                onValueChange = { jenis_pembayaran_menu = it },
                label = { Text("Jenis Pembayaran Menu") },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    dataState?.let {
                        val updatedData = DataEntity(
                            id = dataId,
                            nama_menu = nama_menu,
                            biaya_menu = biaya_menu.toIntOrNull() ?: 0,
                            jenis_pembayaran_menu = jenis_pembayaran_menu
                        )
                        viewModel.updateData(updatedData)
                        Toast.makeText(context, "Menu berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Update Data")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    dataState?.let {
                        viewModel.deleteData(it)
                        Toast.makeText(context, "Menu berhasil dihapus!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete Menu")
            }
        }
    }
}