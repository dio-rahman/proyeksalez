package com.salez.kasir.ui.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.salez.kasir.data.models.MenuItem
import com.salez.kasir.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    viewModel: MenuManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMenuItem by remember { mutableStateOf<MenuItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Menu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Menu",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuItems) { menuItem ->
                    MenuItemCard(
                        menuItem = menuItem,
                        onEditClick = {
                            selectedMenuItem = menuItem
                            showAddDialog = true
                        },
                        onDeleteClick = { viewModel.deleteMenuItem(menuItem.itemId) },
                        onToggleAvailability = {
                            viewModel.toggleMenuItemAvailability(
                                menuItem.itemId,
                                !menuItem.isAvailable
                            )
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        MenuItemDialog(
            menuItem = selectedMenuItem,
            categories = categories,
            onDismiss = {
                showAddDialog = false
                selectedMenuItem = null
            },
            onSave = { menuItem, _ ->
                if (selectedMenuItem == null) {
                    viewModel.addMenuItem(menuItem, null)
                } else {
                    viewModel.updateMenuItem(menuItem, null)
                }
                showAddDialog = false
                selectedMenuItem = null
            }
        )
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleAvailability: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Menu Image (placeholder)
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(menuItem.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = menuItem.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Availability Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (menuItem.isAvailable) Color(0xFF4CAF50)
                            else Color(0xFFE57373)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (menuItem.isAvailable) "Tersedia" else "Tidak Tersedia",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Menu Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = menuItem.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = FormatUtils.formatPrice(menuItem.price),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onToggleAvailability) {
                        Icon(
                            imageVector = if (menuItem.isAvailable) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (menuItem.isAvailable) "Set Tidak Tersedia" else "Set Tersedia",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDialog(
    menuItem: MenuItem?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (MenuItem, Any?) -> Unit
) {
    val isEditMode = menuItem != null
    var name by remember { mutableStateOf(menuItem?.name ?: "") }
    var description by remember { mutableStateOf(menuItem?.description ?: "") }
    var price by remember { mutableStateOf((menuItem?.price ?: 0.0).toString()) }
    var category by remember { mutableStateOf(menuItem?.category ?: categories.firstOrNull() ?: "") }
    var preparationTime by remember { mutableStateOf((menuItem?.preparationTime ?: 0).toString()) }
    var isAvailable by remember { mutableStateOf(menuItem?.isAvailable ?: true) }

    // URI for image would be handled here in a real implementation
    var imageUri by remember { mutableStateOf<Any?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (isEditMode) "Edit Menu" else "Tambah Menu Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Menu") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                            price = it
                        }
                    },
                    label = { Text("Harga") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = false, // Simplified for this example
                    onExpandedChange = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    // In a real implementation, we would show the dropdown menu here
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = preparationTime,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            preparationTime = it
                        }
                    },
                    label = { Text("Waktu Preparasi (menit)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it }
                    )

                    Text(
                        text = "Tersedia",
                        modifier = Modifier.clickable { isAvailable = !isAvailable }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Image upload button would go here in a real implementation
                    OutlinedButton(onClick = { /* Handle image selection */ }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Upload Gambar"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload Gambar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val newMenuItem = MenuItem(
                                itemId = menuItem?.itemId ?: "",
                                name = name,
                                description = description,
                                price = price.toDoubleOrNull() ?: 0.0,
                                category = category,
                                imageUrl = menuItem?.imageUrl ?: "",
                                isAvailable = isAvailable,
                                preparationTime = preparationTime.toIntOrNull() ?: 0
                            )
                            onSave(newMenuItem, imageUri)
                        },
                        enabled = name.isNotBlank() && price.isNotBlank() && category.isNotBlank()
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}