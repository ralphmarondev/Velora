package com.ralphmarondev.velora.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {

    @Serializable
    data object Auth : Routes

    @Serializable
    data object Login : Routes

    @Serializable
    data object Register : Routes

    @Serializable
    data object Home : Routes

    @Serializable
    data object Dashboard : Routes

    @Serializable
    data object Account : Routes

    @Serializable
    data object Profile : Routes
}