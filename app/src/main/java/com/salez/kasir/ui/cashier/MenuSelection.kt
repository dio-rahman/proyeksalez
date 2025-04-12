package com.salez.kasir.ui.cashier

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.salez.kasir.data.models.MenuItem
import com.salez.kasir.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSelectionScreen(
    onNavigateToCart: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    // Hard-coded data untuk mengatasi masalah
    val dummyCategories = listOf("Makanan", "Minuman", "Camilan", "Dessert")

    val dummyMenuItems = listOf(
        MenuItem(
            itemId = "item1",
            name = "Nasi Goreng",
            description = "Nasi goreng dengan telur, ayam, dan sayuran",
            price = 25000.0,
            category = "Makanan",
            imageUrl = "https://example.com/nasigoreng.jpg",
            isAvailable = true,
            preparationTime = 15
        ),
        MenuItem(
            itemId = "item2",
            name = "Mie Goreng",
            description = "Mie goreng dengan telur, ayam, dan sayuran",
            price = 22000.0,
            category = "Makanan",
            imageUrl = "https://example.com/miegoreng.jpg",
            isAvailable = true,
            preparationTime = 12
        ),
        MenuItem(
            itemId = "item3",
            name = "Es Teh",
            description = "Teh manis dingin",
            price = 7000.0,
            category = "Minuman",
            imageUrl = "https://example.com/esteh.jpg",
            isAvailable = true,
            preparationTime = 3
        ),
        MenuItem(
            itemId = "item4",
            name = "Kopi Hitam",
            description = "Kopi hitam panas",
            price = 10000.0,
            category = "Minuman",
            imageUrl = "https://example.com/kopihitam.jpg",
            isAvailable = true,
            preparationTime = 5
        ),
        MenuItem(
            itemId = "item5",
            name = "Pisang Goreng",
            description = "Pisang goreng dengan tepung crispy",
            price = 15000.0,
            category = "Camilan",
            imageUrl = "https://example.com/pisanggoreng.jpg",
            isAvailable = true,
            preparationTime = 10
        ),
        MenuItem(
            itemId = "item6",
            name = "Es Krim",
            description = "Es krim vanilla dengan topping coklat",
            price = 18000.0,
            category = "Dessert",
            imageUrl = "https://example.com/eskrim.jpg",
            isAvailable = true,
            preparationTime = 2
        )
    )

    // State untuk menu dan kategori
    val orderItems by viewModel.orderItems.collectAsState()
    val isLoading by remember { mutableStateOf(false) } // Nonaktifkan loading

    // State untuk kategori terpilih
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Filter menu berdasarkan kategori
    val filteredMenuItems = if (selectedCategory == null) {
        dummyMenuItems
    } else {
        dummyMenuItems.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Menu") },
                actions = {
                    Box(
                        modifier = Modifier.padding(end = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onNavigateToCart
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Lihat Keranjang",
                                tint = Color.White
                            )
                        }
                        if (orderItems.isNotEmpty()) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = orderItems.sumOf { it.quantity }.toString(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Debug Info (menunjukkan sumber data)
            Text(
                text = "Menggunakan data dummy: ${dummyCategories.size} kategori, ${filteredMenuItems.size} menu",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Categories
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    CategoryChip(
                        category = "Semua",
                        isSelected = selectedCategory == null,
                        onCategorySelected = { selectedCategory = null }
                    )
                }

                items(dummyCategories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onCategorySelected = { selectedCategory = category }
                    )
                }
            }

            // Menu Items
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredMenuItems) { menuItem ->
                    MenuItemCard(
                        menuItem = menuItem,
                        onAddItem = { item, quantity, notes ->
                            viewModel.addItemToOrder(item, quantity, notes)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onCategorySelected: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onCategorySelected() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onAddItem: (MenuItem, Int, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Menu Image
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = menuItem.imageUrl)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = menuItem.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
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
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FormatUtils.formatPrice(menuItem.price),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = { showDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah ke Keranjang"
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddToCartDialog(
            menuItem = menuItem,
            onDismiss = { showDialog = false },
            onAddToCart = { item, quantity, notes ->
                onAddItem(item, quantity, notes)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddToCartDialog(
    menuItem: MenuItem,
    onDismiss: () -> Unit,
    onAddToCart: (MenuItem, Int, String) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Tambahkan ke Keranjang") },
        text = {
            Column {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Jumlah:")

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(text = "-", fontSize = 18.sp)
                        }

                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )

                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "+", fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Subtotal: ${FormatUtils.formatPrice(menuItem.price * quantity)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAddToCart(menuItem, quantity, notes) }) {
                Text("Tambahkan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}