package com.main.proyek_salez.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.ui.SidebarMenu
import com.main.proyek_salez.ui.cart.CartViewModel
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodMenuScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var menuText by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val showScrollToTopButton by remember { derivedStateOf { scrollState.value > 100 } }
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))
    val foodItems = listOf(
        FoodItem(
            id = 0, name = "Mi Goreng El Salvadore", description = "Olahan dengan nuansa tradisional yang berani tercipta dalam dan nuansa elegan",
            price = "Rp 12.000", rating = "4.5", reviews = "305 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 1, name = "Nasi Goreng Spesial", description = "Nasi goreng dengan campuran bumbu khas dan topping telur",
            price = "Rp 15.000", rating = "4.7", reviews = "420 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 2, name = "Ayam Bakar Madu", description = "Ayam bakar dengan balutan madu manis dan rempah pilihan",
            price = "Rp 20.000", rating = "4.8", reviews = "250 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 3, name = "Soto Ayam Lamongan", description = "Soto ayam khas Lamongan dengan kuah gurih dan suwiran ayam",
            price = "Rp 18.000", rating = "4.6", reviews = "180 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 4, name = "Bakso Beranak", description = "Bakso besar berisi bakso kecil dengan kuah kaldu sapi",
            price = "Rp 25.000", rating = "4.9", reviews = "350 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        )
    )

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
                    text = "MENU MAKANAN",
                    style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold, letterSpacing = 6.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Mau balik lagi bakal pilih hidangan yang lain? Klik disini buat bakik lagi!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua, textAlign = TextAlign.Center, fontSize = 8.sp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cari nama menu disini biar instan! Jangan lupa periksa keranjangnya ya!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua, textAlign = TextAlign.Center, fontSize = 8.sp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(50)),
                        placeholder = { Text(text = "Cari menu yang akan dipesan", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Putih, focusedContainerColor = Putih, focusedBorderColor = UnguTua, unfocusedBorderColor = AbuAbu),
                        shape = RoundedCornerShape(50),
                        trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = UnguTua) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { navController.navigate("cart_screen") },
                        modifier = Modifier.size(40.dp).background(Color(0xFF351C4D), shape = CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Putih)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        menuText = if (searchQuery.isNotEmpty()) "Menu yang dicari: $searchQuery" else "Cari nama menu terkait pesanan"
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                    shape = RoundedCornerShape(50),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp)
                ) {
                    Text(
                        text = "Cari Menu",
                        style = MaterialTheme.typography.headlineLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = menuText,
                    style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua, fontWeight = FontWeight.Medium),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    for (i in foodItems.indices step 2) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) { MenuItemCard(foodItem = foodItems[i], cartViewModel = cartViewModel) }
                            if (i + 1 < foodItems.size) {
                                Box(modifier = Modifier.weight(1f)) { MenuItemCard(foodItem = foodItems[i + 1], cartViewModel = cartViewModel) }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
            AnimatedVisibility(
                visible = showScrollToTopButton,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).navigationBarsPadding()
            ) {
                FloatingActionButton(
                    onClick = { scope.launch { scrollState.animateScrollTo(0) } },
                    modifier = Modifier,
                    containerColor = Oranye,
                    shape = RoundedCornerShape(50),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 15.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Scroll to top", tint = UnguTua, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kembali Ke Atas",
                            style = MaterialTheme.typography.bodyLarge.copy(color = UnguTua, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        )
                    }
                }
            }
        }
    }
}