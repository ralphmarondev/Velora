package com.ralphmarondev.velora.features.dashboard.presentation

data class DashboardState(
    val message: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val imagePath: Int? = null,
    val navigateToProfile: Boolean = false
)