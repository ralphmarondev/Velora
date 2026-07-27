package com.ralphmarondev.velora.features.dashboard.model.repository

import com.ralphmarondev.velora.core.domain.model.TrafficRecord
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeLatestTrafficRecord(): Flow<TrafficRecord?>
}