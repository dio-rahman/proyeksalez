package com.main.proyek_salez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.proyek_salez.ui.theme.ProyekSalezTheme
import com.main.proyek_salez.ui.HomeScreen
import com.main.proyek_salez.ui.menu.FoodMenuScreen
import com.main.proyek_salez.ui.menu.DrinkMenuScreen
import com.main.proyek_salez.ui.menu.OtherMenuScreen
import com.main.proyek_salez.ui.cart.CartScreen
import com.main.proyek_salez.ui.cart.CartViewModel
import com.main.proyek_salez.ui.checkout.CheckoutScreen
import com.main.proyek_salez.ui.checkout.CompletionScreen
import androidx.lifecycle.viewmodel.compose.viewModel

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
    val cartViewModel: CartViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingApp(
                onFinish = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } }
            )
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("food_menu") {
            FoodMenuScreen(navController = navController)
        }
        composable("drink_menu") {
            DrinkMenuScreen(navController = navController)
        }
        composable("other_menu") {
            OtherMenuScreen(navController = navController)
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
    }
}