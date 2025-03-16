package com.salez.kasir.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.salez.kasir.viewmodel.DataViewModel
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(viewModel: DataViewModel) {
    val navController = rememberNavController()
    val onboardingCompleted = rememberOnboardingStatus()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            TampilanOnboarding(onFinish = {
                setOnboardingCompleted(context)
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("MasukanMenu") {
            MasukanMenu(navController = navController, viewModel = viewModel)
        }
        composable("MasukanMenuAutoExcel") {
            MasukanMenuAutoExcel(navController = navController, viewModel = viewModel)
        }
        composable("MasukanMenuAutoPdf") {
            MasukanMenuAutoPdf(navController = navController, viewModel = viewModel)
        }
        composable("list") {
            EditList(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditList(navController = navController, viewModel = viewModel, dataId = id)
        }
    }
}