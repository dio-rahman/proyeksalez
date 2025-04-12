package com.main.proyek_salez

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.main.proyek_salez.data.DatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SalezApplication : Application() {
    @Inject lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            databaseInitializer.initialize()
        }
    }
}