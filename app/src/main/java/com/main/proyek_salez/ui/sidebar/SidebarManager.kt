package com.main.proyek_salez.ui.sidebar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.main.proyek_salez.R
import com.main.proyek_salez.ui.theme.*
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import com.main.proyek_salez.navigation.Screen

@Composable
fun SidebarManager(
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val showDialog = remember { mutableStateOf(false) }
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Jingga,
            Oranye
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(brush = gradientBackground)
            .padding(horizontal = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.salez_logo),
                contentDescription = "Salez Logo",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopStart)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        MenuItem(
            text = "Dashboard",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            onClick = {
                navController.navigate("manager_dashboard")
                onCloseDrawer()}
        )
        MenuItem(
            text = "Tambah Menu",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            onClick = {
                navController.navigate("manager_screen")
                onCloseDrawer()
            }
        )
        MenuItem(
            text = "Cek Histori",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            onClick = {
                navController.navigate("order_history")
                onCloseDrawer()
            }
        )
        MenuItem(
            text = "Log Out",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Putih,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            onClick = { showDialog.value = true }
        )
        if (showDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    println("Logout button pressed")
                    Button(onClick = {
                        authViewModel.logout()
                        showDialog.value = false
                        println("Navigating to login screen")
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Batal")
                    }
                },
                title = { Text("Konfirmasi Logout") },
                text = { Text("Apakah Anda yakin ingin logout?") }
            )
        }
    }
}


@Composable
fun MenuItem(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = Putih,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = style
        )
    }
}

