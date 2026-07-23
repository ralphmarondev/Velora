package com.ralphmarondev.velora.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ralphmarondev.velora.core.di.coreModule
import com.ralphmarondev.velora.features.auth.di.authModule
import org.koin.dsl.module

val appModule = module {
    includes(coreModule)
    includes(authModule)

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseDatabase.getInstance() }
}