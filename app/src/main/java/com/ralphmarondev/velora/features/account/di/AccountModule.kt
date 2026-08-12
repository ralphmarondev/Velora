package com.ralphmarondev.velora.features.account.di

import com.ralphmarondev.velora.features.account.data.repository.AccountRepositoryImpl
import com.ralphmarondev.velora.features.account.domain.repository.AccountRepository
import com.ralphmarondev.velora.features.account.presentation.change_image.ChangeImageViewModel
import com.ralphmarondev.velora.features.account.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val accountModule = module {
    singleOf(::AccountRepositoryImpl).bind<AccountRepository>()
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ChangeImageViewModel)
}