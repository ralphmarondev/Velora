package com.ralphmarondev.velora.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ralphmarondev.velora.features.account.presentation.change_image.ChangeImageScreenRoot
import com.ralphmarondev.velora.features.account.presentation.profile.ProfileScreenRoot
import com.ralphmarondev.velora.features.auth.presentation.login.LoginScreenRoot
import com.ralphmarondev.velora.features.auth.presentation.register.RegisterScreenRoot
import com.ralphmarondev.velora.features.calendar.presentation.CalendarScreenRoot
import com.ralphmarondev.velora.features.dashboard.presentation.DashboardScreenRoot

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
                DashboardScreenRoot(
                    navigateToProfile = {
                        navController.navigate(Routes.Account) {
                            launchSingleTop = true
                        }
                    },
                    navigateToCalendar = {
                        navController.navigate(Routes.Calendar) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Routes.Calendar> {
                CalendarScreenRoot()
            }
        }

        navigation<Routes.Account>(
            startDestination = Routes.Profile
        ) {
            composable<Routes.Profile> {
                ProfileScreenRoot(
                    logout = {
                        navController.navigate(Routes.Auth) {
                            popUpTo(Routes.Home) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    selectImage = {
                        navController.navigate(Routes.ChangeImage) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Routes.ChangeImage> {
                ChangeImageScreenRoot()
            }
        }
    }
}