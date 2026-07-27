package com.ralphmarondev.velora.features.dashboard.presentation

import com.ralphmarondev.velora.core.domain.model.TrafficRecord

data class DashboardState(
    val message: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val imagePath: Int? = null,
    val navigateToProfile: Boolean = false,
    val trafficRecord: TrafficRecord = TrafficRecord()
)