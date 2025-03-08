package com.opendata.jabar

import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.opendata.jabar.ui.AppNavHost
import com.opendata.jabar.ui.theme.jabarTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.opendata.jabar.ui.AppNavHost
import com.opendata.jabar.viewmodel.DataViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            jabarTheme {
                val dataViewModel: DataViewModel = viewModel()
                AppNavHost(viewModel = dataViewModel)
            }
        }
    }
}