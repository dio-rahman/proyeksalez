package com.main.proyek_salez.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.viewmodel.CloseOrderViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CloseOrderScreen(
    viewModel: CloseOrderViewModel = hiltViewModel(),
    navController: NavController
) {
    val orders by viewModel.orders.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val closeStatus by viewModel.closeStatus.collectAsState()
    val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("CloseOrderScreen", "Loading orders for date: $currentDate")
        viewModel.loadDailyOrders(currentDate)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarMenu(
                navController = navController,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ){
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(
                    text = "Pesanan Harian - $currentDate",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total pesanan: ${orders.size}",
                    style = MaterialTheme.typography.bodyLarge
                )

                if (orders.isEmpty()) {
                    Text(
                        text = "Tidak ada pesanan untuk hari ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    val totalRevenue = orders.sumOf { it.totalPrice }
                    Text(
                        text = "Total pendapatan: Rp ${String.format("%,d", totalRevenue)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(orders) { order ->
                        OrderItem(order)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { showDialog = true },
                    enabled = orders.isNotEmpty()
                ) {
                    Text("Tutup Pesanan Harian")
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Konfirmasi") },
                        text = {
                            Text("Tutup ${orders.size} pesanan untuk hari ini?\n\nTotal pendapatan: Rp ${String.format("%,d", orders.sumOf { it.totalPrice })}")
                        },
                        confirmButton = {
                            Button(onClick = {
                                Log.d("CloseOrderScreen", "Closing orders for date: $currentDate")
                                viewModel.closeOrders(currentDate)
                                showDialog = false
                            }) {
                                Text("Ya")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Tidak")
                            }
                        }
                    )
                }

                closeStatus?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = if (it.contains("Gagal")) Color.Red else Color.Green,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: OrderEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pelanggan: ${order.customerName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Rp ${String.format("%,d", order.totalPrice)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pembayaran: ${order.paymentMethod}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Status: ${order.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (order.status == "open") MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Waktu: ${formatTimestamp(order.orderDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Items: ${order.items.sumOf { (it["quantity"] as? Number)?.toInt() ?: 0 }} item",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    return try {
        val date = timestamp.toDate()
        val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        "Invalid time"
    }
}