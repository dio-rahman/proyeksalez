package com.main.proyek_salez.ui.cashier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.main.proyek_salez.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cashierId: String,
    onNavigateBack: () -> Unit,
    onPaymentComplete: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val currentOrder by viewModel.currentOrder.collectAsState()
    val orderItems by viewModel.orderItems.collectAsState()
    val tableNumber by viewModel.tableNumber.collectAsState()
    val orderSuccess by viewModel.orderSuccess.collectAsState()

    var amountReceived by remember { mutableStateOf("") }
    var showCompletionDialog by remember { mutableStateOf(false) }

    // Subtotal dan tax diambil dari currentOrder
    val subtotal = currentOrder.totalPrice
    val taxAmount = currentOrder.taxAmount
    val totalAmount = currentOrder.finalPrice

    // Hitung kembalian
    val change = amountReceived.toDoubleOrNull()?.let { it - totalAmount } ?: 0.0

    // Check if payment is sufficient
    val canComplete = amountReceived.toDoubleOrNull()?.let { it >= totalAmount } ?: false

    // Monitor order success state
    LaunchedEffect(orderSuccess) {
        if (orderSuccess == true) {
            showCompletionDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Order Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                            text = "Ringkasan Pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Meja #$tableNumber",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Order items summary
                    orderItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.menuItem.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = FormatUtils.formatPrice(item.subtotal),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = FormatUtils.formatPrice(subtotal),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tax
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pajak (10%)",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = FormatUtils.formatPrice(taxAmount),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Total (with tax)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = FormatUtils.formatPrice(totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pembayaran Tunai",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountReceived,
                    onValueChange = {
                        // Only allow numeric input
                        if (it.isEmpty() || it.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                            amountReceived = it
                        }
                    },
                    label = { Text("Jumlah Diterima") },
                    prefix = { Text("Rp ") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        QuickAmountButton(amount = 50000.0, onAmountSelected = { amountReceived = it.toString() })
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickAmountButton(amount = 100000.0, onAmountSelected = { amountReceived = it.toString() })
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickAmountButton(amount = 200000.0, onAmountSelected = { amountReceived = it.toString() })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Another row of quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val exactAmount = (totalAmount * 100).toInt() / 100.0 // Round to 2 decimal places
                    Box(modifier = Modifier.weight(1f)) {
                        QuickAmountButton(amount = exactAmount, label = "Uang Pas", onAmountSelected = { amountReceived = it.toString() })
                    }

                    val roundedUp = (totalAmount / 1000).toInt() * 1000.0 + 1000.0
                    Box(modifier = Modifier.weight(1f)) {
                        QuickAmountButton(amount = roundedUp, onAmountSelected = { amountReceived = it.toString() })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Change calculation
                if (amountReceived.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kembalian:",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = FormatUtils.formatPrice(change),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (change >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Complete Payment Button
                Button(
                    onClick = {
                        viewModel.finalizeOrder(cashierId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = canComplete
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Selesaikan Pembayaran")
                }
            }
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Pembayaran Berhasil") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pesanan berhasil dibuat!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (change > 0) {
                        Text(
                            text = "Kembalian: ${FormatUtils.formatPrice(change)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetOrderSuccess()
                        onPaymentComplete()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selesai")
                }
            }
        )
    }
}

@Composable
fun QuickAmountButton(
    amount: Double,
    label: String? = null,
    onAmountSelected: (Double) -> Unit
) {
    OutlinedButton(
        onClick = { onAmountSelected(amount) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = label ?: FormatUtils.formatPriceShort(amount),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}