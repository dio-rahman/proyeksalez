package com.main.proyek_salez

import SalezTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.proyek_salez.ui.auth.LoginScreen
import com.main.proyek_salez.ui.cashier.CartScreen
import com.main.proyek_salez.ui.cashier.CheckoutScreen
import com.main.proyek_salez.ui.cashier.MenuSelectionScreen
import com.main.proyek_salez.ui.cashier.OrderViewModel
import com.main.proyek_salez.ui.cashier.dashboard.DashboardScreen
import com.main.proyek_salez.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    // ID kasir untuk simulasi
    private val cashierId = "user_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate called")

        setContent {
            SalezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inisialisasi navController untuk navigasi
                    val navController = rememberNavController()

                    // Shared ViewModel untuk seluruh flow pemesanan
                    val orderViewModel: OrderViewModel = hiltViewModel()
                    val authViewModel: AuthViewModel = hiltViewModel()

                    // Observe state untuk loading dan error
                    val isLoading by orderViewModel.isLoading.collectAsState()
                    val errorMessage by orderViewModel.errorMessage.collectAsState()

                    // Loading overlay jika diperlukan
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Setup NavHost dengan navigation graph
                        NavHost(
                            navController = navController,
                            startDestination = "login"
                        ) {
                            // Login screen
                            composable("login") {
                                Log.d(TAG, "Navigating to Login screen")
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onLoginSuccess = { user ->
                                        // Navigasi ke dashboard setelah login berhasil
                                        navController.navigate("dashboard") {
                                            // Hapus backstack agar tidak bisa kembali ke login
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // Dashboard screen
                            composable("dashboard") {
                                Log.d(TAG, "Navigating to Dashboard screen")
                                DashboardScreen(
                                    cashierId = cashierId,
                                    onStartNewOrder = {
                                        // Reset state order sebelum memulai order baru
                                        orderViewModel.clearOrder()
                                        // Navigasi ke menu selection
                                        navController.navigate("menu_selection")
                                    }
                                )
                            }

                            // Menu selection screen
                            composable("menu_selection") {
                                Log.d(TAG, "Navigating to Menu Selection screen")
                                MenuSelectionScreen(
                                    onNavigateToCart = {
                                        navController.navigate("cart")
                                    },
                                    viewModel = orderViewModel
                                )
                            }

                            // Cart screen
                            composable("cart") {
                                Log.d(TAG, "Navigating to Cart screen")
                                CartScreen(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToCheckout = {
                                        navController.navigate("checkout")
                                    },
                                    viewModel = orderViewModel
                                )
                            }

                            // Checkout screen
                            composable("checkout") {
                                Log.d(TAG, "Navigating to Checkout screen")
                                CheckoutScreen(
                                    cashierId = cashierId,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    onPaymentComplete = {
                                        // Reset state order setelah pembayaran selesai
                                        orderViewModel.clearOrder()
                                        // Kembali ke dashboard setelah pembayaran selesai
                                        navController.popBackStack("dashboard", inclusive = false)
                                    },
                                    viewModel = orderViewModel
                                )
                            }
                        }

                        // Tampilkan loading indicator jika diperlukan
                        if (isLoading) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Black.copy(alpha = 0.3f)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        // Tampilkan error message jika ada
                        errorMessage?.let {
                            Snackbar(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp),
                                action = {
                                    TextButton(onClick = { orderViewModel.resetErrorMessage() }) {
                                        Text("Tutup")
                                    }
                                }
                            ) {
                                Text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}