package com.ralphmarondev.velora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ralphmarondev.velora.core.data.local.preferences.AppPreferences
import com.ralphmarondev.velora.core.presentation.theme.LocalThemeState
import com.ralphmarondev.velora.core.presentation.theme.ThemeProvider
import com.ralphmarondev.velora.core.presentation.theme.VeloraTheme
import com.ralphmarondev.velora.navigation.AppNavigation
import com.ralphmarondev.velora.navigation.Routes
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemeProvider(preferences = preferences) {
                val themeState = LocalThemeState.current
                val darkTheme = themeState.darkTheme.value
                var startDestination by remember { mutableStateOf<Routes?>(null) }

                LaunchedEffect(Unit) {
                    val uidNotEmpty = preferences.uid.first()?.isNotEmpty() == true
                    startDestination = if (uidNotEmpty) {
                        Routes.Home
                    } else {
                        Routes.Auth
                    }
                }

                VeloraTheme(darkTheme = darkTheme) {
                    startDestination?.let { destination ->
                        AppNavigation(startDestination = destination)
                    }
                }
            }
        }
    }
}