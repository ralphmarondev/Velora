package com.ralphmarondev.velora.features.dashboard.presentation

sealed interface DashboardAction {
    data object Refresh : DashboardAction
    data object NavigateToProfile : DashboardAction
    data object ClearNavigation : DashboardAction
}