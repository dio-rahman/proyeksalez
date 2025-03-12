package com.salez.kasir

import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salez.kasir.ui.AppNavHost
import com.salez.kasir.ui.theme.kasirTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.salez.kasir.viewmodel.DataViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            kasirTheme {
                val dataViewModel: DataViewModel = viewModel()
                AppNavHost(viewModel = dataViewModel)
            }
        }
    }
}