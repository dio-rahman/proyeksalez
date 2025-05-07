package com.main.proyek_salez.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.SidebarMenu
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    cashierViewModel: CashierViewModel = hiltViewModel()
) {
    val orders by cashierViewModel.getAllOrders().collectAsState(initial = emptyList())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))
    var selectedOrder by remember { mutableStateOf<OrderEntity?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarMenu(
                navController = navController,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackground)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }, modifier = Modifier.padding(start = 10.dp)) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = UnguTua)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.salez_logo),
                        contentDescription = "Salez Logo",
                        modifier = Modifier.size(180.dp).offset(x = (-35).dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "RIWAYAT PESANAN",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Belum ada pesanan.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = UnguTua,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    orders.forEach { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Putih),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Pelanggan: ${order.customerName}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = UnguTua,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = "Total: ${order.totalPrice}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = AbuAbuGelap,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "Tanggal: ${order.orderDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = AbuAbuGelap,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "Jumlah Item: ${order.items.sumOf { it.quantity }}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = AbuAbuGelap,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = { selectedOrder = order },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Detail",
                                        tint = UnguTua
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Dialog untuk detail pesanan
    selectedOrder?.let { order ->
        val foodItems by produceState<Map<Int, FoodItemEntity?>>(
            initialValue = emptyMap(),
            key1 = order
        ) {
            val map = mutableMapOf<Int, FoodItemEntity?>()
            order.items.forEach { cartItem ->
                map[cartItem.cartItemId] = cashierViewModel.getFoodItemById(cartItem.foodItemId)
            }
            value = map
        }

        AlertDialog(
            onDismissRequest = { selectedOrder = null },
            title = { Text("Detail Pesanan", color = UnguTua) },
            text = {
                Column {
                    Text(
                        text = "Pelanggan: ${order.customerName}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AbuAbuGelap,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "Total: ${order.totalPrice}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AbuAbuGelap,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "Tanggal: ${order.orderDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AbuAbuGelap,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Item Pesanan:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    order.items.forEach { cartItem ->
                        val foodItem = foodItems[cartItem.cartItemId]
                        if (foodItem != null) {
                            Text(
                                text = "${foodItem.name} x ${cartItem.quantity} (Rp ${foodItem.price * cartItem.quantity})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = AbuAbuGelap,
                                    fontSize = 12.sp
                                )
                            )
                        } else {
                            Text(
                                text = "Item tidak ditemukan x ${cartItem.quantity}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = AbuAbuGelap,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrder = null }) {
                    Text("Tutup", color = UnguTua)
                }
            },
            containerColor = Jingga,
            titleContentColor = UnguTua,
            textContentColor = AbuAbuGelap
        )
    }
}