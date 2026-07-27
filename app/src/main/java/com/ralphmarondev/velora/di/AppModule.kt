package com.ralphmarondev.velora.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ralphmarondev.velora.core.di.coreModule
import com.ralphmarondev.velora.features.account.di.accountModule
import com.ralphmarondev.velora.features.auth.di.authModule
import com.ralphmarondev.velora.features.dashboard.di.dashboardModule
import com.ralphmarondev.velora.receiver.NotificationHelper
import com.ralphmarondev.velora.receiver.TrafficListener
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    includes(coreModule)
    includes(authModule)
    includes(dashboardModule)
    includes(accountModule)

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseDatabase.getInstance("https://project-velora-default-rtdb.asia-southeast1.firebasedatabase.app") }
    singleOf(::NotificationHelper)
    singleOf(::TrafficListener)
}