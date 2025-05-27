package com.main.proyek_salez.ui.menu

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.SidebarMenu
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodMenuScreen(
    navController: NavController,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))

    // State untuk food items
    var foodItems by remember { mutableStateOf<List<FoodItemEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function untuk load food items
    fun loadFoodItems() {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null
                Log.d("FoodMenuScreen", "Starting to load food items for 'Makanan'")

                viewModel.getFoodItemsByCategory("Makanan").collect { items ->
                    Log.d("FoodMenuScreen", "Received ${items.size} food items")
                    foodItems = items
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("FoodMenuScreen", "Error loading food items: ${e.message}")
                errorMessage = "Error: ${e.message}"
                isLoading = false
            }
        }
    }

    // Load data saat pertama kali
    LaunchedEffect(Unit) {
        loadFoodItems()
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
                    .padding(16.dp)
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
                        contentDescription = "Logo Salez",
                        modifier = Modifier.size(180.dp).offset(x = (-35).dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "MENU MAKANAN",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Debug buttons (Remove in production)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { loadFoodItems() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reload", color = Putih)
                    }
                    Button(
                        onClick = { viewModel.debugFirestoreData() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Debug", color = Putih)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = UnguTua)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Memuat menu makanan...",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = UnguTua)
                                )
                            }
                        }
                    }

                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = errorMessage!!,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color.Red,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(16.dp)
                                )
                                Button(
                                    onClick = { loadFoodItems() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Oranye)
                                ) {
                                    Text("Coba Lagi", color = UnguTua)
                                }
                            }
                        }
                    }

                    foodItems.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Belum ada menu makanan.\nSilakan tambahkan dari halaman Manager.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = UnguTua,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(16.dp)
                                )
                                Button(
                                    onClick = { loadFoodItems() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Oranye)
                                ) {
                                    Text("Refresh", color = UnguTua)
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = "${foodItems.size} menu tersedia",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnguTua,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            foodItems.forEach { foodItem ->
                                MenuItemCard(
                                    foodItem = foodItem,
                                    onAddToCart = {
                                        scope.launch {
                                            viewModel.addToCart(foodItem)
                                            Log.d("FoodMenuScreen", "Added ${foodItem.name} to cart")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}