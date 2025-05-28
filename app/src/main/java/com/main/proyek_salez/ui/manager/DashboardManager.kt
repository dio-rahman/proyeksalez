package com.main.proyek_salez.ui.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.ManagerViewModel
import com.main.proyek_salez.ui.sidebar.SidebarManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardManager(
    navController: NavController,
    viewModel: ManagerViewModel = hiltViewModel()
) {
    val summary by viewModel.summary.collectAsState()
    val error by viewModel.error.collectAsState()
    val popularFoodItems by viewModel.popularFoodItems.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarManager(
                navController = navController,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SALEZ",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                summary?.let { summaryData ->
                    DashboardCard(
                        title = "Total Revenue",
                        value = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            .format(summaryData.totalRevenue.toLong()),
                        percentageChange = "+32.40%", // Ganti dengan percentageRevenue dari ViewModel jika diperlukan
                        isPositive = true,
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                    DashboardCard(
                        title = "Total Dish Ordered",
                        value = summaryData.totalMenuItems.toString(),
                        percentageChange = "-12.40%",
                        isPositive = false,
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                    DashboardCard(
                        title = "Total Customer",
                        value = summaryData.totalCustomers.toString(),
                        percentageChange = "+2.40%",
                        isPositive = true,
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                } ?: run {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Menu Populer",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (popularFoodItems.isEmpty()) {
                    Text(
                        text = "Belum ada data menu populer",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(popularFoodItems) { (foodItem, quantity) ->
                            PopularMenuCard(foodItem = foodItem, quantity = quantity)
                        }
                    }
                }
                error?.let {
                    Text(
                        text = it,
                        color = Color.Merah,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    percentageChange: String,
    isPositive: Boolean,
    icon: Painter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = percentageChange,
                    color = if (isPositive) Color.Green else Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PopularMenuCard(foodItem: FoodItemEntity, quantity: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ) {
                // Placeholder untuk gambar
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(foodItem.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Dipesan: $quantity kali",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}