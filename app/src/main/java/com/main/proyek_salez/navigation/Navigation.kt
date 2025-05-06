package com.main.proyek_salez.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.proyek_salez.data.model.User
import com.main.proyek_salez.data.model.UserRole
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.data.viewmodel.CashierViewModel
import com.main.proyek_salez.ui.HomeScreen
import com.main.proyek_salez.ui.LoginScreen
import com.main.proyek_salez.ui.OnboardingApp
import com.main.proyek_salez.ui.cart.CartScreen
import com.main.proyek_salez.ui.checkout.CheckoutScreen
import com.main.proyek_salez.ui.checkout.CompletionScreen
import com.main.proyek_salez.ui.manager.ManagerScreen
import com.main.proyek_salez.ui.menu.DrinkMenuScreen
import com.main.proyek_salez.ui.menu.FoodMenuScreen
import com.main.proyek_salez.ui.menu.OtherMenuScreen
import com.main.proyek_salez.ui.order.OrderHistoryScreen
import com.main.proyek_salez.ui.sidebar.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val mainNavigation = remember { MainNavigation(navController) }
    val currentUserState = authViewModel.currentUser.observeAsState()
    val currentUser = currentUserState.value

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUser()
    }
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingApp(
                onFinish = { navController.navigate(Screen.Login.route) { popUpTo("onboarding") { inclusive = true } } }
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

        composable("manager_dashboard") {
            ManagerScreen()
        }

        composable("cashier_dashboard") {
            HomeScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable("food_menu") {
            FoodMenuScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable("drink_menu") {
            DrinkMenuScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable("other_menu") {
            OtherMenuScreen(navController = navController, viewModel = hiltViewModel())
            }
        composable("cart_screen") {
            CartScreen(
                navController = navController,
                cartViewModel = hiltViewModel<CartViewModel>()
            )
        }
        composable("checkout_screen") {
            CheckoutScreen(
                navController = navController
            )
        }
        composable("completion_screen") {
            CompletionScreen(
                navController = navController
            )
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("order_history") {
            OrderHistoryScreen(navController = navController)
        }
    }

    LaunchedEffect(currentUserState.value) {
        currentUserState.value?.let { user ->
            mainNavigation.navigateBasedOnRole(user)
        }
    }
}

class MainNavigation(
    private val navController: NavHostController
) {
    fun navigateBasedOnRole(user: User) {
        when (user.role) {
            UserRole.CASHIER -> navController.navigate(Screen.CashierDashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }

            UserRole.CHEF -> TODO()
            UserRole.MANAGER -> navController.navigate(Screen.ManagerDashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CashierDashboard : Screen("cashier_dashboard")
    object ManagerDashboard : Screen("manager_dashboard")
}