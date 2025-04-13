package com.main.proyek_salez.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.main.proyek_salez.data.viewmodel.CartItemWithFood
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.ui.SidebarMenu
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel<CartViewModel>()
) {
    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    var totalPrice by remember { mutableStateOf("Rp 0") }
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))

    // Update total price when cartItems change
    LaunchedEffect(cartItems) {
        totalPrice = cartViewModel.getTotalPrice()
    }

    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin ingin membatalkan pesanan?") },
            confirmButton = {
                TextButton(onClick = {
                    cartViewModel.clearCart()
                    showConfirmationDialog.value = false
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
                    text = "KERANJANG",
                    style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold, letterSpacing = 6.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Masukkan nama pelanggan disini",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua, textAlign = TextAlign.Center, fontSize = 12.sp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cartViewModel.customerName.value,
                    onValueChange = { cartViewModel.customerName.value = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).clip(RoundedCornerShape(50)),
                    placeholder = { Text(text = "Masukkan nama pelanggan disini", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp) },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Putih, focusedContainerColor = Putih, focusedBorderColor = UnguTua, unfocusedBorderColor = AbuAbu),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { cartViewModel.createOrder() },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(50),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp)
                ) {
                    Text(
                        text = "Buat Pesanan",
                        style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (cartItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Keranjang kosong, silakan tambahkan menu terlebih dahulu.",
                            style = MaterialTheme.typography.bodyLarge.copy(color = UnguTua, textAlign = TextAlign.Center),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        cartItems.forEach { cartItemWithFood ->
                            CartItemCard(
                                foodItem = cartItemWithFood.foodItem,
                                quantity = cartItemWithFood.cartItem.quantity,
                                onIncrement = { cartViewModel.addToCart(cartItemWithFood.foodItem) },
                                onDecrement = { cartViewModel.decrementItem(cartItemWithFood.foodItem) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Putih),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = UnguTua)
                                )
                                Text(
                                    text = totalPrice,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = UnguTua)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (cartItems.isNotEmpty()) {
                        Button(
                            onClick = { navController.navigate("checkout_screen") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                            shape = RoundedCornerShape(50),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp)
                        ) {
                            Text(
                                text = "Checkout",
                                style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showConfirmationDialog.value = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(50),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp)
                        ) {
                            Text(
                                text = "Batalkan Pesanan",
                                style = MaterialTheme.typography.headlineLarge.copy(color = Putih, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mau balik lagi buat tambahkan hidangan yang lain? Klik disini buat balik lagi!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua, textAlign = TextAlign.Center, fontSize = 8.sp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(50),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp)
                ) {
                    Text(
                        text = "Pilih Jenis Hidangan",
                        style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}