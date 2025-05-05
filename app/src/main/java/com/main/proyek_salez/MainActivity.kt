package com.main.proyek_salez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.main.proyek_salez.navigation.AppNavigation
import com.main.proyek_salez.ui.theme.ProyekSalezTheme
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

