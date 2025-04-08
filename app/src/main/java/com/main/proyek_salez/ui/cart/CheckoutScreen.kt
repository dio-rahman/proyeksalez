package com.main.proyek_salez.ui.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.main.proyek_salez.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    customerName: String
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "PILIH PEMBAYARAN",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Pesanan atas nama",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnguTua,
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { /* Read-only */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .clip(RoundedCornerShape(50)),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Putih,
                        focusedContainerColor = Putih,
                        focusedBorderColor = UnguTua,
                        unfocusedBorderColor = AbuAbu
                    ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = { navController.navigate("completion_screen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(50)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Oranye
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Tunai",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("completion_screen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(50)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Oranye
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Non Tunai",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(100 .dp))
                Button(
                    onClick = { navController.navigate("cart_screen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(50)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Kembali Ke Keranjang",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Putih,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}