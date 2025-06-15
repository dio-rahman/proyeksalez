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

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                duration = 1800L,
                onTimeout = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingAlur(onFinish = {
                navController.navigate(Screen.Login.route) {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    mainNavigation.navigateBasedOnRole(user)
                }
            )
        }

        composable(Screen.CashierDashboard.route) {
            HomeScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                cashierViewModel = cashierViewModel
            )
        }

        composable(Screen.ManagerScreen.route) {
            ManagerScreen(navController = navController)
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
    fun navigateBasedOnRole(user: User) {
        Log.d("MainNavigation", "Navigating for user: ${user.email}, role: ${user.role}")
        val destination = when (user.role) {
            UserRole.CASHIER -> Screen.CashierDashboard.route
            UserRole.MANAGER -> Screen.ManagerScreen.route
            else -> Screen.Login.route
        }
        navController.navigate(destination) {
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