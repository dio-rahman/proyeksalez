package com.main.proyek_salez.ui

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
                LazyColumn {
                    items(orders) { order ->
                        OrderItem(order)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { showDialog = true }) {
                    Text("Tutup Pesanan Harian")
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Konfirmasi") },
                        text = { Text("Tutup semua pesanan hari ini?") },
                        confirmButton = {
                            Button(onClick = {
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
                    Text(
                        text = it,
                        color = if (it.contains("Gagal")) Color.Red else Color.Green
                    )
                }
            }
        }
    }
}



@Composable
fun OrderItem(order: OrderEntity) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${order.orderId}")
            Text("Pelanggan: ${order.customerName}")
            Text("Total: Rp ${order.totalPrice}")
            Text("Status: ${order.status}")
            Text("Tanggal: ${order.orderDate}")
        }
    }
}