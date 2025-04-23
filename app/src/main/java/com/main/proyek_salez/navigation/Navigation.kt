package com.main.proyek_salez.navigation

import androidx.navigation.NavHostController
import com.main.proyek_salez.data.entities.User
import com.main.proyek_salez.data.entities.UserRole

class MainNavigation(
    private val navController: NavHostController
) {
    fun navigateBasedOnRole(user: User) {
        when (user.role) {
            UserRole.CASHIER -> navController.navigate(Screen.CashierDashboard.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
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