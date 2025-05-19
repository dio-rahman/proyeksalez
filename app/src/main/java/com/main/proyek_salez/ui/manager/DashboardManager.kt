package com.main.proyek_salez.ui.manager

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
                when {
                    error != null -> {
                        Text(
                            text = error ?: "Unknown error",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    summary != null -> {
                        summary?.let { summaryData ->
                            DashboardCard(
                                title = "Total Revenue",
                                value = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                                    .format(summaryData.totalRevenue.toLong()),
                                percentageChange = viewModel.percentageRevenue, // Gunakan dari ViewModel
                                isPositive = summaryData.totalRevenue >= (summaryData.previousRevenue ?: 0.0),
                                icon = painterResource(id = R.drawable.ic_menu_gallery)
                            )
                            DashboardCard(
                                title = "Total Dish Ordered",
                                value = summaryData.totalMenuItems.toString(),
                                percentageChange = if (summaryData.previousMenuItems != null && summaryData.previousMenuItems > 0) {
                                    val change = ((summaryData.totalMenuItems - summaryData.previousMenuItems) * 100.0) / summaryData.previousMenuItems
                                    "${"%.2f".format(change)}%"
                                } else "0.00%",
                                isPositive = summaryData.totalMenuItems >= (summaryData.previousMenuItems ?: 0),
                                icon = painterResource(id = R.drawable.ic_menu_gallery)
                            )
                            DashboardCard(
                                title = "Total Customer",
                                value = summaryData.totalCustomers.toString(),
                                percentageChange = if (summaryData.previousCustomers != null && summaryData.previousCustomers > 0) {
                                    val change = ((summaryData.totalCustomers - summaryData.previousCustomers) * 100.0) / summaryData.previousCustomers
                                    "${"%.2f".format(change)}%"
                                } else "0.00%",
                                isPositive = summaryData.totalCustomers >= (summaryData.previousCustomers ?: 0),
                                icon = painterResource(id = R.drawable.ic_menu_gallery)
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "Tidak ada data ringkasan tersedia",
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
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