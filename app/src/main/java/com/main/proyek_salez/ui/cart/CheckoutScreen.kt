package com.main.proyek_salez.ui.cart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.sidebar.SidebarMenu
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    val totalPrice by viewModel.totalPrice.collectAsState(initial = "Rp 0")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))
    var paymentMethod by remember { mutableStateOf("Tunai") }
    var expanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf("Tunai", "Kartu", "QRIS")
    var errorMessage by remember { mutableStateOf("") }
    val customerName by viewModel.customerName.collectAsState()
    val showConfirmationDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("CheckoutScreen", "CheckoutScreen loaded")
        Log.d("CheckoutScreen", "Customer name from ViewModel: '${viewModel.customerName.value}'")
        Log.d("CheckoutScreen", "Cart items: ${cartItems.size}")
    }

    LaunchedEffect(customerName) {
        Log.d("CheckoutScreen", "Customer name changed to: '${viewModel.customerName.value}'")
    }

    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            title = { Text("Konfirmasi", color = UnguTua) },
            text = { Text("Apakah Anda yakin ingin membatalkan pembayaran?", color = AbuAbuGelap) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmationDialog.value = false
                    navController.navigate("cart_screen") {
                        popUpTo("checkout_screen") { inclusive = true }
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    text = "PEMBAYARAN",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
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
                        Text(
                            text = "Ringkasan Pesanan",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = UnguTua,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Pelanggan: $customerName",
                            style = MaterialTheme.typography.bodyMedium.copy(color = AbuAbuGelap)
                        )

                        if (customerName.isBlank()) {
                            Text(
                                text = "⚠️ NAMA PELANGGAN KOSONG!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Merah,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (cartItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Keranjang kosong",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = UnguTua,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(cartItems) { cartItemWithFood ->
                                    CartItemCard(
                                        cartItemWithFood = cartItemWithFood,
                                        onIncrement = {
                                            Log.d("CheckoutScreen", "=== INCREMENT CLICKED ===")
                                            Log.d("CheckoutScreen", "Item: ${cartItemWithFood.foodItem.name}")
                                            Log.d("CheckoutScreen", "Current quantity: ${cartItemWithFood.cartItem.quantity}")
                                            Log.d("CheckoutScreen", "Food ID: ${cartItemWithFood.foodItem.id}")

                                            scope.launch {
                                                try {
                                                    viewModel.addToCart(cartItemWithFood.foodItem)
                                                    Log.d("CheckoutScreen", "Successfully called addToCart for ${cartItemWithFood.foodItem.name}")
                                                } catch (e: Exception) {
                                                    Log.e("CheckoutScreen", "Error incrementing item: ${e.message}")
                                                    errorMessage = "Gagal menambah ${cartItemWithFood.foodItem.name}: ${e.message}"
                                                }
                                            }
                                        },
                                        onDecrement = {
                                            Log.d("CheckoutScreen", "=== DECREMENT CLICKED ===")
                                            Log.d("CheckoutScreen", "Item: ${cartItemWithFood.foodItem.name}")
                                            Log.d("CheckoutScreen", "Current quantity: ${cartItemWithFood.cartItem.quantity}")
                                            Log.d("CheckoutScreen", "Food ID: ${cartItemWithFood.foodItem.id}")

                                            scope.launch {
                                                try {
                                                    viewModel.decrementItem(cartItemWithFood.foodItem)
                                                    Log.d("CheckoutScreen", "Successfully called decrementItem for ${cartItemWithFood.foodItem.name}")
                                                } catch (e: Exception) {
                                                    Log.e("CheckoutScreen", "Error decrementing item: ${e.message}")
                                                    errorMessage = "Gagal mengurangi ${cartItemWithFood.foodItem.name}: ${e.message}"
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
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
                                text = cartItems.sumOf { it.cartItem.quantity }.toString(),
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
                Text(
                    text = "Pilih Metode Pembayaran",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Putih),
                    contentAlignment = Alignment.Center
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.width(250.dp)
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = UnguTua
                                )
                            },
                            modifier = Modifier
                                .width(265.dp)
                                .height(50.dp)
                                .menuAnchor(MenuAnchorType.PrimaryEditable),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Putih,
                                focusedContainerColor = Putih,
                                focusedBorderColor = UnguTua,
                                unfocusedBorderColor = AbuAbu
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                color = UnguTua,
                                fontSize = 16.sp
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(Putih)
                                .width(250.dp)
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = method,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = UnguTua,
                                                fontSize = 16.sp
                                            )
                                        )
                                    },
                                    onClick = {
                                        paymentMethod = method
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
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
                        Log.d("CheckoutScreen", "=== CHECKOUT BUTTON CLICKED ===")
                        Log.d("CheckoutScreen", "Payment method: $paymentMethod")
                        Log.d("CheckoutScreen", "Customer name: '${viewModel.customerName.value}'")
                        Log.d("CheckoutScreen", "Customer name length: ${viewModel.customerName.value.length}")
                        Log.d("CheckoutScreen", "Cart items: ${cartItems.size}")

                        when {
                            paymentMethod.isEmpty() -> {
                                errorMessage = "Pilih metode pembayaran"
                                Log.e("CheckoutScreen", "Payment method is empty")
                            }
                            viewModel.customerName.value.isBlank() -> {
                                errorMessage = "Nama pelanggan tidak boleh kosong"
                                Log.e("CheckoutScreen", "Customer name is blank: '${viewModel.customerName.value}'")
                            }
                            cartItems.isEmpty() -> {
                                errorMessage = "Keranjang kosong"
                                Log.e("CheckoutScreen", "Cart is empty")
                            }
                            else -> {
                                Log.d("CheckoutScreen", "Creating order...")
                                scope.launch {
                                    try {
                                        viewModel.createOrder(paymentMethod)
                                        Log.d("CheckoutScreen", "Order created successfully")
                                        navController.navigate("completion_screen") {
                                            popUpTo("checkout_screen") { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("CheckoutScreen", "Error creating order: ${e.message}")
                                        errorMessage = "Gagal membuat pesanan: ${e.message}"
                                    }
                                }
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
                        text = "SELESAIKAN",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showConfirmationDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Merah),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "BATALKAN",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Putih,
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