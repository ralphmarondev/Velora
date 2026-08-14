package com.ralphmarondev.velora.features.calendar.di

import com.ralphmarondev.velora.features.calendar.presentation.CalendarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val calendarModule = module {
    viewModelOf(::CalendarViewModel)
}