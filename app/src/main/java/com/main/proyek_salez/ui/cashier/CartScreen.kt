package com.main.proyek_salez.ui.cashier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.main.proyek_salez.data.models.OrderItem
import com.main.proyek_salez.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val orderItems by viewModel.orderItems.collectAsState()
    val currentOrder by viewModel.currentOrder.collectAsState()
    val tableNumber by viewModel.tableNumber.collectAsState()

    var showTableDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang") },
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
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Nomor Meja: $tableNumber",
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(onClick = { showTableDialog = true }) {
                            Text("Ubah")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal: ",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = FormatUtils.formatPrice(currentOrder.totalPrice),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Pajak
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pajak (10%): ",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = FormatUtils.formatPrice(currentOrder.taxAmount),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Total (dengan pajak)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total: ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = FormatUtils.formatPrice(currentOrder.finalPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToCheckout,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = orderItems.isNotEmpty()
                    ) {
                        Text("Lanjut ke Pembayaran")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (orderItems.isEmpty()) {
            EmptyCartMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 160.dp), // Extra padding for bottom bar
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orderItems) { item ->
                    CartItemCard(
                        orderItem = item,
                        onUpdateQuantity = { newQuantity ->
                            viewModel.updateItemQuantity(item, newQuantity)
                        },
                        onRemoveItem = {
                            viewModel.removeItemFromOrder(item)
                        },
                        onUpdateNotes = { notes ->
                            viewModel.updateItemNotes(item, notes)
                        }
                    )
                }
            }
        }
    }

    if (showTableDialog) {
        TableNumberDialog(
            currentTable = tableNumber,
            onDismiss = { showTableDialog = false },
            onConfirm = { newTableNumber ->
                viewModel.setTableNumber(newTableNumber)
                showTableDialog = false
            }
        )
    }
}

@Composable
fun EmptyCartMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Keranjang Kosong",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Silakan tambahkan menu untuk melanjutkan",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CartItemCard(
    orderItem: OrderItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    onUpdateNotes: (String) -> Unit
) {
    var showEditNotesDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = orderItem.menuItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = FormatUtils.formatPrice(orderItem.menuItem.price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (orderItem.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Catatan: ${orderItem.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )

                            TextButton(
                                onClick = { showEditNotesDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Ubah", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        TextButton(
                            onClick = { showEditNotesDialog = true },
                            contentPadding = PaddingValues(horizontal = 0.dp)
                        ) {
                            Text("+ Tambah Catatan", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                IconButton(onClick = onRemoveItem) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(orderItem.quantity - 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        enabled = orderItem.quantity > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Kurangi",
                            tint = if (orderItem.quantity > 1) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }

                    Text(
                        text = orderItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(orderItem.quantity + 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah"
                        )
                    }
                }

                Text(
                    text = FormatUtils.formatPrice(orderItem.subtotal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showEditNotesDialog) {
        EditNotesDialog(
            currentNotes = orderItem.notes,
            onDismiss = { showEditNotesDialog = false },
            onSaveNotes = { notes ->
                onUpdateNotes(notes)
                showEditNotesDialog = false
            }
        )
    }
}

@Composable
fun EditNotesDialog(
    currentNotes: String,
    onDismiss: () -> Unit,
    onSaveNotes: (String) -> Unit
) {
    var notes by remember { mutableStateOf(currentNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catatan Item") },
        text = {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        },
        confirmButton = {
            Button(onClick = { onSaveNotes(notes) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun TableNumberDialog(
    currentTable: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var tableNumber by remember { mutableIntStateOf(currentTable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nomor Meja") },
        text = {
            OutlinedTextField(
                value = tableNumber.toString(),
                onValueChange = {
                    tableNumber = it.toIntOrNull() ?: 1
                },
                label = { Text("Nomor Meja") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(tableNumber) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}