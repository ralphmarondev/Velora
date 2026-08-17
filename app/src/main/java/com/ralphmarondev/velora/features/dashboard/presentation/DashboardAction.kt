package com.ralphmarondev.velora.features.dashboard.presentation

sealed interface DashboardAction {
    data object Refresh : DashboardAction
    data object NavigateToProfile : DashboardAction
    data object NavigateToCalendar : DashboardAction
    data object ClearNavigation : DashboardAction
    data object LoadAccountInformation : DashboardAction
}