//package com.salez.kasir.ui.chef
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.salez.kasir.data.models.Order
//import com.salez.kasir.data.models.OrderStatus
//import com.salez.kasir.ui.cashier.OrderViewModel
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChefHomeScreen(
//    onLogout: () -> Unit,
//    viewModel: OrderViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    var selectedTab by remember { mutableStateOf(0) }
//
//    LaunchedEffect(Unit) {
//        viewModel.getPendingOrders()
//        viewModel.getProcessingOrders()
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Dapur - Panel Koki") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                ),
//                actions = {
//                    IconButton(onClick = onLogout) {
//                        Icon(
//                            imageVector = Icons.Default.Logout,
//                            contentDescription = "Logout"
//                        )
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//        ) {
//            TabRow(selectedTabIndex = selectedTab) {
//                Tab(
//                    selected = selectedTab == 0,
//                    onClick = { selectedTab = 0 },
//                    text = {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(Icons.Default.Assignment, contentDescription = null)
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Text("Menunggu (${uiState.pendingOrders.size})")
//                        }
//                    }
//                )
//                Tab(
//                    selected = selectedTab == 1,
//                    onClick = { selectedTab = 1 },
//                    text = {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(Icons.Default.Restaurant, contentDescription = null)
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Text("Diproses (${uiState.processingOrders.size})")
//                        }
//                    }
//                )
//            }
//
//            when (selectedTab) {
//                0 -> OrdersList(
//                    orders = uiState.pendingOrders,
//                    onStatusChanged = { orderId, status ->
//                        viewModel.updateOrderStatus(orderId, status)
//                    },
//                    emptyMessage = "Tidak ada pesanan yang menunggu",
//                    actionText = "Proses",
//                    nextStatus = OrderStatus.PROCESSING
//                )
//                1 -> OrdersList(
//                    orders = uiState.processingOrders,
//                    onStatusChanged = { orderId, status ->
//                        viewModel.updateOrderStatus(orderId, status)
//                    },
//                    emptyMessage = "Tidak ada pesanan yang sedang diproses",
//                    actionText = "Selesai",
//                    nextStatus = OrderStatus.COMPLETED
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun OrdersList(
//    orders: List<Order>,
//    onStatusChanged: (String, OrderStatus) -> Unit,
//    emptyMessage: String,
//    actionText: String,
//    nextStatus: OrderStatus
//) {
//    if (orders.isEmpty()) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = emptyMessage,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    } else {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            items(orders) { order ->
//                OrderCard(
//                    order = order,
//                    onStatusChanged = { onStatusChanged(order.orderId, nextStatus) },
//                    actionText = actionText
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun OrderCard(
//    order: Order,
//    onStatusChanged: () -> Unit,
//    actionText: String
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // Order items
//            Spacer(modifier = Modifier.height(8.dp))
//
//            order.items.forEach { item ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Text(
//                            text = "${item.quantity}x",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.width(40.dp)
//                        )
//
//                        Column {
//                            Text(
//                                text = item.menuItem.name,
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//
//                            if (item.notes.isNotBlank()) {
//                                Text(
//                                    text = "Catatan: ${item.notes}",
//                                    style = MaterialTheme.typography.bodySmall,
//                                    color = MaterialTheme.colorScheme.outline
//                                )
//                            }
//                        }
//                    }
//
//                    if (item.menuItem.preparationTime > 0) {
//                        Surface(
//                            color = MaterialTheme.colorScheme.secondaryContainer,
//                            shape = MaterialTheme.shapes.small
//                        ) {
//                            Text(
//                                text = "${item.menuItem.preparationTime} menit",
//                                style = MaterialTheme.typography.bodySmall,
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
//                            )
//                        }
//                    }
//                }
//            }
//
//            Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//            // Action buttons
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End
//            ) {
//                Button(
//                    onClick = onStatusChanged,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = when (actionText) {
//                            "Proses" -> MaterialTheme.colorScheme.primary
//                            "Selesai" -> MaterialTheme.colorScheme.tertiary
//                            else -> MaterialTheme.colorScheme.primary
//                        }
//                    )
//                ) {
//                    when (actionText) {
//                        "Proses" -> Icon(
//                            Icons.Default.PlayArrow,
//                            contentDescription = null,
//                            modifier = Modifier.size(18.dp)
//                        )
//                        "Selesai" -> Icon(
//                            Icons.Default.Check,
//                            contentDescription = null,
//                            modifier = Modifier.size(18.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(actionText)
//                }
//            }
//        }
//    }
//}
//
//// Helper function untuk format timestamp
//fun formatTimestamp(timestamp: Long): String {
//    val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
//    return sdf.format(Date(timestamp))
//}
