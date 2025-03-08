package com.opendata.jabar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.opendata.jabar.viewmodel.DataViewModel
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(viewModel: DataViewModel) {
    val navController = rememberNavController()
    val onboardingCompleted = rememberOnboardingStatus()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted) "main" else "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                setOnboardingCompleted(context)
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("DataEntryScreen") {
            DataEntryScreen(navController = navController, viewModel = viewModel)
        }
        composable("DataEntryScreenAutoExcel") {
            DataEntryScreenAutoExcel(navController = navController, viewModel = viewModel)
        }
        composable("DataEntryScreenAutoPdf") {
            DataEntryScreenAutoPdf(navController = navController, viewModel = viewModel)
        }
        composable("list") {
            DataListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditScreen(navController = navController, viewModel = viewModel, dataId = id)
        }
    }
}