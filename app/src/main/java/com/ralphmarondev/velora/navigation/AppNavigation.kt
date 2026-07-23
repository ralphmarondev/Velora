package com.ralphmarondev.velora.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ralphmarondev.velora.features.auth.presentation.login.LoginScreenRoot
import com.ralphmarondev.velora.features.auth.presentation.register.RegisterScreenRoot

@Composable
fun AppNavigation(
    startDestination: Routes = Routes.Auth,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        navigation<Routes.Auth>(
            startDestination = Routes.Login
        ) {
            composable<Routes.Login> {
                LoginScreenRoot(
                    onRegister = {
                        navController.navigate(Routes.Register) {
                            launchSingleTop = true
                        }
                    },
                    onLoginSuccess = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Auth) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Routes.Register> {
                RegisterScreenRoot(
                    onLogin = {
                        navController.navigateUp()
                    },
                    onRegisterSuccessful = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Auth) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        navigation<Routes.Home>(
            startDestination = Routes.Dashboard
        ) {
            composable<Routes.Dashboard> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Velora Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}