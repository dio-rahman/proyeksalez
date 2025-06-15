package com.main.proyek_salez.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.proyek_salez.data.model.User
import com.main.proyek_salez.data.model.UserRole
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.*
import com.main.proyek_salez.ui.cart.CartScreen
import com.main.proyek_salez.ui.cart.CheckoutScreen
import com.main.proyek_salez.ui.cart.CompletionScreen
import com.main.proyek_salez.ui.manager.DashboardManager
import com.main.proyek_salez.ui.manager.ManagerScreen
import com.main.proyek_salez.ui.manager.OrderHistoryManager
import com.main.proyek_salez.ui.menu.OrderHistoryScreen
import com.main.proyek_salez.ui.sidebar.ManagerProfileScreen
import com.main.proyek_salez.ui.sidebar.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val cashierViewModel: CashierViewModel = hiltViewModel()
    val cartViewModel: CartViewModel = hiltViewModel()
    val mainNavigation = MainNavigation(navController)

    // Ambil state login sekali untuk menentukan durasi dan navigasi
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(initial = false)
    val currentUser by authViewModel.currentUser.observeAsState()

    NavHost(
        navController = navController,
        startDestination = "splash" // Splash screen SELALU menjadi layar awal
    ) {
        composable("splash") {
            // Tentukan durasi berdasarkan status login
            val duration = if (isLoggedIn) 1500L else 3000L // 1.5 detik jika login, 3 detik jika tidak

            SplashScreen(
                duration = duration,
                onTimeout = {
                    // Setelah timeout, tentukan tujuan navigasi
                    val destination = if (isLoggedIn && currentUser != null) {
                        // Jika sudah login, tentukan rute berdasarkan role
                        when (currentUser!!.role) {
                            UserRole.CASHIER -> Screen.CashierDashboard.route
                            UserRole.MANAGER -> Screen.ManagerScreen.route
                            UserRole.CHEF -> Screen.Login.route // Ganti dengan rute Chef jika ada
                        }
                    } else {
                        // Jika belum login, arahkan ke Login
                        Screen.Login.route
                    }

                    // Lakukan navigasi
                    navController.navigate(destination) {
                        // Hapus splash screen dari back stack agar tidak bisa kembali ke sana
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    mainNavigation.navigateBasedOnRole(user)
                }
            )
        }

        // ... (sisa kode composable Anda TIDAK BERUBAH) ...
        composable(Screen.ManagerScreen.route) {
            ManagerScreen(navController = navController)
        }
        composable(Screen.CashierDashboard.route) {
            HomeScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                cashierViewModel = cashierViewModel
            )
        }
        composable("cart_screen") {
            CartScreen(navController = navController, viewModel = cashierViewModel)
        }
        composable("checkout_screen") {
            CheckoutScreen(navController = navController, viewModel = cashierViewModel)
        }
        composable("completion_screen") {
            CompletionScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("order_history") {
            OrderHistoryScreen(navController = navController)
        }
        composable("order_history_manager") {
            OrderHistoryManager(navController = navController)
        }
        composable("close_order") {
            CloseOrderScreen(navController = navController)
        }
        composable("manager_dashboard") {
            DashboardManager(navController = navController)
        }
        composable("manager_profile") {
            ManagerProfileScreen(navController = navController)
        }
    }
}

class MainNavigation(
    private val navController: NavHostController
) {
    // Fungsi ini sekarang dipakai setelah login berhasil
    fun navigateBasedOnRole(user: User) {
        Log.d("MainNavigation", "Navigating for user: ${user.email}, role: ${user.role}")
        val destination = when (user.role) {
            UserRole.CASHIER -> Screen.CashierDashboard.route
            UserRole.CHEF -> Screen.Login.route // Ganti jika ada layar chef
            UserRole.MANAGER -> Screen.ManagerScreen.route
        }
        navController.navigate(destination) {
            // Hapus semua riwayat navigasi sampai ke login screen
            popUpTo(Screen.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CashierDashboard : Screen("cashier_dashboard")
    object ManagerScreen : Screen("manager_screen")
}