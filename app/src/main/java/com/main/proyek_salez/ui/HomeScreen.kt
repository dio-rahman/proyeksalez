package com.main.proyek_salez.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.menu.MenuItemCard
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel<CartViewModel>(),
    cashierViewModel: CashierViewModel = hiltViewModel<CashierViewModel>()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var menuInput by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<FoodItemEntity?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Putih,
            Jingga,
            UnguTua
        )
    )

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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
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
                    text = "SELAMAT DATANG,",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Oranye,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "DIO!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "ADA YANG BISA DIBANTU?",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Oranye,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(35.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = menuInput,
                        onValueChange = { menuInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = {
                            Text(
                                text = "Cari nama menu terkait pesanan",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Putih,
                            focusedContainerColor = Putih,
                            focusedBorderColor = UnguTua,
                            unfocusedBorderColor = AbuAbu
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    IconButton(
                        onClick = { navController.navigate("cart_screen") },
                        modifier = Modifier
                            .size(40.dp)
                            .background(UnguTua, shape = RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Putih
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            searchResult = null
                            errorMessage = ""
                            cartViewModel.searchFoodItems(menuInput).collectLatest { items ->
                                val foundItem = items.firstOrNull()
                                if (foundItem != null) {
                                    searchResult = foundItem
                                } else {
                                    errorMessage = "Menu '${menuInput}' tidak tersedia."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Oranye
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Cari Menu",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                searchResult?.let { foodItem ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        MenuItemCard(
                            foodItem = foodItem,
                            onAddToCart = { item ->
                                cartViewModel.addToCart(item)
                            }
                        )
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                CategoryButton(
                    text = "Makanan",
                    onClick = { navController.navigate("food_menu") },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                CategoryButton(
                    text = "Minuman",
                    onClick = { navController.navigate("drink_menu") },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                CategoryButton(
                    text = "Lainnya",
                    onClick = { navController.navigate("other_menu") },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun CategoryButton(
    text: String,
    onClick: () -> Unit,
    style: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        color = UnguTua,
        fontWeight = FontWeight.Bold
    )
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(48.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Oranye
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            style = style
        )
    }
}