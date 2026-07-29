package com.ralphmarondev.velora.features.dashboard.di

import com.ralphmarondev.velora.features.dashboard.data.repository.DashboardRepositoryImpl
import com.ralphmarondev.velora.features.dashboard.domain.repository.DashboardRepository
import com.ralphmarondev.velora.features.dashboard.presentation.DashboardViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dashboardModule = module {
    singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()
    viewModelOf(::DashboardViewModel)
}