package com.ralphmarondev.velora.core.di

import com.ralphmarondev.velora.core.data.local.preferences.AppPreferences
import com.ralphmarondev.velora.core.data.network.FirebaseAuthService
import com.ralphmarondev.velora.core.data.network.FirebaseDatabaseService
import com.ralphmarondev.velora.core.data.network.FirebaseFirestoreService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreModule = module {
    singleOf(::AppPreferences)
    singleOf(::FirebaseAuthService)
    singleOf(::FirebaseFirestoreService)
    singleOf(::FirebaseDatabaseService)
}