package com.main.proyek_salez

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.main.proyek_salez.ui.theme.ProyekSalezTheme
import dagger.hilt.android.AndroidEntryPoint
import com.main.proyek_salez.navigation.AppNavigation
import com.google.firebase.messaging.FirebaseMessaging

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivityFCM"
        private const val TOPIC_NAME = "test_topic"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            subscribeToTopic()
        } else {
            Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show()
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        enableEdgeToEdge()
        setContent {
            FirebaseApp.initializeApp(this)
            Firebase.firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true // Dukungan offline
            }
            ProyekSalezTheme {
                AppNavigation()
            }
        }

    }
    private fun askNotificationPermission() {
        // This is only necessary for API level 33 and higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                Log.d(TAG, "Notification permission already granted.")
                subscribeToTopic()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: Display an educational UI explaining to the user the importance of the permission.
                // For now, just request it.
                Log.d(TAG, "Showing rationale for notification permission.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission.
                Log.d(TAG, "Requesting notification permission.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Non-TIRAMISU devices don't need runtime permission for notifications
            subscribeToTopic()
        }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NAME)
            .addOnCompleteListener { task ->
                var msg = "Berhasil Masuk Ke Halaman"
                if (!task.isSuccessful) {
                    msg = "Gagal Masuk Ke Halaman Karena: ${task.exception?.message}"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }
}

