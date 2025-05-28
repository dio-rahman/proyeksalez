package com.main.proyek_salez.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.viewmodel.CloseOrderViewModel
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
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
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Putih, Jingga, UnguTua)
    )

    LaunchedEffect(Unit) {
        Log.d("CloseOrderScreen", "Loading orders for date: $currentDate")
        viewModel.loadDailyOrders(currentDate)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarMenu(
                navController = navController,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = UnguTua
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.salez_logo),
                    contentDescription = "Salez Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .offset(x = (-35).dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(140.dp))
                Text(
                    text = "Pesanan Harian - $currentDate",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total pesanan: ${orders.size}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnguTua
                    )
                )

                if (orders.isEmpty()) {
                    Text(
                        text = "Tidak ada pesanan untuk hari ini",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AbuAbuGelap
                        ),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    val totalRevenue = orders.sumOf { it.totalPrice }
                    Text(
                        text = "Total pendapatan: Rp ${String.format("%,d", totalRevenue)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Oranye,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(orders) { order ->
                        OrderItem(order)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDialog = true },
                    enabled = orders.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Tutup Pesanan Harian",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Konfirmasi") },
                        text = {
                            Text("Tutup ${orders.size} pesanan untuk hari ini?\n\nTotal pendapatan: Rp ${String.format("%,d", orders.sumOf { it.totalPrice })}")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    Log.d("CloseOrderScreen", "Closing orders for date: $currentDate")
                                    viewModel.closeOrders(currentDate)
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Oranye)
                            ) {
                                Text("Ya")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = AbuAbu)
                            ) {
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
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Putih),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pelanggan: ${order.customerName}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnguTua
                    )
                )
                Text(
                    text = "Rp ${String.format("%,d", order.totalPrice)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pembayaran: ${order.paymentMethod}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AbuAbuGelap
                )
            )

            Text(
                text = "Status: ${order.status}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (order.status == "open") Oranye else AbuAbuGelap
                )
            )

            Text(
                text = "Waktu: ${formatTimestamp(order.orderDate)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AbuAbuGelap
                )
            )

            Text(
                text = "Items: ${order.items.sumOf { (it["quantity"] as? Number)?.toInt() ?: 0 }} item",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AbuAbuGelap
                )
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