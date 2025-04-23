package com.main.proyek_salez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.proyek_salez.ui.theme.ProyekSalezTheme
import com.main.proyek_salez.ui.HomeScreen
import com.main.proyek_salez.ui.menu.FoodMenuScreen
import com.main.proyek_salez.ui.menu.DrinkMenuScreen
import com.main.proyek_salez.ui.menu.OtherMenuScreen
import com.main.proyek_salez.ui.cart.CartScreen
import com.main.proyek_salez.ui.checkout.CheckoutScreen
import com.main.proyek_salez.ui.checkout.CompletionScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.main.proyek_salez.data.entities.User
import com.main.proyek_salez.data.entities.UserRole
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.ui.LoginScreen
import com.main.proyek_salez.ui.OnboardingApp
import com.main.proyek_salez.ui.sidebar.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyekSalezTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel(LocalContext.current as ComponentActivity)
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
                },
                startDestination = {
                    navController.navigate("onboarding") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable("cashier_dashboard") {
            HomeScreen(navController = navController, cartViewModel)
        }
        composable("food_menu") {
            FoodMenuScreen(navController = navController, cartViewModel = cartViewModel)
        }
        composable("drink_menu") {
            DrinkMenuScreen(navController = navController, cartViewModel = cartViewModel)
        }
        composable("other_menu") {
            OtherMenuScreen(navController = navController, cartViewModel = cartViewModel)
        }
        composable("cart_screen") {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
        composable("checkout_screen") {
            CheckoutScreen(
                navController = navController,
                customerName = cartViewModel.customerName.value
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
            UserRole.MANAGER -> TODO()
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CashierDashboard : Screen("cashier_dashboard")
}