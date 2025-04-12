package com.salez.kasir.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.salez.kasir.data.models.User
import com.salez.kasir.data.models.UserRole
import com.salez.kasir.ui.auth.LoginScreen
import com.salez.kasir.ui.cashier.dashboard.DashboardScreen
import com.salez.kasir.ui.manager.ManagerDashboardScreen
import com.salez.kasir.ui.manager.MenuManagementScreen
import com.salez.kasir.ui.manager.ReportsScreen
import com.salez.kasir.viewmodel.AuthViewModel

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

@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUserState = authViewModel.currentUser.observeAsState()
    val currentUser = currentUserState.value
    val mainNavigation = remember { MainNavigation(navController) }

    // Periksa user saat ini saat aplikasi dimulai
    LaunchedEffect(Unit) {
        authViewModel.getCurrentUser()
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    mainNavigation.navigateBasedOnRole(user)
                }
            )
        }

        composable(Screen.NewOrder.route) {
            OrderNavigation(
                cashierId = currentUser?.userId ?: "",
                onOrderComplete = {
                    // Kembali ke dashboard setelah pesanan selesai
                    navController.navigate(Screen.CashierDashboard.route) {
                        popUpTo(Screen.CashierDashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CashierDashboard.route) {
            // Periksa apakah pengguna saat ini adalah kasir
            if (currentUser?.role == UserRole.CASHIER) {
                val cashierId = currentUser?.userId ?: ""

                DashboardScreen(
                    cashierId = cashierId,
                    onStartNewOrder = {
                        // Navigasi ke halaman pembuatan pesanan baru
                        navController.navigate(Screen.NewOrder.route)
                    }
                )
            } else {
                // Jika bukan, kembali ke login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.ChefDashboard.route) {
            // Periksa apakah pengguna saat ini adalah kasir
            if (currentUser?.role == UserRole.CASHIER) {
                val cashierId = currentUser?.userId ?: ""

                DashboardScreen(
                    cashierId = cashierId,
                    onStartNewOrder = {
                        // Navigasi ke halaman pembuatan pesanan baru
                        navController.navigate(Screen.NewOrder.route)
                    }
                )
            } else {
                // Jika bukan, kembali ke login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.ManagerDashboard.route) {
            // Periksa apakah pengguna saat ini adalah kasir
            if (currentUser?.role == UserRole.CASHIER) {
                val cashierId = currentUser?.userId ?: ""

                DashboardScreen(
                    cashierId = cashierId,
                    onStartNewOrder = {
                        // Navigasi ke halaman pembuatan pesanan baru
                        navController.navigate(Screen.NewOrder.route)
                    }
                )
            } else {
                // Jika bukan, kembali ke login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
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
            UserRole.CHEF -> navController.navigate(Screen.ChefDashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            UserRole.MANAGER -> navController.navigate(Screen.ManagerDashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CashierDashboard : Screen("cashier_dashboard")
    object ChefDashboard : Screen("chef_dashboard")
    object ManagerDashboard : Screen("manager_dashboard")
    object NewOrder : Screen("new_order")
}

@Composable
fun ManagerNavigation(
    navController: NavHostController = rememberNavController()
) {
    val managerActions = remember(navController) {
        ManagerNavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = ManagerDestinations.DASHBOARD
    ) {
        composable(ManagerDestinations.DASHBOARD) {
            ManagerDashboardScreen(
                onNavigateToMenuManagement = managerActions.navigateToMenuManagement,
                onNavigateToReports = managerActions.navigateToReports
            )
        }

        composable(ManagerDestinations.MENU_MANAGEMENT) {
            MenuManagementScreen(
                onNavigateBack = managerActions.navigateBack
            )
        }

        composable(ManagerDestinations.REPORTS) {
            ReportsScreen(
                onNavigateBack = managerActions.navigateBack
            )
        }
    }
}

class ManagerNavigationActions(private val navController: NavHostController) {
    val navigateToMenuManagement: () -> Unit = {
        navController.navigate(ManagerDestinations.MENU_MANAGEMENT)
    }

    val navigateToReports: () -> Unit = {
        navController.navigate(ManagerDestinations.REPORTS)
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}

object ManagerDestinations {
    const val DASHBOARD = "manager_dashboard"
    const val MENU_MANAGEMENT = "menu_management"
    const val REPORTS = "reports"
}