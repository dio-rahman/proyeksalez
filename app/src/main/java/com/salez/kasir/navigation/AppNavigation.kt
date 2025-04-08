package com.salez.kasir.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.salez.kasir.ui.cashier.CartScreen
import com.salez.kasir.ui.cashier.CheckoutScreen
import com.salez.kasir.ui.cashier.MenuSelectionScreen
import com.salez.kasir.ui.cashier.OrderViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OrderNavigation(
    navController: NavHostController = rememberNavController(),
    cashierId: String,
    onOrderComplete: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {

    val orderActions = remember(navController) {
        OrderNavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = OrderDestinations.MENU_SELECTION
    ) {
        composable(OrderDestinations.MENU_SELECTION) {
            MenuSelectionScreen(
                onNavigateToCart = orderActions.navigateToCart,
                viewModel = viewModel
            )
        }

        composable(OrderDestinations.CART) {
            CartScreen(
                onNavigateBack = orderActions.navigateBack,
                onNavigateToCheckout = orderActions.navigateToCheckout,
                viewModel = viewModel
            )
        }

        composable(OrderDestinations.CHECKOUT) {
            CheckoutScreen(
                cashierId = cashierId,
                onNavigateBack = orderActions.navigateBack,
                onPaymentComplete = {

                    onOrderComplete()
                },
                viewModel = viewModel
            )
        }
    }
}

class OrderNavigationActions(private val navController: NavHostController) {
    val navigateToCart: () -> Unit = {
        navController.navigate(OrderDestinations.CART)
    }

    val navigateToCheckout: () -> Unit = {
        navController.navigate(OrderDestinations.CHECKOUT)
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }


    val cancelOrder: () -> Unit = {
        navController.popBackStack(OrderDestinations.MENU_SELECTION, inclusive = true)
    }
}

object OrderDestinations {
    const val MENU_SELECTION = "menu_selection"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
}