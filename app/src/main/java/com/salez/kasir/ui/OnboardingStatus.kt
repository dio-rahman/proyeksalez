package com.salez.kasir.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val PREFS_NAME = "onboarding_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

@Composable
fun rememberOnboardingStatus(): Boolean {
    val context = LocalContext.current
    return remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
}

fun setOnboardingCompleted(context: Context) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_ONBOARDING_COMPLETED, true)
        .apply()
}