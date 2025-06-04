package com.main.proyek_salez.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.main.proyek_salez.ui.CloseOrderScreen
import com.main.proyek_salez.ui.HomeScreen
import com.main.proyek_salez.ui.LoginScreen
import com.main.proyek_salez.ui.OnboardingApp
import com.main.proyek_salez.ui.cart.CartScreen
import com.main.proyek_salez.ui.cart.CheckoutScreen
import com.main.proyek_salez.ui.checkout.CompletionScreen
import com.main.proyek_salez.ui.manager.DashboardManager
import com.main.proyek_salez.ui.manager.ManagerScreen
import com.main.proyek_salez.ui.manager.OrderHistoryManager
import com.main.proyek_salez.ui.menu.OrderHistoryScreen
import com.main.proyek_salez.ui.sidebar.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val cashierViewModel: CashierViewModel = hiltViewModel()
    val cartViewModel: CartViewModel = hiltViewModel()
    val mainNavigation = MainNavigation(navController)
    val currentUserState = authViewModel.currentUser.observeAsState()
    val isLoggedIn = authViewModel.isLoggedIn.observeAsState(initial = false)

    LaunchedEffect(isLoggedIn.value, currentUserState.value) {
        Log.d("AppNavigation", "State changed: isLoggedIn=${isLoggedIn.value}, user=${currentUserState.value?.email}, role=${currentUserState.value?.role}")
        if (isLoggedIn.value && currentUserState.value != null) {
            mainNavigation.navigateBasedOnRole(currentUserState.value!!)
        } else if (!isLoggedIn.value && navController.currentDestination?.route != Screen.Login.route) {
            Log.d("AppNavigation", "Not logged in, navigating to LoginScreen")
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable("onboarding") {
            OnboardingApp(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo("onboarding") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    Log.d("AppNavigation", "Login success, navigating for user: ${user.email}, role: ${user.role}")
                    mainNavigation.navigateBasedOnRole(user)
                }
            )
        }
        composable(Screen.ManagerScreen.route) {
            ManagerScreen(navController = navController)
        }

        composable(Screen.CashierDashboard.route) {
            if (isLoggedIn.value && currentUserState.value?.role == UserRole.CASHIER) {
                Log.d("AppNavigation", "Showing CashierDashboard for user: ${currentUserState.value?.email}")
                HomeScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    cashierViewModel = cashierViewModel
                )
            } else {
                Log.w("AppNavigation", "Redirecting to Login: isLoggedIn=${isLoggedIn.value}, role=${currentUserState.value?.role}")
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
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
    }
}

class MainNavigation(
    private val navController: NavHostController
) {
    fun navigateBasedOnRole(user: User) {
        Log.d("MainNavigation", "Navigating for user: ${user.email}, role: ${user.role}")
        when (user.role) {
            UserRole.CASHIER -> {
                Log.d("MainNavigation", "Navigating to CashierDashboard")
                navController.navigate(Screen.CashierDashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            UserRole.CHEF -> {
                Log.w("MainNavigation", "Navigasi untuk CHEF belum diimplementasikan")
                // TODO: Tambahkan navigasi untuk CHEF
            }
            UserRole.MANAGER -> {
                Log.d("MainNavigation", "Navigating to ManagerScreen")
                navController.navigate(Screen.ManagerScreen.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CashierDashboard : Screen("cashier_dashboard")
    object ManagerScreen : Screen("manager_screen")
}