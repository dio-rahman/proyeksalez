package com.main.proyek_salez.ui.cart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.SidebarMenu
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    val totalPrice by viewModel.totalPrice.collectAsState(initial = "Rp 0")
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))
    var errorMessage by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf(viewModel.customerName.value) }

    LaunchedEffect(customerName) {
        viewModel.customerName.value = customerName
        Log.d("CartScreen", "Customer name updated to: $customerName")
    }

    LaunchedEffect(viewModel.customerName.value) {
        if (customerName != viewModel.customerName.value) {
            customerName = viewModel.customerName.value
            Log.d("CartScreen", "Customer name synced from ViewModel: ${viewModel.customerName.value}")
        }
    }

    LaunchedEffect(viewModel.checkoutRequested.value) {
        if (viewModel.checkoutRequested.value) {
            Log.d("CartScreen", "Navigating to checkout with customer: ${viewModel.customerName.value}")
            navController.navigate("checkout_screen")
            viewModel.checkoutRequested.value = false
        }
    }


    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            title = { Text("Konfirmasi", color = UnguTua) },
            text = { Text("Apakah Anda yakin ingin membatalkan pesanan?", color = AbuAbuGelap) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.clearCart()
                        showConfirmationDialog.value = false
                        navController.navigate("cashier_dashboard")
                    }
                }) { Text("Iya", color = UnguTua) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog.value = false }) { Text("Tidak", color = UnguTua) }
            },
            containerColor = Jingga,
            titleContentColor = UnguTua,
            textContentColor = AbuAbuGelap
        )
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
        Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackground)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
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
                    text = "KERANJANG",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Items in cart: ${cartItems.size}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UnguTua,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Masukkan nama pelanggan disini",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnguTua,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { newValue ->
                        customerName = newValue
                        errorMessage = ""
                        Log.d("CartScreen", "Customer name input changed to: $newValue")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .clip(RoundedCornerShape(50)),
                    placeholder = {
                        Text(
                            text = "Masukkan nama pelanggan disini",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Putih,
                        focusedContainerColor = Putih,
                        focusedBorderColor = UnguTua,
                        unfocusedBorderColor = AbuAbu
                    ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        Log.d("CartScreen", "Checkout button clicked")
                        Log.d("CartScreen", "Customer name: '$customerName'")
                        Log.d("CartScreen", "ViewModel customer name: '${viewModel.customerName.value}'")
                        Log.d("CartScreen", "Cart items: ${cartItems.size}")

                        when {
                            customerName.isBlank() -> {
                                errorMessage = "Nama pelanggan harus diisi"
                                Log.e("CartScreen", "Customer name is blank")
                            }
                            cartItems.isEmpty() -> {
                                errorMessage = "Keranjang kosong"
                                Log.e("CartScreen", "Cart is empty")
                            }
                            else -> {
                                viewModel.customerName.value = customerName
                                viewModel.checkoutRequested.value = true
                                Log.d("CartScreen", "Setting checkout requested to true with customer: ${viewModel.customerName.value}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keranjang kosong, silakan tambahkan menu terlebih dahulu.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = UnguTua,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { cartItemWithFood ->
                            CartItemCard(
                                cartItemWithFood = cartItemWithFood,
                                onIncrement = {
                                    scope.launch {
                                        viewModel.addToCart(cartItemWithFood.foodItem)
                                    }
                                },
                                onDecrement = {
                                    scope.launch {
                                        viewModel.decrementItem(cartItemWithFood.foodItem)
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Putih),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Jumlah Item:",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = AbuAbuGelap)
                                )
                                Text(
                                    text = cartItems.sumOf { item -> item.cartItem.quantity }.toString(),
                                    style = MaterialTheme.typography.bodyLarge.copy(color = AbuAbuGelap)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Harga:",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = UnguTua
                                    )
                                )
                                Text(
                                    text = totalPrice,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = UnguTua
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showConfirmationDialog.value = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Batalkan Pesanan",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Putih,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("cashier_dashboard") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Pilih Jenis Hidangan",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}