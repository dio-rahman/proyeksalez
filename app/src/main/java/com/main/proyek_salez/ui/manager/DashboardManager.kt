package com.main.proyek_salez.ui.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.ManagerViewModel
import com.main.proyek_salez.ui.sidebar.SidebarManager
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardManager(
    navController: NavController,
    viewModel: ManagerViewModel = hiltViewModel(),
) {
    val summaries: Pair<DailySummaryEntity?, DailySummaryEntity?> by viewModel.summaries.collectAsState(initial = null to null)
    val error by viewModel.error.collectAsState(initial = null)
    val popularFoodItems by viewModel.popularFoodItems.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Putih, Jingga, UnguTua)
    )

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = UnguTua
                        )
                    }
                    Text(
                        text = "SALEZ",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                summaries.first?.let { latestSummary ->
                    DashboardCard(
                        title = "Total Revenue",
                        value = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            .format(latestSummary.totalRevenue.toLong()),
                        percentageChange = calculatePercentageChange(
                            latestSummary.totalRevenue,
                            summaries.second?.totalRevenue
                        ),
                        isPositive = summaries.second?.let { prev ->
                            latestSummary.totalRevenue >= prev.totalRevenue
                        } ?: true,
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                    DashboardCard(
                        title = "Total Dish Ordered",
                        value = latestSummary.totalMenuItems.toString(),
                        percentageChange = calculatePercentageChange(
                            latestSummary.totalMenuItems.toDouble(),
                            summaries.second?.totalMenuItems?.toDouble()
                        ),
                        isPositive = summaries.second?.let { prev ->
                            latestSummary.totalMenuItems >= prev.totalMenuItems
                        } ?: true,
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                    DashboardCard(
                        title = "Total Customer",
                        value = latestSummary.totalCustomers.toString(),
                        percentageChange = calculatePercentageChange(
                            latestSummary.totalCustomers.toDouble(),
                            summaries.second?.totalCustomers?.toDouble()
                        ),
                        isPositive = summaries.second?.let { prev ->
                            latestSummary.totalCustomers >= prev.totalCustomers
                        } ?: true,
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                } ?: run {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Menu Populer",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (popularFoodItems.isEmpty()) {
                    Text(
                        text = "Belum ada data menu populer",
                        color = UnguTua.copy(alpha = 0.6f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(popularFoodItems) { (foodItem, quantity) ->
                            PopularMenuCard(foodItem = foodItem, quantity = quantity)
                        }
                    }
                }
                error?.let {
                    Text(
                        text = it,
                        color = Merah,
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Putih),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = UnguTua
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnguTua.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = UnguTua
                    )
                )
                Text(
                    text = percentageChange,
                    color = if (isPositive) Color.Green else Merah,
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
        colors = CardDefaults.cardColors(containerColor = Putih),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = UnguTua.copy(alpha = 0.2f)
            ) {
                // Placeholder untuk gambar
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = UnguTua
                    )
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(foodItem.price),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UnguTua.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "Dipesan: $quantity kali",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UnguTua.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}

fun calculatePercentageChange(current: Double, previous: Double?): String {
    if (previous == null || previous == 0.0) return "+0.00%"
    val change = ((current - previous) / previous) * 100
    return String.format("%.2f%%", change).let {
        if (change >= 0) "+$it" else it
    }
}