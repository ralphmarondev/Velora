package com.ralphmarondev.velora

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.ralphmarondev.velora.core.data.local.preferences.AppPreferences
import com.ralphmarondev.velora.core.presentation.theme.LocalThemeState
import com.ralphmarondev.velora.core.presentation.theme.ThemeProvider
import com.ralphmarondev.velora.core.presentation.theme.VeloraTheme
import com.ralphmarondev.velora.navigation.AppNavigation
import com.ralphmarondev.velora.navigation.Routes
import com.ralphmarondev.velora.receiver.NotificationHelper
import com.ralphmarondev.velora.receiver.TrafficForegroundService
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, granted) ->
            if (!granted) {
                Log.w("MainActivity", "$permission was denied.")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper(this).createNotificationChannel()
        requestRequiredPermissions()
        setContent {
            ThemeProvider(preferences = preferences) {
                val themeState = LocalThemeState.current
                val darkTheme = themeState.darkTheme.value
                var startDestination by remember { mutableStateOf<Routes?>(null) }

                LaunchedEffect(Unit) {
                    val uidNotEmpty = preferences.uid.first()?.isNotEmpty() == true
                    startDestination = if (uidNotEmpty) {
                        ContextCompat.startForegroundService(
                            this@MainActivity,
                            Intent(this@MainActivity, TrafficForegroundService::class.java)
                        )
                        Routes.Home
                    } else {
                        Routes.Auth
                    }
                    NotificationHelper(this@MainActivity).showNotification(
                        title = "Velora Notification",
                        message = "Traffic may occur. Please consider re-routing!"
                    )
                }

                VeloraTheme(darkTheme = darkTheme) {
                    startDestination?.let { destination ->
                        AppNavigation(startDestination = destination)
                    }
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions += Manifest.permission.POST_NOTIFICATIONS
            }
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }

    }
}