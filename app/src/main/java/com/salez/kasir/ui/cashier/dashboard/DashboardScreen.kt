package com.salez.kasir.ui.cashier.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salez.kasir.data.models.OrderStatus
import com.salez.kasir.ui.cashier.OrderViewModel
import com.salez.kasir.ui.theme.AppGradientBackground


/**
 * Layar Dashboard utama aplikasi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    cashierId: String,
    onStartNewOrder: () -> Unit,
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        DashboardTab.Today,
        DashboardTab.Pending,
        DashboardTab.Completed
    )

    val todayOrders by orderViewModel.todayOrders.collectAsState(initial = emptyList())
    val pendingOrders by orderViewModel.pendingOrders.collectAsState(initial = emptyList())
    val completedOrders by orderViewModel.completedOrders.collectAsState(initial = emptyList())

    // Load orders
    LaunchedEffect(Unit) {
        orderViewModel.loadTodayOrders()
        orderViewModel.loadOrdersByStatus(OrderStatus.PENDING)
        orderViewModel.loadOrdersByStatus(OrderStatus.COMPLETED)
    }
    AppGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kasir App") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    actions = {
                        // Menu icon
                        IconButton(onClick = { /* Toggle drawer or menu */ }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onStartNewOrder,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Buat Pesanan Baru",
                        tint = Color.White
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Dashboard Cards - Summary info
                DashboardSummary(
                    totalSales = todayOrders.sumOf { it.finalPrice },
                    totalOrders = todayOrders.size,
                    pendingOrders = pendingOrders.size
                )

                // Tab row for order categories
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(tab.title) }
                        )
                    }
                }

                // Content based on selected tab
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTabIndex) {
                        0 -> OrderList(
                            orders = todayOrders,
                            emptyMessage = "Belum ada pesanan hari ini",
                            onOrderClick = { /* Handle order click */ }
                        )

                        1 -> OrderList(
                            orders = pendingOrders,
                            emptyMessage = "Tidak ada pesanan tertunda",
                            onOrderClick = { /* Handle order click */ }
                        )

                        2 -> OrderList(
                            orders = completedOrders,
                            emptyMessage = "Belum ada pesanan selesai",
                            onOrderClick = { /* Handle order click */ }
                        )
                    }
                }
            }
        }
    }
}