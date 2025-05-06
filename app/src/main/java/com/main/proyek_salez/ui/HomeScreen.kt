package com.main.proyek_salez.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.menu.MenuItemCard
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: CashierViewModel = hiltViewModel()
) {
    // Mengambil semua item makanan dari ViewModel
    val foodItems by viewModel.getAllFoodItems().collectAsState(initial = emptyList<FoodItemEntity>())
    var searchQuery by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val gradientBackground = Brush.verticalGradient(colors = listOf(Putih, Jingga, UnguTua))

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
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .clip(RoundedCornerShape(50)),
                    placeholder = {
                        Text(
                            text = "Cari makanan...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = UnguTua
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(
                        items = foodItems.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        },
                        key = { it.id }
                    ) { foodItem ->
                        MenuItemCard(
                            foodItem = foodItem,
                            onAddToCart = {
                                scope.launch {
                                    viewModel.addToCart(foodItem)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}