package com.main.proyek_salez.ui.manager

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.main.proyek_salez.data.model.FoodItemEntity
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerScreen(viewModel: ManagerViewModel = hiltViewModel()) {
    var categoryName by remember { mutableStateOf("") }
    var categoryDesc by remember { mutableStateOf("") }
    var foodId by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var foodDesc by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var editingFoodItem by remember { mutableStateOf<FoodItemEntity?>(null) }
    var showDeleteCategoryDialog by remember { mutableStateOf<Long?>(null) }
    var showDeleteFoodItemDialog by remember { mutableStateOf<Long?>(null) }

    val categories by viewModel.categories.collectAsState()
    val foodItems by viewModel.foodItems.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    // Delete Category Confirmation Dialog
    showDeleteCategoryDialog?.let { categoryId ->
        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = null },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah anda yakin untuk menghapus kategori ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(categoryId)
                        showDeleteCategoryDialog = null
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCategoryDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete Food Item Confirmation Dialog
    showDeleteFoodItemDialog?.let { foodItemId ->
        AlertDialog(
            onDismissRequest = { showDeleteFoodItemDialog = null },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah anda yakin untuk menghapus menu ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFoodItem(foodItemId)
                        showDeleteFoodItemDialog = null
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteFoodItemDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manajer Dashboard", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Form Tambah Kategori
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Tambah Kategori",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Nama Kategori") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.clearErrorMessage()
                            if (categoryName.isBlank()) {
                                viewModel.setErrorMessage("Nama kategori tidak boleh kosong")
                            } else {
                                viewModel.addCategory(categoryName)
                                if (viewModel.errorMessage.value == null) {
                                    categoryName = ""
                                    categoryDesc = ""
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Tambah Kategori")
                    }
                }
            }

            // Form Tambah/Edit Menu
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (editingFoodItem == null) "Tambah Menu" else "Edit Menu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = foodId,
                        onValueChange = { foodId = it.filter { it.isDigit() } },
                        label = { Text("ID Menu") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = editingFoodItem == null // ID tidak bisa diubah saat edit
                    )
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Nama Menu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = foodDesc,
                        onValueChange = { foodDesc = it },
                        label = { Text("Deskripsi Menu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = foodPrice,
                        onValueChange = { foodPrice = it.filter { it.isDigit() || it == '.' } },
                        label = { Text("Harga Menu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedImageUri == null) "Pilih Gambar (Opsional)" else "Gambar Dipilih")
                    }
                    Box {
                        OutlinedTextField(
                            value = categories.find { it.id == selectedCategoryId }?.name ?: "Pilih Kategori",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCategoryDropdownExpanded = true },
                            trailingIcon = {
                                IconButton(onClick = { isCategoryDropdownExpanded = true }) {
                                    Icon(
                                        imageVector = if (isCategoryDropdownExpanded)
                                            Icons.Default.ArrowDropUp
                                        else
                                            Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (editingFoodItem != null) {
                            TextButton(
                                onClick = {
                                    foodId = ""
                                    foodName = ""
                                    foodDesc = ""
                                    foodPrice = ""
                                    selectedImageUri = null
                                    selectedCategoryId = null
                                    editingFoodItem = null
                                    viewModel.clearErrorMessage()
                                }
                            ) {
                                Text("Batal")
                            }
                        }
                        Button(
                            onClick = {
                                viewModel.clearErrorMessage()
                                when {
                                    foodId.isBlank() -> {
                                        viewModel.setErrorMessage("ID menu tidak boleh kosong")
                                    }
                                    foodName.isBlank() -> {
                                        viewModel.setErrorMessage("Nama menu tidak boleh kosong")
                                    }
                                    foodPrice.isBlank() -> {
                                        viewModel.setErrorMessage("Harga menu tidak boleh kosong")
                                    }
                                    selectedCategoryId == null -> {
                                        viewModel.setErrorMessage("Pilih kategori terlebih dahulu")
                                    }
                                    else -> {
                                        val imagePath = selectedImageUri?.let { uri ->
                                            saveImageToInternalStorage(context, Uri.parse(uri))
                                        }
                                        if (editingFoodItem == null) {
                                            viewModel.addFoodItem(
                                                id = foodId.toLongOrNull() ?: 0,
                                                name = foodName,
                                                description = foodDesc,
                                                price = foodPrice.toDoubleOrNull() ?: 0.0,
                                                imagePath = imagePath,
                                                categoryId = selectedCategoryId!!
                                            )
                                        } else {
                                            viewModel.updateFoodItem(
                                                id = editingFoodItem!!.id,
                                                name = foodName,
                                                description = foodDesc,
                                                price = foodPrice.toDoubleOrNull() ?: 0.0,
                                                imagePath = imagePath,
                                                categoryId = selectedCategoryId!!
                                            )
                                        }
                                        if (viewModel.errorMessage.value == null) {
                                            foodId = ""
                                            foodName = ""
                                            foodDesc = ""
                                            foodPrice = ""
                                            selectedImageUri = null
                                            selectedCategoryId = null
                                            editingFoodItem = null
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(if (editingFoodItem == null) "Tambah Menu" else "Simpan Perubahan")
                        }
                    }
                }
            }

            // Pesan Error
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Daftar Kategori
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Daftar Kategori",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 100.dp)
                    ) {
                        items(categories) { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    category.name,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { showDeleteCategoryDialog = category.id }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus Kategori",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Daftar Menu
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Daftar Menu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(foodItems) { foodItem ->
                            FoodItemCard(
                                foodItem = foodItem,
                                categoryName = categories.find { it.id == foodItem.categoryId }?.name ?: "Unknown",
                                onEdit = {
                                    editingFoodItem = foodItem
                                    foodId = foodItem.id.toString()
                                    foodName = foodItem.name
                                    foodDesc = foodItem.description
                                    foodPrice = foodItem.price.toString()
                                    selectedImageUri = foodItem.imagePath
                                    selectedCategoryId = foodItem.categoryId
                                    viewModel.clearErrorMessage()
                                },
                                onDelete = { showDeleteFoodItemDialog = foodItem.id }
                            )
                        }
                    }
                }
            }

            // Spacer untuk memastikan konten terakhir tidak terpotong
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItemEntity,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    foodItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Kategori: $categoryName",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Harga: Rp ${foodItem.price}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Menu",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.filesDir, "menu_${System.currentTimeMillis()}.jpg")
    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}