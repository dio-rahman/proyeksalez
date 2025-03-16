package com.salez.kasir.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.salez.kasir.viewmodel.DataViewModel
import com.salez.kasir.data.DataEntity
import com.salez.kasir.utils.extractDataFromExcel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasukanMenuAutoExcel(navController: NavHostController, viewModel: DataViewModel) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var extractedData by remember { mutableStateOf<List<DataEntity>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var importSuccess by remember { mutableStateOf(false) }

    // Create file picker launcher
    val excelFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isLoading = true
            errorMessage = null
            importSuccess = false

            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "temp_excel.xlsx")

                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val dataEntities = extractDataFromExcel(tempFile)
                extractedData = dataEntities

                if (dataEntities.isEmpty()) {
                    errorMessage = "No valid data found in the Excel file."
                }

            } catch (e: Exception) {
                errorMessage = "Error importing Excel: ${e.message}"
                extractedData = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Excel Data Import") },
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
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Upload Excel file with the following columns:",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Column Format:")
                    Text("A: Kode Provinsi")
                    Text("B: Nama Provinsi")
                    Text("C: Kode Kabupaten/Kota")
                    Text("D: Nama Kabupaten/Kota")
                    Text("E: Rata-rata Lama Sekolah")
                    Text("F: Satuan")
                    Text("G: biaya_menu")
                }
            }

            Button(
                onClick = {
                    excelFileLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Excel File")
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            } else if (importSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Data successfully imported!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else if (extractedData.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Extracted ${extractedData.size} records from Excel:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            items(extractedData.take(10)) { data ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Text("${data.nama_menu} - ${data.jenis_pembayaran_menu}")
                                        Text("Menu Makanan: ${data.nama_menu} ${data.jenis_pembayaran_menu}, biaya_menu: ${data.biaya_menu}")
                                    }
                                }
                            }

                            if (extractedData.size > 10) {
                                item {
                                    Text(
                                        text = "... and ${extractedData.size - 10} more records",
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.importDataFromExcel(extractedData)
                                importSuccess = true
                                Toast.makeText(context, "${extractedData.size} data records have been imported!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Import All Data")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("list") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Data List")
            }
        }
    }
}