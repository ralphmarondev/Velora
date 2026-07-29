package com.ralphmarondev.velora.features.dashboard.data.repository

import com.ralphmarondev.velora.core.data.network.FirebaseDatabaseService
import com.ralphmarondev.velora.core.domain.model.TrafficRecord
import com.ralphmarondev.velora.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow

class DashboardRepositoryImpl(
    private val firebaseDatabaseService: FirebaseDatabaseService
) : DashboardRepository {
    override fun observeLatestTrafficRecord(): Flow<TrafficRecord?> {
        return firebaseDatabaseService.observeTrafficRecord()
    }
}